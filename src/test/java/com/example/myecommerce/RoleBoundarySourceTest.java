package com.example.myecommerce;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class RoleBoundarySourceTest {

    @Test
    void customerShoppingRoutesAreLimitedToCustomers() throws IOException {
        String securityConfig = Files.readString(Path.of("src/main/java/com/example/myecommerce/config/SecurityConfig.java"));

        assertThat(securityConfig).contains(".requestMatchers(\"/\", \"/products\", \"/products/**\", \"/register\", \"/login\", \"/css/**\", \"/js/**\", \"/upload/**\").permitAll()");
        assertThat(securityConfig).contains(".requestMatchers(\"/orders/**\").hasRole(\"USER\")");
        assertThat(securityConfig).contains(".requestMatchers(\"/addresses/**\").hasRole(\"USER\")");
        assertThat(securityConfig).contains(".requestMatchers(\"/cart/**\").hasRole(\"USER\")");
        assertThat(securityConfig).contains(".requestMatchers(\"/activities/product-browse-duration\").hasRole(\"USER\")");
        assertThat(securityConfig).doesNotContain(".requestMatchers(\"/orders/**\").authenticated()");
        assertThat(securityConfig).doesNotContain(".requestMatchers(\"/addresses/**\").authenticated()");
        assertThat(securityConfig).doesNotContain(".requestMatchers(\"/cart/**\").authenticated()");
    }

    @Test
    void salesRoleCannotReachAdminSalesReports() throws IOException {
        String reportController = Files.readString(Path.of("src/main/java/com/example/myecommerce/controller/ReportController.java"));
        String salesShell = Files.readString(Path.of("src/main/resources/templates/fragments/sales-shell.html"));
        String salesDashboard = Files.readString(Path.of("src/main/resources/templates/sales/dashboard.html"));

        assertThat(reportController).contains("@RequestMapping(\"/admin/reports\")");
        assertThat(reportController).contains("@PreAuthorize(\"hasRole('ADMIN')\")");
        assertThat(reportController).doesNotContain("/sales/reports");
        assertThat(salesShell).doesNotContain("/sales/reports");
        assertThat(salesDashboard).doesNotContain("/sales/reports");
    }

    @Test
    void adminRoleCannotManageSalesOperationalWork() throws IOException {
        String securityConfig = Files.readString(Path.of("src/main/java/com/example/myecommerce/config/SecurityConfig.java"));
        String productController = Files.readString(Path.of("src/main/java/com/example/myecommerce/controller/ProductAdminController.java"));
        String orderController = Files.readString(Path.of("src/main/java/com/example/myecommerce/controller/OrderAdminController.java"));
        String adminShell = Files.readString(Path.of("src/main/resources/templates/fragments/admin-shell.html"));
        String adminDashboard = Files.readString(Path.of("src/main/resources/templates/admin/dashboard.html"));

        assertThat(securityConfig).contains(".requestMatchers(\"/admin/products\", \"/admin/products/**\", \"/admin/orders\", \"/admin/orders/**\").denyAll()");
        assertThat(securityConfig).doesNotContain(".requestMatchers(\"/admin/products/**\", \"/admin/orders/**\", \"/admin/reports/**\", \"/admin/activities/**\").hasRole(\"ADMIN\")");

        assertThat(productController).contains("@RequestMapping(\"/sales/products\")");
        assertThat(productController).contains("@PreAuthorize(\"hasRole('SALES')\")");
        assertThat(productController).doesNotContain("/admin/products");
        assertThat(productController).doesNotContain("hasAnyRole('ADMIN', 'SALES')");

        assertThat(orderController).contains("@RequestMapping(\"/sales/orders\")");
        assertThat(orderController).contains("@PreAuthorize(\"hasRole('SALES')\")");
        assertThat(orderController).doesNotContain("/admin/orders");
        assertThat(orderController).doesNotContain("hasAnyRole('ADMIN', 'SALES')");

        assertThat(adminShell).doesNotContain("/admin/products");
        assertThat(adminShell).doesNotContain("/admin/orders");
        assertThat(adminDashboard).doesNotContain("/admin/products");
        assertThat(adminDashboard).doesNotContain("/admin/orders");

        assertThat(Path.of("src/main/resources/templates/admin/product-list.html")).doesNotExist();
        assertThat(Path.of("src/main/resources/templates/admin/product-form.html")).doesNotExist();
        assertThat(Path.of("src/main/resources/templates/admin/order-list.html")).doesNotExist();
        assertThat(Path.of("src/main/resources/templates/admin/order-detail.html")).doesNotExist();
    }
}
