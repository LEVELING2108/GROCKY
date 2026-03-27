package com.grocky.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.UUID;

/**
 * Analytics entity for storing metric data
 */
@Entity
@Table(name = "analytics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Analytics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "metric_type", nullable = false, length = 50)
    private String metricType;  // SALES, ORDERS, CUSTOMERS, INVENTORY
    
    @Column(name = "metric_name", nullable = false, length = 100)
    private String metricName;
    
    @Column(name = "metric_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal metricValue;
    
    @Column(columnDefinition = "jsonb")
    private String metadata;
    
    @Column(name = "recorded_date", nullable = false)
    private LocalDate recordedDate;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
