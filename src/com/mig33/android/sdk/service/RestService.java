/**
 * Mig33 Pte. Ltd.
 *
 * Copyright (c) 2012 mig33. All rights reserved.
 */
package com.mig33.android.sdk.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.mig33.android.sdk.Session;
import com.mig33.android.sdk.api.People.PeopleField;
import com.mig33.android.sdk.cache.DataCache;
import com.mig33.android.sdk.common.Tools;
import com.mig33.android.sdk.model.UserInfo;
import com.projectgoth.b.android.RestParams;
import com.projectgoth.b.exception.RestClientException;
import com.projectgoth.b.exception.RestErrorException;

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
	
	// TODO: create request queuing and executor service
	// TODO: response cache and new data broadcast update
	
	private RestService() {
	}
	
	public static synchronized RestService getInstance() {
		return instance;
	}
	
	public ArrayList<UserInfo> getMyInfo() {
		return getUserInfo(Session.getInstance().getUserId());
	}
	
	private ArrayList<UserInfo> getUserInfo(String id) {
		
		RestType type = RestType.PEOPLE;
		String url = type.getUrl();
		url = String.format(url, new Object[] { id, "@self" });
		
		Tools.log(TAG, "getUserInfo: " + type.getMethod() + " " + url);
		
		RestParams params = new RestParams();
		params.set("fields", PeopleField.getAllFields());
		
		RestRequest request = new RestRequest(RestRequest.Type.PEOPLE, type.getMethod(), url,
				params.getEncodedUrl());
		manager.queueRequest(request);
		
		String data = cache.getData(request.getKey());
		UserInfo[] users= null;
		if (data != null) {
			try {
				users = (UserInfo[]) RestRequest.parseData(request.getType(), data);
			} catch (RestErrorException e) {
				Tools.log(e);
			} catch (RestClientException e) {
				Tools.log(e);
			}
		}
		return new ArrayList<UserInfo>(Arrays.asList(users));
	}
	
	public ArrayList<UserInfo> getFriendsInfo(String id) {
		ArrayList<UserInfo> friends = new ArrayList<UserInfo>();
		return friends;
	}
	
	public static ArrayList<UserInfo> getMyFriendsInfo() {
		ArrayList<UserInfo> friends = new ArrayList<UserInfo>();
		return friends;
	}
	
}
