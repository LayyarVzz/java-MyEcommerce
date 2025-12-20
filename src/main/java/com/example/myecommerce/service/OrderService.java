package com.example.myecommerce.service;

import com.example.myecommerce.entity.Order;
import com.example.myecommerce.entity.User;
import com.example.myecommerce.entity.OrderItem;
import com.example.myecommerce.entity.Product;
import com.example.myecommerce.repository.OrderRepository;
import com.example.myecommerce.service.UserActivityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final UserActivityService userActivityService;
    private final ProductService productService;

    public OrderService(OrderRepository orderRepository, UserService userService, 
                       UserActivityService userActivityService, ProductService productService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.userActivityService = userActivityService;
        this.productService = productService;
    }

    public List<Order> getOrderHistory(String username) {
        User user = userService.getCurrentUser(username);
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        
        String oldStatus = order.getStatus();
        System.out.println("Updating order status from " + oldStatus + " to " + status);
        order.setStatus(status);
        orderRepository.save(order);
        
        // 如果订单状态从未确认变为已确认，减少商品库存
        if (!"已确认".equals(oldStatus) && "已确认".equals(status)) {
            System.out.println("Reducing product stock for confirmed order");
            reduceProductStock(order);
        }
        
        // 如果订单状态从未取消变为已取消，退还用户资金
        if (!"已取消".equals(oldStatus) && "已取消".equals(status)) {
            System.out.println("Refunding user balance for cancelled order");
            refundUserBalance(order);
        }
    }
    
    // 减少商品库存
    private void reduceProductStock(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            int quantity = item.getQuantity();
            
            // 减少库存
            int newStock = product.getStock() - quantity;
            product.setStock(newStock);
            
            // 如果库存降到0或以下，自动下架商品
            if (newStock <= 0) {
                product.setDiscontinued(true);
            }
            
            // 保存商品信息
            productService.saveProduct(product);
        }
    }
    
    // 退还用户资金
    private void refundUserBalance(Order order) {
        User user = order.getUser();
        BigDecimal totalAmount = order.getTotalAmount();
        
        System.out.println("Refunding " + totalAmount + " to user " + user.getUsername());
        System.out.println("User balance before refund: " + user.getBalance());
        
        // 退还资金给用户
        user.setBalance(user.getBalance().add(totalAmount));
        userService.saveUserWithoutEncryption(user);
        
        System.out.println("User balance after refund: " + user.getBalance());
    }
    
    public void recordPurchaseActivity(Order order) {
        User user = order.getUser();
        for (OrderItem item : order.getOrderItems()) {
            userActivityService.recordProductPurchase(
                user, 
                item.getProduct(), 
                order, 
                item.getPrice().doubleValue() * item.getQuantity()
            );
        }
    }
}