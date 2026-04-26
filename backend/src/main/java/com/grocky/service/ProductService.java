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
        log.debug("Fetching subcategories for category: {}", category);
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
        log.debug("Autocompleting with query: {}", query);
        return productRepository.findTop10ByNameContainingIgnoreCase(query)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductDTO getProductById(UUID id) {
        log.debug("Fetching product by id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return convertToDTO(product);
    }

    @Transactional
    public ProductDTO createProduct(ProductDTO.CreateProductRequest request) {
        log.info("Creating new product: {}", request.getName());

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .subcategory(request.getSubcategory())
                .price(request.getPrice())
                .costPrice(request.getCostPrice())
                .stockQuantity(request.getStockQuantity())
                .reorderLevel(request.getReorderLevel())
                .unit(request.getUnit())
                .brand(request.getBrand())
                .supplier(request.getSupplier())
                .expiryDate(request.getExpiryDate())
                .imageUrl(request.getImageUrl())
                .isAvailable(request.getIsAvailable())
                .discountPercentage(request.getDiscountPercentage())
                .build();

        Product saved = productRepository.save(product);

        // Log initial inventory
        logInventoryChange(saved, "INITIAL", saved.getStockQuantity(), 0, saved.getStockQuantity(), "Initial stock", null, "SYSTEM");

        return convertToDTO(saved);
    }

    @Transactional
    public ProductDTO updateProduct(UUID id, ProductDTO.ProductUpdate request) {
        log.info("Updating product: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getCategory() != null) product.setCategory(request.getCategory());
        if (request.getSubcategory() != null) product.setSubcategory(request.getSubcategory());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getCostPrice() != null) product.setCostPrice(request.getCostPrice());
        if (request.getStockQuantity() != null) {
            int oldStock = product.getStockQuantity();
            product.setStockQuantity(request.getStockQuantity());
            logInventoryChange(product, "ADJUSTMENT",
                    request.getStockQuantity() - oldStock, oldStock,
                    request.getStockQuantity(), "Stock adjustment", null, "SYSTEM");
        }
        if (request.getReorderLevel() != null) product.setReorderLevel(request.getReorderLevel());
        if (request.getUnit() != null) product.setUnit(request.getUnit());
        if (request.getBrand() != null) product.setBrand(request.getBrand());
        if (request.getSupplier() != null) product.setSupplier(request.getSupplier());
        if (request.getExpiryDate() != null) product.setExpiryDate(request.getExpiryDate());
        if (request.getImageUrl() != null) product.setImageUrl(request.getImageUrl());
        if (request.getIsAvailable() != null) product.setIsAvailable(request.getIsAvailable());
        if (request.getDiscountPercentage() != null) product.setDiscountPercentage(request.getDiscountPercentage());

        Product updated = productRepository.save(product);
        return convertToDTO(updated);
    }

    @Transactional
    public ProductDTO updateStock(UUID id, ProductDTO.StockUpdate stockUpdate) {
        log.info("Updating stock for product {}: {}", id, stockUpdate.getQuantityChange());

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        int oldStock = product.getStockQuantity();
        int newStock = oldStock + stockUpdate.getQuantityChange();
        product.setStockQuantity(newStock);

        // Auto-update reorder suggestion
        if (newStock <= product.getReorderLevel()) {
            product.setAiReorderSuggestion(true);
            notificationService.sendLowStockAlert(List.of(product));
        } else {
            product.setAiReorderSuggestion(false);
        }

        Product updated = productRepository.save(product);

        logInventoryChange(product, stockUpdate.getChangeType(),
                stockUpdate.getQuantityChange(), oldStock, newStock,
                stockUpdate.getReason() != null ? stockUpdate.getReason() : "Stock update", null, "ADMIN");

        return convertToDTO(updated);
    }

    @Transactional
    public void deleteProduct(UUID id) {
        log.info("Deleting product: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        productRepository.delete(product);
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
