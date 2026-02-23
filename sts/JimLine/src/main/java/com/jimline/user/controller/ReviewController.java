package com.jimline.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jimline.global.security.CustomUserDetails;
import com.jimline.order.dto.ReviewRequest;
import com.jimline.user.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {
	
	private final ReviewService reviewService;
	
	@PostMapping("/{orderId}")
	public ResponseEntity<String> addReview(
	        @PathVariable("orderId") Long orderId,
	        @RequestBody ReviewRequest dto,
	        @AuthenticationPrincipal CustomUserDetails userDetail) {
	    
	    reviewService.createReview(orderId, dto, userDetail);
	    return ResponseEntity.ok("평가가 완료되었습니다.");
	}
}
