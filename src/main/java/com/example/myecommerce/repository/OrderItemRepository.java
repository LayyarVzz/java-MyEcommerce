package com.example.myecommerce.repository;

import com.example.myecommerce.entity.OrderItem;
import com.example.myecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // 根据商品查找订单项
    List<OrderItem> findByProduct(Product product);
}