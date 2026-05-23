package com.example.myecommerce.controller;

import com.example.myecommerce.entity.User;
import com.example.myecommerce.service.UserActivityService;
import com.example.myecommerce.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserActivityController {

    private final UserService userService;
    private final UserActivityService userActivityService;

    public UserActivityController(UserService userService, UserActivityService userActivityService) {
        this.userService = userService;
        this.userActivityService = userActivityService;
    }

    @PostMapping("/activities/product-browse-duration")
    public ResponseEntity<Void> recordProductBrowseDuration(@RequestParam(required = false) Long activityId,
                                                            @RequestParam int durationSeconds,
                                                            Authentication authentication) {
        User user = userService.getCurrentUser(authentication.getName());
        userActivityService.recordProductBrowseDuration(user, activityId, durationSeconds);
        return ResponseEntity.noContent().build();
    }
}
