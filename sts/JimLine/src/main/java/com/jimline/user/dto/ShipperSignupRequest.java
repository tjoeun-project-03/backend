package com.jimline.user.dto;

public record ShipperSignupRequest(
		String userId, 
	    String userPw, 
	    String userName, 
	    String email,
	    String corpReg,
	    String phone,
	    String zipcode,
	    String address,
	    String detailAddress
	) {}