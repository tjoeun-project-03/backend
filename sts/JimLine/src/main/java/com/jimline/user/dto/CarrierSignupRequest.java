package com.jimline.user.dto;

import com.jimline.user.domain.CarType;

public record CarrierSignupRequest(
	    String userId, 
	    String userPw, 
	    String userName, 
	    String email,
	    String corpReg,
	    String phone,
	    
	    String car, 
	    String carNum, 
	    CarType carType, 
	    String license, 
	    String carReg, 
	    Integer freezer
	) {}