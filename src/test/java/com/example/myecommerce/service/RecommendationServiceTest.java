package com.example.myecommerce.service;

import com.example.myecommerce.entity.Order;
import com.example.myecommerce.entity.OrderItem;
import com.example.myecommerce.entity.Product;
import com.example.myecommerce.entity.User;
import com.example.myecommerce.entity.UserActivity;
import com.example.myecommerce.repository.OrderItemRepository;
import com.example.myecommerce.repository.UserActivityRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RecommendationServiceTest {

    private final ProductService productService = mock(ProductService.class);
    private final UserActivityRepository userActivityRepository = mock(UserActivityRepository.class);
    private final OrderItemRepository orderItemRepository = mock(OrderItemRepository.class);
    private final RecommendationService recommendationService = new RecommendationService(
            productService,
            userActivityRepository,
            orderItemRepository
    );

    @Test
    void recommendationsPrioritizeCoPurchaseAndCollaborativeFilteringBeforeCategoryFallback() {
        User currentUser = user(1L);
        User similarUser = user(2L);

        Product viewed = product(10L, "入门相机", "数码");
        Product sameCategoryFallback = product(11L, "相机包", "数码");
        Product coPurchased = product(12L, "高速存储卡", "配件");
        Product collaborative = product(13L, "三脚架", "摄影");

        Order bundleOrder = order(100L, currentUser);
        Order similarUserOrder = order(200L, similarUser);

        when(productService.getAvailableProducts()).thenReturn(List.of(
                viewed,
                sameCategoryFallback,
                coPurchased,
                collaborative
        ));
        when(userActivityRepository.findByUserIdOrderByTimestampDesc(1L)).thenReturn(List.of(
                activity(currentUser, "VIEW_PRODUCT", viewed)
        ));
        when(userActivityRepository.findAll()).thenReturn(List.of(
                activity(currentUser, "VIEW_PRODUCT", viewed),
                activity(similarUser, "VIEW_PRODUCT", viewed),
                activity(similarUser, "PURCHASE_PRODUCT", collaborative)
        ));
        when(orderItemRepository.findAll()).thenReturn(List.of(
                item(bundleOrder, viewed, 1),
                item(bundleOrder, coPurchased, 3),
                item(similarUserOrder, collaborative, 1)
        ));

        List<Product> recommendations = recommendationService.recommendForUser(currentUser, 2);

        assertThat(recommendations)
                .extracting(Product::getName)
                .containsExactly("高速存储卡", "三脚架");
    }

    private User user(Long id) {
        User user = new User();
        user.setId(id);
        user.setUsername("user" + id);
        return user;
    }

    private Product product(Long id, String name, String category) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setCategory(category);
        product.setPrice(BigDecimal.valueOf(99));
        product.setStock(20);
        product.setDiscontinued(false);
        return product;
    }

    private UserActivity activity(User user, String type, Product product) {
        UserActivity activity = new UserActivity();
        activity.setUser(user);
        activity.setActivityType(type);
        activity.setProductId(product.getId());
        activity.setProductName(product.getName());
        activity.setProductCategory(product.getCategory());
        activity.setQuantity(1);
        activity.setAmount(product.getPrice().doubleValue());
        return activity;
    }

    private Order order(Long id, User user) {
        Order order = new Order();
        order.setId(id);
        order.setUser(user);
        return order;
    }

    private OrderItem item(Order order, Product product, int quantity) {
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setPrice(product.getPrice());
        return item;
    }
}
