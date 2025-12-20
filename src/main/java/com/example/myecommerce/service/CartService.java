package com.example.myecommerce.service;

import com.example.myecommerce.entity.*;
import com.example.myecommerce.repository.CartItemRepository;
import com.example.myecommerce.repository.OrderItemRepository;
import com.example.myecommerce.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final AddressService addressService;
    private final UserActivityService userActivityService;

    public CartService(CartItemRepository cartItemRepository,
                       ProductService productService,
                       UserService userService,
                       OrderRepository orderRepository,
                       OrderItemRepository orderItemRepository,
                       AddressService addressService,
                       UserActivityService userActivityService) {
        this.cartItemRepository = cartItemRepository;
        this.productService = productService;
        this.userService = userService;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.addressService = addressService;
        this.userActivityService = userActivityService;
    }

    // 加入购物车
    public void addToCart(String username, Long productId, int quantity) {
        User user = userService.getCurrentUser(username);
        Product product = productService.getProductById(productId);

        // 检查商品是否存在
        if (product == null) {
            throw new RuntimeException("商品未找到");
        }

        // 检查是否已在购物车中
        CartItem existingItem = cartItemRepository.findByUserAndProduct(user, product);
        if (existingItem != null) {
            // 更新数量
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
        } else {
            // 新增购物车项
            CartItem newItem = new CartItem();
            newItem.setUser(user);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);
        }

        // 记录用户活动
        userActivityService.recordAddToCart(user, product);
    }

    // 获取用户购物车
    public List<CartItem> getCartItems(String username) {
        User user = userService.getCurrentUser(username);
        return cartItemRepository.findByUser(user);
    }

    // 删除购物车项
    public void removeCartItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    // 清空购物车（结算后调用）
    public void clearCart(String username) {
        User user = userService.getCurrentUser(username);
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        cartItemRepository.deleteAll(cartItems);
    }

    // 计算购物车总金额
    public BigDecimal calculateCartTotal(String username) {
        List<CartItem> cartItems = getCartItems(username);
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : cartItems) {
            BigDecimal itemTotal = item.getProduct().getPrice()
                    .multiply(new BigDecimal(item.getQuantity()));
            total = total.add(itemTotal);
        }

        return total;
    }

    // 检查用户余额是否足够
    public boolean hasSufficientBalance(String username) {
        User user = userService.getCurrentUser(username);
        BigDecimal total = calculateCartTotal(username);
        return user.getBalance().compareTo(total) >= 0;
    }

    // 扣除用户资金
    public void deductBalance(String username, BigDecimal amount) {
        User user = userService.getCurrentUser(username);
        user.setBalance(user.getBalance().subtract(amount));
        userService.saveUserWithoutEncryption(user);
    }

    public Order createOrderFromCart(String username, Long addressId) {
        User user = userService.getCurrentUser(username);
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("购物车为空，无法创建订单");
        }
        // 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            totalAmount = totalAmount.add(
                    item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity()))
            );
        }
        // 检查余额
        if (user.getBalance().compareTo(totalAmount) < 0) {
            throw new RuntimeException("余额不足，无法完成购买");
        }
        // 创建订单
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(totalAmount);
        order.setStatus("待处理");
        // 设置收货地址信息
        if (addressId != null) {
            Address address = addressService.getAddressById(addressId);
            if (address != null && address.getUser().getId().equals(user.getId())) {
                order.setContactName(address.getContactName());
                order.setContactPhone(address.getPhone());
                order.setDeliveryAddress(address.getAddress());
            }
        } else {
            Address defaultAddress = addressService.getDefaultAddress(username);
            if (defaultAddress != null) {
                order.setContactName(defaultAddress.getContactName());
                order.setContactPhone(defaultAddress.getPhone());
                order.setDeliveryAddress(defaultAddress.getAddress());
            }
        }
        // 保存订单
        order = orderRepository.save(order);
        // 创建订单项
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            orderItemRepository.save(orderItem);
        }
        // 扣除用户资金
        user.setBalance(user.getBalance().subtract(totalAmount));
        userService.saveUserWithoutEncryption(user);
        // 记录购买活动
        for (CartItem cartItem : cartItems) {
            userActivityService.recordProductPurchase(
                    user,
                    cartItem.getProduct(),
                    order,
                    cartItem.getProduct().getPrice().doubleValue() * cartItem.getQuantity()
            );
        }
        return order;
    }
}