package com.garments.shop.productservice.entity;

import com.garments.shop.productservice.enums.Gender;
import com.garments.shop.productservice.enums.Season;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @NotBlank(message = "Product name is required")
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @NotNull(message = "Product price is required")
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    @Column(nullable = false)
    private BigDecimal price;
    
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity must be greater than or equal to 0")
    @Column(nullable = false)
    private Integer stockQuantity;
    
    @ElementCollection
    @CollectionTable(name = "product_images", 
                    joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    @Builder.Default
    private Set<String> imageUrls = new HashSet<>();
    
    @NotBlank(message = "Category is required")
    @Column(nullable = false)
    private String category;
    
    @ElementCollection
    @CollectionTable(name = "product_sizes", 
                    joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "size")
    @Builder.Default
    private Set<String> availableSizes = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "product_colors", 
                    joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "color")
    @Builder.Default
    private Set<String> availableColors = new HashSet<>();
    
    @Column(nullable = false)
    private String brand;
    
    private String material;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;
    
    @Enumerated(EnumType.STRING)
    private Season season;
    
    @Column(nullable = false)
    @Builder.Default
    private boolean isActive = true;
    
    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;
    
    private BigDecimal discountPercentage;
    
    @Column(nullable = false)
    @Builder.Default
    private int viewCount = 0;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    private LocalDateTime deletedAt;
    
    @Version
    private Long version;  // For optimistic locking
    
    private String sku;  // Stock Keeping Unit
    
    @PrePersist
    protected void onCreate() {
        if (sku == null) {
            // Generate SKU based on category and UUID
            String uuid = UUID.randomUUID().toString().substring(0, 8);
            this.sku = (category != null ? category.substring(0, Math.min(3, category.length())) : "XXX") 
                      + "-" + uuid.toUpperCase();
        }
    }
}
