/**
 * Mig33 Pte. Ltd.
 *
 * Copyright (c) 2012 mig33. All rights reserved.
 */
package com.mig33.android.sdk.model;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.mig33.android.sdk.service.RestRequest.Type;

/**
 * ActivityResponse.java
 * 
 * @author warrenbalcos on May 21, 2013
 * 
 */
public class ActivityResponse extends ApiResponse {
	
	public ActivityResponse(String responseStr) {
		super(responseStr);
	}
	
	public ActivityResponse(ApiResponse response) {
		super(response.getResponseStr());
		setResponseIds(response.getId(), response.getUserId(), response.getGroupId(),
				response.getAppId());
		setType(Type.ACTIVITIES);
	}
	
	public Data getData() {
		String entry = getEntry();
		if (TextUtils.isEmpty(entry)) {
			return new Gson().fromJson(entry, Data.class);
		}
		return null;
	}
	
	public class Data {
		private String	id;
		private String	result;
		
		/**
		 * @return the id
		 */
		public String getId() {
			return id;
		}
		
		/**
		 * @param id
		 *            the id to set
		 */
		public void setId(String id) {
			this.id = id;
		}
		
		/**
		 * @return the result
		 */
		public String getResult() {
			return result;
		}
		
		/**
		 * @param result
		 *            the result to set
		 */
		public void setResult(String result) {
			this.result = result;
		}
		
		public boolean isSuccessful() {
			return result != null && result.equalsIgnoreCase("ok");
		}
	}
}
