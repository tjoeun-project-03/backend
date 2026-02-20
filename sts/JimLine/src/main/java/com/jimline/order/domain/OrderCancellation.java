package com.jimline.order.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_cancellations")
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderCancellation {

    @Id
    private Long orderId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "order_id")
    private Order order;

    private String canceledBy;    // 취소자 ID (또는 역할)
    private String canceledReason; // 취소 사유
    private LocalDateTime canceledAt;

    public static OrderCancellation create(Order order, String canceledBy, String canceledReason) {
        return OrderCancellation.builder()
                .order(order)
                .canceledBy(canceledBy)
                .canceledReason(canceledReason)
                .canceledAt(LocalDateTime.now())
                .build();
    }
}