package com.example.myecommerce.service;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RoleHomeResolverTest {

    private final RoleHomeResolver roleHomeResolver = new RoleHomeResolver();

    @Test
    void redirectsAdminToAdminDashboard() {
        var authentication = new UsernamePasswordAuthenticationToken(
                "admin",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        assertThat(roleHomeResolver.resolveHomeUrl(authentication)).isEqualTo("/admin/dashboard");
    }

    @Test
    void redirectsSalesToSalesDashboard() {
        var authentication = new UsernamePasswordAuthenticationToken(
                "sales",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_SALES"))
        );

        assertThat(roleHomeResolver.resolveHomeUrl(authentication)).isEqualTo("/sales/dashboard");
    }

    @Test
    void redirectsCustomersToProducts() {
        var authentication = new UsernamePasswordAuthenticationToken(
                "user",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        assertThat(roleHomeResolver.resolveHomeUrl(authentication)).isEqualTo("/products");
    }
}
