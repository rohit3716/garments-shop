package com.garments.shop.productservice.controller;

import com.garments.shop.productservice.dto.ApiResponse;
import com.garments.shop.productservice.dto.ProductFilter;
import com.garments.shop.productservice.dto.ProductRequest;
import com.garments.shop.productservice.dto.ProductResponse;
import com.garments.shop.productservice.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "APIs for managing products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @Operation(summary = "Create a new product")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductRequest request) {
        log.info("Received request to create product: {}", request.getName());
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Product created successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing product")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody ProductRequest request) {
        log.info("Received request to update product with ID: {}", id);
        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Product updated successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a product by ID")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(@PathVariable UUID id) {
        log.debug("Received request to get product with ID: {}", id);
        ProductResponse response = productService.getProduct(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Get all products with pagination")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getAllProducts(
            @RequestParam(required = false, defaultValue = "false") Boolean includeInactive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        log.debug("Received request to get all products, includeInactive: {}, page: {}, size: {}", 
                includeInactive, page, size);
                
        Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<ProductResponse> response = productService.getAllProducts(includeInactive, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    @Operation(summary = "Search products with filters and pagination")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> searchProducts(
            @Valid ProductFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        log.debug("Received request to search products with filter: {}, page: {}, size: {}", 
                filter, page, size);
                
        Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<ProductResponse> response = productService.searchProducts(filter, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get products by category with pagination")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProductsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.debug("Received request to get products by category: {}, page: {}", category, page);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> response = productService.getProductsByCategory(category, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/brand/{brand}")
    @Operation(summary = "Get products by brand with pagination")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProductsByBrand(
            @PathVariable String brand,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.debug("Received request to get products by brand: {}, page: {}", brand, page);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> response = productService.getProductsByBrand(brand, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable UUID id) {
        log.info("Received request to delete product with ID: {}", id);
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Product deleted successfully"));
    }

    @PostMapping("/{id}/view")
    @Operation(summary = "Increment product view count")
    public ResponseEntity<ApiResponse<ProductResponse>> incrementViewCount(@PathVariable UUID id) {
        log.debug("Received request to increment view count for product with ID: {}", id);
        ProductResponse response = productService.incrementViewCount(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}/images")
    @Operation(summary = "Update product images")
    public ResponseEntity<ApiResponse<Void>> updateProductImages(
            @PathVariable UUID id,
            @RequestBody List<String> imageUrls) {
        log.info("Received request to update images for product with ID: {}", id);
        productService.updateProductImages(id, imageUrls);
        return ResponseEntity.ok(ApiResponse.success(null, "Product images updated successfully"));
    }
}
