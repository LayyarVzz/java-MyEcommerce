package com.example.myecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增ID
    private Long id;

    @Column(unique = true, nullable = false) // 用户名唯一、非空
    private String username;

    @Column(nullable = false)
    private String password; // 密码（Spring Security 会加密）

    private String fullName; // 昵称

    @Column(nullable = false)
    private String role = "USER"; // 默认为普通用户，管理员为 "ADMIN"

    // 关联购物车（一个用户对应多个购物车项）
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<CartItem> cartItems;

    // 添加资金属性
    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;
    
    @PostLoad
    public void onLoad() {
        // 确保balance不会为null
        if (this.balance == null) {
            this.balance = BigDecimal.ZERO;
        }
    }
}