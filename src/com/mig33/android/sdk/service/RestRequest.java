/**
 * Mig33 Pte. Ltd.
 *
 * Copyright (c) 2012 mig33. All rights reserved.
 */
package com.mig33.android.sdk.service;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import android.text.TextUtils;

import com.mig33.android.sdk.common.Config;
import com.mig33.android.sdk.common.Tools;
import com.mig33.android.sdk.model.ActivityItem;
import com.mig33.android.sdk.model.ApiResponse;
import com.mig33.android.sdk.model.PaymentItem;
import com.mig33.android.sdk.model.RequestItem;
import com.mig33.android.sdk.service.RequestQueueManager.RequestWorkerTask;

/**
 * RestRequest.java
 * 
 * @author warrenbalcos on May 2, 2013
 * 
 */
public class RestRequest {
	
	private static final String					TAG					= "RestRequest";
	
	private WeakReference<RequestWorkerTask>	requestWorkerTaskReference;
	
	public static final String					CONTENT_TYPE_JSON	= "application/json";
	
	private static final int					MAX_RETRIES_ON_FAIL	= 3;
	
	private static final String					GET					= "GET";
	private static final String					POST				= "POST";
	
	public static final String					ME					= "@me";
	public static final String					SELF				= "@self";
	public static final String					ALL					= "@all";
	public static final String					FRIENDS				= "@friends";
	public static final String					APP					= "@app";
	
	public static final String					USER_ID				= "{userId}";
	public static final String					GROUP_ID			= "{groupId}";
	public static final String					APP_ID				= "{appId}";
	
	private Type								type;
	
	private String								contentType;
	
	private HashMap<String, String>				payloadParams;
	
	private RequestItem							requestItem;
	
	private ApiResponse							apiResponse;
	
	private String								userId;
	
	private String								groupId;
	
	private String								appId;
	
	private boolean								retryOnFail			= false;
	private int									retries				= 0;
	
	public enum Type {
		
		/**
		 * People API
		 * 
		 * GET people/{userId}/{groupId}
		 */
		PEOPLE("people/" + USER_ID + "/" + GROUP_ID, GET),
		
		/**
		 * Payment API
		 * 
		 * POST payment/{userId}/{groupId}/{appId}
		 */
		PAYMENT("payment/" + USER_ID + "/" + GROUP_ID + "/" + APP_ID, POST),
		
		/**
		 * Activity API
		 * 
		 * POST activities/{userId}/{groupId}/{appId}
		 */
		ACTIVITIES("activities/" + USER_ID + "/" + GROUP_ID + "/" + APP_ID, POST);
		
		private String	template;
		
		private String	method;
		
		private Type(String template, String method) {
			this.template = template;
			this.method = method;
		}
		
		/**
		 * @return the template
		 */
		public String getTemplate() {
			return template;
		}
		
		/**
		 * @return the method
		 */
		public String getMethod() {
			return method;
		}
		
		public String toString() {
			return getMethod() + " " + getTemplate();
		}
		
		public String getApiUrl(String userId, String groupId, String appId) {
			String result = template;
			result = result.replace(USER_ID, userId);
			result = result.replace(GROUP_ID, groupId);
			result = result.replace(APP_ID, appId);
			return result;
		}
	}
	
	/**
	 * @param type
	 * @param userId
	 * @param groupId
	 * @param appId
	 */
	public RestRequest(Type type, String userId, String groupId, String appId) {
		this(type, userId, groupId, appId, false);
	}
	
	/**
	 * @param type
	 * @param userId
	 * @param groupId
	 * @param appId
	 * @param retryOnFail
	 */
	public RestRequest(Type type, String userId, String groupId, String appId, boolean retryOnFail) {
		this.type = type;
		this.userId = userId;
		this.groupId = groupId;
		this.appId = appId;
		
		this.retryOnFail = retryOnFail;
		
		payloadParams = new HashMap<String, String>();
	}
	
	public String getMethod() {
		return type.method;
	}
	
	public HttpParams getHttpParams() {
		HttpParams params = new BasicHttpParams();
		for (Entry<String, String> entry : payloadParams.entrySet()) {
			params.setParameter(entry.getKey(), entry.getValue());
		}
		return params;
	}
	
	public String getParamStr() {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Entry<String, String> entry : payloadParams.entrySet()) {
			if (first) {
				first = false;
			} else {
				sb.append("&");
			}
			try {
				sb.append(entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	public String getParamJson() {
		RequestItem item = getRequestItem();
		if (item != null) {
			return item.getJsonPayLoad();
		}
		return null;
		// JsonObject params = new JsonObject();
		// for (Entry<String, String> entry : payloadParams.entrySet()) {
		// params.addProperty(entry.getKey(), entry.getValue());
		// }
		// return params.toString();
	}
	
	public String getBodyHash() {
		return Tools.getBodyHash(getParamJson());
	}
	
	public String getUrl() {
		return Config.getInstance().getRestEndPoint() + type.getApiUrl(userId, groupId, appId);
	}
	
	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}
	
	/**
	 * @param contentType
	 *            the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	/**
	 * @return the retryOnFail
	 */
	public boolean isRetryOnFail() {
		return retryOnFail;
	}
	
	/**
	 * @param retryOnFail
	 *            the retryOnFail to set
	 */
	public void setRetryOnFail(boolean retryOnFail) {
		this.retryOnFail = retryOnFail;
	}
	
	/**
	 * @return the retries
	 */
	public int getRetries() {
		return retries;
	}
	
	/**
	 * @param retries
	 *            the retries to set
	 */
	public void setRetries(int retries) {
		this.retries = retries;
	}
	
	public void incrementRetries() {
		this.retries++;
	}
	
	public boolean canRetry() {
		return retries <= MAX_RETRIES_ON_FAIL;
	}
	
	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}
	
	public String getKey() {
		String paramStr = getParamStr();
		paramStr = (!TextUtils.isEmpty(paramStr)) ? "?" + paramStr : "";
		return getMethod() + " " + getUrl() + paramStr + " " + contentType;
	}
	
	public RequestWorkerTask getRequestWorkerTaskReference() {
		return requestWorkerTaskReference != null ? requestWorkerTaskReference.get() : null;
	}
	
	public void setRequestWorkerTaskReference(RequestWorkerTask task) {
		this.requestWorkerTaskReference = new WeakReference<RequestWorkerTask>(task);
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
	 * @return the requestItem
	 */
	public RequestItem getRequestItem() {
		return requestItem;
	}
	
	/**
	 * @param requestItem
	 *            the requestItem to set
	 */
	public void setRequestItem(RequestItem requestItem) {
		this.requestItem = requestItem;
	}
	
	/**
	 * @return the apiResponse
	 */
	public ApiResponse getApiResponse() {
		return apiResponse;
	}
	
	/**
	 * @param apiResponse
	 *            the apiResponse to set
	 */
	public void setApiResponse(ApiResponse apiResponse) {
		this.apiResponse = apiResponse;
	}
	
	/**
	 * @return the payloadParams
	 */
	public HashMap<String, String> getPayloadParams() {
		return payloadParams;
	}
	
	/**
	 * @param key
	 * @param value
	 */
	public void addPayloadParam(String key, String value) {
		payloadParams.put(key, value);
	}
	
	public static RestRequest createActivityRequest(ActivityItem item) {
		RestRequest.Type type = RestRequest.Type.ACTIVITIES;
		
		RestRequest request = new RestRequest(type, item.getUserId(), item.getGroupId(),
				item.getAppId(), true);
		request.setContentType(CONTENT_TYPE_JSON);
		
		Tools.log(TAG, "createActivityRequest: " + request.getKey());
		request.setRequestItem(item);
		
		return request;
	}
	
	public static RestRequest createPaymentRequest(PaymentItem item) {
		RestRequest.Type type = RestRequest.Type.PAYMENT;
		
		RestRequest request = new RestRequest(type, item.getUserId(), item.getGroupId(),
				item.getAppId(), true);
		request.setContentType(CONTENT_TYPE_JSON);
		
		Tools.log(TAG, "createPaymentRequest: " + request.getKey());
		request.setRequestItem(item);
		
		return request;
	}
}
