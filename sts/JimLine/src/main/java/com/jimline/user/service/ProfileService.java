package com.jimline.user.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jimline.user.domain.User;
import com.jimline.user.dto.CarrierResponse;
import com.jimline.user.dto.ShipperResponse;
import com.jimline.user.dto.UserResponse;
// 각 리포지토리 필요
import com.jimline.user.repository.CarrierRepository;
import com.jimline.user.repository.ShipperRepository;
import com.jimline.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final CarrierRepository carrierRepository;
    private final ShipperRepository shipperRepository;
    private final UserRepository userRepository;
    // UserRepository는 이미 있다고 가정합니다.

    public CarrierResponse getCarrierDetail(User user) {
        return carrierRepository.findById(user.getUserId())
                .map(CarrierResponse::from)
                .orElseThrow(() -> new RuntimeException("차주 정보를 찾을 수 없습니다."));
    }

    public ShipperResponse getShipperDetail(User user) {
        return shipperRepository.findById(user.getUserId())
                .map(ShipperResponse::from)
                .orElseThrow(() -> new RuntimeException("화주 정보를 찾을 수 없습니다."));
    }

    public UserResponse getProfile(User user) {
        // 관리자는 별도 테이블이 없다면 공통 UserResponse 반환
        return UserResponse.from(user);
    }
    
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::from) // User 엔티티를 UserResponse DTO로 변환
                .collect(Collectors.toList());
    }
}