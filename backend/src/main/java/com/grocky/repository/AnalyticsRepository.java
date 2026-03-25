package com.grocky.repository;

import com.grocky.entity.Analytics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnalyticsRepository extends JpaRepository<Analytics, UUID> {
    
    Page<Analytics> findByMetricType(String metricType, Pageable pageable);
    
    Page<Analytics> findByRecordedDate(LocalDate recordedDate, Pageable pageable);
    
    @Query("SELECT a FROM Analytics a WHERE a.metricType = :type AND a.recordedDate = :date")
    List<Analytics> findByMetricTypeAndDate(
        @Param("type") String metricType,
        @Param("date") LocalDate recordedDate
    );
    
    @Query("SELECT a FROM Analytics a WHERE a.metricType = :type AND a.recordedDate BETWEEN :startDate AND :endDate ORDER BY a.recordedDate ASC")
    List<Analytics> findByMetricTypeAndDateRange(
        @Param("type") String metricType,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    @Query("SELECT a.metricName, AVG(a.metricValue) FROM Analytics a WHERE a.metricType = :type GROUP BY a.metricName")
    List<Object[]> findAverageMetricsByType(@Param("type") String metricType);
    
    @Query("SELECT a.recordedDate, SUM(a.metricValue) FROM Analytics a WHERE a.metricType = :type GROUP BY a.recordedDate ORDER BY a.recordedDate ASC")
    List<Object[]> getDailyTotalsByType(@Param("type") String metricType);
    
    @Query("SELECT a FROM Analytics a WHERE a.metricType = :type ORDER BY a.recordedDate DESC")
    Page<Analytics> findLatestByType(@Param("type") String metricType, Pageable pageable);
    
    @Query("SELECT MAX(a.metricValue) FROM Analytics a WHERE a.metricType = :type AND a.recordedDate = :date")
    Optional<Double> findMaxValueByTypeAndDate(
        @Param("type") String metricType,
        @Param("date") LocalDate recordedDate
    );
    
    @Query("SELECT a FROM Analytics a WHERE a.metricName = :name ORDER BY a.recordedDate DESC")
    Page<Analytics> findByMetricName(@Param("name") String metricName, Pageable pageable);
    
    @Query("DELETE FROM Analytics a WHERE a.recordedDate < :threshold")
    void deleteOlderThan(@Param("threshold") LocalDate threshold);
}
