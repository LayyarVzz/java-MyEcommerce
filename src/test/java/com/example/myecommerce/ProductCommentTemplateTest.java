package com.example.myecommerce;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class ProductCommentTemplateTest {

    @Test
    void productDetailShowsHighlightedCommentsComposerAndMoreLink() throws IOException {
        String html = Files.readString(Path.of("src/main/resources/templates/product-detail.html"));

        assertThat(html).contains("id=\"comments\"");
        assertThat(html).contains("th:if=\"${newComment != null}\"");
        assertThat(html).contains("th:each=\"comment : ${highlightedComments}\"");
        assertThat(html).contains("th:action=\"@{/products/{productId}/comments(productId=${product.id})}\"");
        assertThat(html).contains("name=\"content\"");
        assertThat(html).contains("th:action=\"@{/products/{productId}/comments/{commentId}/like");
        assertThat(html).contains("th:href=\"@{/products/{productId}/comments(productId=${product.id})}\"");
        assertThat(html).contains("查看更多");
    }

    @Test
    void allCommentsPageKeepsCommentAndLikeControls() throws IOException {
        String html = Files.readString(Path.of("src/main/resources/templates/product-comments.html"));

        assertThat(html).contains("th:each=\"comment : ${comments}\"");
        assertThat(html).contains("th:action=\"@{/products/{productId}/comments(productId=${product.id})}\"");
        assertThat(html).contains("th:action=\"@{/products/{productId}/comments/{commentId}/like");
        assertThat(html).contains("name=\"returnTo\" value=\"all\"");
    }
}
