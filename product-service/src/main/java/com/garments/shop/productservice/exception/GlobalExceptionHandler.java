package com.garments.shop.productservice.exception;

import com.garments.shop.productservice.dto.ApiResponse;
import com.garments.shop.productservice.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        
        return new ResponseEntity<>(ApiResponse.error(errorResponse), ex.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ErrorResponse.ValidationError> validationErrors = new ArrayList<>();
        
        ex.getBindingResult().getFieldErrors().forEach(error ->
            validationErrors.add(ErrorResponse.ValidationError.builder()
                    .field(error.getField())
                    .message(error.getDefaultMessage())
                    .build())
        );

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("VALIDATION_ERROR")
                .message("Validation failed")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .errors(validationErrors)
                .build();

        return new ResponseEntity<>(ApiResponse.error(errorResponse), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {
        List<ErrorResponse.ValidationError> validationErrors = new ArrayList<>();
        
        ex.getConstraintViolations().forEach(violation ->
            validationErrors.add(ErrorResponse.ValidationError.builder()
                    .field(violation.getPropertyPath().toString())
                    .message(violation.getMessage())
                    .build())
        );

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("VALIDATION_ERROR")
                .message("Validation failed")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .errors(validationErrors)
                .build();

        return new ResponseEntity<>(ApiResponse.error(errorResponse), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("DATA_INTEGRITY_ERROR")
                .message("Database constraint violation")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        log.error("Data integrity violation", ex);
        return new ResponseEntity<>(ApiResponse.error(errorResponse), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAllUncaughtException(
            Exception ex, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        log.error("Unexpected error", ex);
        return new ResponseEntity<>(ApiResponse.error(errorResponse), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
