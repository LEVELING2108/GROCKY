package com.grocky.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * InventoryLog entity for tracking inventory changes
 */
@Entity
@Table(name = "inventory_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class InventoryLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(name = "change_type", nullable = false, length = 20)
    private String changeType;  // SALE, RESTOCK, ADJUSTMENT, EXPIRED, DAMAGED
    
    @Column(name = "quantity_change", nullable = false)
    private Integer quantityChange;
    
    @Column(name = "quantity_before", nullable = false)
    private Integer quantityBefore;
    
    @Column(name = "quantity_after", nullable = false)
    private Integer quantityAfter;
    
    @Column(length = 100)
    private String reason;
    
    @Column(name = "reference_id")
    private UUID referenceId;  // Order ID, Purchase Order ID, etc.
    
    @Column(name = "created_by", length = 50)
    private String createdBy;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
