/**
 * Mig33 Pte. Ltd.
 *
 * Copyright (c) 2012 mig33. All rights reserved.
 */
package com.mig33.android.sdk.cache;

import android.support.v4.util.LruCache;
import android.text.TextUtils;

import com.mig33.android.sdk.common.Tools;
import com.mig33.android.sdk.model.ApiResponse;

/**
 * DataCache.java
 * 
 * @author warrenbalcos on May 2, 2013
 * 
 */
public class DataCache {
	
	private static final String				TAG								= "DataCache";
	
	public static final int					CACHE_MAX_SIZE					= 200;
	
	private LruCache<String, CachedData>	cache;
	
	private static final DataCache			instance						= new DataCache();
	
	private static final int				DEFAULT_RESPONSE_DATA_EXPIRY	= 60;
	
	public class CachedData {
		
		private String	data;
		
		private long	expiry;
		
		/**
		 * @param data
		 *            - the data to cache
		 * @param expiry
		 *            - data expiry in seconds. defaults to
		 *            {@value #DEFAULT_RESPONSE_DATA_EXPIRY} seconds if not
		 *            specified. cannot be lower than
		 *            {@value #DEFAULT_RESPONSE_DATA_EXPIRY} seconds.
		 */
		public CachedData(String data, int expiry) {
			init(data, expiry);
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
	
	private DataCache() {
		cache = new LruCache<String, CachedData>(CACHE_MAX_SIZE);
	}
	
	public static synchronized DataCache getInstance() {
		return instance;
	}
	
	public void cacheData(String key, ApiResponse response) {
		cacheData(key, response.toJsonStr(), DEFAULT_RESPONSE_DATA_EXPIRY);
	}
	
	public void cacheData(String key, String data) {
		cacheData(key, data, DEFAULT_RESPONSE_DATA_EXPIRY);
	}
	
	public void cacheData(String key, String data, int expiry) {
		if (TextUtils.isEmpty(data)) {
			return;
		}
		synchronized (cache) {
			Tools.log(TAG, "set cache key: " + key + " data: " + data);
			cache.put(key, new CachedData(data, expiry));
		}
	}
	
	public boolean isExpired(String key) {
		CachedData data = cache.get(key);
		return data == null || data.isExpired();
	}
	
	public String getData(String key) {
		String result = null;
		CachedData data = getCachedData(key);
		if (data != null) {
			result = data.getData();
		}
		return result;
	}
	
	public ApiResponse getApiResponseData(String key) {
		String data = getData(key);
		return ApiResponse.fromString(data, ApiResponse.class);
	}
	
	public CachedData getCachedData(String key) {
		synchronized (cache) {
			CachedData data = cache.get(key);
			Tools.log(TAG, "get cache key: " + key + " data: "
					+ (data != null ? data.getData() : "<no data>"));
			return data;
		}
	}
}
