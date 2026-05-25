package com.example.myecommerce.service;

import com.example.myecommerce.entity.Product;
import com.example.myecommerce.entity.ProductComment;
import com.example.myecommerce.entity.ProductCommentLike;
import com.example.myecommerce.entity.User;
import com.example.myecommerce.repository.ProductCommentLikeRepository;
import com.example.myecommerce.repository.ProductCommentRepository;
import com.example.myecommerce.repository.ProductRepository;
import com.example.myecommerce.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductCommentService {
    private static final int MAX_CONTENT_LENGTH = 500;

    private final ProductCommentRepository commentRepository;
    private final ProductCommentLikeRepository likeRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductCommentService(ProductCommentRepository commentRepository,
                                 ProductCommentLikeRepository likeRepository,
                                 ProductRepository productRepository,
                                 UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductComment> getHighlightedComments(Product product, int limit) {
        int safeLimit = Math.max(1, limit);
        PageRequest page = PageRequest.of(0, safeLimit, commentSort());
        return commentRepository.findByProduct(product, page);
    }

    @Transactional(readOnly = true)
    public List<ProductComment> getAllComments(Product product) {
        return commentRepository.findByProductOrderByLikeCountDescCreatedAtDesc(product);
    }

    @Transactional(readOnly = true)
    public long countByProduct(Product product) {
        return commentRepository.countByProduct(product);
    }

    @Transactional
    public ProductComment addComment(Long productId, String username, String content) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("商品不存在"));
        User user = findUser(username);
        String normalizedContent = normalizeContent(content);

        ProductComment comment = new ProductComment();
        comment.setProduct(product);
        comment.setUser(user);
        comment.setContent(normalizedContent);
        comment.setLikeCount(0);
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    @Transactional
    public ProductComment likeComment(Long commentId, String username) {
        ProductComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("评论不存在"));
        User user = findUser(username);
        if (isSameUser(comment.getUser(), user)) {
            throw new IllegalArgumentException("不能给自己的评论点赞");
        }
        if (likeRepository.existsByCommentAndUser(comment, user)) {
            return comment;
        }

        ProductCommentLike like = new ProductCommentLike();
        like.setComment(comment);
        like.setUser(user);
        likeRepository.save(like);

        comment.setLikeCount(comment.getLikeCount() + 1);
        return commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<Long> findLikedCommentIds(User user, List<ProductComment> comments) {
        if (user == null || comments == null || comments.isEmpty()) {
            return List.of();
        }
        return likeRepository.findByUserAndCommentIn(user, comments).stream()
                .map(ProductCommentLike::getComment)
                .map(ProductComment::getId)
                .toList();
    }

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
    }

    private String normalizeContent(String content) {
        String normalized = content == null ? "" : content.trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("评论内容不能为空");
        }
        if (normalized.length() > MAX_CONTENT_LENGTH) {
            throw new IllegalArgumentException("评论内容不能超过500字");
        }
        return normalized;
    }

    private boolean isSameUser(User left, User right) {
        if (left == null || right == null) {
            return false;
        }
        if (left.getId() != null && right.getId() != null) {
            return left.getId().equals(right.getId());
        }
        return left.getUsername() != null && left.getUsername().equals(right.getUsername());
    }

    private Sort commentSort() {
        return Sort.by(
                Sort.Order.desc("likeCount"),
                Sort.Order.desc("createdAt")
        );
    }
}
