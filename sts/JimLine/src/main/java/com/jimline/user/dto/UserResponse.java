package com.jimline.user.dto;

import java.time.LocalDateTime;

import com.jimline.user.domain.User;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private String userId;
    private Integer banned;      // 정지 여부
    private String corpReg;      // 사업자 등록 번호
    private String email;
    private String phone;
    private String role;
    private String userName;
    private LocalDateTime createdAt;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .banned(user.getBanned())
                .corpReg(user.getCorpReg())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .userName(user.getUserName())
                .createdAt(user.getCreatedAt())
                .build();
    }
}