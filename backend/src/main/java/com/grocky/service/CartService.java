package com.grocky.service;

import com.grocky.dto.CartDTO;
import com.grocky.entity.Cart;
import com.grocky.entity.Customer;
import com.grocky.entity.Product;
import com.grocky.repository.CartRepository;
import com.grocky.repository.CustomerRepository;
import com.grocky.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {
    
    private final CartRepository cartRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    
    @Transactional(readOnly = true)
    public CartDTO getCartByCustomer(UUID customerId) {
        log.debug("Fetching cart for customer: {}", customerId);
        
        List<Cart> cartItems = cartRepository.findByCustomerId(customerId);
        return buildCartDTO(customerId, cartItems);
    }
    
    @Transactional
    public CartDTO addToCart(UUID customerId, CartDTO.AddToCartRequest addToCart) {
        log.info("Adding product {} to cart for customer {}", addToCart.getProductId(), customerId);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Product product = productRepository.findById(addToCart.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getIsAvailable()) {
            throw new RuntimeException("Product is not available");
        }

        Cart cartItem = cartRepository.findByCustomerIdAndProductId(customerId, addToCart.getProductId())
                .orElse(null);

        if (cartItem != null) {
            // Update quantity
            cartItem.setQuantity(cartItem.getQuantity() + addToCart.getQuantity());
        } else {
            // Create new cart item
            cartItem = Cart.builder()
                    .customer(customer)
                    .product(product)
                    .quantity(addToCart.getQuantity())
                    .build();
        }

        cartRepository.save(cartItem);
        log.info("Added to cart successfully");

        return getCartByCustomer(customerId);
    }

    @Transactional
    public CartDTO updateQuantity(UUID customerId, UUID productId, CartDTO.UpdateQuantityRequest updateQuantity) {
        log.info("Updating quantity for product {} in cart", productId);
        
        Cart cartItem = cartRepository.findByCustomerIdAndProductId(customerId, productId)
                .orElseThrow(() -> new RuntimeException("Item not in cart"));
        
        cartItem.setQuantity(updateQuantity.getQuantity());
        cartRepository.save(cartItem);
        
        return getCartByCustomer(customerId);
    }
    
    @Transactional
    public CartDTO removeFromCart(UUID customerId, UUID productId) {
        log.info("Removing product {} from cart", productId);
        
        cartRepository.deleteByCustomerIdAndProductId(customerId, productId);
        
        return getCartByCustomer(customerId);
    }
    
    @Transactional
    public void clearCart(UUID customerId) {
        log.info("Clearing cart for customer {}", customerId);
        cartRepository.deleteByCustomerId(customerId);
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getCartTotal(UUID customerId) {
        return cartRepository.calculateCartTotal(customerId).orElse(BigDecimal.ZERO);
    }
    
    @Transactional(readOnly = true)
    public long getCartItemCount(UUID customerId) {
        return cartRepository.countItemsByCustomer(customerId);
    }
    
    private CartDTO buildCartDTO(UUID customerId, List<Cart> cartItems) {
        List<CartDTO.CartItemDTO> itemDTOs = cartItems.stream()
                .map(cart -> CartDTO.CartItemDTO.builder()
                        .id(cart.getId())
                        .productId(cart.getProduct().getId())
                        .productName(cart.getProduct().getName())
                        .productImage(cart.getProduct().getImageUrl())
                        .productPrice(cart.getProduct().getPrice())
                        .quantity(cart.getQuantity())
                        .totalPrice(cart.getProduct().getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())))
                        .isAvailable(cart.getProduct().getIsAvailable())
                        .build())
                .collect(Collectors.toList());
        
        BigDecimal subtotal = itemDTOs.stream()
                .map(CartDTO.CartItemDTO::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        int totalItems = itemDTOs.stream()
                .mapToInt(CartDTO.CartItemDTO::getQuantity)
                .sum();
        
        return CartDTO.builder()
                .customerId(customerId)
                .items(itemDTOs)
                .totalItems(totalItems)
                .subtotal(subtotal)
                .build();
    }
}
