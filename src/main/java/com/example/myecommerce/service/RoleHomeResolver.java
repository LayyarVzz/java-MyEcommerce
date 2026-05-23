package com.example.myecommerce.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class RoleHomeResolver {

    public String resolveHomeUrl(Authentication authentication) {
        if (hasRole(authentication, "ROLE_ADMIN")) {
            return "/admin/dashboard";
        }
        if (hasRole(authentication, "ROLE_SALES")) {
            return "/sales/dashboard";
        }
        return "/products";
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication != null
                && authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role::equals);
    }
}
