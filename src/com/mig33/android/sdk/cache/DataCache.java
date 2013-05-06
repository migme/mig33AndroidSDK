/**
 * Mig33 Pte. Ltd.
 *
 * Copyright (c) 2012 mig33. All rights reserved.
 */
package com.mig33.android.sdk.cache;

import android.support.v4.util.LruCache;
import android.text.TextUtils;

/**
 * DataCache.java
 * 
 * @author warrenbalcos on May 2, 2013
 * 
 */
public class DataCache {
	
	public static final int					CACHE_MAX_SIZE					= 200;
	
	private LruCache<String, CachedData>	cache;
	
	private static final DataCache		instance						= new DataCache();
	
	private static final int				DEFAULT_RESPONSE_DATA_EXPIRY	= 1;
	
	private class CachedData {
		
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
	
	public void cacheData(String key, String data) {
		cacheData(key, data, DEFAULT_RESPONSE_DATA_EXPIRY);
	}
	
	public void cacheData(String key, String data, int expiry) {
		if (TextUtils.isEmpty(data)) {
			return;
		}
		cache.put(key, new CachedData(data, expiry));
	}
	
	public String getData(String key) {
		CachedData data = cache.get(key);
		String result = null;
		if (data != null) {
			if (!data.isExpired()) {
				result = data.getData();
			} else {
				cache.remove(key);
			}
		}
		return result;
	}
}
