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
}
