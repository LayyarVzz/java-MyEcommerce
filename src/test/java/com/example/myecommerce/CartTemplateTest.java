package com.example.myecommerce;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class CartTemplateTest {

    @Test
    void cartPageProvidesQuantityUpdateControls() throws IOException {
        String html = Files.readString(Path.of("src/main/resources/templates/cart.html"));

        assertThat(html).contains("th:action=\"@{/cart/update/{id}(id=${item.id})}\"");
        assertThat(html).contains("name=\"quantity\"");
        assertThat(html).contains("class=\"quantity-input\"");
        assertThat(html).contains("aria-label=\"减少数量\"");
        assertThat(html).contains("aria-label=\"增加数量\"");
        assertThat(html).contains("th:value=\"${item.quantity}\"");
        assertThat(html).contains("input.addEventListener('change'");
        assertThat(html).contains("input.addEventListener('keydown'");
        assertThat(html).doesNotContain("quantity-save");
        assertThat(html).doesNotContain("aria-label=\"更新数量\"");
        assertThat(html).doesNotContain("bi-check2");
    }
}
