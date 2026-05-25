package com.example.myecommerce.service;

import com.example.myecommerce.entity.User;
import com.example.myecommerce.entity.UserActivity;
import com.example.myecommerce.repository.UserActivityRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AnalyticsServiceTest {

    private final UserActivityRepository userActivityRepository = mock(UserActivityRepository.class);
    private final AnalyticsService analyticsService = new AnalyticsService(userActivityRepository);

    @Test
    void userProfileWeightsPurchasesAboveCasualBrowsing() {
        User user = new User();
        user.setId(9L);

        when(userActivityRepository.findByUserIdOrderByTimestampDesc(9L)).thenReturn(List.of(
                browse("数码", 180),
                browse("数码", 90),
                browse("数码", 30),
                purchase("食品", 4200.0, 2, "203.0.113.8")
        ));

        AnalyticsService.UserProfile profile = analyticsService.buildUserProfile(user);

        assertThat(profile.getFavoriteCategory()).isEqualTo("食品");
        assertThat(profile.getBuyingPower()).isEqualTo("高购买力");
        assertThat(profile.getAverageOrderAmount()).isEqualTo(4200.0);
        assertThat(profile.getPreferenceCategories())
                .extracting(AnalyticsService.CategoryPreference::getCategory)
                .containsExactly("食品", "数码");
    }

    private UserActivity browse(String category, int durationSeconds) {
        UserActivity activity = new UserActivity();
        activity.setActivityType("BROWSE_PRODUCTS");
        activity.setProductCategory(category);
        activity.setDurationSeconds(durationSeconds);
        activity.setIpAddress("192.168.1.10");
        return activity;
    }

    private UserActivity purchase(String category, double amount, int quantity, String ipAddress) {
        UserActivity activity = new UserActivity();
        activity.setActivityType("PURCHASE_PRODUCT");
        activity.setProductCategory(category);
        activity.setAmount(amount);
        activity.setQuantity(quantity);
        activity.setIpAddress(ipAddress);
        return activity;
    }
}
