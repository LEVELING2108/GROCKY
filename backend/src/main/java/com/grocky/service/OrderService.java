package com.grocky.service;

import com.grocky.dto.OrderDTO;
import com.grocky.entity.*;
import com.grocky.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final InventoryLogRepository inventoryLogRepository;
    private final PaymentRepository paymentRepository;
    private final OrderWebSocketService orderWebSocketService;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders(int page, int size, String status) {
        log.debug("Fetching all orders");
        List<Order> orders;
        if (status != null && !status.isEmpty()) {
            orders = orderRepository.findByStatus(OrderStatus.valueOf(status.toUpperCase()));
        } else {
            orders = orderRepository.findAll();
        }
        return orders.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Object getOrderStats() {
        return new Object() {
            public long totalOrders = orderRepository.count();
            public long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);
            public long processingOrders = orderRepository.countByStatus(OrderStatus.PROCESSING);
            public long deliveredOrders = orderRepository.countByStatus(OrderStatus.DELIVERED);
        };
    }

    @Transactional(readOnly = true)
    public Page<OrderDTO> getAllOrdersPage(Pageable pageable) {
        log.debug("Fetching all orders");
        return orderRepository.findAll(pageable)
                .map(this::convertToDTO);
    }
    
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(UUID id) {
        log.debug("Fetching order by id: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return convertToDTO(order);
    }
    
    @Transactional(readOnly = true)
    public OrderDTO getOrderByOrderNumber(String orderNumber) {
        log.debug("Fetching order by order number: {}", orderNumber);
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return convertToDTO(order);
    }
    
    @Transactional(readOnly = true)
    public Page<OrderDTO> getOrdersByCustomer(UUID customerId, Pageable pageable) {
        log.debug("Fetching orders for customer: {}", customerId);
        return orderRepository.findByCustomerId(customerId, pageable)
                .map(this::convertToDTO);
    }
    
    @Transactional(readOnly = true)
    public Page<OrderDTO> getOrdersByStatus(String status, Pageable pageable) {
        log.debug("Fetching orders with status: {}", status);
        return orderRepository.findByStatus(OrderStatus.valueOf(status), pageable)
                .map(this::convertToDTO);
    }
    
    @Transactional
    public OrderDTO createOrder(OrderDTO.CreateOrder createOrder) {
        log.info("Creating new order for customer: {}", createOrder.getCustomerId());
        
        Customer customer = customerRepository.findById(createOrder.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        Order order = Order.builder()
                .customer(customer)
                .orderNumber(generateOrderNumber())
                .status(OrderStatus.PENDING)
                .subtotal(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .deliveryFee(BigDecimal.ZERO)
                .discountAmount(BigDecimal.ZERO)
                .totalAmount(BigDecimal.ZERO)
                .deliveryAddress(createOrder.getDeliveryAddress())
                .deliveryCity(createOrder.getDeliveryCity())
                .deliveryState(createOrder.getDeliveryState())
                .deliveryZip(createOrder.getDeliveryZip())
                .deliveryInstructions(createOrder.getDeliveryInstructions())
                .scheduledDeliveryDate(convertToInstant(createOrder.getScheduledDeliveryDate()))
                .paymentStatus(PaymentStatus.PENDING)
                .items(new ArrayList<>())
                .build();
        
        BigDecimal subtotal = BigDecimal.ZERO;
        
        for (OrderDTO.OrderItemInput itemInput : createOrder.getItems()) {
            Product product = productRepository.findById(itemInput.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemInput.getProductId()));
            
            if (product.getStockQuantity() < itemInput.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
            
            if (!product.getIsAvailable()) {
                throw new RuntimeException("Product not available: " + product.getName());
            }
            
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemInput.getQuantity())
                    .unitPrice(product.getPrice())
                    .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(itemInput.getQuantity())))
                    .build();
            
            order.getItems().add(orderItem);
            subtotal = subtotal.add(orderItem.getTotalPrice());
            
            // Update product stock
            int oldStock = product.getStockQuantity();
            product.setStockQuantity(oldStock - itemInput.getQuantity());
            productRepository.save(product);
            
            // Log inventory change
            logInventoryChange(product, "SALE", -itemInput.getQuantity(), 
                    oldStock, product.getStockQuantity(), 
                    "Order: " + order.getOrderNumber(), order.getId(), "SYSTEM");
        }
        
        // Calculate totals
        BigDecimal tax = subtotal.multiply(BigDecimal.valueOf(0.08)); // 8% tax
        BigDecimal deliveryFee = subtotal.compareTo(BigDecimal.valueOf(50)) >= 0 ? BigDecimal.ZERO : BigDecimal.valueOf(5.99);
        BigDecimal total = subtotal.add(tax).add(deliveryFee);
        
        order.setSubtotal(subtotal);
        order.setTaxAmount(tax);
        order.setDeliveryFee(deliveryFee);
        order.setTotalAmount(total);
        
        // AI: Predict delivery time
        order.setAiPredictedDeliveryTime(predictDeliveryTime());
        
        Order savedOrder = orderRepository.save(order);

        // Update customer loyalty points
        int loyaltyPoints = total.intValue() / 10; // 1 point per $10
        customer.setLoyaltyPoints(customer.getLoyaltyPoints() + loyaltyPoints);
        customerRepository.save(customer);

        log.info("Order created successfully: {}", savedOrder.getOrderNumber());
        OrderDTO response = convertToDTO(savedOrder);

        // Send WebSocket notifications
        orderWebSocketService.sendOrderCreated(savedOrder);
        orderWebSocketService.sendOrderStatusUpdate(savedOrder);

        // Notify customer
        notificationService.sendOrderConfirmation(savedOrder);

        return response;
    }
    
    @Transactional
    public OrderDTO updateOrderStatus(UUID id, String status) {
        log.info("Updating order status: {} -> {}", id, status);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
        order.setStatus(newStatus);

        if (newStatus == OrderStatus.DELIVERED) {
            order.setDeliveredAt(Instant.now());
        }

        Order updated = orderRepository.save(order);
        OrderDTO response = convertToDTO(updated);

        // Send WebSocket update
        orderWebSocketService.sendOrderStatusUpdate(order);

        return response;
    }
    
    @Transactional
    public OrderDTO cancelOrder(UUID id, String reason) {
        log.info("Cancelling order: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot cancel delivered order");
        }

        // Restore product stock
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            int oldStock = product.getStockQuantity();
            product.setStockQuantity(oldStock + item.getQuantity());
            productRepository.save(product);

            logInventoryChange(product, "RESTOCK", item.getQuantity(),
                    oldStock, product.getStockQuantity(),
                    "Cancelled order: " + order.getOrderNumber() + " - " + reason, order.getId(), "SYSTEM");
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setNotes(reason);
        Order saved = orderRepository.save(order);
        OrderDTO response = convertToDTO(saved);

        // Send WebSocket update
        orderWebSocketService.sendOrderStatusUpdate(order);

        return response;
    }
    
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersDueForDelivery() {
        log.debug("Fetching orders due for delivery");
        return orderRepository.findOrdersDueForDelivery(OrderStatus.PENDING, LocalDateTime.now())
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<OrderDTO> getOrdersByCustomerId(UUID customerId, Pageable pageable) {
        log.debug("Fetching orders for customer: {}", customerId);
        return orderRepository.findByCustomerId(customerId, pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public BigDecimal getOrderStatistics(UUID customerId) {
        return orderRepository.sumTotalOrdersByCustomer(customerId).orElse(BigDecimal.ZERO);
    }
    
    private String generateOrderNumber() {
        String year = String.valueOf(LocalDateTime.now().getYear());
        long random = ThreadLocalRandom.current().nextLong(1000, 9999);
        return "ORD-" + year + "-" + random;
    }
    
    private String predictDeliveryTime() {
        // Simple AI prediction: 2-5 business days
        int days = ThreadLocalRandom.current().nextInt(2, 6);
        return days + " business days";
    }
    
    private Instant convertToInstant(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.atZone(ZoneId.systemDefault()).toInstant();
    }
    
    private void logInventoryChange(Product product, String changeType, int quantityChange,
                                     int quantityBefore, int quantityAfter,
                                     String reason, UUID referenceId, String createdBy) {
        InventoryLog log = InventoryLog.builder()
                .product(product)
                .changeType(changeType)
                .quantityChange(quantityChange)
                .quantityBefore(quantityBefore)
                .quantityAfter(quantityAfter)
                .reason(reason)
                .referenceId(referenceId)
                .createdBy(createdBy)
                .build();
        inventoryLogRepository.save(log);
    }
    
    private OrderDTO convertToDTO(Order order) {
        List<OrderDTO.OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(item -> OrderDTO.OrderItemDTO.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .productImage(item.getProduct().getImageUrl())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getTotalPrice())
                        .build())
                .toList();
        
        return OrderDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomer().getId())
                .customerName(order.getCustomer().getName())
                .customerEmail(order.getCustomer().getEmail())
                .status(order.getStatus().name())
                .subtotal(order.getSubtotal())
                .taxAmount(order.getTaxAmount())
                .deliveryFee(order.getDeliveryFee())
                .discountAmount(order.getDiscountAmount())
                .totalAmount(order.getTotalAmount())
                .deliveryAddress(order.getDeliveryAddress())
                .deliveryCity(order.getDeliveryCity())
                .deliveryState(order.getDeliveryState())
                .deliveryZip(order.getDeliveryZip())
                .deliveryInstructions(order.getDeliveryInstructions())
                .scheduledDeliveryDate(order.getScheduledDeliveryDate() != null ? 
                        LocalDateTime.ofInstant(order.getScheduledDeliveryDate(), ZoneId.systemDefault()) : null)
                .aiPredictedDeliveryTime(order.getAiPredictedDeliveryTime())
                .paymentStatus(order.getPaymentStatus().name())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt() != null ? LocalDateTime.ofInstant(order.getCreatedAt(), ZoneId.systemDefault()) : null)
                .updatedAt(order.getUpdatedAt() != null ? LocalDateTime.ofInstant(order.getUpdatedAt(), ZoneId.systemDefault()) : null)
                .deliveredAt(order.getDeliveredAt() != null ? LocalDateTime.ofInstant(order.getDeliveredAt(), ZoneId.systemDefault()) : null)
                .items(itemDTOs)
                .build();
    }
}
