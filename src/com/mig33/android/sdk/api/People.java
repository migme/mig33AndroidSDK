package com.mig33.android.sdk.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mig33.android.sdk.common.Tools;
import com.mig33.android.sdk.model.PeopleResponse;
import com.mig33.android.sdk.model.UserInfo;
import com.mig33.android.sdk.service.RestRequest;
import com.mig33.android.sdk.service.RestService;

/**
 * People.java
 * 
 * @author warrenbalcos on May 8, 2013
 */
public class People {
	
	private static final String		TAG			= "PEOPLE";
	
	private RestService				restService	= RestService.getInstance();
	
	private List<PeopleListener>	listeners;
	
	private static final People		instance	= new People();
	
	private People() {
		listeners = Collections.synchronizedList(new ArrayList<PeopleListener>());
	}
	
	public static synchronized People getInstance() {
		return instance;
	}
	
	public interface PeopleListener {
		public void onNewDataAvailable(String userId, String groupId);
	}
	
	public enum PeopleField {
		ID("id"),
		USERNAME("username"),
		DISPLAY_NAME("displayName"),
		COUNTRY("country"),
		STATUS_MESSAGE("statusMessage"),
		THUMBNAIL_URL("thumbnailUrl");
		
		private String	fieldValue;
		
		private PeopleField(String fieldValue) {
			this.fieldValue = fieldValue;
		}
		
		public static String getAllFields() {
			String fields = "";
			int ctr = PeopleField.values().length;
			for (PeopleField field : PeopleField.values()) {
				ctr--;
				fields += field.fieldValue;
				if (ctr > 0) {
					fields += ",";
				}
			}
			return fields;
		}
		
		public String getFieldValue() {
			return fieldValue;
		}
	}
	
	public UserInfo getMyInfo() {
		PeopleResponse result = restService.processPeople(RestRequest.ME, RestRequest.SELF);
		if (result != null) {
			Tools.log(TAG, "getMyInfo: " + result.toJsonStr());
			return result.getUserInfo();
		}
		return null;
	}
	
	public ArrayList<UserInfo> getMyFriendsInfo() {
		PeopleResponse result = restService.processPeople(RestRequest.ME, RestRequest.FRIENDS);
		if (result != null) {
			Tools.log(TAG, "getMyFriendsInfo: " + result.toJsonStr());
			return result.getUserInfoList();
		}
		return null;
	}
	
	public UserInfo getUserInfo(String id) {
		PeopleResponse result = restService.processPeople(id, RestRequest.SELF);
		if (result != null) {
			Tools.log(TAG, "getUserInfo: " + result.toJsonStr());
			return result.getUserInfo();
		}
		return null;
	}
	
	public ArrayList<UserInfo> getFriendsInfo(String id) {
		PeopleResponse result = restService.processPeople(id, RestRequest.FRIENDS);
		if (result != null) {
			Tools.log(TAG, "getFriendsInfo: " + result.toJsonStr());
			return result.getUserInfoList();
		}
		return null;
	}
	
	public void addListener(PeopleListener listener) {
		listeners.add(listener);
	}
	
	public void onDataUpdated(String userId, String groupId) {
		cleanListeners();
		for (PeopleListener listener : listeners) {
			if (listener != null) {
				listener.onNewDataAvailable(userId, groupId);
			}
		}
	}
	
	private void cleanListeners() {
		for (int i = 0; i < listeners.size(); i++) {
			PeopleListener temp = listeners.get(i);
			if (temp == null) {
				listeners.remove(i);
			}
		}
	}
}
