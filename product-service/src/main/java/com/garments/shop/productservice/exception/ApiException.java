package com.garments.shop.productservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final String code;

    public ApiException(String message, HttpStatus status, String code) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public ApiException(String message, HttpStatus status) {
        this(message, status, status.name());
    }
}
