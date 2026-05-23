package com.example.myecommerce.controller;

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
        when(productService.searchProducts(null, null)).thenReturn(List.of());
        when(productService.getAvailableCategories()).thenReturn(List.of());
    }

    @Test
    void customerBrowsingProductListRecordsBrowseDurationSeed() {
        User customer = user(11L, "customer");
        UserActivity browseActivity = new UserActivity();
        browseActivity.setId(99L);

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
        when(userService.getCurrentUser("sales")).thenReturn(sales);
        when(recommendationService.recommendForContext(sales, null, null, 8)).thenReturn(List.of());

        Model model = new ExtendedModelMap();
        String view = controller.productList(null, null, model, authentication("sales", "ROLE_SALES"), request);

        assertThat(view).isEqualTo("products");
        assertThat(model.asMap()).containsEntry("trackBrowseDuration", false);
        assertThat(model.asMap()).doesNotContainKey("browseActivityId");
        verify(userActivityService, never()).recordProductBrowse(any(), eq(null), eq(null), any());
    }

    private User user(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setBalance(BigDecimal.ZERO);
        return user;
    }

    private Authentication authentication(String username, String role) {
        return new UsernamePasswordAuthenticationToken(
                username,
                "password",
                List.of(new SimpleGrantedAuthority(role))
        );
    }
}
