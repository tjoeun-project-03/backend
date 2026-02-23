package com.jimline.user.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.jimline.order.repository.OrderRepository;
import com.jimline.user.dto.CarrierStatsResponse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CarrierService {
    private final OrderRepository orderRepository;

    @Transactional
    public CarrierStatsResponse getMonthlyDashboard(String carrierId, int year, int month) {
        Map<String, Object> rawData = orderRepository.getMonthlyStatsRaw(carrierId, year, month);
        
        long totalAttempts = ((Number) rawData.get("TOTAL_ATTEMPTS")).longValue();
        long completedCount = ((Number) rawData.get("COMPLETED_COUNT")).longValue();
        long monthlyRevenue = ((Number) rawData.get("MONTHLY_REVENUE")).longValue();
        long monthlyDist = ((Number) rawData.get("MONTHLY_DIST")).longValue();

        // 완료율 계산
        double completionRate = (totalAttempts == 0) ? 0.0 : 
                                Math.round(((double) completedCount / totalAttempts * 100) * 10) / 10.0;

        return CarrierStatsResponse.builder()
                .monthlyRevenue(monthlyRevenue)
                .totalDeliveryCount(completedCount)
                .monthlyCompletionRate(completionRate)
                .totalDistance(monthlyDist)
                .build();
    }
}