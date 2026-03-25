package com.grocky.controller;

import com.grocky.dto.AnalyticsDTO;
import com.grocky.dto.ResponseDTO;
import com.grocky.service.AICustomerSegmentationService;
import com.grocky.service.AIPriceOptimizationService;
import com.grocky.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller for AI and Advanced Analytics endpoints
 */
@Slf4j
@RestController
@RequestMapping("/analytics/ai")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AIAnalyticsController {

    private final AnalyticsService analyticsService;
    private final AICustomerSegmentationService customerSegmentationService;
    private final AIPriceOptimizationService priceOptimizationService;

    /**
     * Run AI demand forecasting for all products
     */
    @PostMapping("/forecast")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> runAIForecasting() {
        log.info("Triggering AI demand forecasting");

        try {
            analyticsService.runAIForecasting();

            Map<String, Object> result = Map.of(
                    "message", "AI demand forecasting completed successfully",
                    "timestamp", LocalDate.now().toString()
            );

            return ResponseEntity.ok(ResponseDTO.success(result));
        } catch (Exception e) {
            log.error("AI forecasting failed", e);
            return ResponseEntity.internalServerError()
                    .body(ResponseDTO.error("Forecasting failed: " + e.getMessage()));
        }
    }

    /**
     * Get customer segmentation using AI clustering
     */
    @GetMapping("/customers/segmentation")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<List<AnalyticsDTO.CustomerInsight>>> getCustomerSegmentation() {
        log.info("Fetching customer segmentation");

        try {
            List<AnalyticsDTO.CustomerInsight> segments = customerSegmentationService.segmentCustomers();

            return ResponseEntity.ok(ResponseDTO.success(segments));
        } catch (Exception e) {
            log.error("Customer segmentation failed", e);
            return ResponseEntity.internalServerError()
                    .body(ResponseDTO.error("Segmentation failed: " + e.getMessage()));
        }
    }

    /**
     * Get segment for a specific customer
     */
    @GetMapping("/customers/{customerId}/segment")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<Map<String, String>>> getCustomerSegment(
            @PathVariable UUID customerId) {
        log.info("Fetching segment for customer: {}", customerId);

        try {
            String segment = customerSegmentationService.getCustomerSegment(customerId);

            Map<String, String> result = Map.of(
                    "customerId", customerId.toString(),
                    "segment", segment
            );

            return ResponseEntity.ok(ResponseDTO.success(result));
        } catch (Exception e) {
            log.error("Failed to get customer segment", e);
            return ResponseEntity.internalServerError()
                    .body(ResponseDTO.error("Failed to get segment: " + e.getMessage()));
        }
    }

    /**
     * Get price optimization suggestions
     */
    @GetMapping("/pricing/optimization")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<List<AIPriceOptimizationService.PriceOptimizationSuggestion>>> getPriceOptimization() {
        log.info("Fetching price optimization suggestions");

        try {
            List<AIPriceOptimizationService.PriceOptimizationSuggestion> suggestions =
                    priceOptimizationService.getPriceOptimizationSuggestions();

            return ResponseEntity.ok(ResponseDTO.success(suggestions));
        } catch (Exception e) {
            log.error("Price optimization failed", e);
            return ResponseEntity.internalServerError()
                    .body(ResponseDTO.error("Price optimization failed: " + e.getMessage()));
        }
    }

    /**
     * Get price elasticity for a specific product
     */
    @GetMapping("/pricing/{productId}/elasticity")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<AIPriceOptimizationService.PriceElasticity>> getPriceElasticity(
            @PathVariable UUID productId) {
        log.info("Fetching price elasticity for product: {}", productId);

        try {
            AIPriceOptimizationService.PriceElasticity elasticity =
                    priceOptimizationService.analyzePriceElasticity(productId);

            if (elasticity == null) {
                return ResponseEntity.badRequest()
                        .body(ResponseDTO.error("Insufficient data for elasticity analysis"));
            }

            return ResponseEntity.ok(ResponseDTO.success(elasticity));
        } catch (Exception e) {
            log.error("Failed to get price elasticity", e);
            return ResponseEntity.internalServerError()
                    .body(ResponseDTO.error("Failed to analyze elasticity: " + e.getMessage()));
        }
    }

    /**
     * Get comprehensive AI insights dashboard
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> getAIDashboard() {
        log.info("Fetching AI insights dashboard");

        try {
            Map<String, Object> dashboard = Map.of(
                    "customerSegments", customerSegmentationService.segmentCustomers(),
                    "priceOptimizations", priceOptimizationService.getPriceOptimizationSuggestions(),
                    "generatedAt", LocalDate.now().toString()
            );

            return ResponseEntity.ok(ResponseDTO.success(dashboard));
        } catch (Exception e) {
            log.error("Failed to generate AI dashboard", e);
            return ResponseEntity.internalServerError()
                    .body(ResponseDTO.error("Dashboard generation failed: " + e.getMessage()));
        }
    }
}
