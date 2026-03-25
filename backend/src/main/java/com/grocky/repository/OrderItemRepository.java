package com.grocky.repository;

import com.grocky.entity.Order;
import com.grocky.entity.OrderItem;
import com.grocky.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    
    List<OrderItem> findByOrder(Order order);
    
    List<OrderItem> findByProduct(Product product);
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId")
    List<OrderItem> findByOrderId(@Param("orderId") UUID orderId);
    
    @Query("SELECT oi.product, SUM(oi.quantity) FROM OrderItem oi GROUP BY oi.product ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findTopSellingProducts();
    
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.product.id = :productId")
    Optional<Long> getTotalQuantitySoldByProduct(@Param("productId") UUID productId);
    
    @Query("SELECT SUM(oi.totalPrice) FROM OrderItem oi WHERE oi.order.id = :orderId")
    Optional<BigDecimal> calculateOrderTotal(@Param("orderId") UUID orderId);
    
    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.product.id = :productId")
    long countOrdersContainingProduct(@Param("productId") UUID productId);

    @Query("SELECT DATE(oi.order.createdAt), SUM(oi.quantity) FROM OrderItem oi " +
           "WHERE oi.product.id = :productId AND oi.order.createdAt >= :startDate " +
           "GROUP BY DATE(oi.order.createdAt) ORDER BY DATE(oi.order.createdAt) ASC")
    List<Object[]> findDailySalesByProduct(@Param("productId") UUID productId, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT oi2.product, COUNT(oi2.product) FROM OrderItem oi1 JOIN OrderItem oi2 ON oi1.order = oi2.order " +
           "WHERE oi1.product.id = :productId AND oi2.product.id <> :productId " +
           "GROUP BY oi2.product ORDER BY COUNT(oi2.product) DESC")
    List<Object[]> findProductsFrequentlyBoughtTogether(@Param("productId") UUID productId);
}
