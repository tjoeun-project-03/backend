package com.jimline.order.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import com.jimline.order.domain.Order;
import com.jimline.order.domain.OrderCancellation;
import com.jimline.order.domain.OrderDetail;
import com.jimline.order.domain.OrderLog;
import com.jimline.order.domain.OrderStatus;
import com.jimline.order.dto.OrderCancelRequest;
import com.jimline.order.dto.OrderCompleteRequest;
import com.jimline.order.dto.OrderCreateRequest;
import com.jimline.order.dto.OrderResponse;
import com.jimline.order.repository.OrderCancellationRepository;
import com.jimline.order.repository.OrderLogRepository;
import com.jimline.order.repository.OrderRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderLogRepository orderLogRepository;
    private final OrderCancellationRepository cancellationRepository;
    private final TossPaymentService tossPaymentService;
    
    
    // id조회
    @Transactional
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 주문이 존재하지 않습니다. ID: " + orderId));
    }
    
    // 인보이스조회
    @Transactional
    public Order getOrderByInvoiceNo(String invoiceNo) {
        return orderRepository.findByInvoiceNo(invoiceNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문이 없습니다."));
    }
    
    // 상태변경
    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        // 1. 상태 업데이트
        order.updateStatus(newStatus);

        // 2. 로그 기록
        OrderLog log = OrderLog.builder()
                .order(order)
                .statusCode(newStatus)
                .build();
        orderLogRepository.save(log);

        // 3. 알람 및 남은 시간 안내 로직 (요청 사항 반영)
        printTimeRemaining(order, newStatus);
    }
    
    //남은시간계산
    private void printTimeRemaining(Order order, OrderStatus status) {
        if (order.getEta() == null) return;

        LocalDateTime now = LocalDateTime.now(); // 현재 시간: 2026-02-20
        Duration duration = Duration.between(now, order.getEta());
        long hoursLeft = duration.toHours();
        long minutesLeft = duration.toMinutesPart();

        if (status == OrderStatus.ACCEPTED) {
            System.out.printf("[알람] 배차가 완료되었습니다! 지금으로부터 배송 완료(ETA)까지 약 %d시간 %d분 남았습니다.%n", 
                hoursLeft, minutesLeft);
        } else if (status == OrderStatus.DEPARTED) {
            System.out.printf("[알람] 물건이 출발했습니다! 도착까지 남은 시간: %d시간 %d분%n", 
                hoursLeft, minutesLeft);
        }
    }
    
    //주문취소
    @Transactional
    public void cancelOrder(Long orderId, OrderCancelRequest request, String userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        // 이미 완료된 주문 등 취소 불가능한 상태 체크 로직 추가 가능
        if (order.getCurrentStatus() == OrderStatus.COMPLETED) {
            throw new IllegalStateException("이미 완료된 주문은 취소할 수 없습니다.");
        }

        // 1. 주문 상태 업데이트 (OrderStatus.CANCELED 추가 가정)
        order.updateStatus(OrderStatus.CANCELED);

        // 2. 취소 상세 정보 저장
        OrderCancellation cancellation = OrderCancellation.create(
                order, userId, request.getCanceledReason());
        cancellationRepository.save(cancellation);

        System.out.println("주문이 취소되었습니다. 취소 사유: " + request.getCanceledReason());
    }
    
    // [흐름 2] 차주용 목록 조회 (CREATED 상태만)
    public List<OrderResponse> getAvailableOrders() {
        return orderRepository.findByCurrentStatus(OrderStatus.CREATED)
                .stream().map(OrderResponse::from).toList();
    }
    
    // [흐름 3] 주문 수락
    public void acceptOrder(Long orderId, String carrierId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.assignCarrier(carrierId);
        saveLog(order, OrderStatus.ACCEPTED);
    }
    
    // [흐름 5] QR 촬영 후 완료 (송장번호 대조)
    public void completeOrderWithQr(Long orderId, OrderCompleteRequest request) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        
        if (!order.getInvoiceNo().equals(request.getInvoice())) {
            throw new IllegalArgumentException("송장 번호가 일치하지 않습니다. 올바른 QR을 스캔해주세요.");
        }
        
        order.updateStatus(OrderStatus.COMPLETED);
        saveLog(order, OrderStatus.COMPLETED);
    }

    private void saveLog(Order order, OrderStatus status) {
        orderLogRepository.save(OrderLog.builder().order(order).statusCode(status).build());
    }

	public void startDelivery(Long orderId, OrderStatus departed) {
		Order order = orderRepository.findById(orderId).orElseThrow();
		order.startDeliveryTime();
		saveLog(order, OrderStatus.DEPARTED);
	}
	
	// 주문생성
	public Order processOrderAndPayment(OrderCreateRequest dto, String shipperId) {
        // 1. 토스 페이먼츠 결제 승인 요청
        boolean isApproved = tossPaymentService.confirmPayment(
                dto.getPaymentKey(),
                dto.getInvoiceNo(),
                dto.getPrice()
        );

        if (!isApproved) {
            throw new RuntimeException("결제 승인에 실패했습니다.");
        }

        // 2. Order 엔티티 생성
        Order order = Order.builder()
                .shipperId(shipperId)
                .invoiceNo(dto.getInvoiceNo())
                .paymentKey(dto.getPaymentKey())
                .price(dto.getPrice())
                .created(LocalDateTime.now())
                .payTime(LocalDateTime.now())
                .payStatus(1) // 결제 완료
                .currentStatus(OrderStatus.CREATED)
                .build();

        // 3. OrderDetail 엔티티 생성 (Order와 연결)
        OrderDetail detail = OrderDetail.builder()
                .order(order) // 연관관계 설정
                .consigneeName(dto.getConsigneeName())
                .consigneeContact(dto.getConsigneeContact())
                .distance(dto.getDistance())
                .duration(dto.getDuration())
                .carType(dto.getCarType())
                .weight(dto.getWeight())
                .content(dto.getContent())
                .departure(dto.getDeparture())
                .arrival(dto.getArrival())
                .startLat(dto.getStartLat())
                .startLng(dto.getStartLng())
                .endLat(dto.getEndLat())
                .endLng(dto.getEndLng())
                .freezer(0) // 기본값 예시
                .build();

        // 4. Order에 Detail 세팅 (편의 메서드 활용)
        order.setOrderDetail(detail);

        // 5. 저장 (CascadeType.ALL에 의해 detail도 함께 저장됨)
        return orderRepository.save(order);
    }
}