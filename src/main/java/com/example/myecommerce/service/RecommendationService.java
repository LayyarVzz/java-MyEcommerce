package com.example.myecommerce.service;

import com.example.myecommerce.entity.OrderItem;
import com.example.myecommerce.entity.Product;
import com.example.myecommerce.entity.User;
import com.example.myecommerce.entity.UserActivity;
import com.example.myecommerce.repository.OrderItemRepository;
import com.example.myecommerce.repository.UserActivityRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
    private final ProductService productService;
    private final UserActivityRepository userActivityRepository;
    private final OrderItemRepository orderItemRepository;

    public RecommendationService(ProductService productService,
                                 UserActivityRepository userActivityRepository,
                                 OrderItemRepository orderItemRepository) {
        this.productService = productService;
        this.userActivityRepository = userActivityRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public List<Product> recommendForUser(User user, int limit) {
        List<Product> availableProducts = productService.getAvailableProducts();
        List<UserActivity> activities = userActivityRepository.findByUserIdOrderByTimestampDesc(user.getId());

        Set<Long> interactedProductIds = activities.stream()
                .map(UserActivity::getProductId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<String> preferredCategories = activities.stream()
                .map(UserActivity::getProductCategory)
                .filter(category -> category != null && !category.isBlank())
                .collect(Collectors.groupingBy(category -> category, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();

        LinkedHashSet<Product> recommendations = new LinkedHashSet<>();
        for (String category : preferredCategories) {
            availableProducts.stream()
                    .filter(product -> category.equals(normalizeCategory(product.getCategory())))
                    .filter(product -> !interactedProductIds.contains(product.getId()))
                    .forEach(recommendations::add);
            if (recommendations.size() >= limit) {
                return recommendations.stream().limit(limit).toList();
            }
        }

        recommendByCollaborativeFiltering(interactedProductIds, availableProducts).forEach(recommendations::add);
        if (recommendations.size() < limit) {
            recommendPopularProducts(limit).forEach(recommendations::add);
        }
        if (recommendations.size() < limit) {
            recommendations.addAll(availableProducts);
        }

        return recommendations.stream()
                .filter(product -> !interactedProductIds.contains(product.getId()))
                .limit(limit)
                .toList();
    }

    public List<Product> recommendPopularProducts(int limit) {
        Map<Product, Integer> salesCount = orderItemRepository.findAll().stream()
                .collect(Collectors.groupingBy(OrderItem::getProduct, Collectors.summingInt(OrderItem::getQuantity)));

        List<Product> popularProducts = salesCount.entrySet().stream()
                .sorted(Map.Entry.<Product, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .filter(product -> !Boolean.TRUE.equals(product.getDiscontinued()))
                .limit(limit)
                .toList();

        if (popularProducts.size() >= limit) {
            return popularProducts;
        }

        List<Product> fallback = new ArrayList<>(popularProducts);
        Set<Long> existingIds = fallback.stream().map(Product::getId).collect(Collectors.toSet());
        productService.getAvailableProducts().stream()
                .filter(product -> !existingIds.contains(product.getId()))
                .limit(limit - fallback.size())
                .forEach(fallback::add);

        return fallback;
    }

    public List<Product> recommendForContext(User user, String keyword, String category, int limit) {
        boolean hasKeyword = keyword != null && !keyword.isBlank();
        boolean hasCategory = category != null && !category.isBlank();
        if (!hasKeyword && !hasCategory) {
            return user == null ? recommendPopularProducts(limit) : recommendForUser(user, limit);
        }

        String normalizedCategory = normalizeCategory(category);
        LinkedHashSet<Product> recommendations = new LinkedHashSet<>();
        productService.searchProducts(keyword, category).forEach(recommendations::add);

        if (recommendations.size() < limit && user != null) {
            recommendForUser(user, limit * 2).stream()
                    .filter(product -> !hasCategory || normalizedCategory.equals(normalizeCategory(product.getCategory())))
                    .forEach(recommendations::add);
        }

        if (recommendations.size() < limit) {
            recommendPopularProducts(limit * 2).stream()
                    .filter(product -> !hasCategory || normalizedCategory.equals(normalizeCategory(product.getCategory())))
                    .forEach(recommendations::add);
        }

        if (recommendations.size() < limit) {
            productService.getAvailableProducts().stream()
                    .filter(product -> !hasCategory || normalizedCategory.equals(normalizeCategory(product.getCategory())))
                    .forEach(recommendations::add);
        }

        return recommendations.stream()
                .limit(limit)
                .toList();
    }

    private List<Product> recommendByCollaborativeFiltering(Set<Long> interactedProductIds, List<Product> availableProducts) {
        if (interactedProductIds.isEmpty()) {
            return List.of();
        }

        Set<Long> availableIds = availableProducts.stream()
                .map(Product::getId)
                .collect(Collectors.toSet());

        Set<Long> relatedOrderIds = orderItemRepository.findAll().stream()
                .filter(item -> interactedProductIds.contains(item.getProduct().getId()))
                .map(item -> item.getOrder().getId())
                .collect(Collectors.toSet());

        Map<Product, Integer> relatedProducts = orderItemRepository.findAll().stream()
                .filter(item -> relatedOrderIds.contains(item.getOrder().getId()))
                .filter(item -> !interactedProductIds.contains(item.getProduct().getId()))
                .filter(item -> availableIds.contains(item.getProduct().getId()))
                .collect(Collectors.groupingBy(OrderItem::getProduct, Collectors.summingInt(OrderItem::getQuantity)));

        return relatedProducts.entrySet().stream()
                .sorted(Map.Entry.<Product, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();
    }

    private String normalizeCategory(String category) {
        if (category == null || category.isBlank() || "未分类".equals(category.trim())) {
            return Product.DEFAULT_CATEGORY;
        }
        return category.trim();
    }
}
