package com.grocky.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class CustomerDTO {
    
    private UUID id;
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    private String phone;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    
    @Builder.Default
    private Integer loyaltyPoints = 0;
    
    private String aiPreferenceProfile;
    
    @Builder.Default
    private Boolean isActive = true;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Statistics fields (computed)
    private Long totalOrders;
    private BigDecimal totalSpent;
    private BigDecimal averageOrderValue;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerUpdate {
        private String name;
        private String phone;
        private String address;
        private String city;
        private String state;
        private String zipCode;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoyaltyUpdate {
        @DecimalMin(value = "0", message = "Points must be non-negative")
        private Integer pointsToAdd;
    }
}
