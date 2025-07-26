package com.garments.shop.productservice.exception;

import org.springframework.http.HttpStatus;

public class InsufficientStockException extends ApiException {
    public InsufficientStockException(String productName, int requested, int available) {
        super(String.format("Insufficient stock for product '%s'. Requested: %d, Available: %d",
                productName, requested, available),
                HttpStatus.BAD_REQUEST,
                "INSUFFICIENT_STOCK");
    }
}
