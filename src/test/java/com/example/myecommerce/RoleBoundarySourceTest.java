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
}
