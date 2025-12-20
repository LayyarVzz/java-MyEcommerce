package com.example.myecommerce.service;

import com.example.myecommerce.entity.Order;
import com.example.myecommerce.entity.OrderItem;
import com.example.myecommerce.entity.Product;
import com.example.myecommerce.repository.OrderRepository;
import com.example.myecommerce.repository.ProductRepository;
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
    private final ProductRepository productRepository;

    public ReportService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public Map<String, Object> generateSalesReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

        List<Order> orders = orderRepository.findAll().stream()
                .filter(order -> !order.getStatus().equals("已取消"))
                .filter(order -> order.getOrderDate().isAfter(startDateTime) &&
                        order.getOrderDate().isBefore(endDateTime))
                .collect(Collectors.toList());

        Map<String, Object> reportData = new HashMap<>();

        // 总销售额
        BigDecimal totalSales = orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 订单总数
        int totalOrders = orders.size();

        // 商品销售排行
        Map<Product, Integer> productSales = new HashMap<>();
        for (Order order : orders) {
            for (OrderItem item : order.getOrderItems()) {
                Product product = item.getProduct();
                productSales.merge(product, item.getQuantity(), Integer::sum);
            }
        }

        // 按销量排序
        List<Map.Entry<Product, Integer>> topProducts = productSales.entrySet()
                .stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(10)
                .collect(Collectors.toList());

        reportData.put("totalSales", totalSales);
        reportData.put("totalOrders", totalOrders);
        reportData.put("topProducts", topProducts);
        reportData.put("averageOrderValue", totalOrders > 0 ? totalSales.divide(BigDecimal.valueOf(totalOrders), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);

        return reportData;
    }
}
