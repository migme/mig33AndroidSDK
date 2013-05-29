/**
 * Mig33 Pte. Ltd.
 *
 * Copyright (c) 2012 mig33. All rights reserved.
 */
package com.mig33.android.sdk.model;

/**
 * RequestItem.java
 * 
 * @author warrenbalcos on May 20, 2013
 * 
 */
public class RequestItem {
	
	private String	userId;
	
	private String	groupId;
	
	private String	appId;
	
	public RequestItem() {
	}
	
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	
	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return groupId;
	}
	
	/**
	 * @param groupId
	 *            the groupId to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	/**
	 * @return the appId
	 */
	public String getAppId() {
		return appId;
	}
	
	/**
	 * @param appId
	 *            the appId to set
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}
	
	/**
	 * @return the jsonPayLoad
	 */
	public String getJsonPayLoad() {
		return null;
	}
	
}
