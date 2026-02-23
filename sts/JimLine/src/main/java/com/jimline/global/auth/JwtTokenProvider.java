package com.jimline.global.auth;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.jimline.auth.dto.TokenResponse;
import com.jimline.global.security.CustomUserDetails;
import com.jimline.global.service.CustomUserDetailsService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtTokenProvider(
            @Value("${jimline.jwt.secret}") String secretKey,
            @Value("${jimline.jwt.access-exp-seconds}") long accessTokenValidity,
            @Value("${jimline.jwt.refresh-exp-seconds}") long refreshTokenValidity,
            CustomUserDetailsService customUserDetailsService) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidity = accessTokenValidity * 1000; // 초 단위를 밀리초로 변환
        this.refreshTokenValidity = refreshTokenValidity * 1000;
        this.customUserDetailsService = customUserDetailsService;
    }

    public TokenResponse generateTokenDto(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = userDetails.getUser().getUserId();

        long now = (new Date()).getTime();
        
        // 1. Access Token 생성 (기존 로직 유지)
        String accessToken = Jwts.builder()
                .subject(userId)
                .claim("auth", authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(",")))
                .expiration(new Date(now + accessTokenValidity))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        // 2. Refresh Token 생성 (권한 정보 없이 만료일만 길게)
        String refreshToken = Jwts.builder()
                .subject(userId)
                .expiration(new Date(now + refreshTokenValidity))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return TokenResponse.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 2. 토큰을 복호화하여 권한 정보 가져오기
    public Authentication getAuthentication(String token) {
        // parserBuilder() 대신 parser()를 사용합니다.
        Claims claims = Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) key) // setSigningKey 대신 verifyWith
                .build()
                .parseSignedClaims(token) // parseClaimsJws 대신 parseSignedClaims
                .getPayload(); // getBody() 대신 getPayload()

     // 1. 토큰에서 사용자 식별값(Email 등) 추출
        String email = claims.getSubject();

        // 2. DB에서 실제 유저 정보를 포함한 CustomUserDetails 가져오기
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        // 3. 인증 객체 생성 시 userDetails 전달
        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
    }

    // 3. 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) key)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.info("유효하지 않은 JWT 토큰: {}", e.getMessage());
        }
        return false;
    }
    
    public String getUserId(String token) {
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}