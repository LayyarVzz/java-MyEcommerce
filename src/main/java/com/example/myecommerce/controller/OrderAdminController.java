package com.example.myecommerce.controller;

import com.example.myecommerce.entity.Order;
import com.example.myecommerce.entity.User;
import com.example.myecommerce.service.BackOfficeWorkspaceService;
import com.example.myecommerce.service.OrderService;
import com.example.myecommerce.service.UserActivityService;
import com.example.myecommerce.service.UserService;
import com.example.myecommerce.util.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/sales/orders")
@PreAuthorize("hasRole('SALES')")
public class OrderAdminController {

    private final OrderService orderService;
    private final UserService userService;
    private final UserActivityService userActivityService;
    private final BackOfficeWorkspaceService workspaceService;

    public OrderAdminController(OrderService orderService,
                                UserService userService,
                                UserActivityService userActivityService,
                                BackOfficeWorkspaceService workspaceService) {
        this.orderService = orderService;
        this.userService = userService;
        this.userActivityService = userActivityService;
        this.workspaceService = workspaceService;
    }

    // 订单列表页面
    @GetMapping
    public String listOrders(Model model, Authentication authentication) {
        List<Order> orders = orderService.getAllOrders();
        String username = authentication.getName();
        User user = userService.getCurrentUser(username);
        model.addAttribute("username", username);
        model.addAttribute("userBalance", user.getBalance());
        model.addAttribute("orders", orders);
        workspaceService.addWorkspaceAttributes(model, authentication);
        return workspaceService.resolveView(authentication, "order-list");
    }

    // 订单详情页面
    @GetMapping("/{id}")
    public String orderDetail(@PathVariable Long id, Model model, Authentication authentication) {
        Order order = orderService.getOrderById(id);
        String username = authentication.getName();
        User user = userService.getCurrentUser(username);
        model.addAttribute("username", username);
        model.addAttribute("userBalance", user.getBalance());
        model.addAttribute("order", order);
        workspaceService.addWorkspaceAttributes(model, authentication);
        return workspaceService.resolveView(authentication, "order-detail");
    }

    // 更新订单状态
    @PostMapping("/{id}/status")
    public String updateOrderStatus(@PathVariable Long id,
                                    @RequestParam String status,
                                    Authentication authentication,
                                    HttpServletRequest request) {
        orderService.updateOrderStatus(id, status);
        User user = userService.getCurrentUser(authentication.getName());
        userActivityService.recordAdminOperation(user, "更新订单状态: #" + id + " -> " + status, RequestUtils.getClientIp(request));
        return "redirect:" + workspaceService.ordersPath(authentication) + "/" + id;
    }
}
