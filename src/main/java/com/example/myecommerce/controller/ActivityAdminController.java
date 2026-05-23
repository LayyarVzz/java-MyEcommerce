package com.example.myecommerce.controller;

import com.example.myecommerce.entity.User;
import com.example.myecommerce.entity.UserActivity;
import com.example.myecommerce.service.BackOfficeWorkspaceService;
import com.example.myecommerce.service.UserActivityService;
import com.example.myecommerce.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping({"/admin/activities", "/sales/activities"})
@PreAuthorize("hasAnyRole('ADMIN', 'SALES')")
public class ActivityAdminController {
    private final UserActivityService userActivityService;
    private final UserService userService;
    private final BackOfficeWorkspaceService workspaceService;

    public ActivityAdminController(UserActivityService userActivityService,
                                   UserService userService,
                                   BackOfficeWorkspaceService workspaceService) {
        this.userActivityService = userActivityService;
        this.userService = userService;
        this.workspaceService = workspaceService;
    }

    @GetMapping
    public String activityLog(Model model, Authentication authentication) {
        String username = authentication.getName();
        User currentUser = userService.getCurrentUser(username);
        List<UserActivity> activities = userActivityService.getAllActivities();

        model.addAttribute("username", username);
        model.addAttribute("userBalance", currentUser.getBalance());
        model.addAttribute("activities", activities);
        workspaceService.addWorkspaceAttributes(model, authentication);
        return workspaceService.resolveView(authentication, "activity-log");
    }
}
