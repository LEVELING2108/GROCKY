package com.grocky.service;

import com.grocky.dto.OrderDTO;
import com.grocky.entity.Order;
import com.grocky.entity.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * WebSocket Service for Real-time Order Updates
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Send order status update to subscribed clients
     */
    public void sendOrderStatusUpdate(Order order) {
        log.info("Sending order status update for order: {}", order.getOrderNumber());

        Map<String, Object> update = new HashMap<>();
        update.put("orderId", order.getId().toString());
        update.put("orderNumber", order.getOrderNumber());
        update.put("status", order.getStatus().name());
        update.put("previousStatus", getPreviousStatus(order.getStatus()));
        update.put("updatedAt", Instant.now().toString());
        update.put("estimatedDelivery", order.getScheduledDeliveryDate());

        messagingTemplate.convertAndSend(
                "/topic/order/" + order.getId(),
                update
        );

        // Also send to customer's personal queue
        if (order.getCustomer() != null) {
            messagingTemplate.convertAndSend(
                    "/topic/customer/" + order.getCustomer().getId(),
                    update
            );
        }

        // Send broadcast to admin for all order updates
        messagingTemplate.convertAndSend("/topic/admin/orders", update);
    }

    /**
     * Send order creation notification
     */
    public void sendOrderCreated(Order order) {
        log.info("Broadcasting new order created: {}", order.getOrderNumber());

        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "ORDER_CREATED");
        notification.put("orderId", order.getId().toString());
        notification.put("orderNumber", order.getOrderNumber());
        notification.put("customerId", order.getCustomer().getId().toString());
        notification.put("totalAmount", order.getTotalAmount());
        notification.put("status", order.getStatus().name());
        notification.put("createdAt", Instant.now().toString());

        // Send to admin queue
        messagingTemplate.convertAndSend("/topic/admin/new-orders", notification);
    }

    /**
     * Send payment confirmation
     */
    public void sendPaymentConfirmation(UUID orderId, String transactionId, BigDecimal amount) {
        log.info("Sending payment confirmation for order: {}", orderId);

        Map<String, Object> confirmation = new HashMap<>();
        confirmation.put("type", "PAYMENT_CONFIRMED");
        confirmation.put("orderId", orderId.toString());
        confirmation.put("transactionId", transactionId);
        confirmation.put("amount", amount);
        confirmation.put("confirmedAt", Instant.now().toString());

        messagingTemplate.convertAndSend("/topic/order/" + orderId, confirmation);
        messagingTemplate.convertAndSend("/topic/admin/payments", confirmation);
    }

    /**
     * Send inventory alert
     */
    public void sendInventoryAlert(UUID productId, String productName, int currentStock, int reorderLevel) {
        log.warn("Sending inventory alert for product: {}", productName);

        Map<String, Object> alert = new HashMap<>();
        alert.put("type", "INVENTORY_LOW");
        alert.put("productId", productId.toString());
        alert.put("productName", productName);
        alert.put("currentStock", currentStock);
        alert.put("reorderLevel", reorderLevel);
        alert.put("alertLevel", currentStock <= reorderLevel / 2 ? "CRITICAL" : "WARNING");
        alert.put("timestamp", Instant.now().toString());

        messagingTemplate.convertAndSend("/topic/admin/inventory", alert);
    }

    /**
     * Send real-time analytics update
     */
    public void sendAnalyticsUpdate(String metricType, String metricName, BigDecimal value) {
        Map<String, Object> update = new HashMap<>();
        update.put("type", "ANALYTICS_UPDATE");
        update.put("metricType", metricType);
        update.put("metricName", metricName);
        update.put("value", value);
        update.put("timestamp", Instant.now().toString());

        messagingTemplate.convertAndSend("/topic/admin/analytics", update);
    }

    /**
     * Get previous status in the order lifecycle
     */
    private String getPreviousStatus(OrderStatus currentStatus) {
        return switch (currentStatus) {
            case DELIVERED -> "SHIPPED";
            case SHIPPED -> "PROCESSING";
            case PROCESSING -> "CONFIRMED";
            case CONFIRMED -> "PENDING";
            default -> null;
        };
    }
}
