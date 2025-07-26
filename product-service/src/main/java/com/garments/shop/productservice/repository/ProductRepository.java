package com.garments.shop.productservice.repository;

import com.garments.shop.productservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    List<Product> findByIsActiveTrue();
    List<Product> findByCategoryAndIsActiveTrue(String category);
    List<Product> findByBrandAndIsActiveTrue(String brand);
    boolean existsBySkuAndIsActiveTrue(String sku);
}
