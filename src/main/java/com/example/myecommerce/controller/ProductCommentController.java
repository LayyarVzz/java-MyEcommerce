package com.example.myecommerce.controller;

import com.example.myecommerce.entity.Product;
import com.example.myecommerce.entity.ProductComment;
import com.example.myecommerce.entity.ProductCommentRating;
import com.example.myecommerce.entity.User;
import com.example.myecommerce.service.ProductCommentService;
import com.example.myecommerce.service.ProductService;
import com.example.myecommerce.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ProductCommentController {
    private static final DateTimeFormatter COMMENT_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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
                                  @RequestParam(required = false) Long newCommentId,
                                  @RequestParam(required = false) ProductCommentRating rating,
                                  Model model,
                                  Authentication authentication) {
        Product product = productService.getProductById(productId);
        User currentUser = currentUser(authentication);
        List<ProductComment> comments = productCommentService.getAllComments(product, rating);
        ProductComment newComment = newCommentId == null
                ? null
                : productCommentService.findCommentForProduct(newCommentId, product).orElse(null);
        List<ProductComment> likeScopeComments = new ArrayList<>(comments);
        if (newComment != null && comments.stream().noneMatch(comment -> comment.getId().equals(newComment.getId()))) {
            likeScopeComments.add(newComment);
        }
        ProductCommentService.ProductCommentStats commentStats = productCommentService.getStats(product);

        model.addAttribute("product", product);
        model.addAttribute("comments", comments);
        model.addAttribute("newComment", newComment);
        model.addAttribute("commentCount", commentStats.total());
        model.addAttribute("commentStats", commentStats);
        model.addAttribute("ratingOptions", ProductCommentRating.values());
        model.addAttribute("selectedRating", rating);
        model.addAttribute("likedCommentIds", productCommentService.findLikedCommentIds(currentUser, likeScopeComments));
        model.addAttribute("username", currentUser != null ? currentUser.getUsername() : "游客");
        model.addAttribute("userBalance", currentUser != null ? currentUser.getBalance() : BigDecimal.ZERO);
        model.addAttribute("loggedIn", currentUser != null);
        return "product-comments";
    }

    @PostMapping("/products/{productId}/comments")
    @PreAuthorize("hasRole('USER')")
    public Object addComment(@PathVariable Long productId,
                             @RequestParam String content,
                             @RequestParam(defaultValue = "GOOD") ProductCommentRating rating,
                             @RequestParam(defaultValue = "detail") String returnTo,
                             Authentication authentication,
                             HttpServletRequest request,
                             RedirectAttributes redirectAttributes) {
        try {
            ProductComment comment = productCommentService.addComment(productId, authentication.getName(), content, rating);
            String message = "评论已发布，感谢你的真实反馈。";
            if (isAjax(request)) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", message,
                        "comment", toCommentPayload(comment),
                        "stats", toStatsPayload(productCommentService.getStatsByProductId(productId))
                ));
            }
            redirectAttributes.addFlashAttribute("commentSuccess", message);
            if ("all".equals(returnTo)) {
                return "redirect:/products/" + productId + "/comments?newCommentId=" + comment.getId() + "#comments";
            }
            return "redirect:/products/" + productId + "?newCommentId=" + comment.getId() + "#comments";
        } catch (IllegalArgumentException ex) {
            if (isAjax(request)) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", ex.getMessage()
                ));
            }
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

    private boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    private Map<String, Object> toCommentPayload(ProductComment comment) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("id", comment.getId());
        payload.put("content", comment.getContent());
        payload.put("likeCount", comment.getLikeCount());
        payload.put("rating", comment.getRating().name());
        payload.put("ratingLabel", comment.getRating().getLabel());
        payload.put("authorName", comment.getUser().getFullName() != null && !comment.getUser().getFullName().isBlank()
                ? comment.getUser().getFullName()
                : comment.getUser().getUsername());
        payload.put("authorInitial", comment.getUser().getUsername().substring(0, 1));
        payload.put("createdAt", comment.getCreatedAt() != null ? COMMENT_TIME_FORMATTER.format(comment.getCreatedAt()) : "");
        return payload;
    }

    private Map<String, Object> toStatsPayload(ProductCommentService.ProductCommentStats stats) {
        return Map.of(
                "total", stats.total(),
                "good", stats.good(),
                "neutral", stats.neutral(),
                "bad", stats.bad()
        );
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
