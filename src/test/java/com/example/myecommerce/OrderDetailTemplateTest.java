package com.example.myecommerce;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class OrderDetailTemplateTest {

    @Test
    void orderDetailShowsWarningFlashMessages() throws IOException {
        String html = Files.readString(Path.of("src/main/resources/templates/order-detail.html"));

        assertThat(html).contains("th:if=\"${warning}\"");
        assertThat(html).contains("th:text=\"${warning}\"");
        assertThat(html).contains("alert-warning");
    }
}
