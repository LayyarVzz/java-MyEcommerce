package com.example.myecommerce.service;

import com.example.myecommerce.entity.User;
import com.example.myecommerce.entity.UserActivity;
import com.example.myecommerce.repository.UserActivityRepository;
import com.example.myecommerce.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserActivityServiceTest {

    private final UserActivityRepository userActivityRepository = mock(UserActivityRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserActivityService userActivityService = new UserActivityService(userActivityRepository, userRepository);

    @Test
    void updatesLatestProductBrowseDurationForOnlyThatUser() {
        User user = new User();
        user.setId(42L);
        UserActivity browseActivity = new UserActivity();
        browseActivity.setUser(user);
        browseActivity.setActivityType("BROWSE_PRODUCTS");

        when(userActivityRepository.findFirstByUserIdAndActivityTypeOrderByTimestampDesc(42L, "BROWSE_PRODUCTS"))
                .thenReturn(Optional.of(browseActivity));

        userActivityService.recordLatestProductBrowseDuration(user, 17);

        assertThat(browseActivity.getDurationSeconds()).isEqualTo(17);
        verify(userActivityRepository).findFirstByUserIdAndActivityTypeOrderByTimestampDesc(42L, "BROWSE_PRODUCTS");
        verify(userActivityRepository).save(browseActivity);
    }

    @Test
    void clampsInvalidProductBrowseDuration() {
        User user = new User();
        user.setId(7L);
        UserActivity browseActivity = new UserActivity();
        browseActivity.setUser(user);
        browseActivity.setActivityType("BROWSE_PRODUCTS");

        when(userActivityRepository.findFirstByUserIdAndActivityTypeOrderByTimestampDesc(7L, "BROWSE_PRODUCTS"))
                .thenReturn(Optional.of(browseActivity));

        userActivityService.recordLatestProductBrowseDuration(user, 0);

        assertThat(browseActivity.getDurationSeconds()).isEqualTo(1);
    }

    @Test
    void getsOnlyCustomerActivitiesForSalesWorkspace() {
        User customer = user(1L, "customer", "USER");
        UserActivity customerActivity = activity(customer, "PURCHASE_PRODUCT");

        when(userActivityRepository.findByUserRoleOrderByTimestampDesc("USER"))
                .thenReturn(List.of(customerActivity));

        List<UserActivity> activities = userActivityService.getCustomerActivities();

        assertThat(activities).containsExactly(customerActivity);
        verify(userActivityRepository).findByUserRoleOrderByTimestampDesc("USER");
    }

    private User user(Long id, String username, String role) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setRole(role);
        return user;
    }

    private UserActivity activity(User user, String activityType) {
        UserActivity activity = new UserActivity();
        activity.setUser(user);
        activity.setActivityType(activityType);
        return activity;
    }
}
