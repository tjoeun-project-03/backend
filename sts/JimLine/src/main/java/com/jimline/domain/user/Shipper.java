package com.jimline.domain.user;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shippers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Shipper {

    @Id
    private String userId; // User의 userId를 PK이자 FK로 사용

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // User의 ID를 그대로 자신의 PK로 매핑
    @JoinColumn(name = "user_id")
    private User user;

    private String companyName;
    private String representative;
}