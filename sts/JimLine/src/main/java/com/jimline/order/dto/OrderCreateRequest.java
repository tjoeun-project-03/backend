package com.jimline.order.dto;

import java.time.LocalDateTime;

import com.jimline.order.domain.Order;
import com.jimline.order.domain.OrderDetail;
import com.jimline.order.domain.OrderStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderCreateRequest {
    // 주문 기본 정보
    private String shipperId;
    private Integer price;
    
    // 주문 상세 정보
    private String consigneeName;
    private String consigneeContact;
    private String departure;
    private String arrival;
    private Double weight;
    private String content;
    private Integer freezer;
    private Integer duration; // 분 단위
    private String carType;
    private String clientNote;
    private double distance;
    private int surCharge;

    public Order toEntity(String invoiceNo, String shipperId) {
        // 상세 엔티티 생성
        OrderDetail detail = OrderDetail.builder()
                .consigneeName(consigneeName)
                .consigneeContact(consigneeContact)
                .departure(departure)
                .arrival(arrival)
                .weight(weight)
                .content(content)
                .freezer(freezer)
                .duration(duration)
                .carType(carType)
                .clientNote(clientNote)
                .distance(distance)
                .build();

        // 현재 시각(2026-02-20) 기준으로 도착 예정 시간(ETA) 계산
        // 사용자의 알람 요청 사항 반영
        LocalDateTime calculatedEta = LocalDateTime.now().plusMinutes(duration != null ? duration : 0);

        // 기본 엔티티 생성 및 상세 연결
        Order order = Order.builder()
                .shipperId(shipperId)
                .price(price)
                .invoiceNo(invoiceNo)
                .eta(calculatedEta)
                .currentStatus(OrderStatus.CREATED)
                .created(LocalDateTime.now())
                .orderDetail(detail) // 여기서 연관관계 맺음
                .payStatus(0)
                .surcharge(surCharge)
                .build();
        detail.setOrder(order);
        
        return order;
    }
}