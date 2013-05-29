/**
 * Mig33 Pte. Ltd.
 *
 * Copyright (c) 2012 mig33. All rights reserved.
 */
package com.mig33.android.sdk.service;

import com.mig33.android.sdk.api.People;
import com.mig33.android.sdk.cache.DataCache;
import com.mig33.android.sdk.cache.DataCache.CachedData;
import com.mig33.android.sdk.common.Tools;
import com.mig33.android.sdk.model.ActivityItem;
import com.mig33.android.sdk.model.ApiResponse;
import com.mig33.android.sdk.model.PaymentItem;
import com.mig33.android.sdk.model.PeopleResponse;
import com.mig33.android.sdk.service.RestRequest.Type;

/**
 * RestService.java
 * 
 * @author warrenbalcos on Apr 26, 2013
 * 
 */
public class RestService {
	
	public static final String			TAG			= "RestService";
	
	private static final RestService	instance	= new RestService();
	
	private DataCache					cache		= DataCache.getInstance();
	
	private RequestQueueManager			manager		= RequestQueueManager.getInstance();
	
	private RestService() {
	}
	
	public static synchronized RestService getInstance() {
		return instance;
	}
	
	public PeopleResponse processPeople(String id, String groupId) {
		
		RestRequest request = new RestRequest(Type.PEOPLE, id, groupId, "@app", false);
		request.setContentType(RestRequest.CONTENT_TYPE_JSON);
		Tools.log(TAG, "getUserInfo: " + request.getKey());
		
		request.addPayloadParam("fields", People.PeopleField.getAllFields());
		
		PeopleResponse response = null;
		CachedData data = cache.getCachedData(request.getKey());
		if (data != null) {
			ApiResponse resp = ApiResponse.fromString(data.getData(), PeopleResponse.class);
			Tools.log(TAG, "processPeople: " + resp.getType() + " " + resp.getEntry());
			if (resp != null) {
				response = (PeopleResponse) resp;
			}
		}
		
		if (data == null || data.isExpired()) {
			manager.queueRequest(request);
		}
		return response;
	}
	
	public void processPayment(PaymentItem item) {
		manager.queueRequest(RestRequest.createPaymentRequest(item));
	}
	
	public void processActivity(ActivityItem item) {
		manager.queueRequest(RestRequest.createActivityRequest(item));
	}
}
