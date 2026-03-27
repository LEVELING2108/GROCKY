package com.grocky.service;

import com.grocky.dto.ProductDTO;
import com.grocky.entity.InventoryLog;
import com.grocky.entity.Product;
import com.grocky.repository.InventoryLogRepository;
import com.grocky.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    private final InventoryLogRepository inventoryLogRepository;
    private final NotificationService notificationService;
    
    @Transactional(readOnly = true)
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        log.debug("Fetching all products");
        return productRepository.findAll(pageable)
                .map(this::convertToDTO);
    }
    
    @Transactional(readOnly = true)
    public ProductDTO getProductById(UUID id) {
        log.debug("Fetching product by id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return convertToDTO(product);
    }
    
    @Transactional(readOnly = true)
    public Page<ProductDTO> getProductsByCategory(String category, Pageable pageable) {
        log.debug("Fetching products by category: {}", category);
        return productRepository.findByCategory(category, pageable)
                .map(this::convertToDTO);
    }
    
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        log.debug("Fetching all categories");
        return productRepository.findAllCategories();
    }
    
    @Transactional(readOnly = true)
    public List<String> getSubcategoriesByCategory(String category) {
        log.debug("Fetching subcategories for: {}", category);
        return productRepository.findSubcategoriesByCategory(category);
    }
    
    @Transactional(readOnly = true)
    public Page<ProductDTO> searchProducts(String keyword, Pageable pageable) {
        log.debug("Searching products with keyword: {}", keyword);
        return productRepository.searchProducts(keyword, pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> autocomplete(String query) {
        log.debug("Fetching autocomplete suggestions for: {}", query);
        return productRepository.findTop10ByNameContainingIgnoreCase(query)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<ProductDTO> getLowStockProducts() {
        log.debug("Fetching low stock products");
        return productRepository.findLowStockProducts()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<ProductDTO> getOutOfStockProducts() {
        log.debug("Fetching out of stock products");
        return productRepository.findOutOfStockProducts()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<ProductDTO> getExpiringProducts(LocalDate date) {
        log.debug("Fetching expiring products");
        return productRepository.findExpiringProducts(date)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<ProductDTO> getHighDemandProducts(BigDecimal threshold) {
        log.debug("Fetching high demand products");
        return productRepository.findHighDemandProducts(threshold)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsNeedingReorder() {
        log.debug("Fetching products needing reorder");
        return productRepository.findProductsNeedingReorder()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        log.info("Creating new product: {}", productDTO.getName());
        
        Product product = Product.builder()
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .category(productDTO.getCategory())
                .subcategory(productDTO.getSubcategory())
                .price(productDTO.getPrice())
                .costPrice(productDTO.getCostPrice())
                .stockQuantity(productDTO.getStockQuantity())
                .reorderLevel(productDTO.getReorderLevel())
                .unit(productDTO.getUnit())
                .brand(productDTO.getBrand())
                .supplier(productDTO.getSupplier())
                .expiryDate(productDTO.getExpiryDate())
                .imageUrl(productDTO.getImageUrl())
                .isAvailable(productDTO.getIsAvailable())
                .discountPercentage(productDTO.getDiscountPercentage())
                .aiDemandScore(productDTO.getAiDemandScore())
                .aiReorderSuggestion(productDTO.getAiReorderSuggestion())
                .build();
        
        Product saved = productRepository.save(product);
        
        // Log initial inventory
        logInventoryChange(saved, "INITIAL", saved.getStockQuantity(), 0, saved.getStockQuantity(), "Initial stock", null, "SYSTEM");
        
        return convertToDTO(saved);
    }
    
    @Transactional
    public ProductDTO updateProduct(UUID id, ProductDTO.ProductUpdate update) {
        log.info("Updating product: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        if (update.getName() != null) product.setName(update.getName());
        if (update.getDescription() != null) product.setDescription(update.getDescription());
        if (update.getCategory() != null) product.setCategory(update.getCategory());
        if (update.getSubcategory() != null) product.setSubcategory(update.getSubcategory());
        if (update.getPrice() != null) product.setPrice(update.getPrice());
        if (update.getCostPrice() != null) product.setCostPrice(update.getCostPrice());
        if (update.getStockQuantity() != null) {
            int oldStock = product.getStockQuantity();
            product.setStockQuantity(update.getStockQuantity());
            logInventoryChange(product, "ADJUSTMENT", 
                    update.getStockQuantity() - oldStock, oldStock, 
                    update.getStockQuantity(), "Stock adjustment", null, "SYSTEM");
        }
        if (update.getReorderLevel() != null) product.setReorderLevel(update.getReorderLevel());
        if (update.getUnit() != null) product.setUnit(update.getUnit());
        if (update.getBrand() != null) product.setBrand(update.getBrand());
        if (update.getSupplier() != null) product.setSupplier(update.getSupplier());
        if (update.getExpiryDate() != null) product.setExpiryDate(update.getExpiryDate());
        if (update.getImageUrl() != null) product.setImageUrl(update.getImageUrl());
        if (update.getIsAvailable() != null) product.setIsAvailable(update.getIsAvailable());
        if (update.getDiscountPercentage() != null) product.setDiscountPercentage(update.getDiscountPercentage());
        
        Product updated = productRepository.save(product);
        return convertToDTO(updated);
    }
    
    @Transactional
    public ProductDTO updateStock(UUID id, ProductDTO.StockUpdate stockUpdate) {
        log.info("Updating stock for product {}: {} (type: {})", id, stockUpdate.getQuantityChange(), stockUpdate.getChangeType());
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        int oldStock = product.getStockQuantity();
        int newStock = oldStock + stockUpdate.getQuantityChange();
        
        if (newStock < 0) {
            throw new RuntimeException("Insufficient stock");
        }
        
        product.setStockQuantity(newStock);
        
        // Auto-update reorder suggestion based on stock level
        if (newStock <= product.getReorderLevel()) {
            product.setAiReorderSuggestion(true);
            // Trigger low stock alert
            notificationService.sendLowStockAlert(List.of(product));
        } else {
            product.setAiReorderSuggestion(false);
        }
        
        Product updated = productRepository.save(product);
        
        logInventoryChange(product, stockUpdate.getChangeType(), 
                stockUpdate.getQuantityChange(), oldStock, newStock, 
                stockUpdate.getReason(), null, "SYSTEM");
        
        return convertToDTO(updated);
    }
    
    @Transactional
    public void deleteProduct(UUID id) {
        log.info("Deleting product: {}", id);
        productRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getAveragePriceByCategory(String category) {
        return productRepository.findAveragePriceByCategory(category)
                .orElse(BigDecimal.ZERO);
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getTotalInventoryValue() {
        return productRepository.calculateTotalInventoryValue()
                .orElse(BigDecimal.ZERO);
    }

    @Transactional
    public void applyDynamicPricing() {
        log.info("Applying dynamic pricing strategy");
        List<Product> products = productRepository.findAll();
        
        for (Product product : products) {
            BigDecimal currentDiscount = product.getDiscountPercentage();
            BigDecimal newDiscount = BigDecimal.ZERO;

            // 1. Expiry-based pricing (high priority)
            if (product.getExpiryDate() != null) {
                long daysToExpiry = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), product.getExpiryDate());
                if (daysToExpiry <= 3 && daysToExpiry > 0) {
                    newDiscount = BigDecimal.valueOf(50); // 50% off for items expiring in 3 days
                } else if (daysToExpiry <= 7 && daysToExpiry > 3) {
                    newDiscount = BigDecimal.valueOf(25); // 25% off for items expiring in a week
                }
            }

            // 2. Demand-based pricing (if no expiry discount)
            if (newDiscount.equals(BigDecimal.ZERO) && product.getAiDemandScore() != null) {
                double demand = product.getAiDemandScore().doubleValue();
                if (demand < 2.0) { // Low demand
                    newDiscount = BigDecimal.valueOf(15);
                } else if (demand < 5.0) {
                    newDiscount = BigDecimal.valueOf(5);
                }
            }

            if (!newDiscount.equals(currentDiscount)) {
                product.setDiscountPercentage(newDiscount);
                productRepository.save(product);
                log.debug("Updated discount for {}: {}%", product.getName(), newDiscount);
            }
        }
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
