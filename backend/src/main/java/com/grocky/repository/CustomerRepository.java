package com.grocky.repository;

import com.grocky.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, java.util.UUID> {
    
    Optional<Customer> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhone(String phone);
    
    Page<Customer> findByIsActive(boolean isActive, Pageable pageable);
    
    @Query("SELECT c FROM Customer c WHERE c.name LIKE %:keyword% OR c.email LIKE %:keyword%")
    Page<Customer> searchCustomers(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.isActive = true")
    long countActiveCustomers();
    
    @Query("SELECT SUM(c.loyaltyPoints) FROM Customer c WHERE c.isActive = true")
    Long sumAllLoyaltyPoints();
}
