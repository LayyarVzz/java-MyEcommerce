package com.example.myecommerce.controller;

import com.example.myecommerce.entity.User;
import com.example.myecommerce.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {

    private final UserService userService;

    // 构造器注入
    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           Model model) {
        // 1. 简单重复校验
        if (userService.checkIfUserExists(username)) {   // 你也可以自己在 Repo 里写 exists 方法
            model.addAttribute("errorMsg", "用户名已存在");
            return "register";          // 留在注册页并回显错误
        }

        // 2. 组装实体
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);     // 明文传入，Service 里会 encode

        // 3. 保存
        userService.saveUser(user);

        // 4. 注册成功重定向到登录页（带参数可提示）
        return "redirect:/login?registered";
    }
}
