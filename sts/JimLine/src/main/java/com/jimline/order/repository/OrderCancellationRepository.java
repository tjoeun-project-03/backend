package com.jimline.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jimline.order.domain.OrderCancellation;
import com.jimline.order.domain.OrderLog;
import com.jimline.order.domain.Order;
import java.util.List;

// 주문 취소 내역 저장용
public interface OrderCancellationRepository extends JpaRepository<OrderCancellation, Long> {
}

