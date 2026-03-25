package com.grocky.service;

import com.grocky.dto.ProductDTO;
import com.grocky.entity.Product;
import com.grocky.repository.InventoryLogRepository;
import com.grocky.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryLogRepository inventoryLogRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private UUID productId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        testProduct = Product.builder()
                .id(productId)
                .name("Organic Bananas")
                .price(BigDecimal.valueOf(2.99))
                .stockQuantity(100)
                .reorderLevel(20)
                .isAvailable(true)
                .discountPercentage(BigDecimal.ZERO)
                .build();
    }

    @Test
    void testGetProductById_Success() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));

        ProductDTO result = productService.getProductById(productId);

        assertNotNull(result);
        assertEquals("Organic Bananas", result.getName());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void testUpdateStock_IncreasesStock() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        ProductDTO.StockUpdate update = ProductDTO.StockUpdate.builder()
                .quantityChange(50)
                .changeType("RESTOCK")
                .reason("New Shipment")
                .build();

        ProductDTO result = productService.updateStock(productId, update);

        assertEquals(150, result.getStockQuantity());
        verify(productRepository).save(argThat(p -> p.getStockQuantity() == 150));
    }

    @Test
    void testApplyDynamicPricing_LowDemandDiscounts() {
        Product lowDemandProduct = Product.builder()
                .name("Slow Selling Apple")
                .price(BigDecimal.valueOf(1.00))
                .aiDemandScore(BigDecimal.valueOf(1.5)) // Very low demand
                .discountPercentage(BigDecimal.ZERO)
                .build();

        when(productRepository.findAll()).thenReturn(Arrays.asList(lowDemandProduct));

        productService.applyDynamicPricing();

        // 1.5 demand should trigger 15% discount as per our logic
        assertEquals(BigDecimal.valueOf(15), lowDemandProduct.getDiscountPercentage());
        verify(productRepository, atLeastOnce()).save(lowDemandProduct);
    }

    @Test
    void testApplyDynamicPricing_ExpiryDiscounts() {
        Product expiringProduct = Product.builder()
                .name("Expiring Milk")
                .price(BigDecimal.valueOf(4.00))
                .expiryDate(LocalDate.now().plusDays(2)) // Expiring in 2 days
                .discountPercentage(BigDecimal.ZERO)
                .build();

        when(productRepository.findAll()).thenReturn(Arrays.asList(expiringProduct));

        productService.applyDynamicPricing();

        // Expiring in < 3 days should trigger 50% discount
        assertEquals(BigDecimal.valueOf(50), expiringProduct.getDiscountPercentage());
    }
}
