/**
 * Mig33 Pte. Ltd.
 *
 * Copyright (c) 2012 mig33. All rights reserved.
 */
package com.mig33.android.sdk.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mig33.android.sdk.common.Tools;
import com.mig33.android.sdk.service.RestRequest.Type;

/**
 * PeopleResponse.java
 * 
 * @author warrenbalcos on May 20, 2013
 * 
 */
public class PeopleResponse extends ApiResponse {
	
	public PeopleResponse(String responseStr) {
		super(responseStr);
	}
	
	public PeopleResponse(ApiResponse response) {
		super(response.getResponseStr());
		setResponseIds(response.getId(), response.getUserId(), response.getGroupId(),
				response.getAppId());
		setType(Type.PEOPLE);
	}
	
	public UserInfo getUserInfo() {
		List<UserInfo> list = getUserInfoList();
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	public ArrayList<UserInfo> getUserInfoList() {
		ArrayList<UserInfo> userList = new ArrayList<UserInfo>();
		try {
			String entry = getEntry();
			JsonParser parser = new JsonParser();
			JsonElement obj = parser.parse(entry);
			if (obj != null) {
				if (obj.isJsonArray()) {
					UserInfo[] userArray = new Gson().fromJson(entry, UserInfo[].class);
					userList.addAll(Arrays.asList(userArray));
				} else {
					UserInfo info = new Gson().fromJson(entry, UserInfo.class);
					userList.add(info);
				}
			}
		} catch (Exception e) {
			Tools.log(e);
		}
		return userList;
	}
}
