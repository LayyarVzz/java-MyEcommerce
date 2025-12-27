package com.example.myecommerce.controller;

import com.example.myecommerce.entity.User;
import com.example.myecommerce.entity.UserActivity;
import com.example.myecommerce.service.UserService;
import com.example.myecommerce.service.UserActivityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin/customers")
@PreAuthorize("hasRole('ADMIN')")
public class CustomerController {

    private final UserService userService;
    private final UserActivityService userActivityService;

    public CustomerController(UserService userService, UserActivityService userActivityService) {
        this.userService = userService;
        this.userActivityService = userActivityService;
    }

    // 客户列表页面
    @GetMapping
    public String listCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model,
            Authentication authentication) {

        Pageable pageable = PageRequest.of(page, size);
        Page<User> customersPage = userService.getAllUsers(pageable);

        String username = authentication.getName();
        User currentUser = userService.getCurrentUser(username);

        model.addAttribute("username", username);
        model.addAttribute("userBalance", currentUser.getBalance());
        model.addAttribute("customers", customersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", customersPage.getTotalPages());
        model.addAttribute("totalElements", customersPage.getTotalElements());

        return "admin/customer-list";
    }

    // 客户详情页面（包括活动日志）
    @GetMapping("/{id}")
    public String customerDetail(@PathVariable Long id, Model model, Authentication authentication) {
        User customer = userService.getUserById(id);
        if (customer == null) {
            return "redirect:/admin/customers";
        }

        List<UserActivity> activities = userActivityService.getUserActivities(id);

        String username = authentication.getName();
        User currentUser = userService.getCurrentUser(username);

        model.addAttribute("username", username);
        model.addAttribute("userBalance", currentUser.getBalance());
        model.addAttribute("customer", customer);
        model.addAttribute("activities", activities);

        return "admin/customer-detail";
    }

    // 更新客户信息
    @PostMapping("/{id}/update")
    public String updateCustomer(@PathVariable Long id,
                                 @RequestParam String username,
                                 @RequestParam String role,
                                 @RequestParam Double balance,
                                 Model model,
                                 Authentication authentication) {
        User customer = userService.getUserById(id);
        if (customer == null) {
            return "redirect:/admin/customers";
        }

        // 验证角色是否有效
        if (!java.util.Arrays.asList("USER", "ADMIN").contains(role)) {
            model.addAttribute("error", "无效的角色类型");
            return customerDetail(id, model, authentication); // 返回详情页面并显示错误
        }

        customer.setUsername(username);
        customer.setRole(role);
        customer.setBalance(BigDecimal.valueOf(balance));

        // 使用安全的更新方法，避免重新加密密码
        userService.saveUserWithoutEncryption(customer);

        return "redirect:/admin/customers/" + id; // 重定向回详情页面
    }
}