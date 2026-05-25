package com.example.myecommerce.service;

import com.example.myecommerce.entity.Order;
import com.example.myecommerce.entity.OrderItem;
import com.example.myecommerce.entity.Product;
import com.example.myecommerce.repository.OrderRepository;
import com.example.myecommerce.repository.ProductRepository;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private static final Set<String> EFFECTIVE_STATUSES = Set.of("已确认", "已发货", "已送达");

    public ReportService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public Map<String, Object> generateSalesReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

        List<Order> allPeriodOrders = orderRepository.findAll().stream()
                .filter(order -> order.getOrderDate().isAfter(startDateTime) &&
                        order.getOrderDate().isBefore(endDateTime))
                .toList();

        // 只统计已确认、已发货或已送达的订单
        List<Order> orders = allPeriodOrders.stream()
                .filter(order -> EFFECTIVE_STATUSES.contains(order.getStatus()))
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

        Map<Product, SalesData> productSales = new LinkedHashMap<>();
        Map<String, SalesData> categorySales = new LinkedHashMap<>();
        for (Order order : orders) {
            for (OrderItem item : order.getOrderItems()) {
                Product product = item.getProduct();
                SalesData data = productSales.computeIfAbsent(product, p -> new SalesData(p.getName()));
                data.quantity += item.getQuantity();
                data.revenue = data.revenue.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

                String category = normalizeCategory(product.getCategory());
                SalesData categoryData = categorySales.computeIfAbsent(category, SalesData::new);
                categoryData.quantity += item.getQuantity();
                categoryData.revenue = categoryData.revenue.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }

        // 按销售额排序
        List<SalesData> topSellingProducts = productSales.values()
                .stream()
                .sorted((d1, d2) -> d2.revenue.compareTo(d1.revenue))
                .limit(10)
                .collect(Collectors.toList());

        List<SalesData> categorySalesRanking = categorySales.values().stream()
                .sorted((d1, d2) -> d2.revenue.compareTo(d1.revenue))
                .toList();

        List<StatusData> orderStatusStats = buildOrderStatusStats(allPeriodOrders);
        List<TrendData> dailyTrend = buildDailyTrend(orders, startDate, endDate);
        List<TrendData> weeklyTrend = buildGroupedTrend(orders, "week");
        List<TrendData> monthlyTrend = buildGroupedTrend(orders, "month");
        List<Product> lowStockProducts = productRepository.findAll().stream()
                .filter(product -> !Boolean.TRUE.equals(product.getDiscontinued()))
                .filter(product -> product.getStock() != null && product.getStock() < 10)
                .sorted(Comparator.comparing(Product::getStock))
                .limit(10)
                .toList();
        StockSummary stockSummary = buildStockSummary();
        AnomalyReport anomalyReport = buildAnomalyReport(allPeriodOrders, orders, lowStockProducts);
        BigDecimal forecastRevenue = forecastNextPeriodRevenue(dailyTrend);
        String trendAssessment = assessTrend(dailyTrend);
        String forecastDescription = buildForecastDescription(dailyTrend);

        Map<String, Object> reportData = new LinkedHashMap<>();
        reportData.put("totalRevenue", totalRevenue);
        reportData.put("totalOrders", totalOrders);
        reportData.put("totalProductsSold", totalProductsSold);
        reportData.put("topSellingProducts", topSellingProducts);
        reportData.put("categorySalesRanking", categorySalesRanking);
        reportData.put("orderStatusStats", orderStatusStats);
        reportData.put("dailyTrend", dailyTrend);
        reportData.put("weeklyTrend", weeklyTrend);
        reportData.put("monthlyTrend", monthlyTrend);
        reportData.put("dailyTrendLabels", dailyTrend.stream().map(TrendData::getLabel).toList());
        reportData.put("dailyTrendRevenue", dailyTrend.stream().map(TrendData::getRevenue).toList());
        reportData.put("dailyTrendOrders", dailyTrend.stream().map(TrendData::getOrderCount).toList());
        reportData.put("weeklyTrendLabels", weeklyTrend.stream().map(TrendData::getLabel).toList());
        reportData.put("weeklyTrendRevenue", weeklyTrend.stream().map(TrendData::getRevenue).toList());
        reportData.put("weeklyTrendOrders", weeklyTrend.stream().map(TrendData::getOrderCount).toList());
        reportData.put("monthlyTrendLabels", monthlyTrend.stream().map(TrendData::getLabel).toList());
        reportData.put("monthlyTrendRevenue", monthlyTrend.stream().map(TrendData::getRevenue).toList());
        reportData.put("monthlyTrendOrders", monthlyTrend.stream().map(TrendData::getOrderCount).toList());
        reportData.put("lowStockProducts", lowStockProducts);
        reportData.put("stockSummary", stockSummary);
        reportData.put("anomalyWarnings", anomalyReport.warnings());
        reportData.put("anomalyLevel", anomalyReport.level());
        reportData.put("anomalyLevelLabel", anomalyReport.label());
        reportData.put("forecastRevenue", forecastRevenue);
        reportData.put("forecastDescription", forecastDescription);
        reportData.put("trendAssessment", trendAssessment);
        reportData.put("startDate", startDate);
        reportData.put("endDate", endDate);

        return reportData;
    }

    private List<StatusData> buildOrderStatusStats(List<Order> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(Order::getStatus, LinkedHashMap::new, Collectors.toList()))
                .entrySet()
                .stream()
                .map(entry -> new StatusData(
                        entry.getKey(),
                        entry.getValue().size(),
                        entry.getValue().stream()
                                .map(Order::getTotalAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                ))
                .sorted((s1, s2) -> Integer.compare(s2.count, s1.count))
                .toList();
    }

    private List<TrendData> buildDailyTrend(List<Order> orders, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, List<Order>> groupedOrders = orders.stream()
                .collect(Collectors.groupingBy(order -> order.getOrderDate().toLocalDate()));

        List<TrendData> trend = new ArrayList<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            List<Order> dayOrders = groupedOrders.getOrDefault(current, List.of());
            trend.add(new TrendData(
                    current.toString(),
                    dayOrders.size(),
                    sumRevenue(dayOrders)
            ));
            current = current.plusDays(1);
        }
        return trend;
    }

    private List<TrendData> buildGroupedTrend(List<Order> orders, String type) {
        Map<String, List<Order>> groupedOrders = orders.stream()
                .collect(Collectors.groupingBy(order -> buildTrendLabel(order.getOrderDate().toLocalDate(), type)));

        return groupedOrders.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new TrendData(entry.getKey(), entry.getValue().size(), sumRevenue(entry.getValue())))
                .toList();
    }

    private String buildTrendLabel(LocalDate date, String type) {
        if ("month".equals(type)) {
            return date.getYear() + "-" + String.format("%02d", date.getMonthValue());
        }
        int week = date.get(WeekFields.of(Locale.CHINA).weekOfWeekBasedYear());
        return date.getYear() + "-W" + String.format("%02d", week);
    }

    private BigDecimal sumRevenue(List<Order> orders) {
        return orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private StockSummary buildStockSummary() {
        List<Product> products = productRepository.findAll();
        long activeCount = products.stream().filter(product -> !Boolean.TRUE.equals(product.getDiscontinued())).count();
        long discontinuedCount = products.stream().filter(product -> Boolean.TRUE.equals(product.getDiscontinued())).count();
        long lowStockCount = products.stream()
                .filter(product -> !Boolean.TRUE.equals(product.getDiscontinued()))
                .filter(product -> product.getStock() != null && product.getStock() < 10)
                .count();
        return new StockSummary(activeCount, discontinuedCount, lowStockCount);
    }

    private AnomalyReport buildAnomalyReport(List<Order> allPeriodOrders, List<Order> effectiveOrders, List<Product> lowStockProducts) {
        List<String> warnings = new ArrayList<>();
        int riskScore = 0;
        long cancelledCount = allPeriodOrders.stream().filter(order -> "已取消".equals(order.getStatus())).count();
        if (!allPeriodOrders.isEmpty() && cancelledCount * 1.0 / allPeriodOrders.size() >= 0.3) {
            warnings.add("取消订单占比较高，建议检查商品库存、价格或支付流程。");
            riskScore += cancelledCount * 1.0 / allPeriodOrders.size() >= 0.5 ? 2 : 1;
        }

        BigDecimal averageOrderAmount = effectiveOrders.isEmpty()
                ? BigDecimal.ZERO
                : sumRevenue(effectiveOrders).divide(BigDecimal.valueOf(effectiveOrders.size()), 2, RoundingMode.HALF_UP);
        effectiveOrders.stream()
                .filter(order -> averageOrderAmount.compareTo(BigDecimal.ZERO) > 0)
                .filter(order -> order.getTotalAmount().compareTo(averageOrderAmount.multiply(BigDecimal.valueOf(4))) > 0)
                .limit(3)
                .forEach(order -> warnings.add("订单 #" + order.getId() + " 金额明显高于平均值，建议重点关注。"));
        if (warnings.stream().anyMatch(warning -> warning.contains("金额明显高于平均值"))) {
            riskScore += 1;
        }

        lowStockProducts.stream()
                .limit(3)
                .forEach(product -> warnings.add("商品「" + product.getName() + "」库存不足，当前库存 " + product.getStock() + "。"));
        if (!lowStockProducts.isEmpty()) {
            riskScore += lowStockProducts.size() >= 3 ? 2 : 1;
        }

        if (warnings.isEmpty()) {
            warnings.add("当前统计周期未发现明显销售异常。");
        }
        return new AnomalyReport(classifyAnomalyLevel(riskScore), warnings);
    }

    private String classifyAnomalyLevel(int riskScore) {
        if (riskScore >= 2) {
            return "HIGH";
        }
        if (riskScore == 1) {
            return "MEDIUM";
        }
        return "NORMAL";
    }

    private BigDecimal forecastNextPeriodRevenue(List<TrendData> dailyTrend) {
        if (dailyTrend.isEmpty()) {
            return BigDecimal.ZERO;
        }
        List<TrendData> recentTrend = dailyTrend.size() <= 7
                ? dailyTrend
                : dailyTrend.subList(dailyTrend.size() - 7, dailyTrend.size());
        BigDecimal weightedRevenue = BigDecimal.ZERO;
        int weightSum = 0;
        for (int i = 0; i < recentTrend.size(); i++) {
            int weight = i + 1;
            weightedRevenue = weightedRevenue.add(recentTrend.get(i).getRevenue().multiply(BigDecimal.valueOf(weight)));
            weightSum += weight;
        }
        BigDecimal weightedDailyAverage = weightedRevenue.divide(BigDecimal.valueOf(weightSum), 2, RoundingMode.HALF_UP);
        return weightedDailyAverage.multiply(BigDecimal.valueOf(7)).setScale(2, RoundingMode.HALF_UP);
    }

    private String assessTrend(List<TrendData> dailyTrend) {
        if (dailyTrend.size() < 2) {
            return "数据不足";
        }
        int midpoint = dailyTrend.size() / 2;
        BigDecimal earlyRevenue = dailyTrend.subList(0, midpoint).stream()
                .map(TrendData::getRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal recentRevenue = dailyTrend.subList(midpoint, dailyTrend.size()).stream()
                .map(TrendData::getRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (earlyRevenue.compareTo(BigDecimal.ZERO) == 0) {
            return recentRevenue.compareTo(BigDecimal.ZERO) > 0 ? "快速上升" : "基本平稳";
        }

        BigDecimal changeRate = recentRevenue.subtract(earlyRevenue)
                .divide(earlyRevenue, 4, RoundingMode.HALF_UP);
        if (changeRate.compareTo(BigDecimal.valueOf(0.30)) >= 0) {
            return "快速上升";
        }
        if (changeRate.compareTo(BigDecimal.valueOf(0.05)) >= 0) {
            return "小幅上升";
        }
        if (changeRate.compareTo(BigDecimal.valueOf(-0.30)) <= 0) {
            return "快速下降";
        }
        if (changeRate.compareTo(BigDecimal.valueOf(-0.05)) <= 0) {
            return "小幅下降";
        }
        return "基本平稳";
    }

    private String buildForecastDescription(List<TrendData> dailyTrend) {
        if (dailyTrend.isEmpty()) {
            return "暂无销售数据，预测值按 0 处理。";
        }
        int days = Math.min(dailyTrend.size(), 7);
        return "基于最近 " + days + " 天销售额做加权移动平均，最近销售表现权重更高。";
    }

    private String normalizeCategory(String category) {
        if (category == null || category.isBlank()) {
            return "未分类";
        }
        return category.trim();
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

    @Getter
    public static class StatusData {
        private final String status;
        private final int count;
        private final BigDecimal revenue;

        public StatusData(String status, int count, BigDecimal revenue) {
            this.status = status;
            this.count = count;
            this.revenue = revenue;
        }
    }

    @Getter
    public static class TrendData {
        private final String label;
        private final int orderCount;
        private final BigDecimal revenue;

        public TrendData(String label, int orderCount, BigDecimal revenue) {
            this.label = label;
            this.orderCount = orderCount;
            this.revenue = revenue;
        }
    }

    @Getter
    public static class StockSummary {
        private final long activeCount;
        private final long discontinuedCount;
        private final long lowStockCount;

        public StockSummary(long activeCount, long discontinuedCount, long lowStockCount) {
            this.activeCount = activeCount;
            this.discontinuedCount = discontinuedCount;
            this.lowStockCount = lowStockCount;
        }
    }

    private record AnomalyReport(String level, List<String> warnings) {
        private String label() {
            return switch (level) {
                case "HIGH" -> "高风险";
                case "MEDIUM" -> "需关注";
                default -> "正常";
            };
        }
    }
}
