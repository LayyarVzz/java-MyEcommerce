package com.example.myecommerce;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class StorefrontAccessibilityStylesTest {

    @Test
    void darkStorefrontSurfacesForceReadableLightText() throws IOException {
        String css = Files.readString(Path.of("src/main/resources/static/css/storefront.css"));

        assertThat(css).contains(".storefront-page .dark-surface");
        assertThat(css).contains(".storefront-page .account-header");
        assertThat(css).contains(".storefront-page .account-header h5");
        assertThat(css).contains(".storefront-page .storefront-hero .text-muted");
        assertThat(css).contains(".storefront-page .btn-primary *");
        assertThat(css).contains("color: var(--store-paper-strong) !important");
        assertThat(css).contains("color: inherit !important");
    }
}
