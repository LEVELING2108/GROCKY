package com.grocky.service;

import com.grocky.dto.AnalyticsDTO;
import com.grocky.entity.Customer;
import com.grocky.entity.Order;
import com.grocky.repository.CustomerRepository;
import com.grocky.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AI-powered Customer Segmentation Service using K-Means Clustering
 * Segments customers based on RFM (Recency, Frequency, Monetary) analysis
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AICustomerSegmentationService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    private static final int NUM_CLUSTERS = 4; // High Value, Loyal, At-Risk, New
    private static final int MAX_ITERATIONS = 20;

    /**
     * Segment all customers using K-Means clustering based on RFM analysis
     */
    @Transactional(readOnly = true)
    public List<AnalyticsDTO.CustomerInsight> segmentCustomers() {
        log.info("Starting customer segmentation using K-Means clustering");

        List<Customer> customers = customerRepository.findAll();
        if (customers.size() < NUM_CLUSTERS) {
            log.warn("Not enough customers for segmentation. Need at least {} customers", NUM_CLUSTERS);
            return new ArrayList<>();
        }

        // Prepare data points for clustering
        List<CustomerDataPoint> dataPoints = new ArrayList<>();
        List<DoublePoint> points = new ArrayList<>();

        for (Customer customer : customers) {
            CustomerDataPoint dataPoint = analyzeCustomer(customer);
            dataPoints.add(dataPoint);

            // Normalize values for clustering (0-1 scale)
            double[] normalizedValues = normalizeValues(
                    dataPoint.recency,
                    dataPoint.frequency,
                    dataPoint.monetary.doubleValue()
            );
            points.add(new DoublePoint(normalizedValues));
        }

        // Perform K-Means clustering
        KMeansPlusPlusClusterer<DoublePoint> clusterer = new KMeansPlusPlusClusterer<>(
                NUM_CLUSTERS,
                MAX_ITERATIONS
        );

        List<CentroidCluster<DoublePoint>> clusters = clusterer.cluster(points);

        // Map clusters to customer segments
        Map<Integer, String> clusterSegments = assignSegmentLabels(clusters, dataPoints);

        // Build customer insights
        List<AnalyticsDTO.CustomerInsight> insights = new ArrayList<>();
        for (int i = 0; i < dataPoints.size(); i++) {
            CustomerDataPoint dp = dataPoints.get(i);
            int clusterId = findCustomerCluster(dp, clusters, points, dataPoints);
            String segment = clusterSegments.get(clusterId);

            insights.add(new AnalyticsDTO.CustomerInsight(
                    dp.customerId,
                    dp.customerName,
                    segment,
                    dp.predictedLTV,
                    dp.recommendedProducts
            ));
        }

        log.info("Customer segmentation completed. Segments: {}", clusterSegments.values().stream().distinct().count());
        return insights;
    }

    /**
     * Analyze individual customer for RFM metrics
     */
    @Transactional(readOnly = true)
    public CustomerDataPoint analyzeCustomer(Customer customer) {
        UUID customerId = customer.getId();
        List<Order> orders = orderRepository.findByCustomerId(customerId, org.springframework.data.domain.Pageable.unpaged()).getContent();

        // Calculate Recency (days since last order)
        long recency = 999; // Default for customers with no orders
        if (!orders.isEmpty()) {
            Order lastOrder = orders.stream()
                    .max(Comparator.comparing(Order::getCreatedAt))
                    .orElse(null);
            if (lastOrder != null) {
                recency = ChronoUnit.DAYS.between(lastOrder.getCreatedAt(), java.time.LocalDateTime.now());
            }
        }

        // Calculate Frequency (number of orders)
        long frequency = orders.size();

        // Calculate Monetary (total spent)
        BigDecimal monetary = orders.stream()
                .filter(o -> "DELIVERED".equals(o.getStatus().name()))
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate Customer Lifetime Value (predicted)
        BigDecimal predictedLTV = calculatePredictedLTV(orders, monetary);

        // Get recommended products based on purchase history
        List<String> recommendedProducts = getRecommendedProducts(customer);

        return new CustomerDataPoint(
                customerId,
                customer.getName(),
                recency,
                frequency,
                monetary,
                predictedLTV,
                recommendedProducts
        );
    }

    /**
     * Get customer segment label
     */
    @Transactional(readOnly = true)
    public String getCustomerSegment(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        CustomerDataPoint dataPoint = analyzeCustomer(customer);

        // Simple rule-based segmentation as fallback
        if (dataPoint.frequency == 0) {
            return "NEW";
        } else if (dataPoint.recency > 90) {
            return "AT_RISK";
        } else if (dataPoint.monetary.compareTo(BigDecimal.valueOf(500)) >= 0 && dataPoint.frequency >= 5) {
            return "HIGH_VALUE";
        } else if (dataPoint.frequency >= 3 && dataPoint.recency <= 30) {
            return "LOYAL";
        } else {
            return "REGULAR";
        }
    }

    /**
     * Normalize RFM values to 0-1 scale for clustering
     */
    private double[] normalizeValues(double recency, double frequency, double monetary) {
        // Simple min-max normalization (would need global min/max for production)
        double normRecency = 1.0 / (1.0 + recency / 30.0); // Inverse: recent is better
        double normFreq = Math.min(1.0, frequency / 10.0);
        double normMonetary = Math.min(1.0, monetary / 1000.0);

        return new double[]{normRecency, normFreq, normMonetary};
    }

    /**
     * Assign meaningful labels to clusters based on their centroids
     */
    private Map<Integer, String> assignSegmentLabels(List<CentroidCluster<DoublePoint>> clusters, List<CustomerDataPoint> dataPoints) {
        Map<Integer, String> labels = new HashMap<>();

        for (int i = 0; i < clusters.size(); i++) {
            CentroidCluster<DoublePoint> cluster = clusters.get(i);
            double[] centroid = cluster.getCenter().getPoint();

            // Analyze cluster characteristics
            double avgRecency = 1.0 / centroid[0] * 30; // Convert back
            double avgFrequency = centroid[1] * 10;
            double avgMonetary = centroid[2] * 1000;

            // Assign label based on characteristics
            if (avgMonetary > 500 && avgFrequency > 5) {
                labels.put(i, "HIGH_VALUE");
            } else if (avgFrequency > 3 && avgRecency < 30) {
                labels.put(i, "LOYAL");
            } else if (avgRecency > 60) {
                labels.put(i, "AT_RISK");
            } else {
                labels.put(i, "REGULAR");
            }
        }

        return labels;
    }

    /**
     * Find which cluster a customer belongs to
     */
    private int findCustomerCluster(CustomerDataPoint dp, List<CentroidCluster<DoublePoint>> clusters, List<DoublePoint> points, List<CustomerDataPoint> allDataPoints) {
        int dpIndex = allDataPoints.indexOf(dp);
        if (dpIndex < 0 || dpIndex >= points.size()) {
            return 0;
        }
        DoublePoint customerPoint = points.get(dpIndex);

        int bestCluster = 0;
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < clusters.size(); i++) {
            double distance = distance(customerPoint.getPoint(), clusters.get(i).getCenter().getPoint());
            if (distance < minDistance) {
                minDistance = distance;
                bestCluster = i;
            }
        }

        return bestCluster;
    }

    private double distance(double[] point1, double[] point2) {
        double sum = 0;
        for (int i = 0; i < point1.length; i++) {
            sum += Math.pow(point1[i] - point2[i], 2);
        }
        return Math.sqrt(sum);
    }

    /**
     * Calculate predicted Customer Lifetime Value
     */
    private BigDecimal calculatePredictedLTV(List<Order> orders, BigDecimal totalSpent) {
        if (orders.isEmpty()) {
            return BigDecimal.ZERO;
        }

        // Average order value
        BigDecimal avgOrderValue = totalSpent.divide(
                BigDecimal.valueOf(orders.size()),
                2,
                RoundingMode.HALF_UP
        );

        // Purchase frequency (orders per month)
        Optional<Order> firstOrder = orders.stream()
                .min(Comparator.comparing(Order::getCreatedAt));

        int monthsAsCustomer = 1;
        if (firstOrder.isPresent()) {
            monthsAsCustomer = Math.max(1, Period.between(
                    firstOrder.get().getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate(),
                    LocalDate.now()
            ).getMonths());
        }

        double purchaseFrequency = (double) orders.size() / monthsAsCustomer;

        // Predicted LTV = Avg Order Value × Purchase Frequency × 12 months × 2 years
        BigDecimal predictedLTV = avgOrderValue
                .multiply(BigDecimal.valueOf(purchaseFrequency))
                .multiply(BigDecimal.valueOf(12))
                .multiply(BigDecimal.valueOf(2));

        return predictedLTV.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Get product recommendations based on customer's purchase history
     */
    private List<String> getRecommendedProducts(Customer customer) {
        // Get categories customer has purchased from
        List<String> categories = orderRepository.findRecentCategoriesByCustomer(customer.getId());

        if (categories.isEmpty()) {
            return Arrays.asList("Organic Bananas", "Whole Milk", "Fresh Bread");
        }

        // Return complementary categories
        Map<String, List<String>> complementaryCategories = Map.of(
                "Produce", Arrays.asList("Dairy", "Bakery"),
                "Dairy", Arrays.asList("Bakery", "Produce"),
                "Bakery", Arrays.asList("Dairy", "Pantry"),
                "Meat", Arrays.asList("Produce", "Pantry"),
                "Pantry", Arrays.asList("Meat", "Produce")
        );

        List<String> recommendations = new ArrayList<>();
        for (String category : categories) {
            List<String> complements = complementaryCategories.get(category);
            if (complements != null) {
                recommendations.addAll(complements);
            }
        }

        return recommendations.stream().distinct().limit(3).collect(Collectors.toList());
    }

    // Helper class to hold customer data for clustering
    public static class CustomerDataPoint {
        UUID customerId;
        String customerName;
        long recency;      // Days since last order
        long frequency;    // Number of orders
        BigDecimal monetary; // Total spent
        BigDecimal predictedLTV;
        List<String> recommendedProducts;

        public CustomerDataPoint(UUID customerId, String customerName, long recency,
                                 long frequency, BigDecimal monetary, BigDecimal predictedLTV,
                                 List<String> recommendedProducts) {
            this.customerId = customerId;
            this.customerName = customerName;
            this.recency = recency;
            this.frequency = frequency;
            this.monetary = monetary;
            this.predictedLTV = predictedLTV;
            this.recommendedProducts = recommendedProducts;
        }
    }
}
