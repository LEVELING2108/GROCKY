package com.grocky.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDTO {
    
    private UUID id;
    
    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 200, message = "Name must be between 2 and 200 characters")
    private String name;
    
    private String description;
    
    @NotBlank(message = "Category is required")
    private String category;
    
    private String subcategory;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;
    
    @DecimalMin(value = "0.01", message = "Cost price must be greater than 0")
    private BigDecimal costPrice;
    
    @Min(value = 0, message = "Stock quantity cannot be negative")
    @Builder.Default
    private Integer stockQuantity = 0;
    
    @Min(value = 0, message = "Reorder level cannot be negative")
    @Builder.Default
    private Integer reorderLevel = 10;
    
    private String unit;
    private String brand;
    private String supplier;
    
    private LocalDate expiryDate;
    private String imageUrl;
    
    @Builder.Default
    private Boolean isAvailable = true;
    
    @DecimalMin(value = "0", message = "Discount must be non-negative")
    @Builder.Default
    private BigDecimal discountPercentage = BigDecimal.ZERO;
    
    @Builder.Default
    private BigDecimal aiDemandScore = BigDecimal.ZERO;
    
    @Builder.Default
    private Boolean aiReorderSuggestion = false;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Computed fields
    private BigDecimal finalPrice;
    private Double averageRating;
    private Long reviewCount;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateProductRequest {
        @NotBlank(message = "Product name is required")
        private String name;
        private String description;
        @NotBlank(message = "Category is required")
        private String category;
        private String subcategory;
        @NotNull(message = "Price is required")
        private BigDecimal price;
        private BigDecimal costPrice;
        @Builder.Default
        private Integer stockQuantity = 0;
        @Builder.Default
        private Integer reorderLevel = 10;
        private String unit = "piece";
        private String brand;
        private String supplier;
        private LocalDate expiryDate;
        private String imageUrl;
        @Builder.Default
        private Boolean isAvailable = true;
        @Builder.Default
        private BigDecimal discountPercentage = BigDecimal.ZERO;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProductRequest {
        private String name;
        private String description;
        private String category;
        private String subcategory;
        private BigDecimal price;
        private BigDecimal costPrice;
        private Integer stockQuantity;
        private Integer reorderLevel;
        private String unit;
        private String brand;
        private String supplier;
        private LocalDate expiryDate;
        private String imageUrl;
        private Boolean isAvailable;
        private BigDecimal discountPercentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductUpdate {
        private String name;
        private String description;
        private String category;
        private String subcategory;
        private BigDecimal price;
        private BigDecimal costPrice;
        private Integer stockQuantity;
        private Integer reorderLevel;
        private String unit;
        private String brand;
        private String supplier;
        private LocalDate expiryDate;
        private String imageUrl;
        private Boolean isAvailable;
        private BigDecimal discountPercentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockUpdate {
        @NotNull(message = "Quantity change is required")
        private Integer quantityChange;

        @NotBlank(message = "Change type is required")
        private String changeType;

        private String reason;
    }
}
