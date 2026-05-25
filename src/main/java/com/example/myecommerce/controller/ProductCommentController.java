package com.example.myecommerce.controller;

import com.example.myecommerce.entity.Product;
import com.example.myecommerce.entity.ProductComment;
import com.example.myecommerce.entity.User;
import com.example.myecommerce.service.ProductCommentService;
import com.example.myecommerce.service.ProductService;
import com.example.myecommerce.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class ProductCommentController {
    private final ProductService productService;
    private final ProductCommentService productCommentService;
    private final UserService userService;

    public ProductCommentController(ProductService productService,
                                    ProductCommentService productCommentService,
                                    UserService userService) {
        this.productService = productService;
        this.productCommentService = productCommentService;
        this.userService = userService;
    }

    @GetMapping("/products/{productId}/comments")
    public String productComments(@PathVariable Long productId,
                                  Model model,
                                  Authentication authentication) {
        Product product = productService.getProductById(productId);
        User currentUser = currentUser(authentication);
        List<ProductComment> comments = productCommentService.getAllComments(product);

        model.addAttribute("product", product);
        model.addAttribute("comments", comments);
        model.addAttribute("commentCount", productCommentService.countByProduct(product));
        model.addAttribute("likedCommentIds", productCommentService.findLikedCommentIds(currentUser, comments));
        model.addAttribute("username", currentUser != null ? currentUser.getUsername() : "游客");
        model.addAttribute("userBalance", currentUser != null ? currentUser.getBalance() : BigDecimal.ZERO);
        model.addAttribute("loggedIn", currentUser != null);
        return "product-comments";
    }

    @PostMapping("/products/{productId}/comments")
    @PreAuthorize("hasRole('USER')")
    public String addComment(@PathVariable Long productId,
                             @RequestParam String content,
                             @RequestParam(defaultValue = "detail") String returnTo,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        try {
            productCommentService.addComment(productId, authentication.getName(), content);
            redirectAttributes.addFlashAttribute("commentSuccess", "评论已发布，感谢你的真实反馈。");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("commentError", ex.getMessage());
            redirectAttributes.addFlashAttribute("commentDraft", content);
        }
        if ("all".equals(returnTo)) {
            return "redirect:/products/" + productId + "/comments#comments";
        }
        return "redirect:/products/" + productId + "#comments";
    }

    @PostMapping("/products/{productId}/comments/{commentId}/like")
    @PreAuthorize("hasRole('USER')")
    public String likeComment(@PathVariable Long productId,
                              @PathVariable Long commentId,
                              @RequestParam(defaultValue = "detail") String returnTo,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        try {
            productCommentService.likeComment(commentId, authentication.getName());
            redirectAttributes.addFlashAttribute("commentSuccess", "已为这条评论点赞。");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("commentError", ex.getMessage());
        }
        if ("all".equals(returnTo)) {
            return "redirect:/products/" + productId + "/comments#comments";
        }
        return "redirect:/products/" + productId + "#comments";
    }

    private User currentUser(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return userService.getCurrentUser(authentication.getName());
    }
}
