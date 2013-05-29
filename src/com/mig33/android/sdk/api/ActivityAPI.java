/**
 * Mig33 Pte. Ltd.
 *
 * Copyright (c) 2012 mig33. All rights reserved.
 */
package com.mig33.android.sdk.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.text.TextUtils;

import com.mig33.android.sdk.model.ActivityItem;
import com.mig33.android.sdk.service.RestService;

/**
 * ActivityAPI.java
 * 
 * @author warrenbalcos on May 20, 2013
 * 
 */
public class ActivityAPI {
	
	private List<ActivityListener>		listeners;
	
	private static final ActivityAPI	instance	= new ActivityAPI();
	
	private ActivityAPI() {
		listeners = Collections.synchronizedList(new ArrayList<ActivityListener>());
	}
	
	public static synchronized ActivityAPI getInstance() {
		return instance;
	}
	
	public interface ActivityListener {
		public void onPostSent(String id);
		
		public void onPostFailed(String id);
	}
	
	public void post(ActivityItem item) {
		if (item != null) {
			RestService.getInstance().processActivity(item);
		}
	}
	
	public void post(String title, String url) {
		ActivityItem item = new ActivityItem(title);
		if (!TextUtils.isEmpty(url)) {
			item.setWebUrl(url);
		}
		RestService.getInstance().processActivity(item);
	}
	
	public void post(String title, String url, String image48x48Url, String image96x96Url,
			String image120x120Url, String image300x300Url) {
		ActivityItem item = new ActivityItem(title);
		item.setWebUrl(url);
		item.set48x48ImageUrl(image48x48Url);
		item.set96x96ImageUrl(image96x96Url);
		item.set120x120ImageUrl(image120x120Url);
		item.set300x300ImageUrl(image300x300Url);
		RestService.getInstance().processActivity(item);
	}
	
	public void onActivityUpdate(String id, boolean success) {
		cleanListeners();
		for (ActivityListener listener : listeners) {
			if (listener != null) {
				if (success) {
					listener.onPostSent(id);
				} else {
					listener.onPostFailed(id);
				}
			}
		}
	}
	
	public void addListener(ActivityListener listener) {
		listeners.add(listener);
	}
	
	private void cleanListeners() {
		for (int i = 0; i < listeners.size(); i++) {
			ActivityListener temp = listeners.get(i);
			if (temp == null) {
				listeners.remove(i);
			}
		}
	}
}
