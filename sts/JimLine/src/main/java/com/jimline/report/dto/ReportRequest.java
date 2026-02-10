package com.jimline.report.dto;

public record ReportRequest(
	    String reportedUserId, // 신고 대상자 ID
	    String reason,       // 신고 사유 (예: 노쇼, 대금 미지급)
	    String content       // 상세 내용
	) {}