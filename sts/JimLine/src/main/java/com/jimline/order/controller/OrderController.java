package com.jimline.order.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jimline.global.security.CustomUserDetails;
import com.jimline.global.util.InvoiceGenerator;
import com.jimline.order.domain.Order;
import com.jimline.order.domain.OrderStatus;
import com.jimline.order.dto.OrderCancelRequest;
import com.jimline.order.dto.OrderCompleteRequest;
import com.jimline.order.dto.OrderCreateRequest;
import com.jimline.order.dto.OrderResponse;
import com.jimline.order.repository.OrderRepository;
import com.jimline.order.service.OrderService;
import com.jimline.user.dto.ShipmentSummary;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final InvoiceGenerator invoiceGenerator;
    
    //기본키(주문id)조회
    @GetMapping("/id/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable("orderId") Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(OrderResponse.from(order));
    }
    
    //인보이스조회
    @GetMapping("/invoice/{invoiceNo}")
    public ResponseEntity<OrderResponse> getOrderByInvoice(@PathVariable("invoiceNo") String invoiceNo) {
        Order order = orderService.getOrderByInvoiceNo(invoiceNo);
        return ResponseEntity.ok(OrderResponse.from(order));
    }
    
    // 상태변경
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<String> updateStatus(
            @PathVariable("orderId") Long orderId, 
            @RequestParam OrderStatus status) {
        
        orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok("상태가 " + status + "(으)로 변경되었습니다.");
    }
    
    //주문취소
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(
            @PathVariable("orderId") Long orderId,
            @RequestBody OrderCancelRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        orderService.cancelOrder(orderId, request, userDetails.getUser().getUserId());
        return ResponseEntity.ok("주문이 정상적으로 취소되었습니다.");
    }
    
    // 주문 목록 조회 (차주 - 대기 중인 주문만)
    @GetMapping("/available")
    public ResponseEntity<List<OrderResponse>> getAvailableOrders() {
        return ResponseEntity.ok(orderService.getAvailableOrders());
    }
    
    // 주문 수락 (차주)
    @PatchMapping("/{orderId}/accept")
    public ResponseEntity<String> acceptOrder(
            @PathVariable("orderId") Long orderId, 
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        orderService.acceptOrder(orderId, userDetails.getUser().getUserId());
        return ResponseEntity.ok("주문이 수락되었습니다. 배송을 시작해 주세요.");
    }
    
    // 주문 수행 (차주 - 배송 시작)
    @PatchMapping("/{orderId}/pickup")
    public ResponseEntity<String> startDelivery(@PathVariable("orderId") Long orderId) {
        orderService.startDelivery(orderId, OrderStatus.DEPARTED);
        return ResponseEntity.ok("배송이 시작되었습니다.");
    }
    
    // 배송 완료 (QR 스캔 대용 - 추후 invoiceNo 검증으로 수정)
    @PostMapping("/{orderId}/complete")
    @PreAuthorize("hasRole('CARRIER')")
    public ResponseEntity<String> completeOrder(
            @PathVariable("orderId") Long orderId,
            @RequestBody OrderCompleteRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        orderService.completeOrderWithQr(orderId, request);
        return ResponseEntity.ok("QR 인증 완료: 배송이 성공적으로 마무리되었습니다.");
    }
    
    
    //주문생성
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmOrder(@RequestBody OrderCreateRequest dto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
           
            Order savedOrder = orderService.processOrderAndPayment(dto, userDetails.getUser().getUserId());
            return ResponseEntity.ok(savedOrder.getOrderId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/generate-invoice")
    public ResponseEntity<String> getNewInvoiceNo() {
        String invoiceNo = invoiceGenerator.generateInvoiceNo();
        // DB에 이미 있는지 체크
        while(orderRepository.existsByInvoiceNo(invoiceNo)) {
            invoiceNo = invoiceGenerator.generateInvoiceNo();
        }
        return ResponseEntity.ok(invoiceNo);
    }
    
    @GetMapping("/summary")
    public ResponseEntity<ShipmentSummary> getMyOrderSummary(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // principal.getName()이 로그인 시 사용한 ID(shipperId)라고 가정
        String shipperId = userDetails.getUser().getUserId(); 
        return ResponseEntity.ok(orderService.getShipperOrderSummary(shipperId));
    }
}