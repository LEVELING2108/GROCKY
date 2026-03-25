package com.grocky.repository;

import com.grocky.entity.Customer;
import com.grocky.entity.Product;
import com.grocky.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    
    Page<Review> findByProduct(Product product, Pageable pageable);
    
    Page<Review> findByCustomer(Customer customer, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.product.id = :productId ORDER BY r.createdAt DESC")
    Page<Review> findByProductId(@Param("productId") UUID productId, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.customer.id = :customerId")
    Page<Review> findByCustomerId(@Param("customerId") UUID customerId, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.product.id = :productId AND r.customer.id = :customerId")
    Optional<Review> findByProductAndCustomer(@Param("productId") UUID productId, @Param("customerId") UUID customerId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Optional<Double> findAverageRatingByProduct(@Param("productId") UUID productId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId")
    long countReviewsByProduct(@Param("productId") UUID productId);
    
    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.product.id = :productId GROUP BY r.rating")
    List<Object[]> findRatingDistributionByProduct(@Param("productId") UUID productId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.isVerifiedPurchase = true AND r.product.id = :productId")
    long countVerifiedReviewsByProduct(@Param("productId") UUID productId);
    
    @Query("SELECT p, AVG(r.rating) as avgRating FROM Product p JOIN Review r ON r.product.id = p.id " +
           "GROUP BY p.id ORDER BY avgRating DESC")
    List<Object[]> findTopRatedProducts(Pageable pageable);
}
