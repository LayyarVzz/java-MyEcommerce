package com.example.myecommerce.entity;

public enum ProductCommentRating {
    GOOD("好评"),
    NEUTRAL("中评"),
    BAD("差评");

    private final String label;

    ProductCommentRating(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
