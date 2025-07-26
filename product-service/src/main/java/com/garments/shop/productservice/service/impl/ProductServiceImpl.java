package com.garments.shop.productservice.service.impl;

import com.garments.shop.productservice.aspect.TrackExecutionTime;
import com.garments.shop.productservice.dto.ProductFilter;
import com.garments.shop.productservice.dto.ProductRequest;
import com.garments.shop.productservice.dto.ProductResponse;
import com.garments.shop.productservice.entity.Product;
import com.garments.shop.productservice.exception.InsufficientStockException;
import com.garments.shop.productservice.exception.ResourceNotFoundException;
import com.garments.shop.productservice.repository.ProductRepository;
import com.garments.shop.productservice.service.ProductService;
import com.garments.shop.productservice.specification.ProductSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating new product with name: {}", request.getName());
        
        Product product = mapToEntity(request);
        Product savedProduct = productRepository.save(product);
        
        log.info("Product created successfully with ID: {}", savedProduct.getId());
        return mapToResponse(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(UUID id, ProductRequest request) {
        log.info("Updating product with ID: {}", id);
        
        Product product = getProductEntity(id);
        updateProductFromRequest(product, request);
        Product updatedProduct = productRepository.save(product);
        
        log.info("Product updated successfully with ID: {}", id);
        return mapToResponse(updatedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProduct(UUID id) {
        log.debug("Fetching product with ID: {}", id);
        return mapToResponse(getProductEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    @TrackExecutionTime
    public Page<ProductResponse> getAllProducts(Boolean includeInactive, Pageable pageable) {
        log.debug("Fetching all products, includeInactive: {}, page: {}", includeInactive, pageable);
        
        Page<Product> products = includeInactive ? 
            productRepository.findAll(pageable) : 
            productRepository.findAll(
                (root, query, cb) -> cb.isTrue(root.get("isActive")), 
                pageable
            );
            
        return products.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByCategory(String category, Pageable pageable) {
        log.debug("Fetching products by category: {}, page: {}", category, pageable);
        Page<Product> products = productRepository.findAll(
            (root, query, cb) -> cb.and(
                cb.equal(root.get("category"), category),
                cb.isTrue(root.get("isActive"))
            ),
            pageable
        );
        return products.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByBrand(String brand, Pageable pageable) {
        log.debug("Fetching products by brand: {}, page: {}", brand, pageable);
        Page<Product> products = productRepository.findAll(
            (root, query, cb) -> cb.and(
                cb.equal(root.get("brand"), brand),
                cb.isTrue(root.get("isActive"))
            ),
            pageable
        );
        return products.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @TrackExecutionTime
    public Page<ProductResponse> searchProducts(ProductFilter filter, Pageable pageable) {
        log.debug("Searching products with filter: {}, page: {}", filter, pageable);
        Page<Product> products = productRepository.findAll(
            ProductSpecifications.withDynamicFilter(filter),
            pageable
        );
        return products.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateProductExists(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product", "id", id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void validateStockAvailability(UUID id, int quantity) {
        Product product = getProductEntity(id);
        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException(
                product.getName(),
                quantity,
                product.getStockQuantity()
            );
        }
    }

    @Override
    @Transactional
    public void deleteProduct(UUID id) {
        log.info("Soft deleting product with ID: {}", id);
        
        Product product = getProductEntity(id);
        product.setDeleted(true);
        product.setActive(false);
        product.setDeletedAt(LocalDateTime.now());
        productRepository.save(product);
        
        log.info("Product soft deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional
    public ProductResponse incrementViewCount(UUID id) {
        log.debug("Incrementing view count for product with ID: {}", id);
        
        Product product = getProductEntity(id);
        product.setViewCount(product.getViewCount() + 1);
        return mapToResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public void updateProductImages(UUID id, List<String> imageUrls) {
        log.info("Updating images for product with ID: {}", id);
        
        Product product = getProductEntity(id);
        product.getImageUrls().clear();
        product.getImageUrls().addAll(imageUrls);
        productRepository.save(product);
        
        log.info("Product images updated successfully for ID: {}", id);
    }

    private Product getProductEntity(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    private Product mapToEntity(ProductRequest request) {
        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .imageUrls(request.getImageUrls())
                .category(request.getCategory())
                .availableSizes(request.getAvailableSizes())
                .availableColors(request.getAvailableColors())
                .brand(request.getBrand())
                .material(request.getMaterial())
                .gender(request.getGender())
                .season(request.getSeason())
                .discountPercentage(request.getDiscountPercentage())
                .isActive(true)
                .build();
    }

    private void updateProductFromRequest(Product product, ProductRequest request) {
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        if (request.getImageUrls() != null) {
            product.getImageUrls().clear();
            product.getImageUrls().addAll(request.getImageUrls());
        }
        product.setCategory(request.getCategory());
        if (request.getAvailableSizes() != null) {
            product.getAvailableSizes().clear();
            product.getAvailableSizes().addAll(request.getAvailableSizes());
        }
        if (request.getAvailableColors() != null) {
            product.getAvailableColors().clear();
            product.getAvailableColors().addAll(request.getAvailableColors());
        }
        product.setBrand(request.getBrand());
        product.setMaterial(request.getMaterial());
        product.setGender(request.getGender());
        product.setSeason(request.getSeason());
        product.setDiscountPercentage(request.getDiscountPercentage());
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .imageUrls(product.getImageUrls())
                .category(product.getCategory())
                .availableSizes(product.getAvailableSizes())
                .availableColors(product.getAvailableColors())
                .brand(product.getBrand())
                .material(product.getMaterial())
                .gender(product.getGender())
                .season(product.getSeason())
                .sku(product.getSku())
                .discountPercentage(product.getDiscountPercentage())
                .viewCount(product.getViewCount())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
