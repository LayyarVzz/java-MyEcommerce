package com.example.myecommerce.repository;

import com.example.myecommerce.entity.ProductComment;
import com.example.myecommerce.entity.ProductCommentLike;
import com.example.myecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductCommentLikeRepository extends JpaRepository<ProductCommentLike, Long> {
    boolean existsByCommentAndUser(ProductComment comment, User user);

    List<ProductCommentLike> findByUserAndCommentIn(User user, List<ProductComment> comments);
}
