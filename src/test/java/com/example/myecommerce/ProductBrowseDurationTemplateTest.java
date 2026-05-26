package com.example.myecommerce;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class ProductBrowseDurationTemplateTest {

    @Test
    void productsPageReportsBrowseDurationOnPageExit() throws IOException {
        String html = Files.readString(Path.of("src/main/resources/templates/products.html"));

        assertThat(html).contains("th:data-track-duration=\"${trackBrowseDuration}\"");
        assertThat(html).contains("data-duration-endpoint");
        assertThat(html).contains("/activities/product-browse-duration");
        assertThat(html).contains("navigator.sendBeacon");
        assertThat(html).contains("visibilitychange");
        assertThat(html).contains("pagehide");
    }

    @Test
    void productsPageOnlyShowsPurchaseControlsToCustomers() throws IOException {
        String html = Files.readString(Path.of("src/main/resources/templates/products.html"));

        assertThat(html).contains("hasRole(''USER'')");
        assertThat(html).doesNotContain("isAuthenticated()");
    }

    @Test
    void productsHeroUsesCommerceServicePromisesInsteadOfOperationalStats() throws IOException {
        String html = Files.readString(Path.of("src/main/resources/templates/products.html"));

        assertThat(html).contains("hero-service-panel");
        assertThat(html).contains("正品保障");
        assertThat(html).contains("快速发货");
        assertThat(html).doesNotContain("hero-stats");
        assertThat(html).doesNotContain("当前商品");
        assertThat(html).doesNotContain("当前身份");
    }
}
