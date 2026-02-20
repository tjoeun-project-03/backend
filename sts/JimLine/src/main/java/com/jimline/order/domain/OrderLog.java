package com.jimline.order.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_log")
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderLog {
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long logId;

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "order_id")
 private Order order;
 @Enumerated(EnumType.STRING)
 private OrderStatus statusCode;
 private LocalDateTime updateTime;

 @Builder
 public OrderLog(Order order, OrderStatus statusCode) { // 인자 타입 확인
     this.order = order;
     this.statusCode = statusCode;
     this.updateTime = LocalDateTime.now();
 }
}