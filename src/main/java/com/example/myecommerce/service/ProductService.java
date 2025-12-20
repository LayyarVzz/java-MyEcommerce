package com.example.myecommerce.service;

import com.example.myecommerce.entity.Product;
import com.example.myecommerce.entity.OrderItem;
import com.example.myecommerce.entity.Order;
import com.example.myecommerce.repository.CartItemRepository;
import com.example.myecommerce.repository.ProductRepository;
import com.example.myecommerce.repository.OrderItemRepository;
import com.example.myecommerce.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    public ProductService(ProductRepository productRepository, CartItemRepository cartItemRepository, 
                         OrderItemRepository orderItemRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
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
        return products;
    }

    // 根据ID查询商品
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("商品不存在：" + id));
    }

    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAvailableProducts();
        }
        return productRepository.findByNameContainingIgnoreCaseAndDiscontinuedFalse(keyword);
    }

    // 保存商品（初始化数据用）
    public Product saveProduct(Product product) {
        // 确保 discontinued 字段不为 null
        if (product.getDiscontinued() == null) {
            product.setDiscontinued(false);
        }
        return productRepository.save(product);
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
            if ("待处理".equals(order.getStatus()) || "已取消".equals(order.getStatus())) {
                order.setStatus("已取消");
                orderRepository.save(order);
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
                } else if (hasAvailable) {
                    // 部分商品下架
                    order.setStatus("部分商品下架");
                    orderRepository.save(order);
                }
            }
        }
    }
}