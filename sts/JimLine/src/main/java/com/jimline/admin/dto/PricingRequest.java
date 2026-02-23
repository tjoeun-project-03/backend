package com.jimline.admin.dto;

public record PricingRequest(
	    int baseFee,
	    int weatherRule,
	    int nightRule,
	    int holidayRule
	) {}