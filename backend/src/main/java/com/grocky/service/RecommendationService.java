package com.grocky.service;

import com.grocky.dto.ProductDTO;
import com.grocky.entity.Product;
import com.grocky.repository.OrderItemRepository;
import com.grocky.repository.OrderRepository;
import com.grocky.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional(readOnly = true)
    public List<ProductDTO> getPersonalizedRecommendations(UUID customerId, int limit) {
        log.info("Generating personalized recommendations for customer: {}", customerId);
        
        // 1. Get categories the customer has recently bought from
        List<String> favoriteCategories = orderRepository.findRecentCategoriesByCustomer(customerId);
        
        if (favoriteCategories.isEmpty()) {
            // Fallback to trending products if no history
            return getTrendingProducts(limit);
        }

        // 2. Fetch products from those categories that the customer hasn't bought yet (or just general recommendations from those categories)
        List<Product> recommendations = productRepository.findByCategory(favoriteCategories.get(0), PageRequest.of(0, limit)).getContent();
        
        return recommendations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getFrequentlyBoughtTogether(UUID productId, int limit) {
        log.info("Fetching products frequently bought with: {}", productId);
        
        List<Object[]> results = orderItemRepository.findProductsFrequentlyBoughtTogether(productId);
        
        return results.stream()
                .limit(limit)
                .map(row -> convertToDTO((Product) row[0]))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getTrendingProducts(int limit) {
        log.info("Fetching trending products");
        
        // Trending defined as products with high AI demand score or high recent sales
        List<Product> trending = productRepository.findHighDemandProducts(BigDecimal.valueOf(70.0)); // Threshold 70
        
        if (trending.isEmpty()) {
            trending = productRepository.findAll(PageRequest.of(0, limit)).getContent();
        }
        
        return trending.stream()
                .limit(limit)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ProductDTO convertToDTO(Product product) {
        BigDecimal finalPrice = product.getPrice();
        if (product.getDiscountPercentage() != null && product.getDiscountPercentage().compareTo(BigDecimal.ZERO) > 0) {
            finalPrice = product.getPrice().multiply(
                    BigDecimal.ONE.subtract(product.getDiscountPercentage().divide(BigDecimal.valueOf(100)))
            );
        }
        
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory())
                .subcategory(product.getSubcategory())
                .price(product.getPrice())
                .costPrice(product.getCostPrice())
                .stockQuantity(product.getStockQuantity())
                .reorderLevel(product.getReorderLevel())
                .unit(product.getUnit())
                .brand(product.getBrand())
                .supplier(product.getSupplier())
                .expiryDate(product.getExpiryDate())
                .imageUrl(product.getImageUrl())
                .isAvailable(product.getIsAvailable())
                .discountPercentage(product.getDiscountPercentage())
                .aiDemandScore(product.getAiDemandScore())
                .aiReorderSuggestion(product.getAiReorderSuggestion())
                .finalPrice(finalPrice)
                .createdAt(product.getCreatedAt() != null ? java.time.LocalDateTime.ofInstant(product.getCreatedAt(), java.time.ZoneId.systemDefault()) : null)
                .updatedAt(product.getUpdatedAt() != null ? java.time.LocalDateTime.ofInstant(product.getUpdatedAt(), java.time.ZoneId.systemDefault()) : null)
                .build();
    }
}
