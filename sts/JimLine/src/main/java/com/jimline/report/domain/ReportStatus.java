package com.jimline.report.domain; // 적절한 패키지에 위치

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportStatus {
    PENDING("대기 중"),
    PROCESSED("처리 완료"),
    REJECTED("반려됨");

    private final String description; // 한글 설명이 필요할 때 사용
}