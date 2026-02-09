package com.jimline.inquiry.dto;

import java.time.LocalDateTime;

import com.jimline.inquiry.domain.Inquiry;
import com.jimline.inquiry.domain.InquiryStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InquiryResponse{
	Long id;
    String category;
    String title;
    String content;
    String answer;
    String adminId;
    LocalDateTime answeredAt;
    String user;
    InquiryStatus status; // 조회할 때는 보여줘야 함!
    LocalDateTime createdAt;
    
    public static InquiryResponse from(Inquiry inquiry) {
        return InquiryResponse.builder()
                .id(inquiry.getId())
                .title(inquiry.getTitle())
                .content(inquiry.getContent())
                .answer(inquiry.getAnswer())
                .status(inquiry.getStatus())
                .user(inquiry.getUser().getUserId())
                .adminId(inquiry.getAdminId())
                .answeredAt(inquiry.getAnsweredAt())
                .createdAt(inquiry.getCreatedAt())
                .category(inquiry.getCategory())
                .build();
    }
}