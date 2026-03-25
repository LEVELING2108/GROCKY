package com.grocky.controller;

import com.grocky.dto.ReviewDTO;
import com.grocky.dto.ResponseDTO;
import com.grocky.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<ResponseDTO<Page<ReviewDTO>>> getReviewsByProduct(@PathVariable UUID productId, Pageable pageable) {
        return ResponseEntity.ok(ResponseDTO.success(reviewService.getReviewsByProduct(productId, pageable), "Product reviews retrieved successfully"));
    }

    @GetMapping("/product/{productId}/summary")
    public ResponseEntity<ResponseDTO<ReviewDTO.ReviewSummary>> getProductReviewSummary(@PathVariable UUID productId) {
        return ResponseEntity.ok(ResponseDTO.success(reviewService.getProductReviewSummary(productId), "Product review summary retrieved successfully"));
    }

    @PostMapping("/customer/{customerId}")
    public ResponseEntity<ResponseDTO<ReviewDTO>> createReview(@PathVariable UUID customerId, @RequestBody ReviewDTO.CreateReview createReview) {
        return ResponseEntity.ok(ResponseDTO.success(reviewService.createReview(customerId, createReview), "Review created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<ReviewDTO>> updateReview(@PathVariable UUID id, @RequestBody ReviewDTO.UpdateReview updateReview) {
        return ResponseEntity.ok(ResponseDTO.success(reviewService.updateReview(id, updateReview), "Review updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<Void>> deleteReview(@PathVariable UUID id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok(ResponseDTO.success(null, "Review deleted successfully"));
    }
}
