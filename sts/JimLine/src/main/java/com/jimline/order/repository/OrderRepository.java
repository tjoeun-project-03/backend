package com.jimline.order.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jimline.order.domain.Order;
import com.jimline.order.domain.OrderStatus;
import com.jimline.user.dto.ShipmentSummary;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // 송장 번호로 주문 찾기 같은 사용자 정의 쿼리 가능
    Optional<Order> findByInvoiceNo(String invoiceNo);

	boolean existsByInvoiceNo(String invoiceNo);

	// list로 변경해 어려개의 오더를 모두 받아오게 수정함
	// Optional<Order> findByCurrentStatus(OrderStatus status);
	@Query("SELECT o FROM Order o WHERE o.currentStatus NOT IN (com.jimline.order.domain.OrderStatus.COMPLETED, com.jimline.order.domain.OrderStatus.CANCELED)")
	List<Order> findByCurrentStatus(OrderStatus status);
	
	// 화주의 ID로 주문 목록을 최신순으로 가져오는 메서드 추가
	List<Order> findByShipperIdOrderByCreatedDesc(String shipperId);
	
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
	
	@Query("SELECT new com.jimline.user.dto.ShipmentSummary(" +
	           "COUNT(CASE WHEN o.currentStatus = com.jimline.order.domain.OrderStatus.CREATED THEN 1 END), " +
	           "COUNT(CASE WHEN o.currentStatus = com.jimline.order.domain.OrderStatus.ACCEPTED THEN 1 END), " +
	           "COUNT(CASE WHEN o.currentStatus IN (com.jimline.order.domain.OrderStatus.COMPLETED, com.jimline.order.domain.OrderStatus.CANCELED) THEN 1 END)) " +
	           "FROM Order o WHERE o.shipperId = :shipperId")
	    ShipmentSummary getOrderSummaryByShipperId(@Param("shipperId") String shipperId);

	List<Order> findAllByShipperIdAndCurrentStatusInOrderByCreatedDesc(String shipperId, List<OrderStatus> statuses);
	
	List<Order> findAllByShipperIdOrderByCreatedDesc(String shipperId);

	List<Order> findByShipperId(String shipperId);
	
	List<Order> findByCarrierId(String carrierId);
}