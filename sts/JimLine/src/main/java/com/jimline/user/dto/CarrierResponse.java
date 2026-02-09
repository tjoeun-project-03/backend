package com.jimline.user.dto;

import com.jimline.user.domain.Carrier;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CarrierResponse {
    private String userId;
    private Integer accepted;   // 수락 건수 (NUMBER)
    private String car;         // 차종/모델
    private String carNum;      // 차량 번호
    private String carReg;      // 차량 등록 정보
    private String carType;     // 차량 유형
    private Integer freezer;    // 냉동기 유무 (0 또는 1)
    private String license;     // 자격증 정보
    private Double rating;      // 평점 (BINARY_DOUBLE)

    public static CarrierResponse from(Carrier carrier) {
        return CarrierResponse.builder()
                .userId(carrier.getUserId())
                .accepted(carrier.getAccepted())
                .car(carrier.getCar())
                .carNum(carrier.getCarNum())
                .carReg(carrier.getCarReg())
                .carType(carrier.getCarType() != null ? carrier.getCarType().name() : null)
                .freezer(carrier.getFreezer())
                .license(carrier.getLicense())
                .rating(carrier.getRating())
                .build();
    }
}