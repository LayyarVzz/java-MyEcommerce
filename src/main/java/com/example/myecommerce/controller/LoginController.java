package com.example.myecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String showLoginForm() {
        // 对应 templates/login.html
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }
}
