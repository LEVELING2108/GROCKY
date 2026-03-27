package com.grocky.controller;

import com.grocky.dto.CartDTO;
import com.grocky.dto.ResponseDTO;
import com.grocky.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{customerId}")
    public ResponseEntity<ResponseDTO<CartDTO>> getCart(@PathVariable UUID customerId) {
        return ResponseEntity.ok(ResponseDTO.success(cartService.getCartByCustomer(customerId), "Cart retrieved successfully"));
    }

    @PostMapping("/{customerId}/add")
    public ResponseEntity<ResponseDTO<CartDTO>> addToCart(@PathVariable UUID customerId, @RequestBody CartDTO.AddToCartRequest request) {
        return ResponseEntity.ok(ResponseDTO.success(cartService.addToCart(customerId, request), "Product added to cart"));
    }

    @PutMapping("/{customerId}/update/{productId}")
    public ResponseEntity<ResponseDTO<CartDTO>> updateQuantity(@PathVariable UUID customerId, @PathVariable UUID productId, @RequestBody CartDTO.UpdateQuantityRequest request) {
        return ResponseEntity.ok(ResponseDTO.success(cartService.updateQuantity(customerId, productId, request), "Cart updated successfully"));
    }

    @DeleteMapping("/{customerId}/remove/{productId}")
    public ResponseEntity<ResponseDTO<CartDTO>> removeFromCart(@PathVariable UUID customerId, @PathVariable UUID productId) {
        return ResponseEntity.ok(ResponseDTO.success(cartService.removeFromCart(customerId, productId), "Product removed from cart"));
    }

    @DeleteMapping("/{customerId}/clear")
    public ResponseEntity<ResponseDTO<Void>> clearCart(@PathVariable UUID customerId) {
        cartService.clearCart(customerId);
        return ResponseEntity.ok(ResponseDTO.success(null, "Cart cleared successfully"));
    }
}
