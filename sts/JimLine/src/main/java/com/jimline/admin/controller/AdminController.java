package com.jimline.admin.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jimline.admin.domain.Pricing;
import com.jimline.admin.dto.CarrierAcceptionResponse;
import com.jimline.admin.dto.PricingRequest;
import com.jimline.admin.service.AdminService;
import com.jimline.admin.service.PricingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final PricingService pricingService;

    // 대기 건수 조회
    @GetMapping("/carriers/pending/count")
    public ResponseEntity<Long> getPendingCount() {
        return ResponseEntity.ok(adminService.getPendingCarrierCount());
    }
    
    // 승인 대기 목록 조회
    @GetMapping("/carriers/pending")
    public ResponseEntity<List<CarrierAcceptionResponse>> getPendingCarriers() {
        return ResponseEntity.ok(adminService.getPendingCarriers());
    }
    
    // 차주 승인 실행
    @PostMapping("/carriers/{carrierId}/approve")
    public ResponseEntity<String> approveCarrier(@PathVariable("carrierId") String id) {
        adminService.approveCarrier(id);
        return ResponseEntity.ok("차주 승인이 완료되었습니다.");
    }
    
    @DeleteMapping("/{carrierId}/reject")
    public ResponseEntity<Void> rejectCarrier(@PathVariable("carrierId") String carrierId) {
        adminService.rejectCarrier(carrierId);
        return ResponseEntity.noContent().build();
    }
    
    // 현재 설정값 조회 (페이지 로드 시)
    @GetMapping("/pricing")
    public ResponseEntity<Pricing> getPricing() {
        return ResponseEntity.ok(pricingService.getCurrentPolicy());
    }
    
    // 설정값 저장
    @PostMapping("/pricing")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updatePricing(@RequestBody PricingRequest request) {
        pricingService.savePolicy(request);
        return ResponseEntity.ok("정책이 성공적으로 업데이트되었습니다.");
    }
}