package com.example.myecommerce.entity;

import lombok.Data;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 商品名称

    private String description; // 商品描述

    @Column(nullable = false)
    private BigDecimal price; // 商品价格

    private String imageUrl; // 商品图片（用占位图）
    
    private Integer stock = 0; // 商品库存
    
    private Boolean discontinued = false; // 是否已下架，默认为false（未下架）
    
    // 提供一个安全的getter方法，确保不会返回null
    public Boolean getDiscontinued() {
        return discontinued != null ? discontinued : false;
    }
    
    // 提供一个安全的setter方法
    public void setDiscontinued(Boolean discontinued) {
        this.discontinued = discontinued != null ? discontinued : false;
    }
    
    @PostLoad
    public void onLoad() {
        // 确保从数据库加载时，discontinued不会为null
        if (this.discontinued == null) {
            this.discontinued = false;
        }
        // 确保stock不会为null
        if (this.stock == null) {
            this.stock = 0;
        }
    }
}