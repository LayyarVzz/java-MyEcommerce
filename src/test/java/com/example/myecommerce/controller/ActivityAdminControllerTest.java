package com.example.myecommerce.controller;

import com.example.myecommerce.entity.User;
import com.example.myecommerce.entity.UserActivity;
import com.example.myecommerce.service.BackOfficeWorkspaceService;
import com.example.myecommerce.service.UserActivityService;
import com.example.myecommerce.service.UserService;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivityAdminControllerTest {

    @Mock
    private UserActivityService userActivityService;

    @Mock
    private UserService userService;

    private ActivityAdminController controller;

    @BeforeEach
    void setUp() {
        controller = new ActivityAdminController(
                userActivityService,
                userService,
                new BackOfficeWorkspaceService()
        );
    }

    @Test
    void salesActivityLogShowsOnlyCustomerActivities() {
        User sales = user("salesuser", "SALES");
        UserActivity customerActivity = activity(user("customer", "USER"));

        when(userService.getCurrentUser("salesuser")).thenReturn(sales);
        when(userActivityService.getCustomerActivities()).thenReturn(List.of(customerActivity));

        Model model = new ExtendedModelMap();
        String view = controller.activityLog(model, authentication("salesuser", "ROLE_SALES"));

        assertThat(view).isEqualTo("sales/activity-log");
        assertThat(model.asMap()).containsEntry("activities", List.of(customerActivity));
        verify(userActivityService).getCustomerActivities();
        verify(userActivityService, never()).getAllActivities();
    }

    @Test
    void adminActivityLogStillShowsAllActivities() {
        User admin = user("admin", "ADMIN");
        UserActivity adminActivity = activity(admin);

        when(userService.getCurrentUser("admin")).thenReturn(admin);
        when(userActivityService.getAllActivities()).thenReturn(List.of(adminActivity));

        Model model = new ExtendedModelMap();
        String view = controller.activityLog(model, authentication("admin", "ROLE_ADMIN"));

        assertThat(view).isEqualTo("admin/activity-log");
        assertThat(model.asMap()).containsEntry("activities", List.of(adminActivity));
        verify(userActivityService).getAllActivities();
        verify(userActivityService, never()).getCustomerActivities();
    }

    private User user(String username, String role) {
        User user = new User();
        user.setUsername(username);
        user.setRole(role);
        user.setBalance(BigDecimal.ZERO);
        return user;
    }

    private UserActivity activity(User user) {
        UserActivity activity = new UserActivity();
        activity.setUser(user);
        activity.setActivityType("LOGIN");
        return activity;
    }

    private Authentication authentication(String username, String role) {
        return new UsernamePasswordAuthenticationToken(
                username,
                "password",
                List.of(new SimpleGrantedAuthority(role))
        );
    }
}
