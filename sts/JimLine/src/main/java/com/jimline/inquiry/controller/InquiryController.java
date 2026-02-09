package com.jimline.inquiry.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jimline.global.security.CustomUserDetails;
import com.jimline.inquiry.dto.InquiryCreateRequest;
import com.jimline.inquiry.dto.InquiryResponse;
import com.jimline.inquiry.service.InquiryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    // 1. 문의 작성
    @PostMapping
    public ResponseEntity<Long> createInquiry(
        @RequestBody InquiryCreateRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails // 로그인한 사용자 정보
    ) {
        Long inquiryId = inquiryService.createInquiry(request, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(inquiryId);
    }

    // 2. 내 문의 내역 조회 (목록)
    @GetMapping("/my")
    public ResponseEntity<List<InquiryResponse>> getMyInquiries(
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<InquiryResponse> responses = inquiryService.getMyInquiries(userDetails.getUser());
        return ResponseEntity.ok(responses);
    }
    
    // 1. [관리자] 모든 문의글 조회
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<InquiryResponse>> getAllInquiries() {
        return ResponseEntity.ok(inquiryService.findAllInquiries());
    }

    // 2. [관리자] 답변 등록 및 상태 변경
    @PatchMapping("/admin/{id}/answer")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateAnswer(
    		@PathVariable("id") Long id, 
    		@RequestBody String answer, 
    		@AuthenticationPrincipal CustomUserDetails adminDetails) {
    	inquiryService.updateAnswer(id, answer, adminDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    // 3. [공통] 삭제 기능 (관리자는 전체, 유저는 본인 것만)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInquiry(
    		@PathVariable("id") Long id,
    		@AuthenticationPrincipal CustomUserDetails userDetails) {
        inquiryService.deleteInquiry(id, userDetails);
        return ResponseEntity.noContent().build();
    }
}