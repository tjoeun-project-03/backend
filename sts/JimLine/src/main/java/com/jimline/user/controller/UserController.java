package com.jimline.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jimline.global.security.CustomUserDetails;
import com.jimline.user.dto.CarrierResponse;
import com.jimline.user.dto.ShipperResponse;
import com.jimline.user.dto.UserResponse;
import com.jimline.user.service.ProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
	private final ProfileService profileService;

    // 1. 차주 전용 프로필 (차량번호, 차종 등)
    @GetMapping("/me/carrier")
    @PreAuthorize("hasRole('CARRIER')")
    public ResponseEntity<CarrierResponse> getCarrierProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(profileService.getCarrierDetail(userDetails.getUser()));
    }

    // 2. 화주 전용 프로필 (사업자번호, 회사명 등)
    @GetMapping("/me/shipper")
    @PreAuthorize("hasRole('SHIPPER')")
    public ResponseEntity<ShipperResponse> getShipperProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(profileService.getShipperDetail(userDetails.getUser()));
    }

    // 3. 공용 기본 사용자 프로필 (아이디, 이메일, 이름 등)
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(profileService.getProfile(userDetails.getUser()));
    }
}
