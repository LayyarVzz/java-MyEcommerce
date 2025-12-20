package com.example.myecommerce.service;

import com.example.myecommerce.entity.Order;
import com.example.myecommerce.entity.User;
import com.example.myecommerce.entity.OrderItem;
import com.example.myecommerce.entity.Product;
import com.example.myecommerce.repository.OrderRepository;
import com.example.myecommerce.repository.UserRepository;
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
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, UserService userService, 
                       UserActivityService userActivityService, ProductService productService,
                       UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.userActivityService = userActivityService;
        this.productService = productService;
        this.userRepository = userRepository;
    }

    public List<Order> getOrderHistory(String username) {
        User user = userService.getCurrentUser(username);
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
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
        // 如果订单状态从已取消变为待处理，需要扣除用户资金（因为之前已退款）
        else if ("已取消".equals(oldStatus) && "待处理".equals(status)) {
            System.out.println("Deducting user balance for order status change from cancelled to pending");
            deductUserBalance(order);
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
        userRepository.save(user);
        
        System.out.println("User balance after refund: " + user.getBalance());
    }
    
    // 扣除用户资金
    private void deductUserBalance(Order order) {
        User user = order.getUser();
        BigDecimal totalAmount = order.getTotalAmount();
        
        System.out.println("Deducting " + totalAmount + " from user " + user.getUsername());
        System.out.println("User balance before deduction: " + user.getBalance());
        
        // 扣除资金
        user.setBalance(user.getBalance().subtract(totalAmount));
        userRepository.save(user);
        
        System.out.println("User balance after deduction: " + user.getBalance());
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