package com.example.myecommerce.controller;

import com.example.myecommerce.entity.Order;
import com.example.myecommerce.service.OrderService;
import com.example.myecommerce.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping
    public String orderHistory(Model model, Authentication authentication) {
        String username = authentication.getName();
        List<Order> orders = orderService.getOrderHistory(username);
        com.example.myecommerce.entity.User user = userService.getCurrentUser(username);
        model.addAttribute("orders", orders);
        model.addAttribute("username", username);
        model.addAttribute("userBalance", user.getBalance());
        return "order-history";
    }

    @GetMapping("/{id}")
    public String orderDetail(@PathVariable Long id, Model model, Authentication authentication) {
        Order order = orderService.getOrderById(id);
        if (order == null || !order.getUser().getUsername().equals(authentication.getName())) {
            return "redirect:/orders";
        }
        String username = authentication.getName();
        com.example.myecommerce.entity.User user = userService.getCurrentUser(username);
        model.addAttribute("order", order);
        model.addAttribute("username", username);
        model.addAttribute("userBalance", user.getBalance());
        return "order-detail";
    }
}

