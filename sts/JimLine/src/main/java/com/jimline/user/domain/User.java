package com.jimline.user.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @Column(name = "user_id", length = 50)
    private String userId; // varchar NOT NULL

    @Column(name = "user_name", nullable = false, length = 100)
    private String userName;

    @Column(name = "user_pw", nullable = false)
    private String userPw;

    @Column(nullable = false, unique = true)
    private String email;
    
    private String zipcode;
    private String address;
    @Column(name = "detail_address")
    private String detailAddress;
    private String phone;

    @Enumerated(EnumType.STRING) // 문자열 그대로 저장 (권장)
    @Column(nullable = false)
    private UserRole role; // SHIPPER, CARRIER, ADMIN 등

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "corp_reg")
    private String corpReg;

    private LocalDateTime banUntil;
    
    // 제재여부확인
    public boolean isBanned() {
        if (this.banUntil == null) return false;
        return LocalDateTime.now().isBefore(this.banUntil);
    }
    
    // 제재 기간 업데이트 (null을 넣으면 즉시 해제)
    public void updateBanStatus(LocalDateTime banUntil) {
        this.banUntil = banUntil;
    }

    // 생성 시 자동으로 날짜 주입
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}