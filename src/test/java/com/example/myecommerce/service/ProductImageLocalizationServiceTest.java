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
import static org.mockito.Mockito.verifyNoInteractions;
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
    void usesExistingLocalProductImageWhenDatabaseHasDefaultImage() throws IOException {
        Product product = product(42L, "测试手机", ProductImageLocalizationService.DEFAULT_IMAGE_URL);
        ProductRepository productRepository = mock(ProductRepository.class);
        when(productRepository.findAll()).thenReturn(List.of(product));
        Files.write(tempDir.resolve("product-42.png"), new byte[]{4, 5, 6});
        ProductImageDownloader downloader = url -> {
            throw new IOException("should not download local fallback");
        };
        ProductImageLocalizationService service = new ProductImageLocalizationService(
                productRepository,
                downloader,
                tempDir,
                "/upload/products"
        );

        service.localizeRemoteProductImages();

        assertThat(product.getImageUrl()).isEqualTo("/upload/products/product-42.png");
        verify(productRepository).save(product);
    }

    @Test
    void usesRootUploadProductImageWithNonJpgExtension() throws IOException {
        Product product = product(42L, "测试手机", ProductImageLocalizationService.DEFAULT_IMAGE_URL);
        ProductRepository productRepository = mock(ProductRepository.class);
        when(productRepository.findAll()).thenReturn(List.of(product));
        Path productsDir = tempDir.resolve("products");
        Files.createDirectories(productsDir);
        Files.write(tempDir.resolve("product-42.webp"), new byte[]{7, 8, 9});
        ProductImageDownloader downloader = url -> {
            throw new IOException("should not download local fallback");
        };
        ProductImageLocalizationService service = new ProductImageLocalizationService(
                productRepository,
                downloader,
                productsDir,
                "/upload/products"
        );

        service.localizeRemoteProductImages();

        assertThat(product.getImageUrl()).isEqualTo("/upload/product-42.webp");
        verify(productRepository).save(product);
    }

    @Test
    void usesRootUploadProductImageBeforeRemoteDownload() throws IOException {
        Product product = product(42L, "测试手机", "https://example.com/images/phone.jpg");
        ProductRepository productRepository = mock(ProductRepository.class);
        when(productRepository.findAll()).thenReturn(List.of(product));
        Path productsDir = tempDir.resolve("products");
        Files.createDirectories(productsDir);
        Files.write(tempDir.resolve("product-42.webp"), new byte[]{7, 8, 9});
        ProductImageDownloader downloader = mock(ProductImageDownloader.class);
        ProductImageLocalizationService service = new ProductImageLocalizationService(
                productRepository,
                downloader,
                productsDir,
                "/upload/products"
        );

        service.localizeRemoteProductImages();

        assertThat(product.getImageUrl()).isEqualTo("/upload/product-42.webp");
        verify(productRepository).save(product);
        verifyNoInteractions(downloader);
    }

    @Test
    void prefersRootUploadImageOverProductsDirectoryImage() throws IOException {
        Product product = product(42L, "测试手机", "/upload/products/product-42.jpg");
        ProductRepository productRepository = mock(ProductRepository.class);
        when(productRepository.findAll()).thenReturn(List.of(product));
        Path productsDir = tempDir.resolve("products");
        Files.createDirectories(productsDir);
        Files.write(productsDir.resolve("product-42.jpg"), new byte[]{1, 2, 3});
        Files.write(tempDir.resolve("product-42.webp"), new byte[]{7, 8, 9});
        ProductImageDownloader downloader = url -> {
            throw new IOException("should not download local image");
        };
        ProductImageLocalizationService service = new ProductImageLocalizationService(
                productRepository,
                downloader,
                productsDir,
                "/upload/products"
        );

        service.localizeRemoteProductImages();

        assertThat(product.getImageUrl()).isEqualTo("/upload/product-42.webp");
        verify(productRepository).save(product);
    }

    @Test
    void usesConfiguredUploadRootDirectory() throws IOException {
        Product product = product(42L, "测试手机", ProductImageLocalizationService.DEFAULT_IMAGE_URL);
        ProductRepository productRepository = mock(ProductRepository.class);
        when(productRepository.findAll()).thenReturn(List.of(product));
        Path storageDir = tempDir.resolve("storage").resolve("products");
        Path uploadRootDir = tempDir.resolve("actual-upload");
        Files.createDirectories(storageDir);
        Files.createDirectories(uploadRootDir);
        Files.write(uploadRootDir.resolve("product-42.png"), new byte[]{1, 2, 3});
        ProductImageDownloader downloader = url -> {
            throw new IOException("should not download local image");
        };
        ProductImageLocalizationService service = new ProductImageLocalizationService(
                productRepository,
                downloader,
                storageDir,
                "/upload/products",
                uploadRootDir
        );

        service.localizeRemoteProductImages();

        assertThat(product.getImageUrl()).isEqualTo("/upload/product-42.png");
        verify(productRepository).save(product);
    }

    @Test
    void createsProductSpecificPlaceholderWhenRemoteDownloadFails() throws IOException {
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

        assertThat(product.getImageUrl()).isEqualTo("/upload/products/product-9.svg");
        assertThat(Files.readString(tempDir.resolve("product-9.svg"))).contains("断链商品");
        verify(productRepository).save(product);
    }

    @Test
    void repairsMissingManagedLocalProductImage() throws IOException {
        Product product = product(12L, "丢失本地图", "/upload/products/product-12.jpg");
        ProductRepository productRepository = mock(ProductRepository.class);
        when(productRepository.findAll()).thenReturn(List.of(product));
        ProductImageDownloader downloader = url -> {
            throw new IOException("should not download local image");
        };
        ProductImageLocalizationService service = new ProductImageLocalizationService(
                productRepository,
                downloader,
                tempDir,
                "/upload/products"
        );

        service.localizeRemoteProductImages();

        assertThat(product.getImageUrl()).isEqualTo("/upload/products/product-12.svg");
        assertThat(Files.readString(tempDir.resolve("product-12.svg"))).contains("丢失本地图");
        verify(productRepository).save(product);
    }

    @Test
    void replacesGeneratedPlaceholderWithRootUploadImage() throws IOException {
        Product product = product(12L, "已有真实图", "/upload/products/product-12.svg");
        ProductRepository productRepository = mock(ProductRepository.class);
        when(productRepository.findAll()).thenReturn(List.of(product));
        Path productsDir = tempDir.resolve("products");
        Files.createDirectories(productsDir);
        Files.writeString(productsDir.resolve("product-12.svg"), "<svg></svg>");
        Files.write(tempDir.resolve("product-12.png"), new byte[]{10, 11, 12});
        ProductImageDownloader downloader = url -> {
            throw new IOException("should not download local image");
        };
        ProductImageLocalizationService service = new ProductImageLocalizationService(
                productRepository,
                downloader,
                productsDir,
                "/upload/products"
        );

        service.localizeRemoteProductImages();

        assertThat(product.getImageUrl()).isEqualTo("/upload/product-12.png");
        verify(productRepository).save(product);
    }

    @Test
    void replacesGeneratedPlaceholderWithProductsDirectoryImage() throws IOException {
        Product product = product(12L, "已有真实图", "/upload/products/product-12.svg");
        ProductRepository productRepository = mock(ProductRepository.class);
        when(productRepository.findAll()).thenReturn(List.of(product));
        Path productsDir = tempDir.resolve("products");
        Files.createDirectories(productsDir);
        Files.writeString(productsDir.resolve("product-12.svg"), "<svg></svg>");
        Files.write(productsDir.resolve("product-12.webp"), new byte[]{10, 11, 12});
        ProductImageDownloader downloader = url -> {
            throw new IOException("should not download local image");
        };
        ProductImageLocalizationService service = new ProductImageLocalizationService(
                productRepository,
                downloader,
                productsDir,
                "/upload/products"
        );

        service.localizeRemoteProductImages();

        assertThat(product.getImageUrl()).isEqualTo("/upload/products/product-12.webp");
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
