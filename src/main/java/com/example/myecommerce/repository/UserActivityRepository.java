package com.example.myecommerce.repository;

import com.example.myecommerce.entity.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
    List<UserActivity> findByUserIdOrderByTimestampDesc(Long userId);
    
    List<UserActivity> findAllByOrderByTimestampDesc();
    
    @Query("SELECT ua FROM UserActivity ua WHERE ua.user.id = :userId AND ua.activityType = 'VIEW_PRODUCT' ORDER BY ua.timestamp DESC")
    List<UserActivity> findViewActivitiesByUserId(@Param("userId") Long userId);
    
    @Query("SELECT ua FROM UserActivity ua WHERE ua.user.id = :userId AND ua.activityType = 'PURCHASE_PRODUCT' ORDER BY ua.timestamp DESC")
    List<UserActivity> findPurchaseActivitiesByUserId(@Param("userId") Long userId);
}