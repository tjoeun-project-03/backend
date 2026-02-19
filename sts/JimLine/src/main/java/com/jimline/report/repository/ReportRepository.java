package com.jimline.report.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jimline.report.domain.PenaltyType;
import com.jimline.report.domain.Report;
import com.jimline.report.domain.ReportStatus;

public interface ReportRepository extends JpaRepository<Report, Long> {
	// Reporter 내의 userId 필드 참조
    List<Report> findByReporter_UserId(String userId);
    
    // ReportedUser 내의 userId 필드 참조
    List<Report> findByReported_UserId(String userId);
    
    List<Report> findByStatus(ReportStatus status);

    List<Report> findByReporter_UserIdOrderByCreateAtDesc(String userId);

    @Query("SELECT r FROM Report r WHERE r.status = :status AND (r.endDate > :now OR r.penalty = :warningType)")
    List<Report> findActiveBansAndWarnings(
            @Param("status") ReportStatus status, 
            @Param("now") LocalDateTime now, 
            @Param("warningType") PenaltyType warningType
    );
}