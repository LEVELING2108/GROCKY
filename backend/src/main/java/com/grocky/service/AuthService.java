package com.grocky.service;

import com.grocky.dto.AuthDTO;
import com.grocky.dto.CustomerDTO;
import com.grocky.entity.Customer;
import com.grocky.repository.CustomerRepository;
import com.grocky.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    @Transactional
    public AuthDTO.AuthResponse register(AuthDTO.RegisterRequest request) {
        log.info("Registering new customer with email: {}", request.getEmail());
        
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        Customer customer = Customer.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .zipCode(request.getZipCode())
                .loyaltyPoints(0)
                .isActive(true)
                .build();
        
        Customer savedCustomer = customerRepository.save(customer);
        
        String token = jwtService.generateToken(savedCustomer.getEmail());
        String refreshToken = jwtService.generateRefreshToken(savedCustomer.getEmail());
        
        return AuthDTO.AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .email(savedCustomer.getEmail())
                .name(savedCustomer.getName())
                .userId(savedCustomer.getId())
                .build();
    }
    
    public AuthDTO.AuthResponse login(AuthDTO.LoginRequest request) {
        log.info("Authenticating user: {}", request.getEmail());
        
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        
        Customer customer = customerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        
        if (!customer.getIsActive()) {
            throw new RuntimeException("Account is deactivated");
        }
        
        String token = jwtService.generateToken(customer.getEmail());
        String refreshToken = jwtService.generateRefreshToken(customer.getEmail());
        
        return AuthDTO.AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .email(customer.getEmail())
                .name(customer.getName())
                .userId(customer.getId())
                .build();
    }
    
    public AuthDTO.AuthResponse refreshToken(String refreshToken) {
        String email = jwtService.extractUsername(refreshToken);
        
        if (email == null || !customerRepository.existsByEmail(email)) {
            throw new RuntimeException("Invalid refresh token");
        }
        
        String newToken = jwtService.generateToken(email);
        
        return AuthDTO.AuthResponse.builder()
                .token(newToken)
                .refreshToken(refreshToken)
                .email(email)
                .build();
    }
    
    @Transactional
    public void logout(UUID customerId) {
        log.info("Customer {} logged out", customerId);
        // In a real application, you might invalidate tokens or add them to a blacklist
    }
}
