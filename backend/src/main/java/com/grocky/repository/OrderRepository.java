package com.grocky.repository;

import com.grocky.entity.Customer;
import com.grocky.entity.Order;
import com.grocky.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    
    Optional<Order> findByOrderNumber(String orderNumber);
    
    Page<Order> findByCustomer(Customer customer, Pageable pageable);
    
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId ORDER BY o.createdAt DESC")
    Page<Order> findByCustomerId(@Param("customerId") UUID customerId, Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.scheduledDeliveryDate <= :date")
    List<Order> findOrdersDueForDelivery(@Param("status") OrderStatus status, @Param("date") LocalDateTime date);
    
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findOrdersByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countOrdersByStatus();
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = :status")
    Optional<BigDecimal> sumOrdersByStatus(@Param("status") OrderStatus status);
    
    @Query("SELECT AVG(o.totalAmount) FROM Order o WHERE o.customer.id = :customerId")
    Optional<BigDecimal> findAverageOrderValueByCustomer(@Param("customerId") UUID customerId);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.customer.id = :customerId")
    long countOrdersByCustomer(@Param("customerId") UUID customerId);
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.customer.id = :customerId")
    Optional<BigDecimal> sumTotalOrdersByCustomer(@Param("customerId") UUID customerId);
    
    @Query("SELECT o FROM Order o WHERE o.paymentStatus = 'PENDING' AND o.createdAt < :threshold")
    List<Order> findStalePendingOrders(@Param("threshold") LocalDateTime threshold);
    
    @Query("SELECT DATE(o.createdAt), COUNT(o), SUM(o.totalAmount) FROM Order o " +
           "WHERE o.createdAt BETWEEN :startDate AND :endDate GROUP BY DATE(o.createdAt)")
    List<Object[]> getDailyOrderStats(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT DISTINCT p.category FROM Order o JOIN o.items oi JOIN oi.product p " +
           "WHERE o.customer.id = :customerId ORDER BY o.createdAt DESC")
    List<String> findRecentCategoriesByCustomer(@Param("customerId") UUID customerId);

    @Query("SELECT DISTINCT p FROM Order o JOIN o.items oi JOIN oi.product p " +
           "WHERE o.customer.id = :customerId ORDER BY o.createdAt DESC")
    List<Product> findRecentProductsByCustomer(@Param("customerId") UUID customerId);
}
