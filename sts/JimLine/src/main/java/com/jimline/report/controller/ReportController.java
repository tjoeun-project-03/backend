package com.jimline.report.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jimline.global.security.CustomUserDetails;
import com.jimline.report.domain.Report;
import com.jimline.report.domain.ReportStatus;
import com.jimline.report.dto.ReportRequest;
import com.jimline.report.dto.ReportResponse;
import com.jimline.report.dto.SanctionRequest;
import com.jimline.report.service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    // 신고하기
    @PostMapping("/reports")
    public ResponseEntity<String> submitReport(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestBody ReportRequest dto
    ) {
        reportService.createReport(userDetails.getUser(), dto);
        return ResponseEntity.ok("신고가 정상적으로 접수되었습니다.");
    }
    
    // 내 신고 내역 조회
    @GetMapping("/reports/my")
    public ResponseEntity<List<ReportResponse>> getMyReports(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // userDetails에서 현재 로그인한 사용자의 ID를 추출하여 전달
        List<ReportResponse> myReports = reportService.getMyReports(userDetails.getUser().getUserId());
        
        return ResponseEntity.ok(myReports);
    }
    
    // ---------------------------------------- 위는 일반유저 아래는 어드민
    
    // 1. 신고 목록 조회 (전체)
    @GetMapping("/admin/reports")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReportResponse>> getReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    // 2. 제재 승인 (처리 완료)
    @PostMapping("/admin/reports/{reportId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> approveReport(
            @PathVariable("reportId") Long reportId, 
            @RequestBody SanctionRequest request) {
        reportService.processReport(reportId, request, ReportStatus.PROCESSED);
        return ResponseEntity.ok("제재가 적용되었습니다.");
    }

    // 3. 신고 반려
    @PostMapping("/admin/reports/{reportId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> rejectReport(
            @PathVariable("reportId") Long reportId, 
            @RequestBody SanctionRequest request) {
        reportService.processReport(reportId, request, ReportStatus.REJECTED);
        return ResponseEntity.ok("신고가 반려되었습니다.");
    }
}