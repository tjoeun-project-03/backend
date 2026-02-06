package com.jimline.controller.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jimline.dto.auth.LoginRequest;
import com.jimline.dto.user.CarrierSignupRequest;
import com.jimline.dto.user.ShipperSignupRequest;
import com.jimline.service.auth.AuthService;

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
    
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        String token = authService.login(loginRequest);
        return ResponseEntity.ok(token);
    }
}