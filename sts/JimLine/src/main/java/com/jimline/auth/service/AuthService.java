package com.jimline.auth.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority; // 🚀 필수 임포트!
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

import java.util.stream.Collectors;

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

    @Transactional(readOnly = true)
    public boolean isIdDuplicated(String userId) {
        return userRepository.existsById(userId);
    }

    @Transactional
    public void signupShipper(ShipperSignupRequest dto) {
        if (isIdDuplicated(dto.userId())) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }
        User user = saveUser(dto.userId(), dto.userPw(), dto.userName(), dto.email(), dto.corpReg(), dto.phone(), UserRole.SHIPPER, dto.zipcode(), dto.address(), dto.detailAddress());
        Shipper shipper = Shipper.builder().user(user).build();
        shipperRepository.save(shipper);
    }

    @Transactional
    public void signupCarrier(CarrierSignupRequest dto) {
        if (isIdDuplicated(dto.userId())) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }
        User user = saveUser(dto.userId(), dto.userPw(), dto.userName(), dto.email(), dto.corpReg(), dto.phone(), UserRole.CARRIER, dto.zipcode(), dto.address(), dto.detailAddress());
        Carrier carrier = Carrier.builder()
                .user(user).car(dto.car()).carType(dto.carType()).carNum(dto.carNum())
                .carReg(dto.carReg()).license(dto.license()).freezer(dto.freezer()).accepted(0).build();
        carrierRepository.save(carrier);
    }

    private User saveUser(String id, String pw, String name, String email, String corpReg, String phone, UserRole role, String zipcode, String address, String detailAddress) {
        User user = User.builder().userId(id).userPw(passwordEncoder.encode(pw)).userName(name).email(email).corpReg(corpReg).phone(phone).role(role).zipcode(zipcode).address(address).detailAddress(detailAddress).build();
        return userRepository.save(user);
    }

    @Transactional
    public TokenResponse login(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getUserId(), loginRequest.getUserPw());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        TokenResponse tokenResponse = jwtTokenProvider.generateTokenDto(authentication);

        // 🚀 역할(Role) 추출 로직
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_SHIPPER");

        RefreshToken refreshToken = refreshTokenRepository.findById(authentication.getName())
                .map(token -> token.updateToken(tokenResponse.getRefreshToken()))
                .orElse(RefreshToken.builder().userId(authentication.getName()).token(tokenResponse.getRefreshToken()).build());
        refreshTokenRepository.save(refreshToken);

        return TokenResponse.builder()
                .grantType(tokenResponse.getGrantType())
                .accessToken(tokenResponse.getAccessToken())
                .refreshToken(tokenResponse.getRefreshToken())
                .accessTokenExpiresIn(tokenResponse.getAccessTokenExpiresIn())
                .role(role) // 🚀 DTO에 필드가 있어야 에러가 안 납니다!
                .build();
    }

    @Transactional
    public TokenResponse refreshToken(String refreshTokenRequest) {
        if (!jwtTokenProvider.validateToken(refreshTokenRequest)) throw new RuntimeException("유효하지 않은 리프레시 토큰입니다.");
        String userId = jwtTokenProvider.getUserId(refreshTokenRequest);
        RefreshToken savedToken = refreshTokenRepository.findById(userId).orElseThrow(() -> new RuntimeException("로그아웃된 사용자입니다."));
        if (!savedToken.getToken().equals(refreshTokenRequest)) throw new RuntimeException("토큰 정보가 일치하지 않습니다.");
        Authentication authentication = jwtTokenProvider.getAuthentication(refreshTokenRequest);
        TokenResponse tokenResponse = jwtTokenProvider.generateTokenDto(authentication);
        savedToken.updateToken(tokenResponse.getRefreshToken());
        return tokenResponse;
    }

    @Transactional
    public void logout(CustomUserDetails userDetail) {
        refreshTokenRepository.deleteById(userDetail.getUser().getUserId());
    }
}