package com.grocky.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentDTO {
    
    private UUID id;
    
    private UUID orderId;
    private String orderNumber;
    
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
    
    private String paymentGateway;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @Builder.Default
    private String status = "PENDING";
    
    private String transactionId;
    private String gatewayResponse;
    
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatePayment {
        @NotNull(message = "Order ID is required")
        private UUID orderId;
        
        @NotBlank(message = "Payment method is required")
        private String paymentMethod;
        
        private String paymentGateway;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdatePaymentStatus {
        @NotBlank(message = "Status is required")
        private String status;
        
        private String transactionId;
        private String gatewayResponse;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentResponse {
        private UUID id;
        private UUID orderId;
        private String orderNumber;
        private String paymentMethod;
        private BigDecimal amount;
        private String status;
        private String transactionId;
        private LocalDateTime processedAt;
    }
}
