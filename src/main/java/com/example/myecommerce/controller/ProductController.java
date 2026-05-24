package com.example.myecommerce.controller;

import com.example.myecommerce.entity.Product;
import com.example.myecommerce.entity.User;
import com.example.myecommerce.entity.UserActivity;
import com.example.myecommerce.service.ProductService;
import com.example.myecommerce.service.RecommendationService;
import com.example.myecommerce.service.UserService;
import com.example.myecommerce.service.UserActivityService;
import com.example.myecommerce.util.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class ProductController {
    private final ProductService productService;
    private final UserService userService;
    private final UserActivityService userActivityService;
    private final RecommendationService recommendationService;

    public ProductController(ProductService productService,
                             UserService userService,
                             UserActivityService userActivityService,
                             RecommendationService recommendationService) {
        this.productService = productService;
        this.userService = userService;
        this.userActivityService = userActivityService;
        this.recommendationService = recommendationService;
    }

    // 商品列表页
    @GetMapping("/products")
    public String productList(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            Model model,
            Authentication authentication,
            HttpServletRequest request) {
        boolean loggedIn = authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
        boolean customer = loggedIn && hasRole(authentication, "ROLE_USER");
        String username = loggedIn ? authentication.getName() : "游客";
        String normalizedSearch = search == null || search.isBlank() ? null : search.trim();
        String normalizedCategory = category == null || category.isBlank() ? null : category.trim();
        if ("未分类".equals(normalizedCategory)) {
            normalizedCategory = Product.DEFAULT_CATEGORY;
        }

        List<Product> products = productService.searchProducts(normalizedSearch, normalizedCategory);

        BigDecimal balance = BigDecimal.ZERO;
        User currentUser = null;
        if (loggedIn) {
            currentUser = userService.getCurrentUser(username);
            balance = currentUser.getBalance();
        }
        if (customer) {
            UserActivity browseActivity = userActivityService.recordProductBrowse(currentUser, normalizedSearch, normalizedCategory, RequestUtils.getClientIp(request));
            model.addAttribute("browseActivityId", browseActivity.getId());
        }
        model.addAttribute("recommendedProducts", recommendationService.recommendForContext(currentUser, normalizedSearch, normalizedCategory, 8));
        model.addAttribute("recommendationHint", getRecommendationHint(normalizedSearch, normalizedCategory));
        model.addAttribute("products", products);
        model.addAttribute("username", username);
        model.addAttribute("userBalance", balance);
        model.addAttribute("loggedIn", loggedIn);
        model.addAttribute("trackBrowseDuration", customer);
        model.addAttribute("searchKeyword", normalizedSearch);
        model.addAttribute("selectedCategory", normalizedCategory);
        model.addAttribute("categories", productService.getAvailableCategories());
        return "products";
    }

    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Long id,
                                Model model,
                                Authentication authentication) {
        Product product = productService.getProductById(id);
        boolean loggedIn = authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
        String username = loggedIn ? authentication.getName() : "游客";

        BigDecimal balance = BigDecimal.ZERO;
        User currentUser = null;
        if (loggedIn) {
            currentUser = userService.getCurrentUser(username);
            balance = currentUser.getBalance();
        }

        model.addAttribute("product", product);
        model.addAttribute("relatedProducts", recommendationService.recommendForContext(currentUser, null, product.getCategory(), 4));
        model.addAttribute("username", username);
        model.addAttribute("userBalance", balance);
        model.addAttribute("loggedIn", loggedIn);
        return "product-detail";
    }

    private String getRecommendationHint(String search, String category) {
        if (category != null && !category.isBlank()) {
            return category + " 分类下的精选商品";
        }
        if (search != null && !search.isBlank()) {
            return "与 \"" + search + "\" 相关的精选商品";
        }
        return "为你挑选的高关注商品，左右滑动查看更多";
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role::equals);
    }
}
