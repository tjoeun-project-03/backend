package com.jimline.report.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.jimline.user.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Report {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User reporter; // 신고자

    @ManyToOne
    private User reported; // 피신고자

    private String reason;
    private String content;

    @Enumerated(EnumType.STRING)
    private ReportStatus status = ReportStatus.PENDING; // 기본값: 대기중
    
    @Enumerated(EnumType.STRING)
    private PenaltyType penalty = PenaltyType.NONE;

    private String adminComment; // 관리자 답변/조치 내용
    
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createAt;
    
    private LocalDateTime endDate;
    
    @LastModifiedDate
    private LocalDateTime updateAt;
}