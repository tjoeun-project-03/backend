package com.jimline.report.dto;

public record SanctionRequest(
	    int penaltyDays,      // 제재 일수 (0: 경고, -1: 영구정지, 그외: 기간정지)
	    String adminComment   // 관리자의 처리 의견
	) {}