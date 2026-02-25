package com.jimline.order.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_detail")
@Getter 
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderDetail {

    @Id
    private Long orderId; // Order의 ID와 공유함

    @OneToOne
    @MapsId // Order의 PK를 이 엔티티의 PK로 매핑
    @JoinColumn(name = "order_id")
    private Order order;

    private String consigneeName;    // 수령인 이름
    private String consigneeContact; // 수령인 연락처
    private String clientNote;       // 요청 사항
    private Double distance;         // 배송 거리
    
    @Column(columnDefinition = "json") // DB의 json 타입 대응
    private String route;            // 경로 데이터
    
    private Integer duration;        // 예상 소요 시간(분)
    private String carType;          // 차량 종류
    private Double weight;           // 화물 무게
    private String content;          // 화물 내용
    private String departure;        // 출발지 주소
    private String arrival;          // 도착지 주소
    private Integer freezer;         // 냉동 여부 (0: 일반, 1: 냉동)
    private String startLat;
    private String startLng;
    private String endLat;
    private String endLng;

    // 연관관계 편의 메서드
    public void setOrder(Order order) {
        this.order = order;
    }
}