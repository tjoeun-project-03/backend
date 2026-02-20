package com.jimline.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jimline.order.domain.Order;
import com.jimline.order.domain.OrderLog;

public interface OrderLogRepository extends JpaRepository<OrderLog, Long> {
    List<OrderLog> findByOrderOrderByUpdateTimeDesc(Order order);
}
