package com.grocky.repository;

import com.grocky.entity.Cart;
import com.grocky.entity.Customer;
import com.grocky.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
    
    List<Cart> findByCustomer(Customer customer);
    
    Optional<Cart> findByCustomerAndProduct(Customer customer, Product product);
    
    @Query("SELECT c FROM Cart c WHERE c.customer.id = :customerId")
    List<Cart> findByCustomerId(@Param("customerId") UUID customerId);
    
    @Query("SELECT c FROM Cart c WHERE c.customer.id = :customerId AND c.product.id = :productId")
    Optional<Cart> findByCustomerIdAndProductId(
        @Param("customerId") UUID customerId,
        @Param("productId") UUID productId
    );
    
    @Query("SELECT SUM(c.quantity * c.product.price) FROM Cart c WHERE c.customer.id = :customerId")
    Optional<java.math.BigDecimal> calculateCartTotal(@Param("customerId") UUID customerId);
    
    @Query("SELECT COUNT(c) FROM Cart c WHERE c.customer.id = :customerId")
    long countItemsByCustomer(@Param("customerId") UUID customerId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Cart c WHERE c.customer.id = :customerId")
    void deleteByCustomerId(@Param("customerId") UUID customerId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Cart c WHERE c.customer.id = :customerId AND c.product.id = :productId")
    void deleteByCustomerIdAndProductId(
        @Param("customerId") UUID customerId,
        @Param("productId") UUID productId
    );
}
