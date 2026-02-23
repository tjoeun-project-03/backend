package com.jimline.user.service;

import org.springframework.stereotype.Service;

import com.jimline.global.security.CustomUserDetails;
import com.jimline.order.domain.Order;
import com.jimline.order.domain.Review;
import com.jimline.order.dto.ReviewRequest;
import com.jimline.order.repository.OrderRepository;
import com.jimline.user.domain.Carrier;
import com.jimline.user.domain.Shipper;
import com.jimline.user.repository.CarrierRepository;
import com.jimline.user.repository.ReviewRepository;
import com.jimline.user.repository.ShipperRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final CarrierRepository carrierRepository;
    private final ShipperRepository shipperRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public void createReview(Long orderId, ReviewRequest dto, CustomUserDetails userDetail) {
        // 1. 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        // 2. 권한 검증: 이 주문의 화주 ID와 현재 로그인한 유저 ID가 일치하는지 확인
        String currentUserId = userDetail.getUser().getUserId();
        if (!order.getShipperId().equals(currentUserId)) {
            throw new RuntimeException("해당 주문에 대해 리뷰를 작성할 권한이 없습니다.");
        }

        // 3. 차주 객체 조회 (평점 업데이트용)
        Carrier carrier = carrierRepository.findById(order.getCarrierId())
                .orElseThrow(() -> new RuntimeException("차주 정보를 찾을 수 없습니다."));

        // 4. 화주 객체 조회
        Shipper shipper = shipperRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("화주 정보를 찾을 수 없습니다."));

        // 5. 리뷰 저장 및 평점 업데이트
        Review review = Review.builder()
                .order(order)
                .shipper(shipper)
                .carrier(carrier)
                .rating(dto.getRating())
                .content(dto.getContent())
                .build();
        
        reviewRepository.save(review);
        carrier.updateRating(dto.getRating());
    }
}