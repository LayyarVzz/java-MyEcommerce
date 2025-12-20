package com.example.myecommerce.controller;

import com.example.myecommerce.entity.Product;
import com.example.myecommerce.entity.User;
import com.example.myecommerce.service.ProductService;
import com.example.myecommerce.service.UserService;
import com.example.myecommerce.service.UserActivityService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProductController {
    private final ProductService productService;
    private final UserService userService;
    private final UserActivityService userActivityService;

    public ProductController(ProductService productService, UserService userService, UserActivityService userActivityService) {
        this.productService = productService;
        this.userService = userService;
        this.userActivityService = userActivityService;
    }

    // 商品列表页
    @GetMapping("/products")
    public String productList(
            @RequestParam(required = false) String search,
            Model model,
            Authentication authentication) {
        String username = authentication.getName();

        List<Product> products;
        if (search != null && !search.trim().isEmpty()) {
            products = productService.searchProducts(search);
        } else {
            // 仅显示未下架商品
            products = productService.getAvailableProducts();
        }
        
        System.out.println("Final products sent to view: " + products.size());

        User user = userService.getCurrentUser(username);
        model.addAttribute("products", products);
        model.addAttribute("username", username);
        model.addAttribute("userBalance", user.getBalance());
        model.addAttribute("searchKeyword", search);
        return "products";
    }
}