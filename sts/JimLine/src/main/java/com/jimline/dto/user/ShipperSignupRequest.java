package com.jimline.dto.user;

public record ShipperSignupRequest(
		String userId, 
	    String userPw, 
	    String userName, 
	    String email,
	    String corpReg,
	    String phone,
	    
	    String companyName,
	    String representative
	) {}