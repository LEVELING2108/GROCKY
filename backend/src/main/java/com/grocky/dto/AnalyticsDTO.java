package com.grocky.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnalyticsDTO {
    
    private UUID id;
    private String metricType;
    private String metricName;
    private BigDecimal metricValue;
    private Map<String, Object> metadata;
    private LocalDate recordedDate;
    private LocalDateTime createdAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardMetrics {
        private BigDecimal totalRevenue;
        private Long totalOrders;
        private Long totalCustomers;
        private Long totalProducts;
        private BigDecimal averageOrderValue;
        private Long pendingOrders;
        private Long lowStockProducts;
        private Map<String, Object> salesTrend;
        private Map<String, Object> ordersTrend;
        private List<TopProduct> topProducts;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopProduct {
        private UUID productId;
        private String productName;
        private Long quantitySold;
        private BigDecimal revenue;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalesReport {
        private LocalDate date;
        private BigDecimal revenue;
        private Long orders;
        private Long customers;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryReport {
        private UUID productId;
        private String productName;
        private String category;
        private Integer currentStock;
        private Integer reorderLevel;
        private Boolean needsReorder;
        private BigDecimal aiDemandScore;
        private String aiReorderSuggestion;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AIInsights {
        private List<ProductInsight> demandPredictions;
        private List<ReorderSuggestion> reorderSuggestions;
        private List<CustomerInsight> customerInsights;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductInsight {
        private UUID productId;
        private String productName;
        private BigDecimal predictedDemand;
        private String confidence;
        private String recommendation;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReorderSuggestion {
        private UUID productId;
        private String productName;
        private Integer currentStock;
        private Integer suggestedQuantity;
        private String reason;
        private BigDecimal priority;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerInsight {
        private UUID customerId;
        private String customerName;
        private String segment;
        private BigDecimal predictedLifetimeValue;
        private List<String> recommendedProducts;
    }
}
