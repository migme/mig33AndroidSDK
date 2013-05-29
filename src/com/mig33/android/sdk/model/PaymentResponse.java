/**
 * Mig33 Pte. Ltd.
 *
 * Copyright (c) 2012 mig33. All rights reserved.
 */
package com.mig33.android.sdk.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.mig33.android.sdk.common.Tools;
import com.mig33.android.sdk.service.RestRequest.Type;

/**
 * PaymentResponse.java
 * 
 * @author warrenbalcos on May 17, 2013
 * 
 */
public class PaymentResponse extends ApiResponse {
	
	private static final String	TAG		= "PaymentResponse";
	
	private static final String	RESULT	= "result";
	
	//@formatter:off
	/**
	 * Sample payment response data
	 * 
	 * {
	 * "entry": {
	 *     "result": {
	 *          "data": {
	 *              "id":1427267623,
	 *              "reference":"sampleapp-993963296-1",
	 *              "amount":{
	 *                  "amount":1.23,
	 *                  "fundedAmount":0,
	 *                  "currency":"SGD"
	 *              },
	 *              "balance":{
	 *                  "amount":971.71,
	 *                  "currency":"SGD"
	 *                  }
	 *              }
	 *          }
	 *      }
	 * }
	 *
	 * @param responseStr
	 */
	public PaymentResponse(String responseStr) {
		super(responseStr);
	}
	//@formatter:on
	
	public PaymentResponse(ApiResponse response) {
		super(response.getResponseStr());
		setResponseIds(response.getId(), response.getUserId(), response.getGroupId(),
				response.getAppId());
		setType(Type.PAYMENT);
	}
	
	public PaymentReceipt getPaymentReceipt() {
		String result = getResult();
		if (result != null) {
			return new Gson().fromJson(result, PaymentReceipt.class);
		}
		return null;
	}
	
	public String getResult() {
		String result = null;
		String entry = getEntry();
		if (!TextUtils.isEmpty(entry)) {
			try {
				JSONObject json = new JSONObject(entry);
				result = json.get(RESULT).toString();
			} catch (JSONException e) {
				Tools.log(e);
			} catch (Exception e) {
				Tools.log(e);
			}
		}
		Tools.log(TAG, "getResult: " + result);
		return result;
	}
}
