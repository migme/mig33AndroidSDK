/**
 * Mig33 Pte. Ltd.
 *
 * Copyright (c) 2012 mig33. All rights reserved.
 */
package com.projectgoth.b.android;

import android.support.v4.util.LruCache;

/**
 * RestResponseCache.java
 * 
 * @author warrenbalcos on Sep 11, 2012
 * 
 */
public class RestResponseCache {

	public static final int		REST_RESPONSE_CACHE_MAX_SIZE	= 150;
	
	private LruCache<String, ResponseObject>	cache;
	
	private static RestResponseCache			instance;
	
	private boolean								enabled = false;

	class ResponseObject {
		private long	expiry = 0;
		private String	data;
		
		/**
		 * @param data
		 *            - the data to cache
		 * @param expiry
		 *            - data expiry in seconds. defaults to {@value #DEFAULT_RESPONSE_DATA_EXPIRY} seconds if not
		 *            specified. cannot be lower than {@value #DEFAULT_RESPONSE_DATA_EXPIRY} seconds.
		 */
		public ResponseObject(String data, int expiry) {
			init(data, expiry);
		}
		
		/**
		 * @param data
		 *            - the data to cache, and will expire in {@value #DEFAULT_RESPONSE_DATA_EXPIRY} seconds.
		 */
		public ResponseObject(String data) {
			init(data, 0);
		}
		
		private void init(String data, int expiry) {
			this.data = data;
			this.expiry = System.currentTimeMillis() + (expiry * 1000);
		}
		
		public String getData() {
			return data;
		}
		
		public boolean isExpired() {
			return System.currentTimeMillis() >= expiry;
		}
	}
	
	private RestResponseCache() {
		cache = new LruCache<String, ResponseObject>(REST_RESPONSE_CACHE_MAX_SIZE);
	}
	
	public static RestResponseCache getInstance() {
		if (null == instance) {
			instance = new RestResponseCache();
		}
		return instance;
	}
	
	public String getData(String url, String method, String params, String contentType) {
		
		if (!isEnabled()) {
			return null;
		}
		
		String key = getKey(url, method, params, contentType);
		ResponseObject data = cache.get(key);
		String response = null;
		if (null != data) {
			if (!data.isExpired()) {
				response = data.getData();
			} else {
				cache.remove(key);
			}
		}
		return response;
	}
	
	public void cacheData(String url, String method, String params, String contentType,
			String response, int expiry) {
		if (!isEnabled() || response == null) {
			return;
		}
		
		String key = getKey(url, method, params, contentType);
		cache.put(key, new ResponseObject(response, expiry));
	}
	
	private String getKey(String url, String method, String params, String contentType) {
		return method + url + params + contentType;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

//	/**
//	 * @param enabled the enabled to set
//	 */
//	public void setEnabled(boolean enabled) {
//		this.enabled = enabled;
//	}
}
