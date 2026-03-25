package com.grocky.repository;

import com.grocky.entity.InventoryLog;
import com.grocky.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryLogRepository extends JpaRepository<InventoryLog, UUID> {
    
    List<InventoryLog> findByProduct(Product product);
    
    Page<InventoryLog> findByChangeType(String changeType, Pageable pageable);
    
    @Query("SELECT il FROM InventoryLog il WHERE il.product.id = :productId ORDER BY il.createdAt DESC")
    Page<InventoryLog> findByProductId(@Param("productId") UUID productId, Pageable pageable);
    
    @Query("SELECT il FROM InventoryLog il WHERE il.createdAt BETWEEN :startDate AND :endDate ORDER BY il.createdAt DESC")
    List<InventoryLog> findByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT il.changeType, COUNT(il) FROM InventoryLog il WHERE il.createdAt BETWEEN :startDate AND :endDate GROUP BY il.changeType")
    List<Object[]> countChangesByTypeAndDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT SUM(il.quantityChange) FROM InventoryLog il WHERE il.product.id = :productId AND il.changeType = :changeType")
    Optional<Integer> sumQuantityChangesByProductAndType(
        @Param("productId") UUID productId,
        @Param("changeType") String changeType
    );
    
    @Query("SELECT il FROM InventoryLog il WHERE il.referenceId = :referenceId")
    List<InventoryLog> findByReferenceId(@Param("referenceId") UUID referenceId);
    
    @Query("SELECT COUNT(il) FROM InventoryLog il WHERE il.createdBy = :createdBy")
    long countLogsByUser(@Param("createdBy") String createdBy);
}
