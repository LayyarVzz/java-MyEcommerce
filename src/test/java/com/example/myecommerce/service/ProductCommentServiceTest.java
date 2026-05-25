package com.example.myecommerce.service;

import com.example.myecommerce.entity.Product;
import com.example.myecommerce.entity.ProductComment;
import com.example.myecommerce.entity.ProductCommentLike;
import com.example.myecommerce.entity.User;
import com.example.myecommerce.repository.ProductCommentLikeRepository;
import com.example.myecommerce.repository.ProductCommentRepository;
import com.example.myecommerce.repository.ProductRepository;
import com.example.myecommerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductCommentServiceTest {

    @Mock
    private ProductCommentRepository commentRepository;

    @Mock
    private ProductCommentLikeRepository likeRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    private ProductCommentService service;

    @BeforeEach
    void setUp() {
        service = new ProductCommentService(commentRepository, likeRepository, productRepository, userRepository);
    }

    @Test
    void highlightedCommentsUseLikeCountThenRecentOrderWithLimit() {
        Product product = product(7L);
        ProductComment comment = comment(1L, product, user(2L, "buyer"), 12);
        PageRequest page = PageRequest.of(0, 3, Sort.by(
                Sort.Order.desc("likeCount"),
                Sort.Order.desc("createdAt")
        ));

        when(commentRepository.findByProduct(product, page)).thenReturn(List.of(comment));

        List<ProductComment> comments = service.getHighlightedComments(product, 3);

        assertThat(comments).containsExactly(comment);
    }

    @Test
    void addCommentTrimsContentAndAssociatesProductAndUser() {
        Product product = product(7L);
        User user = user(2L, "buyer");
        ArgumentCaptor<ProductComment> captor = ArgumentCaptor.forClass(ProductComment.class);

        when(productRepository.findById(7L)).thenReturn(Optional.of(product));
        when(userRepository.findByUsername("buyer")).thenReturn(Optional.of(user));

        service.addComment(7L, "buyer", "  包装很用心，实物质感也不错。  ");

        verify(commentRepository).save(captor.capture());
        ProductComment saved = captor.getValue();
        assertThat(saved.getProduct()).isEqualTo(product);
        assertThat(saved.getUser()).isEqualTo(user);
        assertThat(saved.getContent()).isEqualTo("包装很用心，实物质感也不错。");
        assertThat(saved.getLikeCount()).isZero();
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void findCommentForProductReturnsOnlyMatchingProductComment() {
        Product product = product(7L);
        ProductComment comment = comment(11L, product, user(2L, "buyer"), 0);

        when(commentRepository.findById(11L)).thenReturn(Optional.of(comment));

        Optional<ProductComment> result = service.findCommentForProduct(11L, product);

        assertThat(result).contains(comment);
    }

    @Test
    void findCommentForProductRejectsCommentFromAnotherProduct() {
        Product product = product(7L);
        Product otherProduct = product(8L);
        ProductComment comment = comment(11L, otherProduct, user(2L, "buyer"), 0);

        when(commentRepository.findById(11L)).thenReturn(Optional.of(comment));

        Optional<ProductComment> result = service.findCommentForProduct(11L, product);

        assertThat(result).isEmpty();
    }

    @Test
    void likeCommentIncrementsOnlyOnceForAnotherUsersComment() {
        Product product = product(7L);
        User author = user(2L, "author");
        User liker = user(3L, "buyer");
        ProductComment comment = comment(11L, product, author, 4);

        when(commentRepository.findById(11L)).thenReturn(Optional.of(comment));
        when(userRepository.findByUsername("buyer")).thenReturn(Optional.of(liker));
        when(likeRepository.existsByCommentAndUser(comment, liker)).thenReturn(false);

        service.likeComment(11L, "buyer");

        verify(likeRepository).save(any(ProductCommentLike.class));
        assertThat(comment.getLikeCount()).isEqualTo(5);
        verify(commentRepository).save(comment);
    }

    @Test
    void likeCommentKeepsExistingLikeIdempotent() {
        Product product = product(7L);
        User author = user(2L, "author");
        User liker = user(3L, "buyer");
        ProductComment comment = comment(11L, product, author, 4);

        when(commentRepository.findById(11L)).thenReturn(Optional.of(comment));
        when(userRepository.findByUsername("buyer")).thenReturn(Optional.of(liker));
        when(likeRepository.existsByCommentAndUser(comment, liker)).thenReturn(true);

        service.likeComment(11L, "buyer");

        assertThat(comment.getLikeCount()).isEqualTo(4);
        verify(likeRepository, never()).save(any());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void likeCommentRejectsOwnCommentAndLeavesCountUnchanged() {
        Product product = product(7L);
        User author = user(2L, "author");
        ProductComment comment = comment(11L, product, author, 4);

        when(commentRepository.findById(11L)).thenReturn(Optional.of(comment));
        when(userRepository.findByUsername("author")).thenReturn(Optional.of(author));

        assertThatThrownBy(() -> service.likeComment(11L, "author"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("自己的评论");

        assertThat(comment.getLikeCount()).isEqualTo(4);
        verify(likeRepository, never()).save(any());
        verify(commentRepository, never()).save(any());
    }

    private Product product(Long id) {
        Product product = new Product();
        product.setId(id);
        product.setName("手工茶杯");
        return product;
    }

    private User user(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }

    private ProductComment comment(Long id, Product product, User user, int likeCount) {
        ProductComment comment = new ProductComment();
        comment.setId(id);
        comment.setProduct(product);
        comment.setUser(user);
        comment.setLikeCount(likeCount);
        comment.setContent("评论内容");
        return comment;
    }
}
