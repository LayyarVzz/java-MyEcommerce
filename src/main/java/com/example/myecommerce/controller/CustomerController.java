package com.example.myecommerce.controller;

import com.example.myecommerce.entity.User;
import com.example.myecommerce.entity.UserActivity;
import com.example.myecommerce.service.AnalyticsService;
import com.example.myecommerce.service.UserService;
import com.example.myecommerce.service.UserActivityService;
import com.example.myecommerce.util.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin/customers")
@PreAuthorize("hasRole('ADMIN')")
public class CustomerController {

    private final UserService userService;
    private final UserActivityService userActivityService;
    private final AnalyticsService analyticsService;

    public CustomerController(UserService userService,
                              UserActivityService userActivityService,
                              AnalyticsService analyticsService) {
        this.userService = userService;
        this.userActivityService = userActivityService;
        this.analyticsService = analyticsService;
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
        model.addAttribute("profile", analyticsService.buildUserProfile(customer));

        return "admin/customer-detail";
    }

    // 更新客户信息
    @PostMapping("/{id}/update")
    public String updateCustomer(@PathVariable Long id,
                                 @RequestParam String username,
                                 @RequestParam String role,
                                 @RequestParam Double balance,
                                 Model model,
                                 Authentication authentication,
                                 HttpServletRequest request) {
        User customer = userService.getUserById(id);
        if (customer == null) {
            return "redirect:/admin/customers";
        }

        // 验证角色是否有效
        if (!java.util.Arrays.asList("USER", "SALES", "ADMIN").contains(role)) {
            model.addAttribute("error", "无效的角色类型");
            return customerDetail(id, model, authentication); // 返回详情页面并显示错误
        }

        customer.setUsername(username);
        customer.setRole(role);
        customer.setBalance(BigDecimal.valueOf(balance));

        // 使用安全的更新方法，避免重新加密密码
        userService.saveUserWithoutEncryption(customer);
        User currentUser = userService.getCurrentUser(authentication.getName());
        userActivityService.recordAdminOperation(currentUser, "更新用户信息: " + username + "，角色: " + role, RequestUtils.getClientIp(request));

        return "redirect:/admin/customers/" + id; // 重定向回详情页面
    }

    @PostMapping("/sales")
    public String createSales(@RequestParam String username,
                              @RequestParam String password,
                              @RequestParam String email,
                              @RequestParam(required = false) String fullName,
                              RedirectAttributes redirectAttributes,
                              Authentication authentication,
                              HttpServletRequest request) {
        if (userService.checkIfUserExists(username)) {
            redirectAttributes.addFlashAttribute("error", "用户名已存在，无法创建销售人员账号");
            return "redirect:/admin/customers";
        }

        userService.createSalesUser(username, password, email, fullName);
        User currentUser = userService.getCurrentUser(authentication.getName());
        userActivityService.recordAdminOperation(currentUser, "创建销售人员账号: " + username, RequestUtils.getClientIp(request));
        redirectAttributes.addFlashAttribute("success", "销售人员账号已创建");
        return "redirect:/admin/customers";
    }

    @PostMapping("/{id}/reset-password")
    public String resetPassword(@PathVariable Long id,
                                @RequestParam String password,
                                RedirectAttributes redirectAttributes,
                                Authentication authentication,
                                HttpServletRequest request) {
        userService.resetPassword(id, password);
        User currentUser = userService.getCurrentUser(authentication.getName());
        User targetUser = userService.getUserById(id);
        String targetName = targetUser == null ? String.valueOf(id) : targetUser.getUsername();
        userActivityService.recordAdminOperation(currentUser, "重置用户密码: " + targetName, RequestUtils.getClientIp(request));
        redirectAttributes.addFlashAttribute("success", "密码已重置");
        return "redirect:/admin/customers/" + id;
    }

    @PostMapping("/{id}/delete-sales")
    public String deleteSales(@PathVariable Long id,
                              RedirectAttributes redirectAttributes,
                              Authentication authentication,
                              HttpServletRequest request) {
        User targetUser = userService.getUserById(id);
        if (targetUser == null) {
            redirectAttributes.addFlashAttribute("error", "销售人员账号不存在");
            return "redirect:/admin/customers";
        }
        if (!"SALES".equals(targetUser.getRole())) {
            redirectAttributes.addFlashAttribute("error", "只能删除销售人员账号");
            return "redirect:/admin/customers";
        }
        if (targetUser.getUsername().equals(authentication.getName())) {
            redirectAttributes.addFlashAttribute("error", "不能删除当前登录账号");
            return "redirect:/admin/customers";
        }

        try {
            userService.deleteUser(id);
            User currentUser = userService.getCurrentUser(authentication.getName());
            userActivityService.recordAdminOperation(currentUser, "删除销售人员账号: " + targetUser.getUsername(), RequestUtils.getClientIp(request));
            redirectAttributes.addFlashAttribute("success", "销售人员账号已删除");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "删除失败，该账号可能存在关联数据");
        }
        return "redirect:/admin/customers";
    }
}
