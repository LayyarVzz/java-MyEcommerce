package com.example.myecommerce.service;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BackOfficeWorkspaceServiceTest {

    private final BackOfficeWorkspaceService service = new BackOfficeWorkspaceService();

    @Test
    void resolvesAdminViewsAndPaths() {
        var authentication = authentication("ROLE_ADMIN");

        assertThat(service.resolveView(authentication, "sales-report")).isEqualTo("admin/sales-report");
        assertThat(service.productsPath(authentication)).isNull();
        assertThat(service.ordersPath(authentication)).isNull();
        assertThat(service.reportsPath(authentication)).isEqualTo("/admin/reports");
        assertThat(service.activitiesPath(authentication)).isEqualTo("/admin/activities");
    }

    @Test
    void resolvesSalesViewsAndPaths() {
        var authentication = authentication("ROLE_SALES");

        assertThat(service.resolveView(authentication, "product-list")).isEqualTo("sales/product-list");
        assertThat(service.productsPath(authentication)).isEqualTo("/sales/products");
        assertThat(service.ordersPath(authentication)).isEqualTo("/sales/orders");
        assertThat(service.reportsPath(authentication)).isEqualTo("/sales/dashboard");
        assertThat(service.activitiesPath(authentication)).isEqualTo("/sales/activities");
    }

    private UsernamePasswordAuthenticationToken authentication(String role) {
        return new UsernamePasswordAuthenticationToken(
                "worker",
                "password",
                List.of(new SimpleGrantedAuthority(role))
        );
    }
}
