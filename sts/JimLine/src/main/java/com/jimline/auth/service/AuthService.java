package com.jimline.auth.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jimline.auth.domain.RefreshToken;
import com.jimline.auth.dto.LoginRequest;
import com.jimline.auth.dto.TokenResponse;
import com.jimline.auth.repository.RefreshTokenRepository;
import com.jimline.global.auth.JwtTokenProvider;
import com.jimline.global.security.CustomUserDetails;
import com.jimline.user.domain.Carrier;
import com.jimline.user.domain.Shipper;
import com.jimline.user.domain.User;
import com.jimline.user.domain.UserRole;
import com.jimline.user.dto.CarrierSignupRequest;
import com.jimline.user.dto.ShipperSignupRequest;
import com.jimline.user.repository.CarrierRepository;
import com.jimline.user.repository.ShipperRepository;
import com.jimline.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final ShipperRepository shipperRepository;
    private final CarrierRepository carrierRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    
    // 1. 화주 회원가입
    @Transactional
    public void signupShipper(ShipperSignupRequest dto) {
    	//중복체크
    	if (userRepository.existsById(dto.userId())) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }
    	
        // 공통 유저 정보 생성 및 저장
        User user = saveUser(dto.userId(), dto.userPw(), dto.userName(), dto.email(), dto.corpReg(), dto.phone(), UserRole.SHIPPER, dto.zipcode(), dto.address(), dto.detailAddress());

        // 화주 상세 정보 연결 (Builder 패턴 사용 시)
        Shipper shipper = Shipper.builder()
                .user(user)
                .build();
        
        shipperRepository.save(shipper);
    }
    
    // 2. 차주 회원가입
    @Transactional
    public void signupCarrier(CarrierSignupRequest dto) {
    	// 중복체크
    	if (userRepository.existsById(dto.userId())) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }
    	
        // 공통 유저 정보 생성 및 저장
        User user = saveUser(dto.userId(), dto.userPw(), dto.userName(), dto.email(), dto.corpReg(), dto.phone(), UserRole.CARRIER, dto.zipcode(), dto.address(), dto.detailAddress());

        // 차주 상세 정보 연결
        Carrier carrier = Carrier.builder()
                .user(user)
                .car(dto.car())
                .carType(dto.carType())
                .carNum(dto.carNum())
                .carReg(dto.carReg())
                .license(dto.license())
                .freezer(dto.freezer())
                .accepted(0) // 초기 상태: 승인 대기
                .build();
        
        carrierRepository.save(carrier);
    }
    
    // 공통 유저 저장 로직 (중복 제거)
    private User saveUser(String id, String pw, String name, String email, String corpReg, String phone, UserRole role, String zipcode, String address, String detailAddress) {
        User user = User.builder()
                .userId(id)
                .userPw(passwordEncoder.encode(pw))
                .userName(name)
                .email(email)
                .corpReg(corpReg)
                .phone(phone)
                .role(role)
                .zipcode(zipcode)
                .address(address)
                .detailAddress(detailAddress)
                .build();
        return userRepository.save(user);
    }
    
    // 로그인
    @Transactional
    public TokenResponse login(LoginRequest loginRequest) {
        // 1. ID/PW를 기반으로 Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = 
            new UsernamePasswordAuthenticationToken(loginRequest.getUserId(), loginRequest.getUserPw());

        // 2. 실제 검증 (성공 시 인증된 authentication 반환)
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 토큰 세트 생성 (Access + Refresh)
        TokenResponse tokenResponse = jwtTokenProvider.generateTokenDto(authentication);

        // 4. Refresh Token DB 저장 (기존 유저면 업데이트, 신규면 생성)
        // authentication.getName()은 userId를 반환합니다.
        RefreshToken refreshToken = refreshTokenRepository.findById(authentication.getName())
                .map(token -> token.updateToken(tokenResponse.getRefreshToken()))
                .orElse(RefreshToken.builder()
                        .userId(authentication.getName())
                        .token(tokenResponse.getRefreshToken())
                        .build());
        
        refreshTokenRepository.save(refreshToken);

        return tokenResponse;
    }
    
    // 토큰 재발급
    @Transactional
    public TokenResponse refreshToken(String refreshTokenRequest) {
        // 1. Refresh Token 검증 (JwtTokenProvider에 validateToken 메서드 활용)
        if (!jwtTokenProvider.validateToken(refreshTokenRequest)) {
            throw new RuntimeException("유효하지 않은 리프레시 토큰입니다.");
        }

        // 2. 토큰에서 User ID 추출
        // (JwtTokenProvider에 토큰에서 ID를 꺼내는 getUserId 메서드를 추가해야 합니다)
        String userId = jwtTokenProvider.getUserId(refreshTokenRequest);

        // 3. DB에 저장된 토큰과 일치하는지 확인
        RefreshToken savedToken = refreshTokenRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("로그아웃된 사용자입니다."));

        if (!savedToken.getToken().equals(refreshTokenRequest)) {
            throw new RuntimeException("토큰 정보가 일치하지 않습니다.");
        }

        // 4. 새로운 토큰 세트 생성 및 DB 업데이트
        // 인증 객체를 다시 만들기 위해 DB에서 유저 정보를 가져옵니다.
        Authentication authentication = jwtTokenProvider.getAuthentication(refreshTokenRequest);
        TokenResponse tokenResponse = jwtTokenProvider.generateTokenDto(authentication);

        savedToken.updateToken(tokenResponse.getRefreshToken());
        
        return tokenResponse;
    }
    
    // 로그아웃
    @Transactional
    public void logout(CustomUserDetails userDetail) {
        // DB에서 해당 사용자의 리프레시 토큰 삭제
    	String userId = userDetail.getUser().getUserId();
    	refreshTokenRepository.deleteById(userId);
    }
}