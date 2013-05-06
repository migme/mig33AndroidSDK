/**
 * Mig33 Pte. Ltd.
 *
 * Copyright (c) 2012 mig33. All rights reserved.
 */
package com.mig33.android.sdk.service;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.mig33.android.sdk.common.Tools;
import com.mig33.android.sdk.model.UserInfo;
import com.projectgoth.b.data.Error;
import com.projectgoth.b.exception.RestClientException;
import com.projectgoth.b.exception.RestErrorException;

/**
 * RestRequest.java
 * 
 * @author warrenbalcos on May 2, 2013
 * 
 */
public class RestRequest {
	
	private static final int	MAX_RETRIES_ON_FAIL	= 3;
	
	public static String		DATA_IDENTIFIER		= "entry";
	public static String		ERROR_IDENTIFIER	= "error";
	
	private static final String	GET					= "GET";
	private static final String	POST				= "POST";
	
	private static final Gson	gson				= new Gson();
	
	private Type				type;
	
	private String				method;
	private String				url;
	private String				params;
	private String				contentType;
	
	private boolean				retryOnFail			= false;
	private int					retries				= 0;
	
	public enum Type {
		/**
		 * People API
		 * 
		 * GET /api/rest/people/{userid}/{groupId}
		 */
		PEOPLE("/api/rest/people/%s/%s", GET),
		
		/**
		 * Payment API
		 * 
		 * POST /api/rest/payment/
		 */
		PAYMENT("/api/rest/payment/", POST);
		
		private String	url;
		
		private String	method;
		
		private Type(String url, String method) {
			this.url = url;
			this.method = method;
		}
		
		/**
		 * @return the url
		 */
		public String getUrl() {
			return url;
		}
		
		/**
		 * @return the method
		 */
		public String getMethod() {
			return method;
		}
	}
	
	/**
	 * @param method
	 * @param url
	 * @param params
	 */
	public RestRequest(Type type, String method, String url, String params) {
		this(type, method, url, params, false);
	}
	
	/**
	 * @param method
	 * @param url
	 * @param params
	 * @param retryOnFail
	 */
	public RestRequest(Type type, String method, String url, String params, boolean retryOnFail) {
		this.type = type;
		this.method = method;
		this.url = url;
		this.params = params;
		this.retryOnFail = retryOnFail;
	}
	
	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}
	
	/**
	 * @param method
	 *            the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}
	
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * @return the params
	 */
	public String getParams() {
		return params;
	}
	
	/**
	 * @param params
	 *            the params to set
	 */
	public void setParams(String params) {
		this.params = params;
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
		return method + url + params + contentType;
	}
	
	public static String getStringData(String response) {
		String result = null;
		try {
			JSONObject json = new JSONObject(response);
			result = json.get(DATA_IDENTIFIER).toString();
		} catch (JSONException e) {
			Tools.log(e);
		}
		return result;
	}
	
	public static Object parseData(Type type, String data) throws RestErrorException,
			RestClientException {
		Object object = null;
		try {
			if (data != null) {
				switch (type) {
				case PEOPLE:
					object = gson.fromJson(data, UserInfo[].class);
					break;
				case PAYMENT:
					// TODO: handle this
					break;
				}
			}
		} catch (Exception e) {
			throw new RestClientException("Unable to parse response", e);
		}
		if (object == null) {
			throw new RestClientException("Unable to parse response");
		}
		return object;
	}
	
	public static Error getErrorFromResponse(String response) {
		Error error = null;
		try {
			JSONObject json = new JSONObject(response);
			String errorString = json.getJSONObject(ERROR_IDENTIFIER).toString();
			error = gson.fromJson(errorString, Error.class);
		} catch (JSONException e) {
			Tools.log(e);
		}
		return error;
	}
}
