package com.grocky.controller;

import com.grocky.dto.OrderDTO;
import com.grocky.dto.ResponseDTO;
import com.grocky.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<ResponseDTO<List<OrderDTO>>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String status) {
        Page<OrderDTO> orderPage = orderService.getAllOrders(PageRequest.of(page, size));
        return ResponseEntity.ok(ResponseDTO.success(orderPage.getContent(), "Orders retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<OrderDTO>> getOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(ResponseDTO.success(orderService.getOrderById(id), "Order retrieved successfully"));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ResponseDTO<OrderDTO>> updateOrderStatus(
            @PathVariable UUID id,
            @Valid @RequestBody OrderDTO.UpdateStatus request) {
        return ResponseEntity.ok(ResponseDTO.success(orderService.updateOrderStatus(id, request.getStatus()), "Order status updated successfully"));
    }

    @GetMapping("/stats")
    public ResponseEntity<ResponseDTO<Object>> getOrderStats() {
        return ResponseEntity.ok(ResponseDTO.success(orderService.getOrderStats(), "Order stats retrieved successfully"));
    }
}
