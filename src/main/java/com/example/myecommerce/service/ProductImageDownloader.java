package com.example.myecommerce.service;

import java.io.IOException;

@FunctionalInterface
public interface ProductImageDownloader {
    DownloadedProductImage download(String imageUrl) throws IOException, InterruptedException;
}
