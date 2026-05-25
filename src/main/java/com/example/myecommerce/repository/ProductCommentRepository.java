package com.example.myecommerce.repository;

import com.example.myecommerce.entity.Product;
import com.example.myecommerce.entity.ProductComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductCommentRepository extends JpaRepository<ProductComment, Long> {
    List<ProductComment> findByProduct(Product product, Pageable pageable);

    List<ProductComment> findByProductOrderByLikeCountDescCreatedAtDesc(Product product);

    long countByProduct(Product product);
}
