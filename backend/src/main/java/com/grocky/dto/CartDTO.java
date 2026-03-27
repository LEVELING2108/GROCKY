package com.grocky.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartDTO {
    
    private UUID id;
    private UUID customerId;
    private String customerName;
    
    private List<CartItemDTO> items;
    
    private Integer totalItems;
    private BigDecimal subtotal;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemDTO {
        private UUID id;
        private UUID productId;
        private String productName;
        private String productImage;
        private BigDecimal productPrice;
        private Integer quantity;
        private BigDecimal totalPrice;
        private Boolean isAvailable;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddToCartRequest {
        @NotNull(message = "Product ID is required")
        private UUID productId;

        @Min(value = 1, message = "Quantity must be at least 1")
        @Builder.Default
        private Integer quantity = 1;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateQuantityRequest {
        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartResponse {
        private CartDTO cart;
        private String message;
    }
}
