package com.example.myecommerce.service;

import com.example.myecommerce.entity.UserActivity;
import com.example.myecommerce.entity.User;
import com.example.myecommerce.entity.Product;
import com.example.myecommerce.entity.Order;
import com.example.myecommerce.repository.UserActivityRepository;
import com.example.myecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserActivityService {
    private final UserActivityRepository userActivityRepository;
    private final UserRepository userRepository;

    public UserActivityService(UserActivityRepository userActivityRepository, UserRepository userRepository) {
        this.userActivityRepository = userActivityRepository;
        this.userRepository = userRepository;
    }

    public void recordProductView(User user, Product product) {
        UserActivity activity = new UserActivity();
        activity.setUser(user);
        activity.setActivityType("VIEW_PRODUCT");
        activity.setDescription("浏览商品: " + product.getName());
        activity.setProductId(product.getId());
        activity.setProductName(product.getName());
        activity.setProductCategory(normalizeCategory(product.getCategory()));
        activity.setTimestamp(LocalDateTime.now());
        userActivityRepository.save(activity);
    }

    public UserActivity recordProductBrowse(User user, String search, String category, String ipAddress) {
        UserActivity activity = new UserActivity();
        activity.setUser(user);
        activity.setActivityType("BROWSE_PRODUCTS");
        activity.setDescription(buildBrowseDescription(search, category));
        activity.setProductCategory(normalizeCategory(category));
        activity.setIpAddress(ipAddress);
        activity.setDurationSeconds(0);
        activity.setTimestamp(LocalDateTime.now());
        return userActivityRepository.save(activity);
    }

    @Transactional
    public void recordLatestProductBrowseDuration(User user, int durationSeconds) {
        userActivityRepository.findFirstByUserIdAndActivityTypeOrderByTimestampDesc(user.getId(), "BROWSE_PRODUCTS")
                .ifPresent(activity -> updateBrowseDuration(user, activity, durationSeconds));
    }

    @Transactional
    public void recordProductBrowseDuration(User user, Long activityId, int durationSeconds) {
        if (activityId == null) {
            recordLatestProductBrowseDuration(user, durationSeconds);
            return;
        }
        userActivityRepository.findById(activityId)
                .filter(activity -> belongsToUser(activity, user))
                .filter(activity -> "BROWSE_PRODUCTS".equals(activity.getActivityType()))
                .ifPresent(activity -> updateBrowseDuration(user, activity, durationSeconds));
    }

    public void recordProductPurchase(User user, Product product, Order order, Double amount) {
        recordProductPurchase(user, product, order, amount, null, null);
    }

    public void recordProductPurchase(User user, Product product, Order order, Double amount, Integer quantity, String ipAddress) {
        UserActivity activity = new UserActivity();
        activity.setUser(user);
        activity.setActivityType("PURCHASE_PRODUCT");
        activity.setDescription("购买商品: " + product.getName() + ", 订单号: " + order.getId());
        activity.setProductId(product.getId());
        activity.setProductName(product.getName());
        activity.setProductCategory(normalizeCategory(product.getCategory()));
        activity.setOrderId(order.getId());
        activity.setAmount(amount);
        activity.setQuantity(quantity);
        activity.setUnitPrice(product.getPrice());
        activity.setIpAddress(ipAddress);
        activity.setTimestamp(LocalDateTime.now());
        userActivityRepository.save(activity);
    }

    public void recordAddToCart(User user, Product product) {
        recordAddToCart(user, product, null, null);
    }

    public void recordAddToCart(User user, Product product, Integer quantity, String ipAddress) {
        UserActivity activity = new UserActivity();
        activity.setUser(user);
        activity.setActivityType("ADD_TO_CART");
        activity.setDescription("添加商品到购物车: " + product.getName());
        activity.setProductId(product.getId());
        activity.setProductName(product.getName());
        activity.setProductCategory(normalizeCategory(product.getCategory()));
        activity.setQuantity(quantity);
        activity.setUnitPrice(product.getPrice());
        activity.setIpAddress(ipAddress);
        activity.setTimestamp(LocalDateTime.now());
        userActivityRepository.save(activity);
    }

    public void recordLogin(String username, String ipAddress) {
        userRepository.findByUsername(username).ifPresent(user -> {
            UserActivity activity = new UserActivity();
            activity.setUser(user);
            activity.setActivityType("LOGIN");
            activity.setDescription("用户登录: " + username);
            activity.setIpAddress(ipAddress);
            activity.setTimestamp(LocalDateTime.now());
            userActivityRepository.save(activity);
        });
    }

    public void recordAdminOperation(User user, String description, String ipAddress) {
        UserActivity activity = new UserActivity();
        activity.setUser(user);
        activity.setActivityType("ADMIN_OPERATION");
        activity.setDescription(description);
        activity.setIpAddress(ipAddress);
        activity.setTimestamp(LocalDateTime.now());
        userActivityRepository.save(activity);
    }

    public List<UserActivity> getUserActivities(Long userId) {
        return userActivityRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    public List<UserActivity> getAllActivities() {
        return userActivityRepository.findAllByOrderByTimestampDesc();
    }

    public List<UserActivity> getCustomerActivities() {
        return userActivityRepository.findByUserRoleOrderByTimestampDesc("USER");
    }

    public List<UserActivity> getViewActivitiesByUser(Long userId) {
        return userActivityRepository.findViewActivitiesByUserId(userId);
    }

    public List<UserActivity> getPurchaseActivitiesByUser(Long userId) {
        return userActivityRepository.findPurchaseActivitiesByUserId(userId);
    }

    private String normalizeCategory(String category) {
        if (category == null || category.isBlank()) {
            return "未分类";
        }
        return category.trim();
    }

    private String buildBrowseDescription(String search, String category) {
        String keyword = search == null || search.isBlank() ? "全部商品" : "搜索: " + search.trim();
        String categoryText = category == null || category.isBlank() ? "全部分类" : "分类: " + category.trim();
        return "浏览商品列表，" + keyword + "，" + categoryText;
    }

    private void updateBrowseDuration(User user, UserActivity activity, int durationSeconds) {
        if (!belongsToUser(activity, user) || !"BROWSE_PRODUCTS".equals(activity.getActivityType())) {
            return;
        }
        activity.setDurationSeconds(clampDuration(durationSeconds));
        userActivityRepository.save(activity);
    }

    private boolean belongsToUser(UserActivity activity, User user) {
        return activity.getUser() != null
                && activity.getUser().getId() != null
                && user != null
                && user.getId() != null
                && activity.getUser().getId().equals(user.getId());
    }

    private int clampDuration(int durationSeconds) {
        if (durationSeconds < 1) {
            return 1;
        }
        return Math.min(durationSeconds, 24 * 60 * 60);
    }
}
