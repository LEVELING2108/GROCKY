package com.grocky.repository;

import com.grocky.entity.Order;
import com.grocky.entity.Payment;
import com.grocky.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    
    Optional<Payment> findByTransactionId(String transactionId);
    
    List<Payment> findByOrder(Order order);
    
    List<Payment> findByStatus(PaymentStatus status);
    
    @Query("SELECT p FROM Payment p WHERE p.order.id = :orderId")
    List<Payment> findByOrderId(@Param("orderId") UUID orderId);
    
    @Query("SELECT p FROM Payment p WHERE p.status = :status AND p.createdAt < :threshold")
    List<Payment> findStalePayments(@Param("status") PaymentStatus status, @Param("threshold") java.time.LocalDateTime threshold);
    
    @Query("SELECT p.paymentMethod, COUNT(p) FROM Payment p GROUP BY p.paymentMethod")
    List<Object[]> countPaymentsByMethod();
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = :status")
    Optional<BigDecimal> sumPaymentsByStatus(@Param("status") PaymentStatus status);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED'")
    Optional<BigDecimal> calculateTotalRevenue();
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'FAILED'")
    long countFailedPayments();
}
