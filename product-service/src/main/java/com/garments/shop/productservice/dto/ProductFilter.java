package com.garments.shop.productservice.dto;

import com.garments.shop.productservice.enums.Gender;
import com.garments.shop.productservice.enums.Season;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilter {
    private String searchTerm;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String category;
    private String brand;
    private Gender gender;
    private Season season;
    private Boolean inStock;
    private Boolean hasDiscount;
    private Boolean includeInactive;
}
