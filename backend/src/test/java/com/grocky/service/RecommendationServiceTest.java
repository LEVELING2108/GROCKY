package com.grocky.service;

import com.grocky.dto.ProductDTO;
import com.grocky.entity.Product;
import com.grocky.repository.OrderItemRepository;
import com.grocky.repository.OrderRepository;
import com.grocky.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    private UUID customerId;
    private Product recommendedProduct;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        recommendedProduct = Product.builder()
                .id(UUID.randomUUID())
                .name("Greek Yogurt")
                .category("Dairy")
                .price(BigDecimal.valueOf(1.99))
                .isAvailable(true)
                .build();
    }

    @Test
    void testGetPersonalizedRecommendations_WithHistory() {
        // Customer has bought from 'Dairy'
        when(orderRepository.findRecentCategoriesByCustomer(customerId))
                .thenReturn(Collections.singletonList("Dairy"));
        
        when(productRepository.findByCategory(eq("Dairy"), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(recommendedProduct)));

        List<ProductDTO> results = recommendationService.getPersonalizedRecommendations(customerId, 5);

        assertFalse(results.isEmpty());
        assertEquals("Greek Yogurt", results.get(0).getName());
        verify(productRepository).findByCategory(eq("Dairy"), any(PageRequest.class));
    }

    @Test
    void testGetPersonalizedRecommendations_FallbackToTrending() {
        // No history for customer
        when(orderRepository.findRecentCategoriesByCustomer(customerId))
                .thenReturn(Collections.emptyList());
        
        // Mock trending fallback
        when(productRepository.findHighDemandProducts(any(BigDecimal.class)))
                .thenReturn(Collections.singletonList(recommendedProduct));

        List<ProductDTO> results = recommendationService.getPersonalizedRecommendations(customerId, 5);

        assertFalse(results.isEmpty());
        verify(productRepository).findHighDemandProducts(any(BigDecimal.class));
    }

    @Test
    void testGetFrequentlyBoughtTogether() {
        UUID productId = UUID.randomUUID();
        Product alsoBought = Product.builder().name("Honey").price(BigDecimal.valueOf(5.00)).build();
        
        // Mock repository returning Object array as expected by our native query
        Object[] row = new Object[]{alsoBought, 10L};
        when(orderItemRepository.findProductsFrequentlyBoughtTogether(productId))
                .thenReturn(Collections.singletonList(row));

        List<ProductDTO> results = recommendationService.getFrequentlyBoughtTogether(productId, 5);

        assertEquals(1, results.size());
        assertEquals("Honey", results.get(0).getName());
    }
}
