package com.example.myecommerce.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class BackOfficeWorkspaceService {

    public String resolveView(Authentication authentication, String pageName) {
        return (isSales(authentication) ? "sales/" : "admin/") + pageName;
    }

    public String productsPath(Authentication authentication) {
        return isSales(authentication) ? "/sales/products" : null;
    }

    public String ordersPath(Authentication authentication) {
        return isSales(authentication) ? "/sales/orders" : null;
    }

    public String reportsPath(Authentication authentication) {
        return isSales(authentication) ? "/sales/dashboard" : "/admin/reports";
    }

    public String activitiesPath(Authentication authentication) {
        return isSales(authentication) ? "/sales/activities" : "/admin/activities";
    }

    public void addWorkspaceAttributes(Model model, Authentication authentication) {
        model.addAttribute("productsPath", productsPath(authentication));
        model.addAttribute("ordersPath", ordersPath(authentication));
        model.addAttribute("reportsPath", reportsPath(authentication));
        model.addAttribute("activitiesPath", activitiesPath(authentication));
        model.addAttribute("workspaceType", isSales(authentication) ? "sales" : "admin");
    }

    private boolean isSales(Authentication authentication) {
        return authentication != null
                && authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_SALES"::equals);
    }
}
