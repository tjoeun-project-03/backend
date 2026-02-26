package com.jimline.order.service;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Service
//@RequiredArgsConstructor // final 필드가 없으므로 지금은 없어도 무방합니다.
public class TossPaymentService {

 @Value("${toss.payments.secret-key}")
 private String secretKey; // final 제거
 
 public boolean confirmPayment(String paymentKey, String orderId, int amount) {
     RestTemplate restTemplate = new RestTemplate();
     
     // 1. 시크릿 키가 정상적으로 로드되었는지 체크 (디버깅용)
     if (secretKey == null || secretKey.isEmpty()) {
         System.err.println("Error: Toss Secret Key is missing!");
         return false;
     }

     String auth = Base64.getEncoder().encodeToString((secretKey + ":").getBytes());

     HttpHeaders headers = new HttpHeaders();
     headers.set("Authorization", "Basic " + auth);
     headers.setContentType(MediaType.APPLICATION_JSON);

     Map<String, Object> params = new HashMap<>();
     params.put("paymentKey", paymentKey);
     params.put("orderId", orderId);
     params.put("amount", amount);

     HttpEntity<Map<String, Object>> request = new HttpEntity<>(params, headers);

     try {
         ResponseEntity<Map> response = restTemplate.postForEntity(
             "https://api.tosspayments.com/v1/payments/confirm",
             request,
             Map.class
         );
         return response.getStatusCode() == HttpStatus.OK;
     } catch (Exception e) {
         // 에러 원인을 알기 위해 로그를 찍어보는 것이 좋습니다.
         System.out.println("Toss Confirm Error: " + e.getMessage());
         return false;
     }
 }
 
}