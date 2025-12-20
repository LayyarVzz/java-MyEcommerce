package com.example.myecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_activities")
public class UserActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String activityType; // VIEW_PRODUCT, PURCHASE_PRODUCT, ADD_TO_CART, etc.

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    // 商品相关信息（如果是浏览或购买商品）
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name")
    private String productName;

    // 订单相关信息（如果是购买）
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "amount")
    private Double amount;
}