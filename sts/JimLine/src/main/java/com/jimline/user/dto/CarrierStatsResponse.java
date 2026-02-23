package com.jimline.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CarrierStatsResponse {
    private long monthlyRevenue;          // 해당 월 누적 수익 (추가)
    private long totalDeliveryCount;      // 해당 월 완료 건수
    private double monthlyCompletionRate;  // 해당 월 완료율 (%)
    private long totalDistance;            // 해당 월 누적 주행 거리 (km)
}