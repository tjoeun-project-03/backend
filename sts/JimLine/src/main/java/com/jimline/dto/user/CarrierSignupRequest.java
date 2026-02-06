package com.jimline.dto.user;

import com.jimline.domain.user.CarType;

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
	    int freezer
	) {}