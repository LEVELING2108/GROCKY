package com.grocky.controller;

import com.grocky.dto.ProductDTO;
import com.grocky.dto.ResponseDTO;
import com.grocky.service.ProductService;
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
@RequestMapping("/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ResponseDTO<List<ProductDTO>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        Page<ProductDTO> productPage = productService.getAllProducts(PageRequest.of(page, size));
        return ResponseEntity.ok(ResponseDTO.success(productPage.getContent(), "Products retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<ProductDTO>> getProduct(@PathVariable UUID id) {
        return ResponseEntity.ok(ResponseDTO.success(productService.getProductById(id), "Product retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<ProductDTO>> createProduct(@Valid @RequestBody ProductDTO.CreateProductRequest request) {
        return ResponseEntity.ok(ResponseDTO.success(productService.createProduct(request), "Product created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<ProductDTO>> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody ProductDTO.ProductUpdate request) {
        return ResponseEntity.ok(ResponseDTO.success(productService.updateProduct(id, request), "Product updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<Void>> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ResponseDTO.success(null, "Product deleted successfully"));
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<ResponseDTO<ProductDTO>> updateStock(
            @PathVariable UUID id,
            @RequestBody ProductDTO.StockUpdate stockUpdate) {
        return ResponseEntity.ok(ResponseDTO.success(productService.updateStock(id, stockUpdate), "Stock updated successfully"));
    }
}
