package com.example.myecommerce.service;

import com.example.myecommerce.entity.Order;
import com.example.myecommerce.entity.OrderItem;
import com.example.myecommerce.entity.Product;
import com.example.myecommerce.entity.User;
import com.example.myecommerce.repository.OrderRepository;
import com.example.myecommerce.repository.ProductRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReportServiceTest {

    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final ReportService reportService = new ReportService(orderRepository, productRepository);

    @Test
    void salesReportUsesRecentWeightedForecastAndTrendAssessment() {
        Product product = product(1L, "智能手表", "数码", 30);
        User user = new User();
        user.setId(1L);

        List<Order> orders = List.of(
                order(1L, user, product, LocalDate.of(2026, 5, 1), "已确认", "100.00"),
                order(2L, user, product, LocalDate.of(2026, 5, 2), "已确认", "100.00"),
                order(3L, user, product, LocalDate.of(2026, 5, 3), "已确认", "100.00"),
                order(4L, user, product, LocalDate.of(2026, 5, 4), "已确认", "100.00"),
                order(5L, user, product, LocalDate.of(2026, 5, 5), "已确认", "100.00"),
                order(6L, user, product, LocalDate.of(2026, 5, 6), "已确认", "100.00"),
                order(7L, user, product, LocalDate.of(2026, 5, 7), "已确认", "700.00")
        );
        when(orderRepository.findAll()).thenReturn(orders);
        when(productRepository.findAll()).thenReturn(List.of(product));

        Map<String, Object> reportData = reportService.generateSalesReport(
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 7)
        );

        assertThat(reportData.get("forecastRevenue")).isEqualTo(new BigDecimal("1750.00"));
        assertThat(reportData.get("trendAssessment")).isEqualTo("快速上升");
        assertThat(reportData.get("forecastDescription")).asString().contains("最近销售表现权重更高");
        assertThat(reportData.get("anomalyLevel")).isEqualTo("NORMAL");
        assertThat(reportData).containsKeys(
                "weeklyTrendLabels",
                "weeklyTrendRevenue",
                "monthlyTrendLabels",
                "monthlyTrendRevenue"
        );
    }

    @Test
    void salesReportClassifiesHighRiskAnomalies() {
        Product product = product(2L, "蓝牙音箱", "数码", 3);
        User user = new User();
        user.setId(2L);

        when(orderRepository.findAll()).thenReturn(List.of(
                order(1L, user, product, LocalDate.of(2026, 5, 1), "已确认", "120.00"),
                order(2L, user, product, LocalDate.of(2026, 5, 2), "已取消", "120.00"),
                order(3L, user, product, LocalDate.of(2026, 5, 3), "已取消", "120.00")
        ));
        when(productRepository.findAll()).thenReturn(List.of(product));

        Map<String, Object> reportData = reportService.generateSalesReport(
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 3)
        );

        assertThat(reportData.get("anomalyLevel")).isEqualTo("HIGH");
        assertThat((List<String>) reportData.get("anomalyWarnings"))
                .anyMatch(warning -> warning.contains("取消订单占比较高"))
                .anyMatch(warning -> warning.contains("库存不足"));
    }

    private Product product(Long id, String name, String category, int stock) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setCategory(category);
        product.setPrice(BigDecimal.valueOf(100));
        product.setStock(stock);
        product.setDiscontinued(false);
        return product;
    }

    private Order order(Long id,
                        User user,
                        Product product,
                        LocalDate date,
                        String status,
                        String amount) {
        Order order = new Order();
        order.setId(id);
        order.setUser(user);
        order.setOrderDate(LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 12, 0));
        order.setStatus(status);
        order.setTotalAmount(new BigDecimal(amount));

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(1);
        item.setPrice(new BigDecimal(amount));
        order.setOrderItems(List.of(item));
        return order;
    }
}
