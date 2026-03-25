package com.grocky.controller;

import com.grocky.dto.ProductDTO;
import com.grocky.dto.ResponseDTO;
import com.grocky.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/personal/{customerId}")
    public ResponseEntity<ResponseDTO<List<ProductDTO>>> getPersonalized(@PathVariable UUID customerId, @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(ResponseDTO.success(recommendationService.getPersonalizedRecommendations(customerId, limit), "Personalized recommendations retrieved"));
    }

    @GetMapping("/bought-together/{productId}")
    public ResponseEntity<ResponseDTO<List<ProductDTO>>> getBoughtTogether(@PathVariable UUID productId, @RequestParam(defaultValue = "4") int limit) {
        return ResponseEntity.ok(ResponseDTO.success(recommendationService.getFrequentlyBoughtTogether(productId, limit), "Frequently bought together items retrieved"));
    }

    @GetMapping("/trending")
    public ResponseEntity<ResponseDTO<List<ProductDTO>>> getTrending(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ResponseDTO.success(recommendationService.getTrendingProducts(limit), "Trending products retrieved"));
    }
}
