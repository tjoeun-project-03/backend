package com.jimline.report.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.jimline.report.domain.PenaltyType;
import com.jimline.user.dto.UserResponse;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.jimline.report.domain.Report;
import com.jimline.report.domain.ReportStatus;
import com.jimline.report.dto.ReportRequest;
import com.jimline.report.dto.ReportResponse;
import com.jimline.report.dto.SanctionRequest;
import com.jimline.report.repository.ReportRepository;
import com.jimline.user.domain.User;
import com.jimline.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public void createReport(User reporter, ReportRequest dto) {
        User reportedUser = userRepository.findById(dto.reportedUserId())
            .orElseThrow(() -> new IllegalArgumentException("대상을 찾을 수 없습니다."));

        if (reporter.getUserId().equals(reportedUser.getUserId())) {
            throw new IllegalArgumentException("자기 자신을 신고할 수 없습니다.");
        }

        Report report = Report.builder()
            .reporter(reporter)
            .reported(reportedUser)
            .reason(dto.reason())
            .content(dto.content())
            .status(ReportStatus.PENDING)
            .build();

        reportRepository.save(report);
    }
    
    @Transactional
    public void processReport(Long reportId, SanctionRequest request, ReportStatus targetStatus) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("신고 내역을 찾을 수 없습니다."));

        if (targetStatus == ReportStatus.REJECTED) {
            report.setStatus(ReportStatus.REJECTED);
            report.setAdminComment(request.adminComment());
            return;
        }

        User reportedUser = report.getReported();
        LocalDateTime banUntil = null;

        if (request.penaltyDays() == -1) {
            // 영구 정지 (9999년까지)
            banUntil = LocalDateTime.MAX;
            report.setPenalty(PenaltyType.PERMANENT_BAN);
        } else if (request.penaltyDays() > 0) {
            banUntil = LocalDateTime.now().plusDays(request.penaltyDays());
            if(request.penaltyDays() == 3) report.setPenalty(PenaltyType.SUSPENSION_3);
            else if(request.penaltyDays() == 7) report.setPenalty(PenaltyType.SUSPENSION_7);
        } else if (request.penaltyDays() == 0) {
            report.setPenalty(PenaltyType.WARNING);
        }

        // 유저 정보 업데이트 (User 엔티티에 banUntil 필드가 있어야 함)
        reportedUser.updateBanStatus(banUntil);
        
        // 신고 상태 업데이트
        report.setStatus(ReportStatus.PROCESSED);
        report.setAdminComment(request.adminComment());
        report.setEndDate(banUntil);
    }
    
    @Transactional
    public List<ReportResponse> getMyReports(String userId) {
        return reportRepository.findByReporter_UserIdOrderByCreateAtDesc(userId)
                .stream()
                .map(report -> new ReportResponse(
            		report.getId(),
            		report.getReporter().getUserId(),
                    report.getReporter().getUserName(),
                    report.getReported().getUserId(),
                    report.getReported().getUserName(),
                    report.getReason(),
                    report.getContent(),
                    report.getAdminComment(),
                    report.getPenalty(),
                    report.getStatus(),
                    report.getReported().getBanUntil(), // 피신고자 제재 상태
                    report.getCreateAt()))
                .toList();
    }

    // 2. 신고 상세 내용 조회 (관리자 및 본인용)
    @Transactional
    public ReportResponse getReportDetail(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("해당 신고 내역을 찾을 수 없습니다."));
        
        return new ReportResponse(
        		report.getId(),
        		report.getReporter().getUserId(),
                report.getReporter().getUserName(),
                report.getReported().getUserId(),
                report.getReported().getUserName(),
        	    report.getReason(),
        	    report.getContent(),
        	    report.getAdminComment(),
                report.getPenalty(),
        	    report.getStatus(),
        	    report.getReported().getBanUntil(),
        	    report.getCreateAt()
            );
    }
    @Transactional
    public List<ReportResponse> getAllReports() {
        // 1. 모든 엔티티를 가져와서
        return reportRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                // 2. 아까 만든 ReportResponse(String 위주)로 변환!
                .map(report -> new ReportResponse(
                    report.getId(),
                    report.getReporter().getUserId(),
                    report.getReporter().getUserName(),
                    report.getReported().getUserId(),
                    report.getReported().getUserName(),
                    report.getReason(),
                    report.getContent(),
                    report.getAdminComment(),
                    report.getPenalty(),
                    report.getStatus(),
                    report.getReported().getBanUntil(),
                    report.getCreateAt()
                ))
                .toList();
    }

    @Transactional
    public List<ReportResponse> getBannedUsers() {
        LocalDateTime now = LocalDateTime.now();
        List<Report> activeBanReports = reportRepository.findByStatusAndEndDateGreaterThan(ReportStatus.PROCESSED, now);
        return activeBanReports.stream()
                .map(report -> new ReportResponse(
                        report.getId(),

                        report.getReporter().getUserId(),   // 예: String 타입의 아이디
                        report.getReporter().getUserName(), // 예: 이름 또는 닉네임
                        report.getReported().getUserId(),
                        report.getReported().getUserName(),
                        report.getReason(),
                        report.getContent(),
                        report.getAdminComment(),
                        report.getPenalty(),
                        report.getStatus(),
                        report.getEndDate(),
                        report.getCreateAt()
                ))
                .collect(Collectors.toList());
    }
}