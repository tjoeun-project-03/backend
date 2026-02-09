package com.jimline.global.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;
    private final long accessTokenValidity;

    public JwtTokenProvider(
            @Value("${jimline.jwt.secret}") String secretKey,
            @Value("${jimline.jwt.access-exp-seconds}") long accessTokenValidity) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidity = accessTokenValidity * 1000; // 초 단위를 밀리초로 변환
    }

    // 토큰 생성 및 검증 로직은 이전과 동일 (key와 accessTokenValidity 변수 사용)
    public String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.accessTokenValidity);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    // 2. 토큰을 복호화하여 권한 정보 가져오기
    public Authentication getAuthentication(String token) {
        // parserBuilder() 대신 parser()를 사용합니다.
        Claims claims = Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) key) // setSigningKey 대신 verifyWith
                .build()
                .parseSignedClaims(token) // parseClaimsJws 대신 parseSignedClaims
                .getPayload(); // getBody() 대신 getPayload()

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .filter(auth->!auth.trim().isEmpty())
                        .map(auth->{
                        	String role = auth.startsWith("ROLE_") ? auth : "ROLE_"+ auth;
                        	return new SimpleGrantedAuthority(role);
                        })
                        .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
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
}