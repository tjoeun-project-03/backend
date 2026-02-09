package com.jimline.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jimline.service.admin.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // 대기 건수 조회
    @GetMapping("/carriers/pending/count")
    public ResponseEntity<Long> getPendingCount() {
        return ResponseEntity.ok(adminService.getPendingCarrierCount());
    }

    // 차주 승인 실행
    @PostMapping("/carriers/{carrierId}/approve")
    public ResponseEntity<String> approveCarrier(@PathVariable("carrierId") String id) {
        adminService.approveCarrier(id);
        return ResponseEntity.ok("차주 승인이 완료되었습니다.");
    }
}