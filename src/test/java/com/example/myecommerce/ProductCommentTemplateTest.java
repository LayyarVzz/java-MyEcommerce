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
        assertThat(html).contains("comment-rating-tabs");
        assertThat(html).contains("data-rating-filter-tabs");
        assertThat(html).contains("data-rating-filter-link");
        assertThat(html).contains("data-comment-form");
        assertThat(html).contains("data-comments-list");
        assertThat(html).contains("th:data-selected-rating");
        assertThat(html).contains("name=\"rating\"");
        assertThat(html).contains("th:each=\"comment : ${highlightedComments}\"");
        assertThat(html).contains("th:action=\"@{/products/{productId}/comments(productId=${product.id})}\"");
        assertThat(html).contains("name=\"content\"");
        assertThat(html).contains("th:action=\"@{/products/{productId}/comments/{commentId}/like");
        assertThat(html).contains("th:href=\"@{/products/{productId}/comments(productId=${product.id}, rating=${selectedRating})} + '#comments'\"");
        assertThat(html).contains("commentSuccess != null or commentError != null");
        assertThat(html).contains("/js/product-comments.js");
        assertThat(html).contains("查看更多");
    }

    @Test
    void allCommentsPageKeepsCommentAndLikeControls() throws IOException {
        String html = Files.readString(Path.of("src/main/resources/templates/product-comments.html"));

        assertThat(html).contains("th:each=\"comment : ${comments}\"");
        assertThat(html).contains("comment-rating-tabs");
        assertThat(html).contains("data-rating-filter-tabs");
        assertThat(html).contains("data-rating-filter-link");
        assertThat(html).contains("name=\"rating\"");
        assertThat(html).contains("data-comment-form");
        assertThat(html).contains("th:data-selected-rating");
        assertThat(html).contains("th:action=\"@{/products/{productId}/comments(productId=${product.id})}\"");
        assertThat(html).contains("th:action=\"@{/products/{productId}/comments/{commentId}/like");
        assertThat(html).contains("commentSuccess != null or commentError != null");
        assertThat(html).contains("name=\"returnTo\" value=\"all\"");
        assertThat(html).contains("/js/product-comments.js");
    }

    @Test
    void commentRatingFiltersUpdateInPlaceWithoutJumpingToTop() throws IOException {
        String script = Files.readString(Path.of("src/main/resources/static/js/product-comments.js"));

        assertThat(script).contains("data-rating-filter-link");
        assertThat(script).contains("event.preventDefault()");
        assertThat(script).contains("fetch(link.href");
        assertThat(script).contains("new DOMParser()");
        assertThat(script).contains("replaceChildren");
        assertThat(script).contains("history.replaceState");
        assertThat(script).doesNotContain("window.scrollTo");
    }
}
