package com.garments.shop.productservice.service;

import com.garments.shop.productservice.dto.ProductFilter;
import com.garments.shop.productservice.dto.ProductRequest;
import com.garments.shop.productservice.dto.ProductResponse;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);
    
    @CacheEvict(value = {"products", "product"}, allEntries = true)
    ProductResponse updateProduct(UUID id, ProductRequest request);
    
    @Cacheable(value = "product", key = "#id")
    ProductResponse getProduct(UUID id);
    
    @Cacheable(value = "products", key = "'all:' + #includeInactive")
    Page<ProductResponse> getAllProducts(Boolean includeInactive, Pageable pageable);
    
    @Cacheable(value = "products", key = "'category:' + #category")
    Page<ProductResponse> getProductsByCategory(String category, Pageable pageable);
    
    @Cacheable(value = "products", key = "'brand:' + #brand")
    Page<ProductResponse> getProductsByBrand(String brand, Pageable pageable);
    
    @CacheEvict(value = {"products", "product"}, allEntries = true)
    void deleteProduct(UUID id);
    
    ProductResponse incrementViewCount(UUID id);
    
    @CacheEvict(value = {"products", "product"}, key = "#id")
    void updateProductImages(UUID id, List<String> imageUrls);
    
    @Cacheable(value = "products", key = "'filter:' + #filter.toString() + ':page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize")
    Page<ProductResponse> searchProducts(ProductFilter filter, Pageable pageable);
    
    void validateProductExists(UUID id);
    
    void validateStockAvailability(UUID id, int quantity);
}
