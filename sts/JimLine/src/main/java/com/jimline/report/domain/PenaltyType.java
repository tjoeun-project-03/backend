package com.jimline.report.domain;

public enum PenaltyType {
    NONE("조치 없음"),
    SUSPENSION_7("7일 정지"),
    SUSPENSION_30("30일 정지"),
    PERMANENT_BAN("영구 정지");

    private final String label;
    PenaltyType(String label) { this.label = label; }
}