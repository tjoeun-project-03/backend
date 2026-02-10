package com.jimline.report.dto;

import java.time.LocalDateTime;

import com.jimline.report.domain.ReportStatus;

public record ReportResponse(
		Long id,
	    String reporterId,
	    String reporterUserName,
	    String reportedUserId,
	    String reportedUserName,
	    String reason,
	    String content,
	    ReportStatus status,
	    LocalDateTime banUntil, // 제재가 있다면 언제까지인지
	    LocalDateTime createdAt
	) {}