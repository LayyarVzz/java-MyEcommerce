package com.example.myecommerce.service;

import com.example.myecommerce.entity.Order;
import com.example.myecommerce.entity.OrderItem;
import com.example.myecommerce.entity.Product;
import com.example.myecommerce.repository.OrderRepository;
import com.example.myecommerce.repository.ProductRepository;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final OrderRepository orderRepository;

    public ReportService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
    }

    public Map<String, Object> generateSalesReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

        // 只统计已确认、已发货或已送达的订单
        List<Order> orders = orderRepository.findAll().stream()
                .filter(order -> "已确认".equals(order.getStatus()) ||
                        "已发货".equals(order.getStatus()) ||
                        "已送达".equals(order.getStatus()))
                .filter(order -> order.getOrderDate().isAfter(startDateTime) &&
                        order.getOrderDate().isBefore(endDateTime))
                .toList();

        // 总销售额
        BigDecimal totalRevenue = orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 订单总数
        int totalOrders = orders.size();

        // 售出商品总数
        int totalProductsSold = orders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .mapToInt(OrderItem::getQuantity)
                .sum();

        // 商品销售排行 (Top 10)
        Map<Product, SalesData> productSales = new HashMap<>();
        for (Order order : orders) {
            for (OrderItem item : order.getOrderItems()) {
                Product product = item.getProduct();
                SalesData data = productSales.computeIfAbsent(product, p -> new SalesData(p.getName()));
                data.quantity += item.getQuantity();
                data.revenue = data.revenue.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }

        // 按销售额排序
        List<SalesData> topSellingProducts = productSales.values()
                .stream()
                .sorted((d1, d2) -> d2.revenue.compareTo(d1.revenue))
                .limit(10)
                .collect(Collectors.toList());

        Map<String, Object> reportData = new HashMap<>();
        reportData.put("totalRevenue", totalRevenue);
        reportData.put("totalOrders", totalOrders);
        reportData.put("totalProductsSold", totalProductsSold);
        reportData.put("topSellingProducts", topSellingProducts);
        reportData.put("startDate", startDate);
        reportData.put("endDate", endDate);

        return reportData;
    }

    // 内部类用于存储商品销售数据
    @Getter
    public static class SalesData {
        // Getters
        private final String productName;
        private int quantity = 0;
        private BigDecimal revenue = BigDecimal.ZERO;

        public SalesData(String productName) {
            this.productName = productName;
        }

    }
}