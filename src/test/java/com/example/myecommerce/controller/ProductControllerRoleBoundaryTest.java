package com.example.myecommerce.controller;

import com.example.myecommerce.entity.Product;
import com.example.myecommerce.entity.User;
import com.example.myecommerce.entity.UserActivity;
import com.example.myecommerce.service.ProductService;
import com.example.myecommerce.service.RecommendationService;
import com.example.myecommerce.service.UserActivityService;
import com.example.myecommerce.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductControllerRoleBoundaryTest {

    @Mock
    private ProductService productService;

    @Mock
    private UserService userService;

    @Mock
    private UserActivityService userActivityService;

    @Mock
    private RecommendationService recommendationService;

    @Mock
    private HttpServletRequest request;

    private ProductController controller;

    @BeforeEach
    void setUp() {
        controller = new ProductController(productService, userService, userActivityService, recommendationService);
    }

    @Test
    void customerBrowsingProductListRecordsBrowseDurationSeed() {
        User customer = user(11L, "customer");
        UserActivity browseActivity = new UserActivity();
        browseActivity.setId(99L);

        when(productService.searchProducts(null, null)).thenReturn(List.of());
        when(productService.getAvailableCategories()).thenReturn(List.of());
        when(userService.getCurrentUser("customer")).thenReturn(customer);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(userActivityService.recordProductBrowse(customer, null, null, "127.0.0.1")).thenReturn(browseActivity);
        when(recommendationService.recommendForContext(customer, null, null, 8)).thenReturn(List.of());

        Model model = new ExtendedModelMap();
        String view = controller.productList(null, null, model, authentication("customer", "ROLE_USER"), request);

        assertThat(view).isEqualTo("products");
        assertThat(model.asMap()).containsEntry("trackBrowseDuration", true);
        assertThat(model.asMap()).containsEntry("browseActivityId", 99L);
    }

    @Test
    void salesBrowsingProductListDoesNotCreateCustomerBrowseRecord() {
        User sales = user(12L, "sales");
        when(productService.searchProducts(null, null)).thenReturn(List.of());
        when(productService.getAvailableCategories()).thenReturn(List.of());
        when(userService.getCurrentUser("sales")).thenReturn(sales);
        when(recommendationService.recommendForContext(sales, null, null, 8)).thenReturn(List.of());

        Model model = new ExtendedModelMap();
        String view = controller.productList(null, null, model, authentication("sales", "ROLE_SALES"), request);

        assertThat(view).isEqualTo("products");
        assertThat(model.asMap()).containsEntry("trackBrowseDuration", false);
        assertThat(model.asMap()).doesNotContainKey("browseActivityId");
        verify(userActivityService, never()).recordProductBrowse(any(), eq(null), eq(null), any());
    }

    @Test
    void productDetailShowsReusableProductTemplateData() {
        User customer = user(21L, "customer");
        Product product = product(7L, "台灯", "家居");
        Product recommended = product(8L, "香薰", "家居");

        when(productService.getProductById(7L)).thenReturn(product);
        when(userService.getCurrentUser("customer")).thenReturn(customer);
        when(recommendationService.recommendForContext(customer, null, "家居", 4)).thenReturn(List.of(recommended));

        Model model = new ExtendedModelMap();
        String view = controller.productDetail(7L, model, authentication("customer", "ROLE_USER"));

        assertThat(view).isEqualTo("product-detail");
        assertThat(model.asMap()).containsEntry("product", product);
        assertThat(model.asMap()).containsEntry("relatedProducts", List.of(recommended));
        assertThat(model.asMap()).containsEntry("loggedIn", true);
        assertThat(model.asMap()).containsEntry("username", "customer");
        assertThat(model.asMap()).containsEntry("userBalance", BigDecimal.ZERO);
    }

    private User user(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setBalance(BigDecimal.ZERO);
        return user;
    }

    private Product product(Long id, String name, String category) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setCategory(category);
        product.setDescription(name + "描述");
        product.setPrice(BigDecimal.TEN);
        product.setStock(10);
        product.setImageUrl("/upload/default.png");
        return product;
    }

    private Authentication authentication(String username, String role) {
        return new UsernamePasswordAuthenticationToken(
                username,
                "password",
                List.of(new SimpleGrantedAuthority(role))
        );
    }
}
