package com.jimline.admin.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.jimline.admin.domain.Pricing;
import com.jimline.admin.dto.PricingRequest;
import com.jimline.admin.repository.PricingRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PricingService {
    private final PricingRepository pricingRepository;

    // 현재 적용 중인 요금 정책 가져오기
    public Pricing getCurrentPolicy() {
        return pricingRepository.findFirstByOrderByIdDesc()
                .orElse(new Pricing()); // 데이터가 없으면 빈 객체 반환
    }

    // 정책 저장 및 수정
    @Transactional
    public void savePolicy(PricingRequest dto) {
        Pricing policy = pricingRepository.findFirstByOrderByIdDesc()
                .orElse(new Pricing());

        policy.setBaseFee(dto.baseFee());
        policy.setWeatherRule(dto.weatherRule());
        policy.setNightRule(dto.nightRule());
        policy.setHolidayRule(dto.holidayRule());
        policy.setUpdatedAt(LocalDateTime.now());

        pricingRepository.save(policy);
    }
}