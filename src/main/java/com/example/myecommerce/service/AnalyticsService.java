package com.example.myecommerce.service;

import com.example.myecommerce.entity.User;
import com.example.myecommerce.entity.UserActivity;
import com.example.myecommerce.repository.UserActivityRepository;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        String favoriteCategory = activities.stream()
                .map(UserActivity::getProductCategory)
                .filter(category -> category != null && !category.isBlank())
                .collect(Collectors.groupingBy(category -> category, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("暂无偏好");

        String buyingPower = classifyBuyingPower(totalPurchaseAmount);
        String activityLevel = classifyActivityLevel(activities.size(), browseCount, purchaseCount);
        String region = inferRegion(activities);

        return new UserProfile(favoriteCategory, buyingPower, activityLevel, region, totalPurchaseAmount, purchaseCount, browseCount);
    }

    private String classifyBuyingPower(double totalPurchaseAmount) {
        if (totalPurchaseAmount >= 10000) {
            return "高购买力";
        }
        if (totalPurchaseAmount >= 3000) {
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

        public UserProfile(String favoriteCategory,
                           String buyingPower,
                           String activityLevel,
                           String region,
                           double totalPurchaseAmount,
                           long purchaseCount,
                           long browseCount) {
            this.favoriteCategory = favoriteCategory;
            this.buyingPower = buyingPower;
            this.activityLevel = activityLevel;
            this.region = region;
            this.totalPurchaseAmount = totalPurchaseAmount;
            this.purchaseCount = purchaseCount;
            this.browseCount = browseCount;
        }
    }
}
