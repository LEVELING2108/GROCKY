package com.grocky.controller;

import com.grocky.dto.ResponseDTO;
import com.grocky.service.OrderWebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * WebSocket Controller for handling real-time messages
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WebSocketController {

    private final OrderWebSocketService webSocketService;

    /**
     * Handle order status update requests from admin
     */
    @MessageMapping("/order.updateStatus")
    @SendTo("/topic/admin/updates")
    public ResponseDTO<Map<String, Object>> handleOrderUpdate(
            @Payload Map<String, Object> payload) {

        log.info("Received order status update: {}", payload);

        String orderId = (String) payload.get("orderId");
        String status = (String) payload.get("status");

        Map<String, Object> response = Map.of(
                "success", true,
                "orderId", orderId,
                "status", status,
                "message", "Order status updated successfully"
        );

        return ResponseDTO.success(response);
    }

    /**
     * Handle inventory check requests
     */
    @MessageMapping("/inventory.check")
    @SendTo("/topic/admin/inventory")
    public ResponseDTO<Map<String, Object>> handleInventoryCheck(
            @Payload Map<String, Object> payload) {

        log.info("Received inventory check request: {}", payload);

        String productId = (String) payload.get("productId");

        Map<String, Object> response = Map.of(
                "success", true,
                "productId", productId,
                "message", "Inventory check initiated"
        );

        return ResponseDTO.success(response);
    }

    /**
     * Subscribe to customer-specific updates
     */
    @PostMapping("/ws/subscribe/customer/{customerId}")
    public ResponseEntity<ResponseDTO<Map<String, String>>> subscribeCustomer(
            @PathVariable UUID customerId) {

        log.info("Customer subscribing to updates: {}", customerId);

        Map<String, String> response = Map.of(
                "channel", "/topic/customer/" + customerId,
                "status", "subscribed"
        );

        return ResponseEntity.ok(ResponseDTO.success(response));
    }

    /**
     * Get WebSocket connection info
     */
    @GetMapping("/ws/info")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> getWebSocketInfo() {
        Map<String, Object> info = Map.of(
                "endpoint", "/ws-grocky",
                "topics", new String[]{
                        "/topic/admin/orders",
                        "/topic/admin/new-orders",
                        "/topic/admin/payments",
                        "/topic/admin/inventory",
                        "/topic/admin/analytics",
                        "/topic/customer/{customerId}",
                        "/topic/order/{orderId}"
                },
                "status", "active"
        );

        return ResponseEntity.ok(ResponseDTO.success(info));
    }
}
