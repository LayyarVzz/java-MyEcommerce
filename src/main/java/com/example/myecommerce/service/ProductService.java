package com.example.myecommerce.service;

import com.example.myecommerce.entity.Product;
import com.example.myecommerce.repository.CartItemRepository;
import com.example.myecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    public ProductService(ProductRepository productRepository, CartItemRepository cartItemRepository) {
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
    }

    // 查询所有商品
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // 根据ID查询商品
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("商品不存在：" + id));
    }

    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts();
        }
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    // 保存商品（初始化数据用）
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    // 删除商品及其相关的购物车项
    @Transactional
    public void deleteProduct(Long id) {
        // 先删除所有引用该商品的购物车项
        cartItemRepository.deleteByProduct_Id(id);
        // 再删除商品本身
        productRepository.deleteById(id);
    }
}
