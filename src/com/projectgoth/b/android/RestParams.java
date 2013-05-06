package com.projectgoth.b.android;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.projectgoth.b.BaseRestParams;

public class RestParams extends BaseRestParams {
	
	private LinkedHashMap<String, String>	params	= new LinkedHashMap<String, String>();
	
	public void set(String key, String value) {
		params.put(key, value);
	}
	
	public void remove(String key) {
		params.remove(key);
	}
	
	public String getEncodedUrl() {
		StringBuilder sb = new StringBuilder();
		
		boolean first = true;
		for (Entry<String, String> entry : params.entrySet()) {
			if (first) {
				first = false;
			} else {
				sb.append("&");
			}
			sb.append(entry.getKey() + "=" + entry.getValue());
		}
		return sb.toString();
	}
	
	public void set(String key, int value) {
		params.put(key, String.valueOf(value));
	}
	
	@Override
	public void set(String key, long value) {
		params.put(key, String.valueOf(value));
	}
	
	@Override
	public void set(String key, byte[] value) {
		params.put(key, new String(value));
	}
}