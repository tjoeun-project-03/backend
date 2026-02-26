package com.jimline.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class TokenResponse {
 private String grantType;    // 보통 "Bearer"
 private String accessToken;
 private String refreshToken;
 private Long accessTokenExpiresIn; // 선택사항: 만료 시간
 private String role; // 역할 정보 필드 추가 
}