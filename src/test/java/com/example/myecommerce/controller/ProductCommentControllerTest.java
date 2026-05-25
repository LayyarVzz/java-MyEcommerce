package com.example.myecommerce.controller;

import com.example.myecommerce.entity.ProductComment;
import com.example.myecommerce.service.ProductCommentService;
import com.example.myecommerce.service.ProductService;
import com.example.myecommerce.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductCommentControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private ProductCommentService productCommentService;

    @Mock
    private UserService userService;

    private ProductCommentController controller;

    @BeforeEach
    void setUp() {
        controller = new ProductCommentController(productService, productCommentService, userService);
    }

    @Test
    void addCommentRedirectsDetailWithNewCommentIdForImmediateDisplay() {
        ProductComment comment = new ProductComment();
        comment.setId(77L);
        when(productCommentService.addComment(7L, "buyer", "很好用")).thenReturn(comment);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        String redirect = controller.addComment(7L, "很好用", "detail", authentication(), redirectAttributes);

        assertThat(redirect).isEqualTo("redirect:/products/7?newCommentId=77#comments");
    }

    @Test
    void addCommentRedirectsAllCommentsWithNewCommentIdForImmediateDisplay() {
        ProductComment comment = new ProductComment();
        comment.setId(78L);
        when(productCommentService.addComment(7L, "buyer", "很好用")).thenReturn(comment);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        String redirect = controller.addComment(7L, "很好用", "all", authentication(), redirectAttributes);

        assertThat(redirect).isEqualTo("redirect:/products/7/comments?newCommentId=78#comments");
    }

    private Authentication authentication() {
        return new UsernamePasswordAuthenticationToken("buyer", "password", List.of());
    }
}
