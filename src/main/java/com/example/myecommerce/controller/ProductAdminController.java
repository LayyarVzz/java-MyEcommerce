package com.example.myecommerce.controller;

import com.example.myecommerce.entity.Product;
import com.example.myecommerce.entity.User;
import com.example.myecommerce.service.ProductService;
import com.example.myecommerce.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ProductAdminController.java
@Controller
@RequestMapping("/admin/products")
public class ProductAdminController {
    private final ProductService productService;
    private final UserService userService;

    public ProductAdminController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    // 显示商品管理页面
    @GetMapping
    public String listProducts(Model model, Authentication authentication) {
        List<Product> products = productService.getAllProducts();
        String username = authentication.getName();
        User user = userService.getCurrentUser(username);
        model.addAttribute("products", products);
        model.addAttribute("username", username);
        model.addAttribute("userBalance", user.getBalance());
        return "admin/product-list";
    }

    // 显示添加商品表单
    @GetMapping("/add")
    public String showAddProductForm(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getCurrentUser(username);
        model.addAttribute("product", new Product());
        model.addAttribute("username", username);
        model.addAttribute("userBalance", user.getBalance());
        return "admin/product-form";
    }

    // 处理添加商品请求
    @PostMapping("/add")
    public String addProduct(@ModelAttribute Product product) {
        productService.saveProduct(product);
        return "redirect:/admin/products";
    }

    // 显示编辑商品表单
    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model, Authentication authentication) {
        Product product = productService.getProductById(id);
        String username = authentication.getName();
        User user = userService.getCurrentUser(username);
        model.addAttribute("product", product);
        model.addAttribute("username", username);
        model.addAttribute("userBalance", user.getBalance());
        return "admin/product-form";
    }

    // 处理编辑商品请求
    @PostMapping("/edit/{id}")
    public String editProduct(@PathVariable Long id, @ModelAttribute Product product) {
        product.setId(id); // 确保ID不变
        productService.saveProduct(product);
        return "redirect:/admin/products";
    }

    // 下架商品
    @PostMapping("/{id}")
    public String discontinueProduct(@PathVariable Long id) {
        productService.discontinueProduct(id);
        return "redirect:/admin/products";
    }
}

