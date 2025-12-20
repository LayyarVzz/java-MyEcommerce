package com.example.myecommerce.service;

import com.example.myecommerce.entity.UserActivity;
import com.example.myecommerce.entity.User;
import com.example.myecommerce.entity.Product;
import com.example.myecommerce.entity.Order;
import com.example.myecommerce.repository.UserActivityRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserActivityService {
    private final UserActivityRepository userActivityRepository;

    public UserActivityService(UserActivityRepository userActivityRepository) {
        this.userActivityRepository = userActivityRepository;
    }

    public void recordProductView(User user, Product product) {
        UserActivity activity = new UserActivity();
        activity.setUser(user);
        activity.setActivityType("VIEW_PRODUCT");
        activity.setDescription("浏览商品: " + product.getName());
        activity.setProductId(product.getId());
        activity.setProductName(product.getName());
        activity.setTimestamp(LocalDateTime.now());
        userActivityRepository.save(activity);
    }

    public void recordProductPurchase(User user, Product product, Order order, Double amount) {
        UserActivity activity = new UserActivity();
        activity.setUser(user);
        activity.setActivityType("PURCHASE_PRODUCT");
        activity.setDescription("购买商品: " + product.getName() + ", 订单号: " + order.getId());
        activity.setProductId(product.getId());
        activity.setProductName(product.getName());
        activity.setOrderId(order.getId());
        activity.setAmount(amount);
        activity.setTimestamp(LocalDateTime.now());
        userActivityRepository.save(activity);
    }

    public void recordAddToCart(User user, Product product) {
        UserActivity activity = new UserActivity();
        activity.setUser(user);
        activity.setActivityType("ADD_TO_CART");
        activity.setDescription("添加商品到购物车: " + product.getName());
        activity.setProductId(product.getId());
        activity.setProductName(product.getName());
        activity.setTimestamp(LocalDateTime.now());
        userActivityRepository.save(activity);
    }

    public List<UserActivity> getUserActivities(Long userId) {
        return userActivityRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    public List<UserActivity> getAllActivities() {
        return userActivityRepository.findAllByOrderByTimestampDesc();
    }

    public List<UserActivity> getViewActivitiesByUser(Long userId) {
        return userActivityRepository.findViewActivitiesByUserId(userId);
    }

    public List<UserActivity> getPurchaseActivitiesByUser(Long userId) {
        return userActivityRepository.findPurchaseActivitiesByUserId(userId);
    }
}