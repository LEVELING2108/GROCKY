package com.grocky.controller;

import com.grocky.dto.ProductDTO;
import com.grocky.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Page<ProductDTO>> getProductsByCategory(@PathVariable String category, Pageable pageable) {
        return ResponseEntity.ok(productService.getProductsByCategory(category, pageable));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        return ResponseEntity.ok(productService.getAllCategories());
    }

    @GetMapping("/categories/{category}/subcategories")
    public ResponseEntity<List<String>> getSubcategoriesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(productService.getSubcategoriesByCategory(category));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductDTO>> searchProducts(@RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok(productService.searchProducts(keyword, pageable));
    }

    @GetMapping("/autocomplete")
    public ResponseEntity<List<ProductDTO>> autocomplete(@RequestParam String query) {
        return ResponseEntity.ok(productService.autocomplete(query));
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO.CreateProductRequest request) {
        return new ResponseEntity<>(productService.createProduct(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable UUID id, @RequestBody ProductDTO.ProductUpdate update) {
        return ResponseEntity.ok(productService.updateProduct(id, update));
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductDTO> updateStock(@PathVariable UUID id, @RequestBody ProductDTO.StockUpdate stockUpdate) {
        return ResponseEntity.ok(productService.updateStock(id, stockUpdate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats/low-stock")
    public ResponseEntity<List<ProductDTO>> getLowStockProducts() {
        return ResponseEntity.ok(productService.getLowStockProducts());
    }

    @GetMapping("/stats/inventory-value")
    public ResponseEntity<BigDecimal> getTotalInventoryValue() {
        return ResponseEntity.ok(productService.getTotalInventoryValue());
    }

    @PostMapping("/admin/apply-dynamic-pricing")
    public ResponseEntity<Void> applyDynamicPricing() {
        productService.applyDynamicPricing();
        return ResponseEntity.ok().build();
    }
}
