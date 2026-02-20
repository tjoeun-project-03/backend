package com.jimline.order.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jimline.order.domain.Order;
import com.jimline.order.domain.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // 송장 번호로 주문 찾기 같은 사용자 정의 쿼리 가능
    Optional<Order> findByInvoiceNo(String invoiceNo);

	boolean existsByInvoiceNo(String invoiceNo);

	Optional<Order> findByCurrentStatus(OrderStatus status);
}