package com.grocky.service;

import com.grocky.dto.CustomerDTO;
import com.grocky.entity.Customer;
import com.grocky.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional(readOnly = true)
    public Page<CustomerDTO> getAllCustomers(Pageable pageable) {
        log.debug("Fetching all customers");
        return customerRepository.findAll(pageable)
                .map(this::convertToDTO);
    }
    
    @Transactional(readOnly = true)
    public CustomerDTO getCustomerById(UUID id) {
        log.debug("Fetching customer by id: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return convertToDTO(customer);
    }
    
    @Transactional(readOnly = true)
    public CustomerDTO getCustomerByEmail(String email) {
        log.debug("Fetching customer by email: {}", email);
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return convertToDTO(customer);
    }
    
    @Transactional
    public CustomerDTO updateCustomer(UUID id, CustomerDTO.CustomerUpdate update) {
        log.info("Updating customer: {}", id);
        
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        if (update.getName() != null) {
            customer.setName(update.getName());
        }
        if (update.getPhone() != null) {
            customer.setPhone(update.getPhone());
        }
        if (update.getAddress() != null) {
            customer.setAddress(update.getAddress());
        }
        if (update.getCity() != null) {
            customer.setCity(update.getCity());
        }
        if (update.getState() != null) {
            customer.setState(update.getState());
        }
        if (update.getZipCode() != null) {
            customer.setZipCode(update.getZipCode());
        }
        
        Customer updated = customerRepository.save(customer);
        return convertToDTO(updated);
    }
    
    @Transactional
    public CustomerDTO updatePassword(UUID id, String oldPassword, String newPassword) {
        log.info("Updating password for customer: {}", id);
        
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        if (!passwordEncoder.matches(oldPassword, customer.getPasswordHash())) {
            throw new RuntimeException("Invalid old password");
        }
        
        customer.setPasswordHash(passwordEncoder.encode(newPassword));
        Customer updated = customerRepository.save(customer);
        
        return convertToDTO(updated);
    }
    
    @Transactional
    public CustomerDTO addLoyaltyPoints(UUID id, Integer points) {
        log.info("Adding {} loyalty points to customer: {}", points, id);
        
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        customer.setLoyaltyPoints(customer.getLoyaltyPoints() + points);
        Customer updated = customerRepository.save(customer);
        
        return convertToDTO(updated);
    }
    
    @Transactional
    public CustomerDTO toggleActiveStatus(UUID id) {
        log.info("Toggling active status for customer: {}", id);
        
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        customer.setIsActive(!customer.getIsActive());
        Customer updated = customerRepository.save(customer);
        
        return convertToDTO(updated);
    }
    
    @Transactional(readOnly = true)
    public Page<CustomerDTO> searchCustomers(String keyword, Pageable pageable) {
        log.debug("Searching customers with keyword: {}", keyword);
        return customerRepository.searchCustomers(keyword, pageable)
                .map(this::convertToDTO);
    }
    
    @Transactional
    public void deleteCustomer(UUID id) {
        log.info("Deleting customer: {}", id);
        customerRepository.deleteById(id);
    }
    
    private CustomerDTO convertToDTO(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .city(customer.getCity())
                .state(customer.getState())
                .zipCode(customer.getZipCode())
                .loyaltyPoints(customer.getLoyaltyPoints())
                .aiPreferenceProfile(customer.getAiPreferenceProfile())
                .isActive(customer.getIsActive())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }
}
