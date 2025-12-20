package com.example.myecommerce.controller;

import com.example.myecommerce.entity.User;
import com.example.myecommerce.service.ReportService;
import com.example.myecommerce.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Controller
@RequestMapping("/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {

    private final ReportService reportService;
    private final UserService userService;

    public ReportController(ReportService reportService, UserService userService) {
        this.reportService = reportService;
        this.userService = userService;
    }

    @GetMapping
    public String salesReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Model model,
            Authentication authentication) {

        LocalDate start = startDate != null ?
                LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")) :
                LocalDate.now().minusMonths(1);

        LocalDate end = endDate != null ?
                LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")) :
                LocalDate.now();

        Map<String, Object> reportData = reportService.generateSalesReport(start, end);
        String username = authentication.getName();
        User user = userService.getCurrentUser(username);

        model.addAttribute("username", username);
        model.addAttribute("userBalance", user.getBalance());
        model.addAttribute("reportData", reportData);
        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);

        return "admin/sales-report";
    }
}
