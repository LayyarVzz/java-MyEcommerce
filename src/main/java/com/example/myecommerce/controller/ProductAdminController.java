package com.example.myecommerce.controller;

import com.example.myecommerce.entity.Product;
import com.example.myecommerce.entity.User;
import com.example.myecommerce.service.BackOfficeWorkspaceService;
import com.example.myecommerce.service.ProductService;
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

// ProductAdminController.java
@Controller
@RequestMapping("/sales/products")
@PreAuthorize("hasRole('SALES')")
public class ProductAdminController {
    private final ProductService productService;
    private final UserService userService;
    private final UserActivityService userActivityService;
    private final BackOfficeWorkspaceService workspaceService;

    public ProductAdminController(ProductService productService,
                                  UserService userService,
                                  UserActivityService userActivityService,
                                  BackOfficeWorkspaceService workspaceService) {
        this.productService = productService;
        this.userService = userService;
        this.userActivityService = userActivityService;
        this.workspaceService = workspaceService;
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
        model.addAttribute("categories", productService.getAvailableCategories());
        workspaceService.addWorkspaceAttributes(model, authentication);
        return workspaceService.resolveView(authentication, "product-list");
    }

    // 显示添加商品表单
    @GetMapping("/add")
    public String showAddProductForm(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getCurrentUser(username);
        model.addAttribute("product", new Product());
        model.addAttribute("username", username);
        model.addAttribute("userBalance", user.getBalance());
        model.addAttribute("categories", productService.getAvailableCategories());
        workspaceService.addWorkspaceAttributes(model, authentication);
        return workspaceService.resolveView(authentication, "product-form");
    }

    // 处理添加商品请求
    @PostMapping("/add")
    public String addProduct(@ModelAttribute Product product, Authentication authentication, HttpServletRequest request) {
        productService.saveProduct(product);
        recordOperation(authentication, "新增商品: " + product.getName(), request);
        return "redirect:" + workspaceService.productsPath(authentication);
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
        model.addAttribute("categories", productService.getAvailableCategories());
        workspaceService.addWorkspaceAttributes(model, authentication);
        return workspaceService.resolveView(authentication, "product-form");
    }

    // 处理编辑商品请求
    @PostMapping("/edit/{id}")
    public String editProduct(@PathVariable Long id, @ModelAttribute Product product, Authentication authentication, HttpServletRequest request) {
        product.setId(id); // 确保ID不变
        productService.saveProduct(product);
        recordOperation(authentication, "编辑商品: " + product.getName(), request);
        return "redirect:" + workspaceService.productsPath(authentication);
    }

    // 下架商品
    @PostMapping("/{id}")
    public String discontinueProduct(@PathVariable Long id, Authentication authentication, HttpServletRequest request) {
        Product product = productService.getProductById(id);
        productService.discontinueProduct(id);
        recordOperation(authentication, "下架商品: " + product.getName(), request);
        return "redirect:" + workspaceService.productsPath(authentication);
    }

    private void recordOperation(Authentication authentication, String description, HttpServletRequest request) {
        User user = userService.getCurrentUser(authentication.getName());
        userActivityService.recordAdminOperation(user, description, RequestUtils.getClientIp(request));
    }
}

