package com.example.myecommerce.service;

import com.example.myecommerce.entity.User;
import com.example.myecommerce.entity.UserActivity;
import com.example.myecommerce.repository.UserActivityRepository;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {
    private final UserActivityRepository userActivityRepository;

    public AnalyticsService(UserActivityRepository userActivityRepository) {
        this.userActivityRepository = userActivityRepository;
    }

    public UserProfile buildUserProfile(User user) {
        List<UserActivity> activities = userActivityRepository.findByUserIdOrderByTimestampDesc(user.getId());

        double totalPurchaseAmount = activities.stream()
                .filter(activity -> "PURCHASE_PRODUCT".equals(activity.getActivityType()))
                .map(UserActivity::getAmount)
                .filter(amount -> amount != null)
                .mapToDouble(Double::doubleValue)
                .sum();

        long purchaseCount = activities.stream()
                .filter(activity -> "PURCHASE_PRODUCT".equals(activity.getActivityType()))
                .count();

        long browseCount = activities.stream()
                .filter(activity -> "VIEW_PRODUCT".equals(activity.getActivityType())
                        || "BROWSE_PRODUCTS".equals(activity.getActivityType()))
                .count();

        double averageOrderAmount = purchaseCount == 0 ? 0 : totalPurchaseAmount / purchaseCount;
        List<CategoryPreference> preferenceCategories = buildPreferenceCategories(activities);
        String favoriteCategory = preferenceCategories.stream()
                .findFirst()
                .map(CategoryPreference::getCategory)
                .orElse("暂无偏好");

        String buyingPower = classifyBuyingPower(totalPurchaseAmount, averageOrderAmount);
        String activityLevel = classifyActivityLevel(activities.size(), browseCount, purchaseCount);
        String region = inferRegion(activities);

        return new UserProfile(
                favoriteCategory,
                buyingPower,
                activityLevel,
                region,
                totalPurchaseAmount,
                purchaseCount,
                browseCount,
                averageOrderAmount,
                preferenceCategories
        );
    }

    private List<CategoryPreference> buildPreferenceCategories(List<UserActivity> activities) {
        Map<String, PreferenceScore> scores = new LinkedHashMap<>();
        for (UserActivity activity : activities) {
            String category = activity.getProductCategory();
            if (category == null || category.isBlank()) {
                continue;
            }

            PreferenceScore score = scores.computeIfAbsent(category.trim(), key -> new PreferenceScore());
            double weight = scoreActivity(activity);
            score.score += weight;
            score.interactionCount++;
            if ("PURCHASE_PRODUCT".equals(activity.getActivityType())) {
                score.purchaseCount++;
            }
        }

        return scores.entrySet().stream()
                .sorted((left, right) -> Double.compare(right.getValue().score, left.getValue().score))
                .map(entry -> new CategoryPreference(
                        entry.getKey(),
                        roundScore(entry.getValue().score),
                        entry.getValue().interactionCount,
                        entry.getValue().purchaseCount
                ))
                .toList();
    }

    private double scoreActivity(UserActivity activity) {
        String type = activity.getActivityType();
        int quantity = activity.getQuantity() == null ? 0 : Math.max(activity.getQuantity(), 0);
        double amount = activity.getAmount() == null ? 0 : Math.max(activity.getAmount(), 0);
        int duration = activity.getDurationSeconds() == null ? 0 : Math.max(activity.getDurationSeconds(), 0);

        if ("PURCHASE_PRODUCT".equals(type)) {
            return 12 + quantity * 2.0 + Math.min(amount / 100.0, 40);
        }
        if ("ADD_TO_CART".equals(type)) {
            return 6 + quantity;
        }
        if ("VIEW_PRODUCT".equals(type)) {
            return 3 + Math.min(duration, 600) / 60.0;
        }
        if ("BROWSE_PRODUCTS".equals(type)) {
            return 1.5 + Math.min(duration, 600) / 120.0;
        }
        return 1;
    }

    private double roundScore(double score) {
        return Math.round(score * 10.0) / 10.0;
    }

    private String classifyBuyingPower(double totalPurchaseAmount, double averageOrderAmount) {
        if (totalPurchaseAmount >= 10000 || averageOrderAmount >= 3000) {
            return "高购买力";
        }
        if (totalPurchaseAmount >= 3000 || averageOrderAmount >= 1000) {
            return "中购买力";
        }
        if (totalPurchaseAmount > 0) {
            return "低购买力";
        }
        return "暂无购买记录";
    }

    private String classifyActivityLevel(int totalActivities, long browseCount, long purchaseCount) {
        if (purchaseCount >= 5 || totalActivities >= 30) {
            return "高活跃";
        }
        if (browseCount >= 5 || purchaseCount >= 1) {
            return "中活跃";
        }
        return "低活跃";
    }

    private String inferRegion(List<UserActivity> activities) {
        return activities.stream()
                .map(UserActivity::getIpAddress)
                .filter(ip -> ip != null && !ip.isBlank())
                .max(Comparator.comparingInt(String::length))
                .map(this::classifyIp)
                .orElse("未知地域");
    }

    private String classifyIp(String ip) {
        if (ip.startsWith("127.") || ip.equals("0:0:0:0:0:0:0:1") || ip.equals("::1")) {
            return "本机访问";
        }
        if (ip.startsWith("10.") || ip.startsWith("192.168.") || ip.startsWith("172.")) {
            return "内网访问";
        }
        return "公网访问";
    }

    @Getter
    public static class UserProfile {
        private final String favoriteCategory;
        private final String buyingPower;
        private final String activityLevel;
        private final String region;
        private final double totalPurchaseAmount;
        private final long purchaseCount;
        private final long browseCount;
        private final double averageOrderAmount;
        private final List<CategoryPreference> preferenceCategories;

        public UserProfile(String favoriteCategory,
                           String buyingPower,
                           String activityLevel,
                           String region,
                           double totalPurchaseAmount,
                           long purchaseCount,
                           long browseCount,
                           double averageOrderAmount,
                           List<CategoryPreference> preferenceCategories) {
            this.favoriteCategory = favoriteCategory;
            this.buyingPower = buyingPower;
            this.activityLevel = activityLevel;
            this.region = region;
            this.totalPurchaseAmount = totalPurchaseAmount;
            this.purchaseCount = purchaseCount;
            this.browseCount = browseCount;
            this.averageOrderAmount = averageOrderAmount;
            this.preferenceCategories = new ArrayList<>(preferenceCategories);
        }
    }

    @Getter
    public static class CategoryPreference {
        private final String category;
        private final double score;
        private final long interactionCount;
        private final long purchaseCount;

        public CategoryPreference(String category, double score, long interactionCount, long purchaseCount) {
            this.category = category;
            this.score = score;
            this.interactionCount = interactionCount;
            this.purchaseCount = purchaseCount;
        }
    }

    private static class PreferenceScore {
        private double score;
        private long interactionCount;
        private long purchaseCount;
    }
}
