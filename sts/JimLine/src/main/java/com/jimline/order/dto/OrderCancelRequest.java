package com.jimline.order.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderCancelRequest {
    private String canceledReason;
}