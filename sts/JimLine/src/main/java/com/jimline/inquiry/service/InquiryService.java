package com.jimline.inquiry.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.jimline.global.security.CustomUserDetails;
import com.jimline.inquiry.domain.Inquiry;
import com.jimline.inquiry.domain.InquiryStatus;
import com.jimline.inquiry.dto.InquiryCreateRequest;
import com.jimline.inquiry.dto.InquiryResponse;
import com.jimline.inquiry.repository.InquiryRepository;
import com.jimline.user.domain.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class InquiryService {
    private final InquiryRepository inquiryRepository;

    public Long createInquiry(InquiryCreateRequest request, User user) {
        Inquiry inquiry = Inquiry.builder()
                .category(request.category())
                .title(request.title())
                .content(request.content())
                .user(user)
                .build();
        
        return inquiryRepository.save(inquiry).getId();
    }
    
    public List<InquiryResponse> getMyInquiries(User user) {
        return inquiryRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(InquiryResponse::from)
                .toList();
    }
    
    // 관리자용 전체 조회
    @Transactional
    public List<InquiryResponse> findAllInquiries() {
        return inquiryRepository.findAll().stream()
                .map(InquiryResponse::from)
                .toList();
    }

    // 답변 등록
    public void updateAnswer(Long id, String answer, String adminId) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의글이 없습니다."));
        
        // 직접 setAnswer 대신 엔티티의 비즈니스 메서드 사용!
        inquiry.registerAnswer(answer, adminId);
    }

    // 삭제 로직 (권한 체크 포함)
    public void deleteInquiry(Long id, CustomUserDetails userDetails) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의글이 없습니다."));

        // 권한 체크: 관리자가 아니면서, 글 작성자와 로그인한 유저가 다를 경우
        if (!userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")) 
            && !inquiry.getUser().getUserId().equals(userDetails.getUser().getUserId())) {
            throw new AccessDeniedException("본인의 문의글만 삭제할 수 있습니다.");
        }

        inquiryRepository.delete(inquiry);
    }
}