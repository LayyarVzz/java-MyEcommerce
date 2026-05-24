package com.example.myecommerce.service;

import com.example.myecommerce.entity.Product;
import com.example.myecommerce.repository.ProductRepository;
import org.springframework.core.io.ClassPathResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class ProductImageLocalizationService {
    public static final String DEFAULT_IMAGE_URL = "/upload/default.png";

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductImageLocalizationService.class);
    private static final List<String> REAL_IMAGE_EXTENSIONS = List.of("jpg", "jpeg", "png", "webp", "gif");

    private final ProductRepository productRepository;
    private final ProductImageDownloader productImageDownloader;
    private final Path storageDirectory;
    private final Path uploadRootDirectory;
    private final String publicPath;
    private final String uploadRootPublicPath;

    @Autowired
    public ProductImageLocalizationService(
            ProductRepository productRepository,
            ProductImageDownloader productImageDownloader,
            @Value("${app.product-images.storage-path:upload/products}") String storagePath,
            @Value("${app.product-images.public-path:/upload/products}") String publicPath,
            @Value("${app.product-images.upload-root:upload/}") String uploadRoot
    ) {
        this(productRepository, productImageDownloader, Path.of(storagePath), publicPath, Path.of(uploadRoot));
    }

    ProductImageLocalizationService(
            ProductRepository productRepository,
            ProductImageDownloader productImageDownloader,
            Path storageDirectory,
            String publicPath
    ) {
        this(
                productRepository,
                productImageDownloader,
                storageDirectory,
                publicPath,
                defaultUploadRootDirectory(storageDirectory)
        );
    }

    ProductImageLocalizationService(
            ProductRepository productRepository,
            ProductImageDownloader productImageDownloader,
            Path storageDirectory,
            String publicPath,
            Path uploadRootDirectory
    ) {
        this.productRepository = productRepository;
        this.productImageDownloader = productImageDownloader;
        this.storageDirectory = storageDirectory;
        this.publicPath = trimTrailingSlash(publicPath);
        this.uploadRootDirectory = uploadRootDirectory == null ? defaultUploadRootDirectory(storageDirectory) : uploadRootDirectory;
        this.uploadRootPublicPath = parentPublicPath(this.publicPath);
    }

    public void localizeRemoteProductImages() {
        for (Product product : productRepository.findAll()) {
            String imageUrl = product.getImageUrl();
            Optional<String> preferredLocalImageUrl = findExistingLocalProductImage(product);
            if (!isRemoteUrl(imageUrl)) {
                if (shouldRepairDefaultImage(imageUrl)) {
                    String repairedImageUrl = preferredLocalImageUrl.orElseGet(() -> createPlaceholderImage(product));
                    if (!repairedImageUrl.equals(imageUrl)) {
                        product.setImageUrl(repairedImageUrl);
                        productRepository.save(product);
                    }
                } else if (isManagedLocalProductImage(imageUrl)) {
                    if (shouldReplaceLocalImageUrl(imageUrl, preferredLocalImageUrl)) {
                        product.setImageUrl(preferredLocalImageUrl.orElseGet(() -> createPlaceholderImage(product)));
                        productRepository.save(product);
                    }
                }
                continue;
            }

            if (preferredLocalImageUrl.isPresent()) {
                product.setImageUrl(preferredLocalImageUrl.get());
                productRepository.save(product);
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
            return fallbackImageFor(product);
        } catch (Exception e) {
            LOGGER.warn("下载商品图片失败，商品ID：{}，图片地址：{}，原因：{}", product.getId(), imageUrl, e.getMessage());
            return fallbackImageFor(product);
        }
    }

    private String buildFileName(Product product, String imageUrl, String contentType) {
        return buildFileStem(product) + "." + resolveExtension(imageUrl, contentType);
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

    private String fallbackImageFor(Product product) {
        return findExistingLocalProductImage(product).orElseGet(() -> createPlaceholderImage(product));
    }

    private Optional<String> findExistingLocalProductImage(Product product) {
        String fileStem = buildFileStem(product);
        Optional<String> uploadRootImage = findUploadRootImageUrl(fileStem, REAL_IMAGE_EXTENSIONS);
        if (uploadRootImage.isPresent()) {
            return uploadRootImage;
        }
        Optional<String> productStorageImage = findProductStorageImageUrl(fileStem, REAL_IMAGE_EXTENSIONS);
        if (productStorageImage.isPresent()) {
            return productStorageImage;
        }
        uploadRootImage = findUploadRootImageUrl(fileStem, List.of("svg"));
        if (uploadRootImage.isPresent()) {
            return uploadRootImage;
        }
        return findProductStorageImageUrl(fileStem, List.of("svg"));
    }

    private boolean localProductImageExists(String imageUrl) {
        String normalizedUrl = imageUrl == null ? "" : imageUrl.trim();
        if (normalizedUrl.startsWith(publicPath + "/")) {
            return fileNameFromPublicUrl(normalizedUrl, publicPath)
                    .map(this::productStorageImageFileExists)
                    .orElse(false);
        }
        if (normalizedUrl.startsWith(uploadRootPublicPath + "/")) {
            return fileNameFromPublicUrl(normalizedUrl, uploadRootPublicPath)
                    .map(this::uploadRootImageFileExists)
                    .orElse(false);
        }
        return false;
    }

    private Optional<String> findUploadRootImageUrl(String fileStem, List<String> extensions) {
        for (String extension : extensions) {
            String fileName = fileStem + "." + extension;
            if (uploadRootImageFileExists(fileName)) {
                return Optional.of(uploadRootPublicPath + "/" + fileName);
            }
        }
        return Optional.empty();
    }

    private Optional<String> findProductStorageImageUrl(String fileStem, List<String> extensions) {
        for (String extension : extensions) {
            String fileName = fileStem + "." + extension;
            if (productStorageImageFileExists(fileName)) {
                return Optional.of(publicPath + "/" + fileName);
            }
        }
        return Optional.empty();
    }

    private boolean productStorageImageFileExists(String fileName) {
        return Files.isRegularFile(storageDirectory.resolve(fileName))
                || new ClassPathResource("static/upload/products/" + fileName).exists();
    }

    private boolean uploadRootImageFileExists(String fileName) {
        return Files.isRegularFile(uploadRootDirectory.resolve(fileName))
                || new ClassPathResource("static/upload/" + fileName).exists();
    }

    private Optional<String> fileNameFromPublicUrl(String normalizedUrl, String basePublicPath) {
        String fileName = normalizedUrl.substring(basePublicPath.length() + 1);
        if (fileName.isBlank() || fileName.contains("/") || fileName.contains("\\")) {
            return Optional.empty();
        }
        return Optional.of(fileName);
    }

    private String createPlaceholderImage(Product product) {
        String fileName = buildFileStem(product) + ".svg";
        try {
            Files.createDirectories(storageDirectory);
            Files.writeString(storageDirectory.resolve(fileName), buildPlaceholderSvg(product), StandardCharsets.UTF_8);
            return publicPath + "/" + fileName;
        } catch (IOException e) {
            LOGGER.warn("生成商品占位图失败，商品ID：{}，原因：{}", product.getId(), e.getMessage());
            return DEFAULT_IMAGE_URL;
        }
    }

    private String buildPlaceholderSvg(Product product) {
        String name = escapeXml(product.getName() == null || product.getName().isBlank() ? "精选商品" : product.getName().trim());
        String category = escapeXml(product.getCategory() == null || product.getCategory().isBlank() ? Product.DEFAULT_CATEGORY : product.getCategory().trim());
        return """
                <svg xmlns="http://www.w3.org/2000/svg" width="900" height="700" viewBox="0 0 900 700" role="img" aria-label="%s">
                  <defs>
                    <linearGradient id="bg" x1="0" x2="1" y1="0" y2="1">
                      <stop offset="0" stop-color="#fffaf1"/>
                      <stop offset="0.55" stop-color="#efe2d0"/>
                      <stop offset="1" stop-color="#dfe9df"/>
                    </linearGradient>
                    <linearGradient id="ink" x1="0" x2="1" y1="0" y2="1">
                      <stop offset="0" stop-color="#173b2c"/>
                      <stop offset="1" stop-color="#b46a3c"/>
                    </linearGradient>
                  </defs>
                  <rect width="900" height="700" fill="url(#bg)"/>
                  <path d="M0 510 C150 455 260 610 420 540 C590 465 700 505 900 420 L900 700 L0 700 Z" fill="#173b2c" opacity="0.12"/>
                  <g fill="none" stroke="#173b2c" stroke-opacity="0.08" stroke-width="2">
                    <path d="M90 90 H810 M90 170 H810 M90 250 H810 M90 330 H810 M90 410 H810 M90 490 H810 M90 570 H810"/>
                    <path d="M130 60 V640 M250 60 V640 M370 60 V640 M490 60 V640 M610 60 V640 M730 60 V640"/>
                  </g>
                  <rect x="92" y="96" width="716" height="508" rx="28" fill="#fffaf1" fill-opacity="0.72" stroke="#173b2c" stroke-opacity="0.12"/>
                  <circle cx="450" cy="245" r="78" fill="url(#ink)" opacity="0.18"/>
                  <path d="M388 262 L433 205 L476 252 L505 222 L565 302 H335 Z" fill="#173b2c" opacity="0.52"/>
                  <text x="450" y="394" fill="#173b2c" font-size="62" font-weight="800" text-anchor="middle" font-family="'Microsoft YaHei', 'Noto Serif SC', serif">%s</text>
                  <text x="450" y="456" fill="#b46a3c" font-size="28" font-weight="700" text-anchor="middle" font-family="'Microsoft YaHei', sans-serif">%s</text>
                </svg>
                """.formatted(name, name, category);
    }

    private String escapeXml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private boolean isRemoteUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return false;
        }
        String normalizedUrl = imageUrl.trim().toLowerCase(Locale.ROOT);
        return normalizedUrl.startsWith("http://") || normalizedUrl.startsWith("https://");
    }

    private boolean shouldRepairDefaultImage(String imageUrl) {
        return imageUrl == null || imageUrl.isBlank() || DEFAULT_IMAGE_URL.equals(imageUrl.trim());
    }

    private boolean shouldReplaceLocalImageUrl(String imageUrl, Optional<String> preferredLocalImageUrl) {
        if (!localProductImageExists(imageUrl)) {
            return true;
        }
        return preferredLocalImageUrl.isPresent() && !preferredLocalImageUrl.get().equals(imageUrl.trim());
    }

    private boolean isManagedLocalProductImage(String imageUrl) {
        if (imageUrl == null) {
            return false;
        }
        String normalizedUrl = imageUrl.trim();
        return normalizedUrl.startsWith(publicPath + "/") || normalizedUrl.startsWith(uploadRootPublicPath + "/");
    }

    private String buildFileStem(Product product) {
        String name = product.getName() == null ? "product" : product.getName();
        long productId = Optional.ofNullable(product.getId()).orElse(Math.abs((long) name.hashCode()));
        return "product-" + productId;
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

    private static Path defaultUploadRootDirectory(Path storageDirectory) {
        return storageDirectory.getParent() == null ? storageDirectory : storageDirectory.getParent();
    }

    private String parentPublicPath(String value) {
        int slashIndex = value.lastIndexOf('/');
        if (slashIndex <= 0) {
            return value;
        }
        return value.substring(0, slashIndex);
    }
}
