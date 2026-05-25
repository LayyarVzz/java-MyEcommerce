package com.example.myecommerce.controller;

import com.example.myecommerce.entity.ProductComment;
import com.example.myecommerce.entity.ProductCommentRating;
import com.example.myecommerce.service.ProductCommentService;
import com.example.myecommerce.service.ProductCommentService.ProductCommentStats;
import com.example.myecommerce.service.ProductService;
import com.example.myecommerce.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.List;
import java.util.Map;

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
        when(productCommentService.addComment(7L, "buyer", "很好用", ProductCommentRating.GOOD)).thenReturn(comment);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        String redirect = (String) controller.addComment(7L, "很好用", ProductCommentRating.GOOD, "detail", authentication(), request(false), redirectAttributes);

        assertThat(redirect).isEqualTo("redirect:/products/7?newCommentId=77#comments");
    }

    @Test
    void addCommentRedirectsAllCommentsWithNewCommentIdForImmediateDisplay() {
        ProductComment comment = new ProductComment();
        comment.setId(78L);
        when(productCommentService.addComment(7L, "buyer", "很好用", ProductCommentRating.NEUTRAL)).thenReturn(comment);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        String redirect = (String) controller.addComment(7L, "很好用", ProductCommentRating.NEUTRAL, "all", authentication(), request(false), redirectAttributes);

        assertThat(redirect).isEqualTo("redirect:/products/7/comments?newCommentId=78#comments");
    }

    @Test
    void ajaxAddCommentReturnsJsonWithoutPageRedirect() {
        ProductComment comment = new ProductComment();
        comment.setId(79L);
        comment.setContent("物流很快，质感不错");
        comment.setRating(ProductCommentRating.GOOD);
        comment.setLikeCount(0);
        var user = new com.example.myecommerce.entity.User();
        user.setUsername("buyer");
        user.setFullName("买家");
        comment.setUser(user);

        when(productCommentService.addComment(7L, "buyer", "物流很快，质感不错", ProductCommentRating.GOOD)).thenReturn(comment);
        when(productCommentService.getStatsByProductId(7L)).thenReturn(new ProductCommentStats(9, 7, 1, 1));

        Object result = controller.addComment(
                7L,
                "物流很快，质感不错",
                ProductCommentRating.GOOD,
                "detail",
                authentication(),
                request(true),
                new RedirectAttributesModelMap()
        );

        assertThat(result).isInstanceOf(ResponseEntity.class);
        ResponseEntity<?> response = (ResponseEntity<?>) result;
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(Map.class);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body).containsEntry("success", true);
        assertThat(body).containsEntry("message", "评论已发布，感谢你的真实反馈。");
        assertThat(body).containsKey("comment");
        assertThat(body).containsKey("stats");
    }

    private Authentication authentication() {
        return new UsernamePasswordAuthenticationToken("buyer", "password", List.of());
    }

    private MockHttpServletRequest request(boolean ajax) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        if (ajax) {
            request.addHeader("X-Requested-With", "XMLHttpRequest");
        }
        return request;
    }
}
