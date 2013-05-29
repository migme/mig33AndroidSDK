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
import com.mig33.android.sdk.service.RestRequest;
import com.projectgoth.b.data.Error;

/**
 * ApiResponse.java
 * 
 * @author warrenbalcos on May 17, 2013
 */
public class ApiResponse {
	
	private static final String	TAG					= "ApiResponse";
	
	public static String		ENTRY_IDENTIFIER	= "entry";
	public static String		ERROR_IDENTIFIER	= "error";
	
	private String				id;
	
	private RestRequest.Type	type;
	
	private String				userId;
	
	private String				groupId;
	
	private String				appId;
	
	private String				responseStr;
	
	public ApiResponse(String responseStr) {
		this.responseStr = responseStr;
	}
	
	public void setResponseIds(String id, String userId, String groupId, String appId) {
		this.id = id;
		this.userId = userId;
		this.groupId = groupId;
		this.appId = appId;
	}
	
	public String getEntry() {
		String result = null;
		if (!TextUtils.isEmpty(responseStr)) {
			try {
				JSONObject json = new JSONObject(responseStr);
				result = json.get(ENTRY_IDENTIFIER).toString();
			} catch (JSONException e) {
				Tools.log(e);
			} catch (Exception e) {
				Tools.log(e);
			}
		}
		Tools.log(TAG, "getEntry: " + result);
		return result;
	}
	
	/**
	 * @return the responseStr
	 */
	public String getResponseStr() {
		return responseStr;
	}
	
	/**
	 * @param responseStr
	 *            the responseStr to set
	 */
	public void setResponseStr(String responseStr) {
		this.responseStr = responseStr;
	}
	
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
	
	public Error getError() {
		Error error = null;
		if (!TextUtils.isEmpty(responseStr)) {
			try {
				JSONObject json = new JSONObject(responseStr);
				String errorString = json.getJSONObject(ERROR_IDENTIFIER).toString();
				error = new Gson().fromJson(errorString, Error.class);
			} catch (JSONException e) {
			}
		} else {
			error = new Error();
			error.setErrorNumber(0L);
			error.setMessage("Empty response");
		}
		return error;
	}
	
	/**
	 * @return the appId
	 */
	public String getAppId() {
		return appId;
	}
	
	/**
	 * @param appId
	 *            the appId to set
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}
	
	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return groupId;
	}
	
	/**
	 * @param groupId
	 *            the groupId to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	
	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	/**
	 * @return the type
	 */
	public RestRequest.Type getType() {
		return type;
	}
	
	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(RestRequest.Type type) {
		this.type = type;
	}
	
	public String toJsonStr() {
		Tools.log(TAG, "start toJsonStr");
		String temp = new Gson().toJson(this);
		Tools.log(TAG, "toJsonStr: " + temp);
		return temp;
	}
	
	public static ApiResponse fromString(String jsonStr, Class respClass) {
		return new Gson().fromJson(jsonStr, respClass);
	}
}
