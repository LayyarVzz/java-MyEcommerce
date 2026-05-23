package com.example.myecommerce.service;

import com.example.myecommerce.entity.Product;
import com.example.myecommerce.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;

@Service
public class ProductImageLocalizationService {
    public static final String DEFAULT_IMAGE_URL = "/upload/default.png";

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductImageLocalizationService.class);

    private final ProductRepository productRepository;
    private final ProductImageDownloader productImageDownloader;
    private final Path storageDirectory;
    private final String publicPath;

    @Autowired
    public ProductImageLocalizationService(
            ProductRepository productRepository,
            ProductImageDownloader productImageDownloader,
            @Value("${app.product-images.storage-path:upload/products}") String storagePath,
            @Value("${app.product-images.public-path:/upload/products}") String publicPath
    ) {
        this(productRepository, productImageDownloader, Path.of(storagePath), publicPath);
    }

    ProductImageLocalizationService(
            ProductRepository productRepository,
            ProductImageDownloader productImageDownloader,
            Path storageDirectory,
            String publicPath
    ) {
        this.productRepository = productRepository;
        this.productImageDownloader = productImageDownloader;
        this.storageDirectory = storageDirectory;
        this.publicPath = trimTrailingSlash(publicPath);
    }

    public void localizeRemoteProductImages() {
        for (Product product : productRepository.findAll()) {
            String imageUrl = product.getImageUrl();
            if (!isRemoteUrl(imageUrl)) {
                continue;
            }

            product.setImageUrl(localizeImage(product, imageUrl));
            productRepository.save(product);
        }
    }

    private String localizeImage(Product product, String imageUrl) {
        try {
            DownloadedProductImage image = productImageDownloader.download(imageUrl);
            Files.createDirectories(storageDirectory);
            String fileName = buildFileName(product, imageUrl, image.contentType());
            Files.write(storageDirectory.resolve(fileName), image.bytes());
            return publicPath + "/" + fileName;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.warn("下载商品图片被中断，商品ID：{}，图片地址：{}", product.getId(), imageUrl);
            return DEFAULT_IMAGE_URL;
        } catch (Exception e) {
            LOGGER.warn("下载商品图片失败，商品ID：{}，图片地址：{}，原因：{}", product.getId(), imageUrl, e.getMessage());
            return DEFAULT_IMAGE_URL;
        }
    }

    private String buildFileName(Product product, String imageUrl, String contentType) {
        long productId = Optional.ofNullable(product.getId()).orElse(Math.abs((long) product.getName().hashCode()));
        return "product-" + productId + "." + resolveExtension(imageUrl, contentType);
    }

    private String resolveExtension(String imageUrl, String contentType) {
        String normalizedContentType = contentType == null
                ? ""
                : contentType.toLowerCase(Locale.ROOT).split(";", 2)[0].trim();
        return switch (normalizedContentType) {
            case "image/jpeg", "image/jpg" -> "jpg";
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            case "image/gif" -> "gif";
            default -> extensionFromUrl(imageUrl).orElse("jpg");
        };
    }

    private Optional<String> extensionFromUrl(String imageUrl) {
        try {
            String path = URI.create(imageUrl).getPath();
            int dotIndex = path.lastIndexOf('.');
            if (dotIndex < 0 || dotIndex == path.length() - 1) {
                return Optional.empty();
            }
            String extension = path.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
            return switch (extension) {
                case "jpg", "jpeg", "png", "webp", "gif" -> Optional.of("jpeg".equals(extension) ? "jpg" : extension);
                default -> Optional.empty();
            };
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private boolean isRemoteUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return false;
        }
        String normalizedUrl = imageUrl.trim().toLowerCase(Locale.ROOT);
        return normalizedUrl.startsWith("http://") || normalizedUrl.startsWith("https://");
    }

    private String trimTrailingSlash(String value) {
        if (value == null || value.isBlank()) {
            return "/upload/products";
        }
        String trimmed = value.trim();
        while (trimmed.endsWith("/") && trimmed.length() > 1) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }
}
