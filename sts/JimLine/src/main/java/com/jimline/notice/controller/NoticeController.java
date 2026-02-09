package com.jimline.notice.controller;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jimline.global.security.CustomUserDetails;
import com.jimline.notice.dto.NoticeRequest;
import com.jimline.notice.dto.NoticeResponse;
import com.jimline.notice.service.NoticeService;
import com.jimline.user.domain.User;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;
    
    // 관리자: 공지사항 신규 등록
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // 관리자 권한 확인
    public ResponseEntity<Long> createNotice(
        @RequestBody NoticeRequest dto,
        @AuthenticationPrincipal CustomUserDetails customUserDetails // 현재 로그인한 관리자 정보 (SecurityContext)
    ) {
    	User writer = customUserDetails.getUser();
    	System.out.println("로그인 유저 확인: " + writer); // 여기서 null이 나오는지 확인
        Long noticeId = noticeService.createNotice(dto, writer);
        return ResponseEntity.status(HttpStatus.CREATED).body(noticeId);
    }
    
    @GetMapping
    public ResponseEntity<List<NoticeResponse>> getAllNotices() {
        return ResponseEntity.ok(noticeService.getAllNotices());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<NoticeResponse> getNotice(@PathVariable("id") Long id, 
                                                    HttpServletRequest request, 
                                                    HttpServletResponse response) {
        
        // 1. 쿠키 확인 (중복 조회 방지)
        Cookie[] cookies = request.getCookies();
        boolean hasViewed = false;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("viewed_notice_" + id)) {
                    hasViewed = true;
                    break;
                }
            }
        }

        // 2. 처음 보는 글이면 조회수 증가 및 쿠키 발급
        if (!hasViewed) {
            noticeService.incrementViewCount(id);
            Cookie viewCookie = new Cookie("viewed_notice_" + id, "true");
            viewCookie.setMaxAge(24 * 60 * 60); // 24시간 유지
            viewCookie.setPath("/");
            response.addCookie(viewCookie);
        }

        return ResponseEntity.ok(noticeService.getNoticeDetail(id));
    }

    @PatchMapping("/{id}/pinned")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> togglePinned(@PathVariable("id") Long id) {
        noticeService.togglePinned(id);
        return ResponseEntity.ok().build();
    }
    
    // 관리자: 공지 수정
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateNotice(
        @PathVariable("id") Long id, 
        @RequestBody NoticeRequest dto
    ) {
        noticeService.updateNotice(id, dto);
        return ResponseEntity.ok().build();
    }

    // 관리자: 공지 삭제
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteNotice(@PathVariable("id") Long id) {
        noticeService.deleteNotice(id);
        return ResponseEntity.ok().build();
    }
}