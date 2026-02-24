package com.jimline.report.dto;

import com.jimline.report.domain.Report;
import com.jimline.report.domain.ReportStatus;
import com.jimline.report.domain.PenaltyType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SanctionResponse {
    private Long reportId;           // 신고 번호
    private String reportedUserId;   // 피신고자(제재 대상) ID
    private String reason;           // 신고 사유
    private String content;          // 신고 상세 내용
    private ReportStatus status;     // 처리 상태 (PROCESSED, REJECTED 등)
    private PenaltyType penalty;     // 제재 종류 (BAN, SUSPENSION 등)
    private String adminComment;     // 관리자 답변/조치 내용
    private LocalDateTime processedAt; // 처리 완료 일시 (updateAt 활용)
    private LocalDateTime endDate;     // 제재 종료 예정일 (있을 경우)

    /**
     * Entity -> DTO 변환 static 메서드
     */
    public static SanctionResponse from(Report report) {
        return SanctionResponse.builder()
                .reportId(report.getId())
                .reportedUserId(report.getReported().getUserId()) // 엔티티의 reported 필드 사용
                .reason(report.getReason())
                .content(report.getContent())
                .status(report.getStatus())
                .penalty(report.getPenalty())
                .adminComment(report.getAdminComment())
                .processedAt(report.getUpdateAt()) // 마지막 수정 시간을 처리 시간으로 간주
                .endDate(report.getEndDate())
                .build();
    }
}