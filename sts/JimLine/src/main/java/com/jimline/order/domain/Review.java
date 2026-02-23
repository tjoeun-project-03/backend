package com.jimline.order.domain;

import com.jimline.user.domain.Carrier;
import com.jimline.user.domain.Shipper;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//package com.jimline.order.domain;
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Review {
 @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @OneToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "order_id")
 private Order order;

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "shipper_id")
 private Shipper shipper;

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "carrier_id")
 private Carrier carrier;

 private double rating;
 private String content;
}