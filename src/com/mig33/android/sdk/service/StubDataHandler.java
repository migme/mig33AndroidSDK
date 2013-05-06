/**
 * Mig33 Pte. Ltd.
 *
 * Copyright (c) 2012 mig33. All rights reserved.
 */
package com.mig33.android.sdk.service;

import java.util.Random;

/**
 * StubDataHandler.java
 * 
 * @author warrenbalcos on Apr 29, 2013
 * 
 */
public class StubDataHandler {
	
	private Random							random;
	
	private static final String				LOCALHOST	= "http://localhost";
	
	// fake stubs
	public static final String				PEOPE_URL	= "/api/rest/people/";
	public static final String				PAYMENT_URL	= "/api/rest/payment/";
	
	private static final StubDataHandler	instance	= new StubDataHandler();
	
	public static synchronized StubDataHandler getInstance() {
		return instance;
	}
	
	private StubDataHandler() {
	}
	
	public String getStubDataUrl(RestType type) {
		String responseType = "success";
		int chance = random.nextInt(100);
		if (chance < 10) {
			responseType = "error";
		} else if (chance < 30) {
			responseType = "fail";
		}
		
		if (type == RestType.PEOPLE) {
			return LOCALHOST + PEOPE_URL + "/" + responseType;
		} else if (type == RestType.PAYMENT) {
			return LOCALHOST + PAYMENT_URL + "/" + responseType;
		}
		
		return LOCALHOST + "/error";
	}
	
}
