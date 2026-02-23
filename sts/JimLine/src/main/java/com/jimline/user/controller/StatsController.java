package com.jimline.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jimline.global.security.CustomUserDetails;
import com.jimline.user.dto.CarrierStatsResponse;
import com.jimline.user.service.CarrierService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {
    private final CarrierService carrierService;

    @GetMapping("/carrier")
    public ResponseEntity<CarrierStatsResponse> getStats(
            @AuthenticationPrincipal CustomUserDetails userDetail,
            @RequestParam(name = "year") int year,
            @RequestParam(name = "month") int month) {
        
        return ResponseEntity.ok(carrierService.getMonthlyDashboard(
                userDetail.getUser().getUserId(), year, month));
    }
}