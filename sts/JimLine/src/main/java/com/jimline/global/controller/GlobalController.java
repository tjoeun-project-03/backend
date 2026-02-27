package com.jimline.global.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jimline.admin.domain.Pricing;
import com.jimline.admin.service.PricingService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GlobalController {
	
	private final PricingService pricingService;
	
	// 현재 요율 설정값 조회 (페이지 로드 시)
    @GetMapping("/pricing")
    public ResponseEntity<Pricing> getPricing() {
        return ResponseEntity.ok(pricingService.getCurrentPolicy());
    }
}
