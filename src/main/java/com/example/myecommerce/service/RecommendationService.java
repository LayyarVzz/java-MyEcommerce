package com.example.myecommerce.service;

import com.example.myecommerce.entity.OrderItem;
import com.example.myecommerce.entity.Product;
import com.example.myecommerce.entity.User;
import com.example.myecommerce.entity.UserActivity;
import com.example.myecommerce.repository.OrderItemRepository;
import com.example.myecommerce.repository.UserActivityRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
        Map<Long, Product> availableProductById = availableProducts.stream()
                .filter(product -> product.getId() != null)
                .collect(Collectors.toMap(Product::getId, product -> product, (left, right) -> left, LinkedHashMap::new));
        List<UserActivity> activities = userActivityRepository.findByUserIdOrderByTimestampDesc(user.getId());

        Set<Long> interactedProductIds = activities.stream()
                .map(UserActivity::getProductId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        LinkedHashSet<Product> recommendations = new LinkedHashSet<>();
        recommendAlsoBought(interactedProductIds, availableProductById).forEach(recommendations::add);
        if (recommendations.size() >= limit) {
            return limitAndExcludeInteracted(recommendations, interactedProductIds, limit);
        }

        recommendByCollaborativeFiltering(user, interactedProductIds, availableProductById).forEach(recommendations::add);
        if (recommendations.size() >= limit) {
            return limitAndExcludeInteracted(recommendations, interactedProductIds, limit);
        }

        List<String> preferredCategories = activities.stream()
                .map(UserActivity::getProductCategory)
                .filter(category -> category != null && !category.isBlank())
                .collect(Collectors.groupingBy(category -> category, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();

        for (String category : preferredCategories) {
            availableProducts.stream()
                    .filter(product -> category.equals(normalizeCategory(product.getCategory())))
                    .filter(product -> !interactedProductIds.contains(product.getId()))
                    .forEach(recommendations::add);
            if (recommendations.size() >= limit) {
                return limitAndExcludeInteracted(recommendations, interactedProductIds, limit);
            }
        }

        if (recommendations.size() < limit) {
            recommendPopularProducts(limit).forEach(recommendations::add);
        }
        if (recommendations.size() < limit) {
            recommendations.addAll(availableProducts);
        }

        return limitAndExcludeInteracted(recommendations, interactedProductIds, limit);
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

    private List<Product> recommendAlsoBought(Set<Long> interactedProductIds, Map<Long, Product> availableProductById) {
        if (interactedProductIds.isEmpty()) {
            return List.of();
        }

        List<OrderItem> orderItems = orderItemRepository.findAll();
        Set<Long> relatedOrderIds = orderItems.stream()
                .filter(item -> item.getProduct() != null && interactedProductIds.contains(item.getProduct().getId()))
                .filter(item -> item.getOrder() != null && item.getOrder().getId() != null)
                .map(item -> item.getOrder().getId())
                .collect(Collectors.toSet());

        Map<Product, Double> relatedProducts = orderItems.stream()
                .filter(item -> item.getOrder() != null && relatedOrderIds.contains(item.getOrder().getId()))
                .filter(item -> item.getProduct() != null && item.getProduct().getId() != null)
                .filter(item -> !interactedProductIds.contains(item.getProduct().getId()))
                .filter(item -> availableProductById.containsKey(item.getProduct().getId()))
                .collect(Collectors.groupingBy(
                        item -> availableProductById.get(item.getProduct().getId()),
                        Collectors.summingDouble(item -> item.getQuantity() == null ? 1 : Math.max(item.getQuantity(), 1))
                ));

        return sortedProductsByScore(relatedProducts);
    }

    private List<Product> recommendByCollaborativeFiltering(User user,
                                                            Set<Long> interactedProductIds,
                                                            Map<Long, Product> availableProductById) {
        if (user == null || user.getId() == null || interactedProductIds.isEmpty()) {
            return List.of();
        }

        List<UserActivity> allActivities = userActivityRepository.findAll();
        Map<Long, List<UserActivity>> activitiesByUser = allActivities.stream()
                .filter(activity -> activity.getUser() != null && activity.getUser().getId() != null)
                .collect(Collectors.groupingBy(activity -> activity.getUser().getId()));

        Map<Long, Double> similarUserScores = new HashMap<>();
        for (Map.Entry<Long, List<UserActivity>> entry : activitiesByUser.entrySet()) {
            Long otherUserId = entry.getKey();
            if (user.getId().equals(otherUserId)) {
                continue;
            }

            Set<Long> otherProducts = entry.getValue().stream()
                    .map(UserActivity::getProductId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            long overlap = otherProducts.stream().filter(interactedProductIds::contains).count();
            if (overlap > 0) {
                similarUserScores.put(otherUserId, overlap * 10.0);
            }
        }

        Map<Product, Double> productScores = new HashMap<>();
        for (UserActivity activity : allActivities) {
            if (!"PURCHASE_PRODUCT".equals(activity.getActivityType())
                    || activity.getUser() == null
                    || activity.getUser().getId() == null
                    || activity.getProductId() == null
                    || interactedProductIds.contains(activity.getProductId())) {
                continue;
            }

            Double userScore = similarUserScores.get(activity.getUser().getId());
            Product product = availableProductById.get(activity.getProductId());
            if (userScore == null || product == null) {
                continue;
            }

            double quantityScore = activity.getQuantity() == null ? 1 : Math.max(activity.getQuantity(), 1);
            double amountScore = activity.getAmount() == null ? 0 : Math.min(activity.getAmount() / 100.0, 20);
            productScores.merge(product, userScore + quantityScore + amountScore, Double::sum);
        }

        return sortedProductsByScore(productScores);
    }

    private List<Product> sortedProductsByScore(Map<Product, Double> productScores) {
        return productScores.entrySet().stream()
                .sorted(Map.Entry.<Product, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();
    }

    private List<Product> limitAndExcludeInteracted(LinkedHashSet<Product> recommendations,
                                                    Set<Long> interactedProductIds,
                                                    int limit) {
        return recommendations.stream()
                .filter(product -> product.getId() == null || !interactedProductIds.contains(product.getId()))
                .limit(limit)
                .toList();
    }

    private String normalizeCategory(String category) {
        if (category == null || category.isBlank() || "未分类".equals(category.trim())) {
            return Product.DEFAULT_CATEGORY;
        }
        return category.trim();
    }
}
