package com.example.myecommerce.repository;

import com.example.myecommerce.entity.Order;
import com.example.myecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByOrderDateDesc(User user);
    
    // 根据状态查找订单
    List<Order> findByStatus(String status);
}