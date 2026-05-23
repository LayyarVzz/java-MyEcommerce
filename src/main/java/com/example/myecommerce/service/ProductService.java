package com.example.myecommerce.service;

import com.example.myecommerce.entity.Product;
import com.example.myecommerce.entity.OrderItem;
import com.example.myecommerce.entity.Order;
import com.example.myecommerce.entity.User;
import com.example.myecommerce.repository.CartItemRepository;
import com.example.myecommerce.repository.ProductRepository;
import com.example.myecommerce.repository.OrderItemRepository;
import com.example.myecommerce.repository.OrderRepository;
import com.example.myecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public ProductService(ProductRepository productRepository, CartItemRepository cartItemRepository,
                          OrderItemRepository orderItemRepository, OrderRepository orderRepository,
                          UserRepository userRepository) {
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    // 查询所有商品（包括已下架商品）
    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();
        System.out.println("Total products: " + products.size());
        return products;
    }

    // 查询所有未下架商品
    public List<Product> getAvailableProducts() {
        List<Product> products = productRepository.findByDiscontinuedFalse();
        System.out.println("Available products: " + products.size());
        // 如果通过discontinued过滤的结果为空，尝试另一种方式
        if (products.isEmpty()) {
            System.out.println("Fallback: getting all products");
            products = productRepository.findAll();
            // 手动过滤discontinued为false或null的商品
            products.removeIf(product -> Boolean.TRUE.equals(product.getDiscontinued()));
        }
        return sortProducts(products);
    }

    public List<String> getAvailableCategories() {
        return getAvailableProducts().stream()
                .map(Product::getCategory)
                .map(this::normalizeCategory)
                .filter(category -> !"未分类".equals(category))
                .distinct()
                .sorted(Comparator.naturalOrder())
                .toList();
    }

    // 根据ID查询商品
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("商品不存在：" + id));
    }

    // 根据keyword搜索商品
    public List<Product> searchProducts(String keyword) {
        return searchProducts(keyword, null);
    }

    public List<Product> searchProducts(String keyword, String category) {
        return filterAndSortProducts(getAvailableProducts(), keyword, category);
    }

    public List<Product> searchProducts(String keyword, String category, boolean includeDiscontinued) {
        List<Product> products = includeDiscontinued ? sortProducts(getAllProducts()) : getAvailableProducts();
        return filterAndSortProducts(products, keyword, category);
    }

    // 保存商品
    public void saveProduct(Product product) {
        product.setCategory(normalizeCategory(product.getCategory()));
        // 确保 discontinued 字段不为 null
        if (product.getDiscontinued() == null) {
            product.setDiscontinued(false);
        }
        productRepository.save(product);
    }

    // 下架商品并处理相关订单
    @Transactional
    public void discontinueProduct(Long id) {
        Product product = getProductById(id);
        product.setDiscontinued(true);
        productRepository.save(product);

        // 删除购物车中的相关项
        cartItemRepository.deleteByProduct_Id(id);

        // 处理相关的订单项
        List<OrderItem> orderItems = orderItemRepository.findByProduct(product);
        for (OrderItem item : orderItems) {
            Order order = item.getOrder();
            // 只处理待处理和已取消的订单
            if ("待处理".equals(order.getStatus())) {
                order.setStatus("已取消");
                orderRepository.save(order);
                
                // 如果是从待处理变为已取消，需要退款
                refundUserBalance(order);
            }
            // 对于其他状态的订单，检查是否需要更新状态
            else if ("已确认".equals(order.getStatus()) || "已发货".equals(order.getStatus()) || "已送达".equals(order.getStatus())) {
                // 检查订单中的其他商品是否都已下架
                boolean allDiscontinued = true;
                boolean hasAvailable = false;

                // 确保orderItems被加载
                List<OrderItem> items = order.getOrderItems();
                if (items != null) {
                    for (OrderItem oi : items) {
                        if (oi.getProduct() != null && !Boolean.TRUE.equals(oi.getProduct().getDiscontinued())) {
                            allDiscontinued = false;
                            hasAvailable = true;
                            break;
                        }
                    }
                }

                if (allDiscontinued) {
                    // 所有商品都已下架
                    order.setStatus("商品已下架");
                    orderRepository.save(order);
                    // 如果是因为商品下架导致订单取消，需要退款
                    refundUserBalance(order);
                } else {
                    // 部分商品下架
                    order.setStatus("部分商品下架");
                    orderRepository.save(order);
                }
            }
        }
    }
    
    // 退还用户资金
    private void refundUserBalance(Order order) {
        User user = order.getUser();
        BigDecimal totalAmount = order.getTotalAmount();

        // 退还资金给用户
        user.setBalance(user.getBalance().add(totalAmount));
        userRepository.save(user);
        System.out.println("Refunded " + totalAmount + " to user " + user.getUsername());
    }

    private String normalizeCategory(String category) {
        if (category == null || category.isBlank() || "未分类".equals(category.trim())) {
            return Product.DEFAULT_CATEGORY;
        }
        return category.trim();
    }

    private List<Product> filterAndSortProducts(List<Product> products, String keyword, String category) {
        String normalizedKeyword = normalizeText(keyword);
        String normalizedCategory = normalizeCategory(category);
        boolean hasKeyword = !normalizedKeyword.isEmpty();
        boolean hasCategory = category != null && !category.isBlank();

        Comparator<Product> comparator = Comparator.comparing(
                Product::getId,
                Comparator.nullsLast(Long::compareTo)
        );
        if (hasKeyword) {
            comparator = Comparator.comparingInt((Product product) -> searchScore(product, normalizedKeyword))
                    .reversed()
                    .thenComparing(comparator);
        }

        return products.stream()
                .filter(product -> !hasKeyword || searchScore(product, normalizedKeyword) > 0)
                .filter(product -> !hasCategory || Objects.equals(normalizeCategory(product.getCategory()), normalizedCategory))
                .sorted(comparator)
                .toList();
    }

    private int searchScore(Product product, String normalizedKeyword) {
        if (normalizedKeyword.isEmpty()) {
            return 1;
        }

        int score = 0;
        String name = normalizeText(product.getName());
        String description = normalizeText(product.getDescription());
        String category = normalizeText(normalizeCategory(product.getCategory()));

        if (name.equals(normalizedKeyword)) {
            score += 120;
        } else if (name.startsWith(normalizedKeyword)) {
            score += 100;
        } else if (name.contains(normalizedKeyword)) {
            score += 80;
        }

        if (category.equals(normalizedKeyword)) {
            score += 70;
        } else if (category.contains(normalizedKeyword)) {
            score += 45;
        }

        if (description.contains(normalizedKeyword)) {
            score += 30;
        }

        for (String token : normalizedKeyword.split("\\s+")) {
            if (token.isBlank() || token.equals(normalizedKeyword)) {
                continue;
            }
            if (name.contains(token)) {
                score += 30;
            }
            if (category.contains(token)) {
                score += 15;
            }
            if (description.contains(token)) {
                score += 10;
            }
        }

        return score;
    }

    private String normalizeText(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private List<Product> sortProducts(List<Product> products) {
        return products.stream()
                .sorted(Comparator.comparing(Product::getId, Comparator.nullsLast(Long::compareTo)))
                .toList();
    }
}
