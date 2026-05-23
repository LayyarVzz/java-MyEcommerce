package com.example.myecommerce;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class ProductImageFallbackTemplateTest {

    private static final Pattern PRODUCT_IMAGE_TAG = Pattern.compile(
            "<img[^>]*th:src=\"\\$\\{(?:product|item\\.product)\\.imageUrl \\?: '/upload/default\\.png'}\"[^>]*>",
            Pattern.DOTALL
    );

    @Test
    void productImagesUseDefaultImageWhenRemoteImageFailsToLoad() throws IOException {
        List<Path> templates;
        try (var paths = Files.walk(Path.of("src/main/resources/templates"))) {
            templates = paths
                    .filter(path -> path.toString().endsWith(".html"))
                    .toList();
        }

        assertThat(templates).isNotEmpty();

        for (Path template : templates) {
            String html = Files.readString(template);
            Matcher matcher = PRODUCT_IMAGE_TAG.matcher(html);
            while (matcher.find()) {
                assertThat(matcher.group())
                        .as("Product image in %s should fall back after browser load errors", template)
                        .contains("onerror=\"this.onerror=null;this.src='/upload/default.png';\"");
            }
        }
    }
}
