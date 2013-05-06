/**
 * Mig33 Pte. Ltd.
 *
 * Copyright (c) 2012 mig33. All rights reserved.
 */
package com.mig33.android.sdk.service;

import com.mig33.android.sdk.common.Config;

/**
 * RestType.java
 * 
 * @author warrenbalcos on Apr 26, 2013
 * 
 * // TODO: deprecate this class, and put functionality on RestRequest class
 */
public class RestType {
	
	private static final String		GET		= "GET";
	private static final String		POST	= "POST";
	// private static final String DELETE = "DELETE";
	
	private String					url;
	private String					method;
	private String					domain;
	
	private static Config			config;
	
	/**
	 * People API
	 * 
	 * GET /api/rest/people/{userid}/{groupId}
	 */
	public static final RestType	PEOPLE	= new RestType("/api/rest/people/%s/%s", GET);
	
	/**
	 * Payment API
	 * 
	 * POST /api/rest/payment/
	 */
	public static final RestType	PAYMENT	= new RestType("/api/rest/payment/", POST);
	
	private RestType(String url, String method, int expiry) {
		this(url, method);
	}
	
	private RestType(String url, String method) {
		this.url = url;
		this.method = method;
	}
	
	public String getMethod() {
		return method;
	}
	
	public void setMethod(String method) {	
		this.method = method;
	}
	
	public String getUrl() {
		if (config != null && config.isUseStubData()) {
			return StubDataHandler.getInstance().getStubDataUrl(this);
		}
		String result = url;
		if (null != domain) {
			result = domain + url;
		}
		return result;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	/**
	 * @param config
	 *            the config to set
	 */
	public static void setConfig(Config config) {
		RestType.config = config;
	}
}
