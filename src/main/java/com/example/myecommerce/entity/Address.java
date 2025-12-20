package com.example.myecommerce.entity;

import lombok.Data;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String contactName; // 联系人姓名

    @Column(nullable = false)
    private String phone; // 联系电话

    @Column(nullable = false)
    private String address; // 详细地址

    @Column(nullable = false)
    private Boolean isDefault = false; // 是否为默认地址

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 创建时间
}
