package com.garments.shop.productservice.specification;

import com.garments.shop.productservice.dto.ProductFilter;
import com.garments.shop.productservice.entity.Product;
import com.garments.shop.productservice.enums.Gender;
import com.garments.shop.productservice.enums.Season;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecifications {

    public static Specification<Product> withDynamicFilter(ProductFilter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always include only active products unless explicitly specified
            if (!Boolean.TRUE.equals(filter.getIncludeInactive())) {
                predicates.add(criteriaBuilder.isTrue(root.get("isActive")));
            }

            // Add filters based on the provided criteria
            if (filter.getMinPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("price"), filter.getMinPrice()));
            }

            if (filter.getMaxPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("price"), filter.getMaxPrice()));
            }

            if (filter.getGender() != null) {
                predicates.add(criteriaBuilder.equal(
                    root.get("gender"), filter.getGender()));
            }

            if (filter.getSeason() != null) {
                predicates.add(criteriaBuilder.equal(
                    root.get("season"), filter.getSeason()));
            }

            if (filter.getBrand() != null && !filter.getBrand().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                    root.get("brand"), filter.getBrand()));
            }

            if (filter.getCategory() != null && !filter.getCategory().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                    root.get("category"), filter.getCategory()));
            }

            if (filter.getSearchTerm() != null && !filter.getSearchTerm().isEmpty()) {
                String searchTermLike = "%" + filter.getSearchTerm().toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchTermLike),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchTermLike)
                ));
            }

            if (filter.getInStock() != null && filter.getInStock()) {
                predicates.add(criteriaBuilder.greaterThan(
                    root.get("stockQuantity"), 0));
            }

            if (filter.getHasDiscount() != null && filter.getHasDiscount()) {
                predicates.add(criteriaBuilder.isNotNull(root.get("discountPercentage")));
                predicates.add(criteriaBuilder.greaterThan(
                    root.get("discountPercentage"), BigDecimal.ZERO));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
