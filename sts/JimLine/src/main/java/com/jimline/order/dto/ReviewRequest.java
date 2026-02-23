package com.jimline.order.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

//package com.jimline.order.dto;
@Getter
@NoArgsConstructor
public class ReviewRequest {
 private double rating; // 평점
 private String content; // 리뷰 내용
}