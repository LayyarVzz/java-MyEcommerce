package com.example.myecommerce.controller;

import com.example.myecommerce.entity.CartItem;
import com.example.myecommerce.entity.Order;
import com.example.myecommerce.entity.User;
import com.example.myecommerce.service.AddressService;
import com.example.myecommerce.service.CartService;
import com.example.myecommerce.service.MailService;
import com.example.myecommerce.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    private final AddressService addressService;
    private final UserService userService;
    private final MailService mailService;

    public CartController(CartService cartService,
                          AddressService addressService,
                          UserService userService, MailService mailService) {
        this.cartService = cartService;
        this.addressService = addressService;
        this.userService = userService;
        this.mailService = mailService;
    }

    // 加入购物车
    @PostMapping("/add")
    public String addToCart(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity,
            Authentication authentication) {
        String username = authentication.getName();
        cartService.addToCart(username, productId, quantity);
        return "redirect:/products"; // 跳转回商品列表页
    }

    // 查看购物车
    @GetMapping
    public String viewCart(Model model, Authentication authentication) {
        String username = authentication.getName();
        // 获取购物车项并传递到前端
        List<CartItem> cartItems = cartService.getCartItems(username);
        User user = userService.getCurrentUser(username);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("username", username);
        model.addAttribute("userBalance", user.getBalance());

        // 计算并传递总金额
        BigDecimal totalAmount = cartService.calculateCartTotal(username);
        model.addAttribute("totalAmount", totalAmount);

        return "cart"; // 对应 templates/cart.html
    }

    // 删除购物车项
    @GetMapping("/remove/{id}")
    public String removeCartItem(@PathVariable Long id) {
        cartService.removeCartItem(id);
        return "redirect:/cart"; // 跳转回购物车页
    }

    // 结算购物车 - 显示地址选择页面
    @GetMapping("/checkout")
    public String showCheckout(Model model, Authentication authentication) {
        String username = authentication.getName();
        BigDecimal totalAmount = cartService.calculateCartTotal(username);
        User user = userService.getCurrentUser(username);

        model.addAttribute("addresses", addressService.getUserAddresses(username));
        model.addAttribute("defaultAddress", addressService.getDefaultAddress(username));
        model.addAttribute("username", username);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("userBalance", user.getBalance());
        model.addAttribute("hasSufficientBalance", user.getBalance().compareTo(totalAmount) >= 0);
        return "checkout";
    }

    // 处理结账
    @PostMapping("/checkout")
    public String checkout(@RequestParam(required = false) Long addressId,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        String username = authentication.getName();

        try {
            // 检查余额
            if (!cartService.hasSufficientBalance(username)) {
                redirectAttributes.addFlashAttribute("error", "余额不足，无法完成购买");
                return "redirect:/cart";
            }

            // 创建订单逻辑
            Order order = cartService.createOrderFromCart(username, addressId);

            // 发送订单确认邮件
            User user = userService.getCurrentUser(username);
            mailService.sendOrderConfirm(user.getEmail(), order.getOrderNo(), order.getTotalAmount());

            // 清空购物车
            cartService.clearCart(username);

            // 添加成功消息
            redirectAttributes.addFlashAttribute("success", "购买成功！商品将发送至您的收货地址。");
            return "redirect:/orders/" + order.getId() + "?checkout=success";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cart";
        }
    }
}
