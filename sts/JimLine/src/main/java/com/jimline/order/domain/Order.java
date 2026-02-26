package com.jimline.order.domain;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ORDERS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder // 클래스 레벨에 빌더를 두면 모든 필드를 대상으로 빌더가 생성됩니다.
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private String carrierId;
    private String shipperId;
    private LocalDateTime created;
    private Integer price;
    private Integer surcharge;
    private Integer payStatus; 
    private LocalDateTime payTime;
    private LocalDateTime atd;
    private LocalDateTime eta;
    private String invoiceNo;
    private String paymentKey;

    @Enumerated(EnumType.STRING)
    private OrderStatus currentStatus;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private OrderDetail orderDetail;

    // 편의 메서드: 주문 생성 시 기본값 세팅
    public static Order createOrder(String shipperId, Integer price, String invoiceNo, OrderDetail detail, LocalDateTime eta) {
        Order order = Order.builder()
                .shipperId(shipperId)
                .price(price)
                .invoiceNo(invoiceNo)
                .created(LocalDateTime.now())
                .payStatus(0) // 미결제 기본값
                .currentStatus(OrderStatus.CREATED) // 초기 상태
                .eta(eta)
                .build();
        order.setOrderDetail(detail);
        return order;
    }

    public void setOrderDetail(OrderDetail orderDetail) {
        this.orderDetail = orderDetail;
        if (orderDetail != null) {
            orderDetail.setOrder(this);
        }
    }

    public void updateStatus(OrderStatus newStatus) {
        this.currentStatus = newStatus;
    }
    
    public void assignCarrier(String carrierId) {
        this.carrierId = carrierId;
        this.currentStatus = OrderStatus.ACCEPTED; // 수락 시 상태 자동 변경
    }
    public void startDeliveryTime() {
    	this.atd = LocalDateTime.now();
    }
}