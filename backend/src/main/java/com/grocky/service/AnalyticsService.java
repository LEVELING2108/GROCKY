package com.grocky.service;

import com.grocky.dto.AnalyticsDTO;
import com.grocky.entity.Analytics;
import com.grocky.entity.Order;
import com.grocky.entity.OrderStatus;
import com.grocky.entity.Product;
import com.grocky.repository.AnalyticsRepository;
import com.grocky.repository.CustomerRepository;
import com.grocky.repository.OrderItemRepository;
import com.grocky.repository.OrderRepository;
import com.grocky.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {
    
    private final AnalyticsRepository analyticsRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final com.grocky.repository.OrderItemRepository orderItemRepository;
    private final com.grocky.repository.CustomerRepository customerRepository;
    
    @Transactional(readOnly = true)
    public AnalyticsDTO.DashboardMetrics getDashboardMetrics() {
        log.debug("Fetching dashboard metrics");
        
        // Calculate metrics
        BigDecimal totalRevenue = calculateTotalRevenue();
        Long totalOrders = calculateTotalOrders();
        Long totalCustomers = calculateTotalCustomers();
        Long totalProducts = calculateTotalProducts();
        BigDecimal averageOrderValue = totalOrders > 0 ? 
                totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        Long pendingOrders = calculatePendingOrders();
        Long lowStockProducts = calculateLowStockProducts();
        
        // Get trends
        Map<String, Object> salesTrend = getSalesTrend(7);
        Map<String, Object> ordersTrend = getOrdersTrend(7);
        
        // Get top products
        List<AnalyticsDTO.TopProduct> topProducts = getTopSellingProducts(5);
        
        return AnalyticsDTO.DashboardMetrics.builder()
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .totalCustomers(totalCustomers)
                .totalProducts(totalProducts)
                .averageOrderValue(averageOrderValue)
                .pendingOrders(pendingOrders)
                .lowStockProducts(lowStockProducts)
                .salesTrend(salesTrend)
                .ordersTrend(ordersTrend)
                .topProducts(topProducts)
                .build();
    }

    /**
     * Predicts demand for a product based on historical sales using Linear Regression
     */
    @Transactional
    public double predictDemand(UUID productId) {
        log.info("Predicting demand for product: {}", productId);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
                
        // Get sales data for the last 30 days
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Object[]> salesData = orderItemRepository.findDailySalesByProduct(productId, thirtyDaysAgo);
        
        if (salesData.size() < 3) {
            log.warn("Insufficient data for demand forecasting for product: {}", productId);
            return 0.0;
        }
        
        SimpleRegression regression = new SimpleRegression();
        LocalDate today = LocalDate.now();
        
        for (Object[] row : salesData) {
            LocalDate date = (LocalDate) row[0];
            Long quantity = ((Number) row[1]).longValue();
            
            // x = days from today (negative), y = quantity
            long daysFromToday = ChronoUnit.DAYS.between(today, date);
            regression.addData(daysFromToday, quantity);
        }
        
        // Predict demand for tomorrow (x = 1)
        double predictedDemand = regression.predict(1.0);
        
        // Update product with AI score
        product.setAiDemandScore(BigDecimal.valueOf(Math.max(0, predictedDemand)));
        
        // Suggest reorder if predicted demand exceeds stock
        if (predictedDemand > product.getStockQuantity()) {
            product.setAiReorderSuggestion(true);
        }
        
        productRepository.save(product);
        
        return Math.max(0, predictedDemand);
    }

    @Transactional
    public void runAIForecasting() {
        log.info("Running AI Demand Forecasting for all available products");
        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            try {
                predictDemand(product.getId());
            } catch (Exception e) {
                log.error("Failed to predict demand for product: {}", product.getId(), e);
            }
        }
    }
    
    @Transactional(readOnly = true)
    public List<AnalyticsDTO.SalesReport> getSalesReport(LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching sales report from {} to {}", startDate, endDate);
        
        List<Object[]> stats = orderRepository.getDailyOrderStats(
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay()
        );
        
        return stats.stream()
                .map(row -> {
                    LocalDate date = (LocalDate) row[0];
                    Long orders = ((Number) row[1]).longValue();
                    BigDecimal revenue = (BigDecimal) row[2];
                    return AnalyticsDTO.SalesReport.builder()
                            .date(date)
                            .revenue(revenue)
                            .orders(orders)
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AnalyticsDTO.InventoryReport> getInventoryReport() {
        log.debug("Fetching inventory report");
        
        List<Product> products = productRepository.findAll();
        
        return products.stream()
                .map(product -> {
                    boolean needsReorder = product.getStockQuantity() <= product.getReorderLevel();
                    return AnalyticsDTO.InventoryReport.builder()
                            .productId(product.getId())
                            .productName(product.getName())
                            .category(product.getCategory())
                            .currentStock(product.getStockQuantity())
                            .reorderLevel(product.getReorderLevel())
                            .needsReorder(needsReorder)
                            .aiDemandScore(product.getAiDemandScore())
                            .aiReorderSuggestion(product.getAiReorderSuggestion() ? "YES" : "NO")
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void recordMetric(String metricType, String metricName, BigDecimal value, Map<String, Object> metadata) {
        log.debug("Recording metric: {} - {}", metricType, metricName);

        String metadataJson = null;
        if (metadata != null) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                metadataJson = mapper.writeValueAsString(metadata);
            } catch (Exception e) {
                log.error("Failed to serialize metadata", e);
            }
        }

        Analytics analytics = Analytics.builder()
                .metricType(metricType)
                .metricName(metricName)
                .metricValue(value)
                .metadata(metadataJson)
                .recordedDate(LocalDate.now())
                .build();

        analyticsRepository.save(analytics);
    }
    
    @Transactional
    public void recordDailyMetrics() {
        log.info("Recording daily metrics");
        
        // Record daily revenue
        BigDecimal revenue = calculateDailyRevenue(LocalDate.now());
        recordMetric("SALES", "daily_revenue", revenue, null);
        
        // Record daily orders
        Long orders = calculateDailyOrders(LocalDate.now());
        recordMetric("ORDERS", "daily_orders", BigDecimal.valueOf(orders), null);
        
        // Record new customers
        Long newCustomers = calculateNewCustomers(LocalDate.now());
        recordMetric("CUSTOMERS", "new_customers", BigDecimal.valueOf(newCustomers), null);
    }
    
    @Transactional(readOnly = true)
    public List<Analytics> getMetricsByType(String metricType, LocalDate startDate, LocalDate endDate) {
        return analyticsRepository.findByMetricTypeAndDateRange(metricType, startDate, endDate);
    }
    
    private BigDecimal calculateTotalRevenue() {
        return orderRepository.sumOrdersByStatus(com.grocky.entity.OrderStatus.DELIVERED)
                .orElse(BigDecimal.ZERO);
    }
    
    private Long calculateTotalOrders() {
        return orderRepository.count();
    }
    
    private Long calculateTotalCustomers() {
        return customerRepository.countActiveCustomers();
    }
    
    private Long calculateTotalProducts() {
        return productRepository.countAvailableProducts();
    }
    
    private Long calculatePendingOrders() {
        List<Object[]> stats = orderRepository.countOrdersByStatus();
        for (Object[] row : stats) {
            if ("PENDING".equals(row[0])) {
                return ((Number) row[1]).longValue();
            }
        }
        return 0L;
    }
    
    private Long calculateLowStockProducts() {
        return (long) productRepository.findLowStockProducts().size();
    }
    
    private Map<String, Object> getSalesTrend(int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        
        List<Object[]> stats = orderRepository.getDailyOrderStats(
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay()
        );
        
        Map<String, Object> trend = new LinkedHashMap<>();
        for (Object[] row : stats) {
            String date = ((LocalDate) row[0]).toString();
            BigDecimal revenue = (BigDecimal) row[2];
            trend.put(date, revenue);
        }
        return trend;
    }
    
    private Map<String, Object> getOrdersTrend(int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        
        List<Object[]> stats = orderRepository.getDailyOrderStats(
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay()
        );
        
        Map<String, Object> trend = new LinkedHashMap<>();
        for (Object[] row : stats) {
            String date = ((LocalDate) row[0]).toString();
            Long orders = ((Number) row[1]).longValue();
            trend.put(date, orders);
        }
        return trend;
    }
    
    private List<AnalyticsDTO.TopProduct> getTopSellingProducts(int limit) {
        List<Object[]> topProducts = orderItemRepository.findTopSellingProducts();
        
        return topProducts.stream()
                .limit(limit)
                .map(row -> {
                    Product product = (Product) row[0];
                    Long quantity = ((Number) row[1]).longValue();
                    return AnalyticsDTO.TopProduct.builder()
                            .productId(product.getId())
                            .productName(product.getName())
                            .quantitySold(quantity)
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    private BigDecimal calculateDailyRevenue(LocalDate date) {
        List<Order> orders = orderRepository.findOrdersByDateRange(
                date.atStartOfDay(),
                date.plusDays(1).atStartOfDay()
        );
        
        return orders.stream()
                .filter(o -> "DELIVERED".equals(o.getStatus()))
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private Long calculateDailyOrders(LocalDate date) {
        List<Order> orders = orderRepository.findOrdersByDateRange(
                date.atStartOfDay(),
                date.plusDays(1).atStartOfDay()
        );
        return (long) orders.size();
    }
    
    private Long calculateNewCustomers(LocalDate date) {
        // This would need a repository method to count customers created on a specific date
        return 0L;
    }
}
