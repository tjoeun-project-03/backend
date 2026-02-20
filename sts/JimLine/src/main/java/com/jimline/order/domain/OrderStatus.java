package com.jimline.order.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OrderStatus {
    CREATED("주문 생성"),
    ACCEPTED("배차 완료"),
    DEPARTED("출발"),
    ARRIVED("도착"),
    COMPLETED("배송 완료"),
    CANCELED("취소됨");

    private final String description;
}