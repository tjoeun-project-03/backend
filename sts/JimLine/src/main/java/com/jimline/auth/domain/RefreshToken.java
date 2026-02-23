package com.jimline.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

 @Id
 private String userId; // 사용자 ID를 PK로 사용 (1인당 1토큰 가정)

 @Column(nullable = false)
 private String token;

 public RefreshToken(String userId, String token) {
     this.userId = userId;
     this.token = token;
 }

 public RefreshToken updateToken(String newToken) {
     this.token = newToken;
     return this;
 }
}