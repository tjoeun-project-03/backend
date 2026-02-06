package com.jimline.service.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jimline.domain.user.Carrier;
import com.jimline.domain.user.Shipper;
import com.jimline.domain.user.User;
import com.jimline.domain.user.UserRole;
import com.jimline.dto.auth.LoginRequest;
import com.jimline.dto.user.CarrierSignupRequest;
import com.jimline.dto.user.ShipperSignupRequest;
import com.jimline.global.auth.JwtTokenProvider;
import com.jimline.repository.user.CarrierRepository;
import com.jimline.repository.user.ShipperRepository;
import com.jimline.repository.user.UserRepository;

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
    
    // 1. 화주 회원가입
    @Transactional
    public void signupShipper(ShipperSignupRequest dto) {
    	//중복체크
    	if (userRepository.existsById(dto.userId())) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }
    	
        // 공통 유저 정보 생성 및 저장
        User user = saveUser(dto.userId(), dto.userPw(), dto.userName(), dto.email(), dto.corpReg(), dto.phone(), UserRole.SHIPPER);

        // 화주 상세 정보 연결 (Builder 패턴 사용 시)
        Shipper shipper = Shipper.builder()
                .user(user) // @MapsId에 의해 이 user의 ID가 Shipper의 PK가 됨
                .companyName(dto.companyName())
                .representative(dto.representative())
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
        User user = saveUser(dto.userId(), dto.userPw(), dto.userName(), dto.email(), dto.corpReg(), dto.phone(), UserRole.CARRIER);

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
    private User saveUser(String id, String pw, String name, String email, String corpReg, String phone, UserRole role) {
        User user = User.builder()
                .userId(id)
                .userPw(passwordEncoder.encode(pw))
                .userName(name)
                .email(email)
                .corpReg(corpReg)
                .phone(phone)
                .role(role)
                .build();
        return userRepository.save(user);
    }
    
    @Transactional
    public String login(LoginRequest loginRequest) {
        // 1. ID/PW를 기반으로 Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = 
            new UsernamePasswordAuthenticationToken(loginRequest.getUserId(), loginRequest.getUserPw());

        // 2. 실제 검증 (CustomUserDetailsService의 loadUserByUsername 실행)
        // 여기서 비밀번호 체크까지 내부적으로 이루어집니다.
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성 및 반환
        return jwtTokenProvider.createToken(authentication);
    }
}