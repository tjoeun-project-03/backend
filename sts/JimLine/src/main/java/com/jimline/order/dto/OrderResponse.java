package com.jimline.order.dto;

import java.time.Duration;
import java.time.LocalDateTime;

import com.jimline.order.domain.Order;
import com.jimline.order.domain.OrderDetail;
import com.jimline.order.domain.OrderStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderResponse {
    // 주문 기본
    private Long orderId;
    private String invoiceNo;
    private OrderStatus status;
    private LocalDateTime created;
    private Integer price;

    // 주문 상세 (OrderDetail 정보 통합)
    private String departure;
    private String arrival;
    private String consigneeName;
    private String consigneeContact;
    private Double weight;
    private String content;
    private String clientNote;
    private Double distance;
    private Integer freezer;
    private String startLat;
    private String startLng;
    private String endLat;
    private String endLng;

    // 시간 관련 알람 정보
    private LocalDateTime eta;
    private String remainingTime; // [2025-08-05] 남은 시간 안내용

    public static OrderResponse from(Order order) {
        OrderDetail detail = order.getOrderDetail();
        
        // 남은 시간 계산 (현재 시각: 2026-02-20 11:14)
        String timeLeft = "정보 없음";
        if (order.getEta() != null) {
            Duration duration = Duration.between(LocalDateTime.now(), order.getEta());
            long h = duration.toHours();
            long m = duration.toMinutesPart();
            timeLeft = (h >= 0) ? String.format("%d시간 %d분 남음", h, m) : "도착 예정 시간 경과";
        }

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .invoiceNo(order.getInvoiceNo())
                .status(order.getCurrentStatus())
                .created(order.getCreated())
                .price(order.getPrice())
                // 상세 정보 매핑
                .departure(detail.getDeparture())
                .arrival(detail.getArrival())
                .consigneeName(detail.getConsigneeName())
                .consigneeContact(detail.getConsigneeContact())
                .weight(detail.getWeight())
                .content(detail.getContent())
                .clientNote(detail.getClientNote())
                .distance(detail.getDistance())
                .freezer(detail.getFreezer())
                // 시간 알람
                .eta(order.getEta())
                .remainingTime(timeLeft)
                .startLat(order.getOrderDetail().getStartLat())
                .startLng(order.getOrderDetail().getStartLng())
                .endLat(order.getOrderDetail().getEndLat())
                .endLng(order.getOrderDetail().getEndLng())
                .build();
    }
}