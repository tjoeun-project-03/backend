package com.jimline.user.dto;

import com.jimline.user.domain.Shipper;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShipperResponse {
    private String userId;
    private String companyName;    // 회사명
    private String representative; // 대표자명

    public static ShipperResponse from(Shipper shipper) {
        return ShipperResponse.builder()
                .userId(shipper.getUserId())
                .companyName(shipper.getCompanyName())
                .representative(shipper.getRepresentative())
                .build();
    }
}