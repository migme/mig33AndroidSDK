/**
 * Mig33 Pte. Ltd.
 *
 * Copyright (c) 2012 mig33. All rights reserved.
 */
package com.mig33.android.sdk.model;

import com.mig33.android.sdk.model.ActivityPayLoad.CustomUrl;
import com.mig33.android.sdk.model.ActivityPayLoad.MediaItem;
import com.mig33.android.sdk.model.ActivityPayLoad.TemplateParam;
import com.mig33.android.sdk.service.RestRequest;

/**
 * ActivityItem.java
 * 
 * @author warrenbalcos on May 20, 2013
 * 
 */
public class ActivityItem extends RequestItem {
	
	private ActivityPayLoad	payload;
	
	public ActivityItem(String title) {
		setUserId(RestRequest.ME);
		setGroupId(RestRequest.SELF);
		setAppId(RestRequest.APP);
		payload = new ActivityPayLoad();
		payload.setTitle(title);
//		setJ2meUrl(String.format("mig33://startapp/%s", new Object[] { Config.getInstance()
//				.getAppName() }));
	}
	
	/**
	 * @return the title
	 */
	public String getTitle() {
		return payload.getTitle();
	}
	
	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		payload.setTitle(title);
	}
	
	public void setActionButtonTitle(String title) {
		TemplateParam param = payload.getTemplateParams();
		param.setActionButtonTitle(title);
		payload.setTemplateParams(param);
	}
	
	public void setWebUrl(String url) {
		CustomUrl temp = payload.new CustomUrl();
		temp.setType(ActivityPayLoad.WEB_TYPE);
		temp.setUrl(url);
		payload.addDeviceCustomUrl(temp);
	}
	
	public void setJ2meUrl(String url) {
		CustomUrl temp = payload.new CustomUrl();
		temp.setType(ActivityPayLoad.J2ME_TYPE);
		temp.setUrl(url);
		payload.addDeviceCustomUrl(temp);
	}
	
	public void setTouchUrl(String url) {
		CustomUrl temp = payload.new CustomUrl();
		temp.setType(ActivityPayLoad.TOUCH_TYPE);
		temp.setUrl(url);
		payload.addDeviceCustomUrl(temp);
	}
	
	public void set48x48ImageUrl(String url) {
		addMediaItem(ActivityPayLoad.IMAGE_MIMETYPE, url);
	}
	
	public void set96x96ImageUrl(String url) {
		addMediaItem(ActivityPayLoad.IMAGE_MIMETYPE, url);
	}
	
	public void set120x120ImageUrl(String url) {
		addMediaItem(ActivityPayLoad.IMAGE_MIMETYPE, url);
	}
	
	public void set300x300ImageUrl(String url) {
		addMediaItem(ActivityPayLoad.IMAGE_MIMETYPE, url);
	}
	
	public void addMediaItem(String mimeType, String url) {
		MediaItem item = payload.new MediaItem();
		item.setMimeType(mimeType);
		item.setUrl(url);
		payload.addMedialItem(item);
	}
	
	//@formatter:off
	/**
	 * Helper method to set images  
	 * 
	 *    "mimeType":"IMAGE", 
	 *    "url":"<baseUrl>/images/<imageName>_48x48.<filenameExt>"
	 * 
	 * @param baseUrl - image host url ex. ("http://yodaquotes.migjam.projectgoth.com")
	 * @param imageName - image name ex ("yoda1")
	 * @param filenameExt - ex. ("jpg")
	 * 
	 * "mediaItems": [
	 *         {
	 *         "mimeType":"IMAGE", 
	 *         "url":"http://yodaquotes.migjam.projectgoth.com/images/yoda1_48x48.jpg"
	 *         }, {
	 *         "mimeType":"IMAGE", 
	 *         "url":"http://yodaquotes.migjam.projectgoth.com/images/yoda1_96x96.jpg"
	 *         }, {
	 *         "mimeType":"IMAGE", 
	 *         "url":"http://yodaquotes.migjam.projectgoth.com/images/yoda1_120x120.jpg"
	 *         }, { 
	 *         "mimeType":"IMAGE", 
	 *         "url":"http://yodaquotes.migjam.projectgoth.com/images/yoda1_300x300.jpg"
	 *         }
	 *     ],
	 * 
	 */
	public void setImageMediaItems(String baseUrl, String imageName, String filenameExt) {
		for (int i = 0; i < ActivityPayLoad.IMAGE_SIZES.length; i++) {
			MediaItem item = payload.new MediaItem();
			item.setMimeType(ActivityPayLoad.IMAGE_MIMETYPE);
			item.setUrl(baseUrl + "/" + imageName + "_" + ActivityPayLoad.IMAGE_SIZES[i] + "."
					+ filenameExt);
			payload.addMedialItem(item);
		}
	}
	//@formatter:on
	
	public String getJsonPayLoad() {
		return payload.toJsonString();
	}
}
