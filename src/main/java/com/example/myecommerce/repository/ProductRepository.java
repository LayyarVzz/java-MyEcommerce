package com.example.myecommerce.repository;

import com.example.myecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCase(String name);
    
    // 查询未下架商品
    List<Product> findByDiscontinuedFalse();
    
    // 查询所有商品（包括已下架）
    List<Product> findAll();
    
    // 查询未下架且名称包含关键字的商品
    List<Product> findByNameContainingIgnoreCaseAndDiscontinuedFalse(String name);
}