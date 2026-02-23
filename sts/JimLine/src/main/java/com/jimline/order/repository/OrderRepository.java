package com.jimline.order.repository;

import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jimline.order.domain.Order;
import com.jimline.order.domain.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // 송장 번호로 주문 찾기 같은 사용자 정의 쿼리 가능
    Optional<Order> findByInvoiceNo(String invoiceNo);

	boolean existsByInvoiceNo(String invoiceNo);

	Optional<Order> findByCurrentStatus(OrderStatus status);
	
	// 차주 통계용 쿼리
	@Query(value = "SELECT " +
		       "  COUNT(DISTINCT o.order_id) as total_attempts, " + // 전체 시도 횟수
		       "  COUNT(DISTINCT CASE WHEN l.status_code = 'COMPLETED' THEN o.order_id END) as completed_count, " +
		       "  NVL(SUM(DISTINCT CASE WHEN l.status_code = 'COMPLETED' THEN o.price ELSE 0 END), 0) as monthly_revenue, " +
		       "  NVL(SUM(DISTINCT CASE WHEN l.status_code = 'COMPLETED' THEN d.distance ELSE 0 END), 0) as monthly_dist " +
		       "FROM orders o " +
		       "JOIN order_log l ON o.order_id = l.order_id " +
		       "LEFT JOIN order_detail d ON o.order_id = d.order_id " +
		       "WHERE o.carrier_id = :carrierId " +
		       "AND l.status_code = 'COMPLETED' " + // 로그 테이블의 완료 상태 기준
		       "AND EXTRACT(YEAR FROM l.update_time) = :year " +
		       "AND EXTRACT(MONTH FROM l.update_time) = :month", 
		       nativeQuery = true)
	    Map<String, Object> getMonthlyStatsRaw(
	        @Param("carrierId") String carrierId, 
	        @Param("year") int year, 
	        @Param("month") int month
	    );
}