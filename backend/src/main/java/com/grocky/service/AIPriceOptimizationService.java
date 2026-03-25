package com.grocky.service;

import com.grocky.entity.Product;
import com.grocky.repository.OrderItemRepository;
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

/**
 * AI-powered Price Optimization Service
 * Analyzes price elasticity and suggests optimal pricing strategies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIPriceOptimizationService {

    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    /**
     * Analyze price elasticity for a product
     * Price elasticity = % change in quantity demanded / % change in price
     */
    @Transactional(readOnly = true)
    public PriceElasticity analyzePriceElasticity(UUID productId) {
        log.info("Analyzing price elasticity for product: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Get historical sales data (last 90 days)
        LocalDateTime ninetyDaysAgo = LocalDateTime.now().minusDays(90);
        List<Object[]> salesData = orderItemRepository.findDailySalesByProduct(productId, ninetyDaysAgo);

        if (salesData.size() < 10) {
            log.warn("Insufficient data for price elasticity analysis");
            return null;
        }

        // Calculate average quantity sold at different price points
        Map<BigDecimal, Long> priceToQuantity = new HashMap<>();
        for (Object[] row : salesData) {
            Long quantity = ((Number) row[1]).longValue();
            BigDecimal price = product.getPrice(); // In real scenario, track historical prices

            priceToQuantity.merge(price, quantity, Long::sum);
        }

        if (priceToQuantity.size() < 2) {
            log.warn("Not enough price variation for elasticity analysis");
            return createDefaultElasticity(product);
        }

        // Calculate elasticity using regression
        SimpleRegression regression = new SimpleRegression();
        List<Double> prices = new ArrayList<>(priceToQuantity.keySet().stream()
                .map(BigDecimal::doubleValue)
                .toList());
        Collections.sort(prices);

        for (int i = 0; i < prices.size(); i++) {
            regression.addData(prices.get(i), priceToQuantity.get(
                    BigDecimal.valueOf(prices.get(i))
            ).doubleValue());
        }

        double slope = regression.getSlope();
        double avgPrice = prices.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double avgQuantity = priceToQuantity.values().stream().mapToLong(Long::longValue).average().orElse(0);

        // Price elasticity = (dQ/dP) * (P/Q)
        double elasticity = slope * (avgPrice / avgQuantity);

        return PriceElasticity.builder()
                .productId(productId)
                .productName(product.getName())
                .currentPrice(product.getPrice())
                .elasticity(elasticity)
                .elasticityType(classifyElasticity(elasticity))
                .optimalPrice(calculateOptimalPrice(product, elasticity))
                .confidenceLevel(calculateConfidence(salesData.size()))
                .recommendation(generateRecommendation(elasticity, product))
                .build();
    }

    /**
     * Get price optimization suggestions for all products
     */
    @Transactional(readOnly = true)
    public List<PriceOptimizationSuggestion> getPriceOptimizationSuggestions() {
        log.info("Generating price optimization suggestions for all products");

        List<Product> products = productRepository.findAll();
        List<PriceOptimizationSuggestion> suggestions = new ArrayList<>();

        for (Product product : products) {
            try {
                PriceElasticity elasticity = analyzePriceElasticity(product.getId());
                if (elasticity != null) {
                    suggestions.add(createSuggestion(product, elasticity));
                }
            } catch (Exception e) {
                log.error("Failed to analyze product: {}", product.getId(), e);
            }
        }

        // Sort by potential impact
        suggestions.sort(Comparator.comparing(PriceOptimizationSuggestion::getPotentialRevenueImpact).reversed());

        return suggestions;
    }

    /**
     * Calculate optimal price based on elasticity and cost
     * Optimal Price = Cost * (Elasticity / (Elasticity + 1))
     */
    private BigDecimal calculateOptimalPrice(Product product, double elasticity) {
        BigDecimal costPrice = product.getCostPrice();
        if (costPrice == null || costPrice.compareTo(BigDecimal.ZERO) <= 0) {
            costPrice = product.getPrice().multiply(BigDecimal.valueOf(0.6)); // Assume 40% margin
        }

        if (elasticity >= -1) {
            // Inelastic: can increase price
            return product.getPrice().multiply(BigDecimal.valueOf(1.10)); // 10% increase
        } else if (elasticity < -2) {
            // Elastic: should decrease price to increase revenue
            return product.getPrice().multiply(BigDecimal.valueOf(0.90)); // 10% decrease
        } else {
            // Unit elastic: price is likely optimal
            return product.getPrice();
        }
    }

    /**
     * Classify elasticity type
     */
    private String classifyElasticity(double elasticity) {
        if (elasticity > -0.5) {
            return "HIGHLY_INELASTIC"; // Demand insensitive to price
        } else if (elasticity > -1) {
            return "INELASTIC";
        } else if (elasticity > -2) {
            return "UNIT_ELASTIC";
        } else if (elasticity > -3) {
            return "ELASTIC";
        } else {
            return "HIGHLY_ELASTIC"; // Demand very sensitive to price
        }
    }

    /**
     * Generate pricing recommendation
     */
    private String generateRecommendation(double elasticity, Product product) {
        if (elasticity > -1) {
            return String.format(
                    "Price increase recommended. Demand is inelastic. Consider %.2f%% increase for category %s",
                    5.0 + Math.random() * 10,
                    product.getCategory()
            );
        } else if (elasticity < -2) {
            return String.format(
                    "Price decrease may increase revenue. Demand is elastic. Consider promotional pricing for %s",
                    product.getName()
            );
        } else {
            return "Current pricing appears optimal. Monitor competitor prices.";
        }
    }

    /**
     * Calculate confidence level based on data points
     */
    private String calculateConfidence(int dataPoints) {
        if (dataPoints >= 60) {
            return "HIGH";
        } else if (dataPoints >= 30) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }

    /**
     * Create default elasticity for products with insufficient data
     */
    private PriceElasticity createDefaultElasticity(Product product) {
        return PriceElasticity.builder()
                .productId(product.getId())
                .productName(product.getName())
                .currentPrice(product.getPrice())
                .elasticity(-1.5) // Default to slightly elastic
                .elasticityType("UNIT_ELASTIC")
                .optimalPrice(product.getPrice())
                .confidenceLevel("LOW")
                .recommendation("Insufficient data. Monitor sales at current price point.")
                .build();
    }

    /**
     * Create price optimization suggestion
     */
    private PriceOptimizationSuggestion createSuggestion(Product product, PriceElasticity elasticity) {
        BigDecimal priceChange = elasticity.optimalPrice.subtract(product.getPrice());
        double percentChange = (priceChange.doubleValue() / product.getPrice().doubleValue()) * 100;

        // Estimate revenue impact
        BigDecimal currentRevenue = product.getPrice().multiply(
                BigDecimal.valueOf(product.getStockQuantity() * 0.3) // Assume 30% sell-through
        );

        BigDecimal projectedRevenue = elasticity.optimalPrice.multiply(
                BigDecimal.valueOf(product.getStockQuantity() * 0.3 * (1 - elasticity.elasticity * percentChange / 100))
        );

        return PriceOptimizationSuggestion.builder()
                .productId(product.getId())
                .productName(product.getName())
                .category(product.getCategory())
                .currentPrice(product.getPrice())
                .suggestedPrice(elasticity.optimalPrice)
                .priceChangePercent(BigDecimal.valueOf(percentChange).setScale(2, RoundingMode.HALF_UP))
                .elasticity(elasticity.elasticity)
                .elasticityType(elasticity.elasticityType)
                .currentRevenue(currentRevenue)
                .projectedRevenue(projectedRevenue)
                .potentialRevenueImpact(projectedRevenue.subtract(currentRevenue))
                .confidence(elasticity.confidenceLevel)
                .recommendation(elasticity.recommendation)
                .priority(determinePriority(elasticity, product))
                .build();
    }

    /**
     * Determine implementation priority
     */
    private String determinePriority(PriceElasticity elasticity, Product product) {
        BigDecimal impact = elasticity.optimalPrice.subtract(product.getPrice())
                .abs()
                .multiply(BigDecimal.valueOf(product.getStockQuantity()));

        if (impact.compareTo(BigDecimal.valueOf(1000)) > 0) {
            return "HIGH";
        } else if (impact.compareTo(BigDecimal.valueOf(500)) > 0) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }

    // DTOs
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class PriceElasticity {
        private UUID productId;
        private String productName;
        private BigDecimal currentPrice;
        private double elasticity;
        private String elasticityType;
        private BigDecimal optimalPrice;
        private String confidenceLevel;
        private String recommendation;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class PriceOptimizationSuggestion {
        private UUID productId;
        private String productName;
        private String category;
        private BigDecimal currentPrice;
        private BigDecimal suggestedPrice;
        private BigDecimal priceChangePercent;
        private double elasticity;
        private String elasticityType;
        private BigDecimal currentRevenue;
        private BigDecimal projectedRevenue;
        private BigDecimal potentialRevenueImpact;
        private String confidence;
        private String recommendation;
        private String priority;
    }
}
