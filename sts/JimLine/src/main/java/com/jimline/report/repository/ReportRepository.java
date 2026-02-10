package com.jimline.report.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.util.Streamable;

import com.jimline.report.domain.Report;
import com.jimline.report.domain.ReportStatus;
import com.jimline.user.domain.User;

public interface ReportRepository extends JpaRepository<Report, Long> {
	// Reporter 내의 userId 필드 참조
    List<Report> findByReporter_UserId(String userId);
    
    // ReportedUser 내의 userId 필드 참조
    List<Report> findByReported_UserId(String userId);
    
    List<Report> findByStatus(ReportStatus status);

    List<Report> findByReporter_UserIdOrderByCreateAtDesc(String userId);
}