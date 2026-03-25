package com.grocky.controller;

import com.grocky.dto.ResponseDTO;
import com.grocky.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-payment-intent/{orderId}")
    public ResponseEntity<ResponseDTO<Map<String, String>>> createPaymentIntent(@PathVariable UUID orderId) {
        return ResponseEntity.ok(ResponseDTO.success(
                paymentService.createPaymentIntent(orderId), 
                "Payment intent created successfully"
        ));
    }
}
