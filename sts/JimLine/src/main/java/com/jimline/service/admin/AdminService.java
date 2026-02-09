package com.jimline.service.admin;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jimline.domain.user.Carrier;
import com.jimline.dto.admin.CarrierAcceptionResponse;
import com.jimline.repository.user.CarrierRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final CarrierRepository carrierRepository;

    // 1. 승인 대기 중인 차주 수 조회
    @Transactional
    public long getPendingCarrierCount() {
        return carrierRepository.countByAccepted(0);
    }

    // 2. 승인 대기 중인 차주 목록 조회 (필요 시)
    @Transactional
    public List<CarrierAcceptionResponse> getPendingCarriers() {
        return carrierRepository.findAllByAcceptedWithUser(0).stream()
                .map(CarrierAcceptionResponse::from) // DTO 변환 로직 필요
                .toList();
    }

    // 3. 차주 승인 처리
    @Transactional
    public void approveCarrier(String carrierId) {
        Carrier carrier = carrierRepository.findById(carrierId)
                .orElseThrow(() -> new RuntimeException("해당 차주를 찾을 수 없습니다."));
        
        carrier.approve(); // 승인 완료 상태로 변경
        // 별도의 save 없이도 @Transactional에 의해 더티 체킹으로 업데이트됩니다.
    }
}