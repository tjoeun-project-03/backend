package com.jimline.inquiry.dto;

public record InquiryCreateRequest(
	    String category,
	    String title,
	    String content
	) {}