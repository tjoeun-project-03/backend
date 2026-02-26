package com.jimline.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {
    // 결제 필수 정보
    private String paymentKey;
    private String invoiceNo; // 토스의 orderId와 매핑
    private Integer price;    // 토스의 amount와 매핑

    // 주문 상세 정보
    private String consigneeName;
    private String consigneeContact;
    private String departure;
    private String arrival;
    private Double weight;
    private String content;
    private Integer duration;
    private Double distance;
    private String startLat;
    private String startLng;
    private String endLat;
    private String endLng;
    private String carType;
}