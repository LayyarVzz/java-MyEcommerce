package com.example.myecommerce.service;

import com.example.myecommerce.entity.User;
import com.example.myecommerce.entity.UserActivity;
import com.example.myecommerce.repository.UserActivityRepository;
import com.example.myecommerce.repository.UserRepository;
import org.junit.jupiter.api.Test;

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
}
