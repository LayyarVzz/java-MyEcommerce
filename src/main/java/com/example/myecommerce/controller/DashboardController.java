package com.example.myecommerce.controller;

import com.example.myecommerce.entity.Order;
import com.example.myecommerce.entity.Product;
import com.example.myecommerce.entity.User;
import com.example.myecommerce.service.OrderService;
import com.example.myecommerce.service.ProductService;
import com.example.myecommerce.service.ReportService;
import com.example.myecommerce.service.UserActivityService;
import com.example.myecommerce.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
public class DashboardController {

    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;
    private final ReportService reportService;
    private final UserActivityService userActivityService;

    public DashboardController(UserService userService,
                               ProductService productService,
                               OrderService orderService,
                               ReportService reportService,
                               UserActivityService userActivityService) {
        this.userService = userService;
        this.productService = productService;
        this.orderService = orderService;
        this.reportService = reportService;
        this.userActivityService = userActivityService;
    }

    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard(Model model, Authentication authentication) {
        addCurrentUser(model, authentication);
        List<Product> products = productService.getAllProducts();
        List<Order> orders = orderService.getAllOrders();
        Map<String, Object> reportData = reportService.generateSalesReport(LocalDate.now().minusDays(30), LocalDate.now());

        model.addAttribute("totalProducts", products.size());
        model.addAttribute("activeProducts", products.stream().filter(product -> !Boolean.TRUE.equals(product.getDiscontinued())).count());
        model.addAttribute("totalOrders", orders.size());
        model.addAttribute("totalUsers", userService.getAllUsers(PageRequest.of(0, 1)).getTotalElements());
        model.addAttribute("totalActivities", userActivityService.getAllActivities().size());
        model.addAttribute("reportData", reportData);
        return "admin/dashboard";
    }

    @GetMapping("/sales/dashboard")
    @PreAuthorize("hasRole('SALES')")
    public String salesDashboard(Model model, Authentication authentication) {
        addCurrentUser(model, authentication);
        List<Product> products = productService.getAllProducts();
        List<Order> orders = orderService.getAllOrders();
        Map<String, Object> reportData = reportService.generateSalesReport(LocalDate.now().minusDays(30), LocalDate.now());

        model.addAttribute("totalProducts", products.size());
        model.addAttribute("lowStockProducts", products.stream()
                .filter(product -> !Boolean.TRUE.equals(product.getDiscontinued()))
                .filter(product -> product.getStock() != null && product.getStock() < 10)
                .count());
        model.addAttribute("pendingOrders", orders.stream().filter(order -> "待处理".equals(order.getStatus())).count());
        model.addAttribute("totalOrders", orders.size());
        model.addAttribute("reportData", reportData);
        return "sales/dashboard";
    }

    private void addCurrentUser(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getCurrentUser(username);
        model.addAttribute("username", username);
        model.addAttribute("userBalance", user.getBalance());
    }
}
