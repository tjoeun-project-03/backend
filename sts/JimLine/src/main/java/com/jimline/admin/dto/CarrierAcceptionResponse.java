package com.jimline.admin.dto;

import com.jimline.user.domain.CarType;
import com.jimline.user.domain.Carrier;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CarrierAcceptionResponse {
    private String userId;      // 아이디
    private String userName;    // 이름
    private String car;         // 차량 모델 (예: 엑시언트)
    private CarType carType;    // 차종 (예: TON_1)
    private String carNum;      // 차량 번호
    private String corpReg;
    private String carReg;
    private String license;     // 면허 번호
    private Integer freezer;        // 냉동 여부
    private Integer accepted;       // 승인 상태 (0: 대기, 1: 완료)

    // 엔티티를 DTO로 변환하는 정적 팩토리 메서드
    public static CarrierAcceptionResponse from(Carrier carrier) {
        return CarrierAcceptionResponse.builder()
                .userId(carrier.getUserId())
                .userName(carrier.getUser().getUserName())
                .car(carrier.getCar())
                .carType(carrier.getCarType())
                .carNum(carrier.getCarNum())
                .corpReg(carrier.getUser().getCorpReg())
                .carReg(carrier.getCarReg())
                .license(carrier.getLicense())
                .freezer(carrier.getFreezer())
                .accepted(carrier.getAccepted())
                .build();
    }
}