package com.jimline.global.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class InvoiceGenerator {

    /**
     * 예: INV-260221-A12B (날짜 + 랜덤 4자리)
     */
    public String generateInvoiceNo() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String randomPart = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "INV-" + datePart + "-" + randomPart;
    }
}