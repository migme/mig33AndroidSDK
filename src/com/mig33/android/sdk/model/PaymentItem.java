/**
 * Mig33 Pte. Ltd.
 *
 * Copyright (c) 2012 mig33. All rights reserved.
 */
package com.mig33.android.sdk.model;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.mig33.android.sdk.common.Config;
import com.mig33.android.sdk.service.RestRequest;

/**
 * PaymentItem.java
 * 
 * @author warrenbalcos on May 10, 2013
 * 
 */
public class PaymentItem extends RequestItem {
	
	private static final Random			rand		= new Random();
	
	private static final AtomicInteger	refIdGen	= new AtomicInteger();
	
	private String						reference;
	
	private String						description;
	
	private String						amount;
	
	private String						currency;
	
	private Status						status;
	
	private enum Status {
		PENDING, SUCCESS, FAIL
	}
	
	/**
	 * @param description
	 * @param amount
	 */
	public PaymentItem(String description, String amount) {
		this.description = description;
		this.amount = amount;
		this.reference = generateReferenceNumber();
		setUserId(RestRequest.ME);
		setGroupId(RestRequest.SELF);
		setAppId(RestRequest.APP);
		if (TextUtils.isEmpty(description)) {
			this.description = "Item - " + reference;
		}
		currency = "USD";
		status = Status.PENDING;
	}
	
	/**
	 * @return the reference
	 */
	public String getReference() {
		return reference;
	}
	
	/**
	 * @param reference
	 *            the reference to set
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @return the amount
	 */
	public String getAmount() {
		return amount;
	}
	
	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}
	
	/**
	 * @param currency
	 *            the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}
	
	public void setSuccessful() {
		this.status = Status.SUCCESS;
	}
	
	public void setFailed() {
		this.status = Status.FAIL;
	}
	
	public boolean isSuccessful() {
		return status == Status.SUCCESS;
	}
	
	public boolean isPending() {
		return status == Status.PENDING;
	}
	
	public boolean isFail() {
		return status == Status.FAIL;
	}
	
	public String getJsonPayLoad() {
		JsonObject params = new JsonObject();
		params.addProperty("reference", getReference());
		params.addProperty("description", getDescription());
		params.addProperty("amount", getAmount());
		params.addProperty("currency", getCurrency());
		return params.toString();
	}
	
	public static String generateReferenceNumber() {
		String result = Config.getInstance().getAppName() + "-" + rand.nextInt() + "-"
				+ refIdGen.incrementAndGet();
		// TODO: include app name and item name on reference string
		return result;
	}
	
}
