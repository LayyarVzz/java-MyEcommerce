package com.example.myecommerce.service;

import com.example.myecommerce.entity.Product;
import com.example.myecommerce.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductImageLocalizationServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void downloadsRemoteProductImageAndStoresLocalUrl() throws Exception {
        Product product = product(42L, "测试手机", "https://example.com/images/phone");
        ProductRepository productRepository = mock(ProductRepository.class);
        when(productRepository.findAll()).thenReturn(List.of(product));
        ProductImageDownloader downloader = url -> new DownloadedProductImage(new byte[]{1, 2, 3}, "image/png");
        ProductImageLocalizationService service = new ProductImageLocalizationService(
                productRepository,
                downloader,
                tempDir,
                "/upload/products"
        );

        service.localizeRemoteProductImages();

        assertThat(product.getImageUrl()).isEqualTo("/upload/products/product-42.png");
        assertThat(Files.readAllBytes(tempDir.resolve("product-42.png"))).containsExactly(1, 2, 3);
        verify(productRepository).save(product);
    }

    @Test
    void usesDefaultImageWhenRemoteDownloadFails() {
        Product product = product(9L, "断链商品", "https://example.com/missing.jpg");
        ProductRepository productRepository = mock(ProductRepository.class);
        when(productRepository.findAll()).thenReturn(List.of(product));
        ProductImageDownloader downloader = url -> {
            throw new IOException("not found");
        };
        ProductImageLocalizationService service = new ProductImageLocalizationService(
                productRepository,
                downloader,
                tempDir,
                "/upload/products"
        );

        service.localizeRemoteProductImages();

        assertThat(product.getImageUrl()).isEqualTo(ProductImageLocalizationService.DEFAULT_IMAGE_URL);
        assertThat(tempDir).isEmptyDirectory();
        verify(productRepository).save(product);
    }

    private Product product(Long id, String name, String imageUrl) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setDescription("商品描述");
        product.setPrice(BigDecimal.TEN);
        product.setImageUrl(imageUrl);
        product.setStock(10);
        product.setDiscontinued(false);
        return product;
    }
}
