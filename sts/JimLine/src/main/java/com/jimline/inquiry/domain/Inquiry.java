package com.jimline.inquiry.domain;

import java.time.LocalDateTime;

import com.jimline.global.entity.BaseTimeEntity;
import com.jimline.user.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Enum 대신 String으로 변경하여 프론트에서 주는 대로 저장합니다.
    @Column(nullable = false)
    private String category; 

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "CLOB")
    private String content;

    @Enumerated(EnumType.STRING)
    private InquiryStatus status = InquiryStatus.WAITING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(columnDefinition = "CLOB")
    private String answer;
    
    @Column(name = "admin_id")
    private String adminId; // 답변을 작성한 관리자 ID

    private LocalDateTime answeredAt; // 답변 등록 시각
    
    @Builder
    public Inquiry(String category, String title, String content, User user) {
        this.category = category;
        this.title = title;
        this.content = content;
        this.user = user;
    }
    
    // 답변 등록용 편의 메서드
    public void registerAnswer(String answer, String adminId) {
        this.answer = answer;
        this.adminId = adminId;
        this.answeredAt = LocalDateTime.now();
        this.status = InquiryStatus.COMPLETED;
    }
}