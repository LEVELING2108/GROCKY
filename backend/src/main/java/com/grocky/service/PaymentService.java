package com.grocky.service;

import com.grocky.dto.PaymentDTO;
import com.grocky.entity.Order;
import com.grocky.entity.Payment;
import com.grocky.repository.OrderRepository;
import com.grocky.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderWebSocketService webSocketService;
    private final NotificationService notificationService;

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    /**
     * Creates a Stripe PaymentIntent for the given order and returns the client secret.
     */
    @Transactional
    public Map<String, String> createPaymentIntent(UUID orderId) {
        log.info("Creating Stripe PaymentIntent for order: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        // Stripe expects amounts in cents
        long amountInCents = order.getTotalAmount().multiply(new BigDecimal("100")).longValue();
        
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency("usd")
                    .putMetadata("orderId", order.getId().toString())
                    .putMetadata("customerEmail", order.getCustomer().getEmail())
                    .build();
            
            PaymentIntent paymentIntent = PaymentIntent.create(params);
            
            Map<String, String> responseData = new HashMap<>();
            responseData.put("clientSecret", paymentIntent.getClientSecret());
            
            return responseData;
        } catch (StripeException e) {
            log.error("Stripe Error creating PaymentIntent: {}", e.getMessage());
            throw new RuntimeException("Payment processing failed", e);
        }
    }
    
    @Transactional(readOnly = true)
    public List<PaymentDTO.PaymentResponse> getPaymentsByOrder(UUID orderId) {
        log.debug("Fetching payments for order: {}", orderId);
        return paymentRepository.findByOrderId(orderId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public PaymentDTO.PaymentResponse getPaymentById(UUID id) {
        log.debug("Fetching payment by id: {}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return convertToResponse(payment);
    }
    
    @Transactional
    public PaymentDTO.PaymentResponse createPayment(PaymentDTO.CreatePayment createPayment) {
        log.info("Creating payment for order: {}", createPayment.getOrderId());
        
        Order order = orderRepository.findById(createPayment.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(createPayment.getPaymentMethod())
                .paymentGateway(createPayment.getPaymentGateway())
                .amount(order.getTotalAmount())
                .status("PENDING")
                .build();
        
        Payment saved = paymentRepository.save(payment);
        log.info("Payment created: {}", saved.getId());
        
        return convertToResponse(saved);
    }
    
    @Transactional
    public PaymentDTO.PaymentResponse processPayment(UUID paymentId, boolean success, String transactionId, String gatewayResponse) {
        log.info("Processing payment: {} - Success: {}", paymentId, success);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (success) {
            payment.setStatus("COMPLETED");
            payment.setTransactionId(transactionId);
            payment.setGatewayResponse(gatewayResponse);
            payment.setProcessedAt(Instant.now());

            // Update order payment status
            Order order = payment.getOrder();
            order.setPaymentStatus("COMPLETED");
            order.setStatus(com.grocky.entity.OrderStatus.CONFIRMED);
            orderRepository.save(order);

            // Send WebSocket notification
            webSocketService.sendPaymentConfirmation(order.getId(), transactionId, order.getTotalAmount());

            // Send payment confirmation email
            notificationService.sendPaymentConfirmation(order, transactionId);

            log.info("Payment completed successfully: {}", transactionId);
        } else {
            payment.setStatus("FAILED");
            payment.setGatewayResponse(gatewayResponse);

            Order order = payment.getOrder();
            order.setPaymentStatus("FAILED");
            orderRepository.save(order);

            log.warn("Payment failed: {}", paymentId);
        }

        Payment updated = paymentRepository.save(payment);
        return convertToResponse(updated);
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenue() {
        return paymentRepository.calculateTotalRevenue().orElse(BigDecimal.ZERO);
    }
    
    @Transactional(readOnly = true)
    public List<Object[]> getPaymentsByMethod() {
        return paymentRepository.countPaymentsByMethod();
    }
    
    @Transactional(readOnly = true)
    public long getFailedPaymentsCount() {
        return paymentRepository.countFailedPayments();
    }
    
    private PaymentDTO.PaymentResponse convertToResponse(Payment payment) {
        return PaymentDTO.PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .orderNumber(payment.getOrder().getOrderNumber())
                .paymentMethod(payment.getPaymentMethod())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .processedAt(payment.getProcessedAt() != null ? 
                        java.time.LocalDateTime.ofInstant(payment.getProcessedAt(), java.time.ZoneId.systemDefault()) : null)
                .build();
    }
}
