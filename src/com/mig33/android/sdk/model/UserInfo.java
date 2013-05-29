/**
 * Mig33 Pte. Ltd.
 *
 * Copyright (c) 2012 mig33. All rights reserved.
 */
package com.mig33.android.sdk.model;

import com.google.gson.Gson;

/**
 * UserInfo.java
 * 
 * @author warrenbalcos on Apr 26, 2013
 * 
 */
public class UserInfo {
	
	private String	username;
	
	private String	id;
	
	private String	displayName;
	
	private String	country;
	
	private String	statusMessage;
	
	private String	thumbnailUrl;
	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}
	
	/**
	 * @param displayName
	 *            the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}
	
	/**
	 * @param country
	 *            the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	
	/**
	 * @return the statusMessage
	 */
	public String getStatusMessage() {
		return statusMessage;
	}
	
	/**
	 * @param statusMessage
	 *            the statusMessage to set
	 */
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	
	/**
	 * @return the thumbnailUrl
	 */
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	
	/**
	 * @param thumbnailUrl
	 *            the thumbnailUrl to set
	 */
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("UserInfo: [");
		sb.append("\n username: " + username);
		sb.append("\n id: " + id);
		sb.append("\n displayName: " + displayName);
		sb.append("\n country: " + country);
		sb.append("\n statusMessage: " + statusMessage);
		sb.append("\n thumbnailUrl: " + thumbnailUrl);
		sb.append("\n ]");
		return sb.toString();
	}
	
	public String toJsonString() {
		return new Gson().toJson(this);
	}
}
