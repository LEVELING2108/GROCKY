package com.grocky.service;

import com.grocky.dto.ReviewDTO;
import com.grocky.entity.Customer;
import com.grocky.entity.Order;
import com.grocky.entity.OrderItem;
import com.grocky.entity.Product;
import com.grocky.entity.Review;
import com.grocky.repository.CustomerRepository;
import com.grocky.repository.OrderRepository;
import com.grocky.repository.ProductRepository;
import com.grocky.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getReviewsByProduct(UUID productId, Pageable pageable) {
        log.debug("Fetching reviews for product: {}", productId);
        return reviewRepository.findByProductId(productId, pageable)
                .map(this::convertToDTO);
    }
    
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getReviewsByCustomer(UUID customerId, Pageable pageable) {
        log.debug("Fetching reviews by customer: {}", customerId);
        return reviewRepository.findByCustomerId(customerId, pageable)
                .map(this::convertToDTO);
    }
    
    @Transactional(readOnly = true)
    public ReviewDTO.ReviewSummary getProductReviewSummary(UUID productId) {
        log.debug("Fetching review summary for product: {}", productId);
        
        Double averageRating = reviewRepository.findAverageRatingByProduct(productId)
                .orElse(0.0);
        Long totalReviews = reviewRepository.countReviewsByProduct(productId);
        Long verifiedReviews = reviewRepository.countVerifiedReviewsByProduct(productId);
        
        List<Object[]> distribution = reviewRepository.findRatingDistributionByProduct(productId);
        ReviewDTO.RatingDistribution ratingDist = new ReviewDTO.RatingDistribution(0L, 0L, 0L, 0L, 0L);
        
        for (Object[] row : distribution) {
            Integer rating = (Integer) row[0];
            Long count = (Long) row[1];
            switch (rating) {
                case 5 -> ratingDist.setFiveStars(count);
                case 4 -> ratingDist.setFourStars(count);
                case 3 -> ratingDist.setThreeStars(count);
                case 2 -> ratingDist.setTwoStars(count);
                case 1 -> ratingDist.setOneStar(count);
            }
        }
        
        return ReviewDTO.ReviewSummary.builder()
                .productId(productId)
                .averageRating(averageRating)
                .totalReviews(totalReviews)
                .verifiedReviews(verifiedReviews)
                .distribution(ratingDist)
                .build();
    }
    
    @Transactional
    public ReviewDTO createReview(UUID customerId, ReviewDTO.CreateReview createReview) {
        log.info("Creating review for product {} by customer {}", createReview.getProductId(), customerId);
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        Product product = productRepository.findById(createReview.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        // Check if review already exists
        Optional<Review> existingReview = reviewRepository.findByProductAndCustomer(
                createReview.getProductId(), customerId);
        if (existingReview.isPresent()) {
            throw new RuntimeException("You have already reviewed this product");
        }
        
        // Check if customer purchased this product (verified purchase)
        boolean isVerifiedPurchase = checkVerifiedPurchase(customerId, createReview.getProductId());
        
        Review review = Review.builder()
                .product(product)
                .customer(customer)
                .rating(createReview.getRating())
                .comment(createReview.getComment())
                .isVerifiedPurchase(isVerifiedPurchase)
                .build();
        
        Review saved = reviewRepository.save(review);
        log.info("Review created successfully");
        
        return convertToDTO(saved);
    }
    
    @Transactional
    public ReviewDTO updateReview(UUID id, ReviewDTO.UpdateReview updateReview) {
        log.info("Updating review: {}", id);
        
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        
        if (updateReview.getRating() != null) {
            review.setRating(updateReview.getRating());
        }
        if (updateReview.getComment() != null) {
            review.setComment(updateReview.getComment());
        }
        
        Review updated = reviewRepository.save(review);
        return convertToDTO(updated);
    }
    
    @Transactional
    public void deleteReview(UUID id) {
        log.info("Deleting review: {}", id);
        reviewRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public List<Object[]> getTopRatedProducts(Pageable pageable) {
        return reviewRepository.findTopRatedProducts(pageable);
    }
    
    private boolean checkVerifiedPurchase(UUID customerId, UUID productId) {
        // Check if customer has a delivered order containing this product
        List<Order> orders = orderRepository.findByCustomerId(customerId, Pageable.unpaged())
                .stream()
                .filter(o -> "DELIVERED".equals(o.getStatus()))
                .toList();
        
        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                if (item.getProduct().getId().equals(productId)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private ReviewDTO convertToDTO(Review review) {
        return ReviewDTO.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .productName(review.getProduct().getName())
                .customerId(review.getCustomer().getId())
                .customerName(review.getCustomer().getName())
                .rating(review.getRating())
                .comment(review.getComment())
                .isVerifiedPurchase(review.getIsVerifiedPurchase())
                .createdAt(review.getCreatedAt() != null ? 
                        java.time.LocalDateTime.ofInstant(review.getCreatedAt(), java.time.ZoneId.systemDefault()) : null)
                .build();
    }
}
