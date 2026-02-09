package com.jimline.inquiry.domain;

public enum InquiryStatus {
    WAITING("답변 대기"),
    COMPLETED("답변 완료");

    private final String description;

    InquiryStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}