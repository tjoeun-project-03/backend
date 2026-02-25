package com.jimline.user.dto;

import java.time.LocalDateTime;

import com.jimline.user.domain.User;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class UserResponse {
    private String userId;
    private LocalDateTime banned;      // 정지 여부
    private String corpReg;      // 사업자 등록 번호
    private String email;
    private String phone;
    private String role;
    private String userName;
    private String zipcode;
    private String address;
    private String detailAddress;
    private LocalDateTime createdAt;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .banned(user.getBanUntil())
                .corpReg(user.getCorpReg())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .userName(user.getUserName())
                .createdAt(user.getCreatedAt())
                .zipcode(user.getZipcode())
                .address(user.getAddress())
                .detailAddress(user.getDetailAddress())
                .build();
    }
}