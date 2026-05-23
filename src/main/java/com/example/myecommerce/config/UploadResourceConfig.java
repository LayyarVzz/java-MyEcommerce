package com.example.myecommerce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class UploadResourceConfig implements WebMvcConfigurer {
    private final String uploadRoot;

    public UploadResourceConfig(@Value("${app.product-images.upload-root:upload/}") String uploadRoot) {
        this.uploadRoot = uploadRoot;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**")
                .addResourceLocations(toFileResourceLocation(uploadRoot), "classpath:/static/upload/");
    }

    private String toFileResourceLocation(String path) {
        String normalizedPath = path == null || path.isBlank() ? "upload/" : path.trim().replace("\\", "/");
        if (!normalizedPath.endsWith("/")) {
            normalizedPath += "/";
        }
        return "file:" + normalizedPath;
    }
}
