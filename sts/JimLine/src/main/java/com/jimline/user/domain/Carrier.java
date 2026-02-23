package com.jimline.user.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "carriers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Carrier {

    @Id
    private String userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
    
    @Enumerated(EnumType.STRING)
    private CarType carType;
    private String carNum;
    private String carReg;
    private String license;
    private Integer freezer;
    private Integer accepted; // 승인 여부 (0:대기, 1:승인)
    private String car;
    
    private double averageRating = 0.0;
    private int reviewCount = 0;

    public void updateRating(double newRating) {
        double totalScore = (this.averageRating * this.reviewCount) + newRating;
        this.reviewCount++;
        this.averageRating = Math.round((totalScore / this.reviewCount) * 10) / 10.0;
    }
    
	public void approve() {
		this.accepted = 1;
	}
}