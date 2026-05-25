package com.example.myecommerce.repository;

import com.example.myecommerce.entity.Product;
import com.example.myecommerce.entity.ProductComment;
import com.example.myecommerce.entity.ProductCommentRating;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductCommentRepository extends JpaRepository<ProductComment, Long> {
    List<ProductComment> findByProduct(Product product, Pageable pageable);

    List<ProductComment> findByProductAndRating(Product product, ProductCommentRating rating, Pageable pageable);

    List<ProductComment> findByProductOrderByLikeCountDescCreatedAtDesc(Product product);

    List<ProductComment> findByProductAndRatingOrderByLikeCountDescCreatedAtDesc(Product product, ProductCommentRating rating);

    long countByProduct(Product product);

    long countByProductAndRating(Product product, ProductCommentRating rating);
}
