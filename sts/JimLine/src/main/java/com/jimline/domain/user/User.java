package com.jimline.domain.user;

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

    private String phone;

    @Enumerated(EnumType.STRING) // 문자열 그대로 저장 (권장)
    @Column(nullable = false)
    private UserRole role; // SHIPPER, CARRIER, ADMIN 등

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "corp_reg")
    private String corpReg;

    @Builder.Default
    private Integer banned = 0;

    // 생성 시 자동으로 날짜 주입
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}