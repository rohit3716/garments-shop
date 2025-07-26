package com.garments.shop.productservice.dto;

import com.garments.shop.productservice.enums.Gender;
import com.garments.shop.productservice.enums.Season;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private Set<String> imageUrls;
    private String category;
    private Set<String> availableSizes;
    private Set<String> availableColors;
    private String brand;
    private String material;
    private Gender gender;
    private Season season;
    private String sku;
    private BigDecimal discountPercentage;
    private int viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
