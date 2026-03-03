package com.jimline.order.dto;

import java.time.LocalDateTime;

import com.jimline.order.domain.OrderLog;

public record OrderLogResponse(
	    String status,
	    String statusName, // "배송 시작", "완료" 등 한글 명칭
	    LocalDateTime updateTime
	) {
	    public static OrderLogResponse from(OrderLog log) {
	        return new OrderLogResponse(
	            log.getStatusCode().name(),
	            log.getStatusCode().getDescription(), // Enum에 한글명칭이 있다고 가정
	            log.getUpdateTime()
	        );
	    }
	}