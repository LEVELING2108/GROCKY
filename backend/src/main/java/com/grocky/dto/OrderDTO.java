package com.grocky.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
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
public class OrderDTO {
    
    private UUID id;
    
    @NotBlank(message = "Order number is required")
    private String orderNumber;
    
    private UUID customerId;
    private String customerName;
    private String customerEmail;
    
    @NotBlank(message = "Status is required")
    private String status;
    
    @NotNull(message = "Subtotal is required")
    @DecimalMin(value = "0.01", message = "Subtotal must be greater than 0")
    private BigDecimal subtotal;
    
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;
    
    @Builder.Default
    private BigDecimal deliveryFee = BigDecimal.ZERO;
    
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    @NotNull(message = "Total amount is required")
    private BigDecimal totalAmount;
    
    private String deliveryAddress;
    private String deliveryCity;
    private String deliveryState;
    private String deliveryZip;
    private String deliveryInstructions;
    
    private LocalDateTime scheduledDeliveryDate;
    private String aiPredictedDeliveryTime;
    
    @Builder.Default
    private String paymentStatus = "PENDING";
    
    private String notes;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deliveredAt;
    
    @Valid
    private List<OrderItemDTO> items;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDTO {
        private UUID id;
        private UUID productId;
        private String productName;
        private String productImage;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateOrder {
        @NotNull(message = "Customer ID is required")
        private UUID customerId;
        
        @NotEmpty(message = "Order must have at least one item")
        private List<OrderItemInput> items;
        
        private String deliveryAddress;
        private String deliveryCity;
        private String deliveryState;
        private String deliveryZip;
        private String deliveryInstructions;
        private LocalDateTime scheduledDeliveryDate;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemInput {
        @NotNull(message = "Product ID is required")
        private UUID productId;
        
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateStatus {
        @NotBlank(message = "Status is required")
        private String status;
        
        private String notes;
    }
}
