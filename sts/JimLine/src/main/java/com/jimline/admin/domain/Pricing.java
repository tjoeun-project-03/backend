package com.jimline.admin.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "pricing_policy")
public class Pricing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int baseFee;       // 플랫폼 수수료 (%)
    private int weatherRule;   // 날씨 할증 (%)
    private int nightRule;     // 심야 할증 (%)
    private int holidayRule;   // 휴일 할증 (%)

    private LocalDateTime updatedAt; // 마지막 수정 시간
}