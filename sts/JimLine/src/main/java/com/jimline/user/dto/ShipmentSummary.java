package com.jimline.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ShipmentSummary {
    private long createdCount;
    private long acceptedCount;
    private long completedCount;
}