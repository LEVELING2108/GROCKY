package com.grocky.repository;

import com.grocky.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    
    Page<Product> findByCategory(String category, Pageable pageable);
    
    Page<Product> findBySubcategory(String subcategory, Pageable pageable);
    
    Page<Product> findByIsAvailable(boolean isAvailable, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% OR p.description LIKE %:keyword%")
    Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= p.reorderLevel")
    List<Product> findLowStockProducts();
    
    @Query("SELECT p FROM Product p WHERE p.stockQuantity = 0")
    List<Product> findOutOfStockProducts();
    
    @Query("SELECT p FROM Product p WHERE p.expiryDate IS NOT NULL AND p.expiryDate <= :date")
    List<Product> findExpiringProducts(@Param("date") LocalDate date);
    
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByPriceRange(
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        Pageable pageable
    );
    
    @Query("SELECT p FROM Product p WHERE p.aiDemandScore >= :threshold ORDER BY p.aiDemandScore DESC")
    List<Product> findHighDemandProducts(@Param("threshold") BigDecimal threshold);
    
    @Query("SELECT p FROM Product p WHERE p.aiReorderSuggestion = true")
    List<Product> findProductsNeedingReorder();
    
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.isAvailable = true")
    List<String> findAllCategories();
    
    @Query("SELECT DISTINCT p.subcategory FROM Product p WHERE p.category = :category AND p.isAvailable = true")
    List<String> findSubcategoriesByCategory(@Param("category") String category);
    
    @Query("SELECT AVG(p.price) FROM Product p WHERE p.category = :category")
    Optional<BigDecimal> findAveragePriceByCategory(@Param("category") String category);
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.isAvailable = true")
    long countAvailableProducts();
    
    @Query("SELECT SUM(p.stockQuantity * p.price) FROM Product p WHERE p.isAvailable = true")
    Optional<BigDecimal> calculateTotalInventoryValue();

    List<Product> findTop10ByNameContainingIgnoreCase(String name);
}
