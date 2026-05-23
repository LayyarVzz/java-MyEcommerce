package com.example.myecommerce.service;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app.product-images", name = "localize-on-startup", havingValue = "true", matchIfMissing = true)
public class ProductImageInitializationRunner implements ApplicationRunner {
    private final ProductImageLocalizationService productImageLocalizationService;

    public ProductImageInitializationRunner(ProductImageLocalizationService productImageLocalizationService) {
        this.productImageLocalizationService = productImageLocalizationService;
    }

    @Override
    public void run(ApplicationArguments args) {
        productImageLocalizationService.localizeRemoteProductImages();
    }
}
