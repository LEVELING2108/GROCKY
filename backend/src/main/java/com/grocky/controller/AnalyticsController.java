package com.grocky.controller;

import com.grocky.dto.AnalyticsDTO;
import com.grocky.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    public ResponseEntity<AnalyticsDTO.DashboardMetrics> getDashboardMetrics() {
        return ResponseEntity.ok(analyticsService.getDashboardMetrics());
    }

    @GetMapping("/sales-report")
    public ResponseEntity<List<AnalyticsDTO.SalesReport>> getSalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(analyticsService.getSalesReport(startDate, endDate));
    }

    @GetMapping("/inventory-report")
    public ResponseEntity<List<AnalyticsDTO.InventoryReport>> getInventoryReport() {
        return ResponseEntity.ok(analyticsService.getInventoryReport());
    }

    @PostMapping("/metrics/record-daily")
    public ResponseEntity<Void> recordDailyMetrics() {
        analyticsService.recordDailyMetrics();
        return ResponseEntity.ok().build();
    }
}
