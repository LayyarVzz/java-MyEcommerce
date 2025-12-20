package com.example.myecommerce.entity;

import lombok.Data;

import jakarta.persistence.*;

@Data
@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 关联用户（多对一）
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 关联商品（多对一）
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity; // 商品数量
}
