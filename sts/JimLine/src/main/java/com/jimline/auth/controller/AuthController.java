package com.jimline.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jimline.auth.dto.LoginRequest;
import com.jimline.auth.dto.TokenResponse;
import com.jimline.auth.service.AuthService;
import com.jimline.global.security.CustomUserDetails;
import com.jimline.user.dto.CarrierSignupRequest;
import com.jimline.user.dto.ShipperSignupRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    
    // 1. 화주 회원가입
    @PostMapping("/signup/shipper")
    public ResponseEntity<String> signupShipper(@RequestBody ShipperSignupRequest dto) {
        authService.signupShipper(dto);
        return ResponseEntity.ok("화주 회원가입 성공");
    }

    // 2. 차주 회원가입
    @PostMapping("/signup/carrier")
    public ResponseEntity<String> signupCarrier(@RequestBody CarrierSignupRequest dto) {
        authService.signupCarrier(dto);
        return ResponseEntity.ok("차주 회원가입 성공");
    }
    
    // 로그인
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        TokenResponse tokenResponse = authService.login(loginRequest);
        return ResponseEntity.ok(tokenResponse);
    }
    

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal CustomUserDetails userDetail) {
        authService.logout(userDetail);
        return ResponseEntity.ok("로그아웃 성공");
    }

    // 토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody String refreshToken) {
        TokenResponse tokenResponse = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(tokenResponse);
    }
}