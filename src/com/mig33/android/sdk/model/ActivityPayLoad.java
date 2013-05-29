/**
 * Mig33 Pte. Ltd.
 *
 * Copyright (c) 2012 mig33. All rights reserved.
 */
package com.mig33.android.sdk.model;

import java.util.HashMap;

import com.google.gson.Gson;

/**
 * ActivityPayLoad.java
 * 
 * @author warrenbalcos on May 20, 2013
 * 
 */
public class ActivityPayLoad {
	
	public static final String		WEB_TYPE		= "web";
	public static final String		J2ME_TYPE		= "j2me";
	public static final String		TOUCH_TYPE		= "touch";
	
	public static final String		IMAGE_MIMETYPE	= "IMAGE";
	
	public static final String[]	IMAGE_SIZES		= { "48x48", "96x96", "120x120", "300x300" };
	
	//@formatter:off
	/**
	 * {
	 * "title": "I just got to Level 7 and I want everyone to celebrate with free Lemon Merigue Pies!", 
	 * "deviceCustomUrls": [
	 *         {
	 *         "type":"j2me", 
	 *         "url":"mig33://startapp/t.game"
	 *         }, {
	 *         "type":"web", 
	 *         "url":"http://someawesomeurl.com/t.php"
	 *         } 
	 *     ],
	 * "mediaItems": [
	 *         {
	 *         "mimeType":"IMAGE", 
	 *         "url":"http://someawesomeurl.com/images/level7_48x48.jpg"
	 *         }, {
	 *         "mimeType":"IMAGE", 
	 *         "url":"http://someawesomeurl.com/images/level7_96x96.jpg"
	 *         }, {
	 *         "mimeType":"IMAGE", 
	 *         "url":"http://someawesomeurl.com/images/level7_120x120.jpg"
	 *         }, { 
	 *         "mimeType":"IMAGE", 
	 *         "url":"http://someawesomeurl.com/images/level7_300x300.jpg"
	 *         }
	 *     ], 
	 * "templateParams": 
	 *     {
	 *     "actionButtonTitle":"Get Lemon Meringue Pies Now!"    
	 *     }
	 * }
	 */
	//@formatter:on
	
	public ActivityPayLoad() {
		templateParams = new TemplateParam();
		templateParams.setActionButtonTitle("Play");
//		mediaItems = new MediaItem[0];
	}
	
	private String			title;
	
	private CustomUrl[]		deviceCustomUrls;
	
	private MediaItem[]		mediaItems;
	
	private TemplateParam	templateParams;
	
	public class MediaItem {
		
		private String	mimeType;
		private String	url;
		
		/**
		 * @return the mimeType
		 */
		public String getMimeType() {
			return mimeType;
		}
		
		/**
		 * @param mimeType
		 *            the mimeType to set
		 */
		public void setMimeType(String mimeType) {
			this.mimeType = mimeType;
		}
		
		/**
		 * @return the url
		 */
		public String getUrl() {
			return url;
		}
		
		/**
		 * @param url
		 *            the url to set
		 */
		public void setUrl(String url) {
			this.url = url;
		}
	}
	
	public class CustomUrl {
		
		private String	type;
		private String	url;
		
		/**
		 * @return the type
		 */
		public String getType() {
			return type;
		}
		
		/**
		 * @param type
		 *            the type to set
		 */
		public void setType(String type) {
			this.type = type;
		}
		
		/**
		 * @return the url
		 */
		public String getUrl() {
			return url;
		}
		
		/**
		 * @param url
		 *            the url to set
		 */
		public void setUrl(String url) {
			this.url = url;
		}
	}
	
	public class TemplateParam {
		private String	actionButtonTitle;
		
		/**
		 * @return the actionButtonTitle
		 */
		public String getActionButtonTitle() {
			return actionButtonTitle;
		}
		
		/**
		 * @param actionButtonTitle
		 *            the actionButtonTitle to set
		 */
		public void setActionButtonTitle(String actionButtonTitle) {
			this.actionButtonTitle = actionButtonTitle;
		}
	}
	
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * @return the deviceCustomUrls
	 */
	public CustomUrl[] getDeviceCustomUrls() {
		return deviceCustomUrls;
	}
	
	/**
	 * @param deviceCustomUrls
	 *            the deviceCustomUrls to set
	 */
	public void setDeviceCustomUrls(CustomUrl[] deviceCustomUrls) {
		this.deviceCustomUrls = deviceCustomUrls;
	}
	
	public void addDeviceCustomUrl(CustomUrl customUrl) {
		HashMap<String, CustomUrl> temp = new HashMap<String, CustomUrl>();
		if (deviceCustomUrls != null) {
			for (CustomUrl cust : deviceCustomUrls) {
				temp.put(cust.getType(), cust);
			}
		}
		temp.put(customUrl.getType(), customUrl);
		deviceCustomUrls = new CustomUrl[temp.size()];
		deviceCustomUrls = temp.values().toArray(deviceCustomUrls);
	}
	
	/**
	 * @return the mediaItems
	 */
	public MediaItem[] getMediaItems() {
		return mediaItems;
	}
	
	/**
	 * @param mediaItems
	 *            the mediaItems to set
	 */
	public void setMediaItems(MediaItem[] mediaItems) {
		this.mediaItems = mediaItems;
	}
	
	public void addMedialItem(MediaItem item) {
		HashMap<String, MediaItem> temp = new HashMap<String, MediaItem>();
		if (mediaItems != null) {
			for (MediaItem med : mediaItems) {
				temp.put(med.getUrl(), med);
			}
		}
		temp.put(item.getUrl(), item);
		mediaItems = new MediaItem[temp.size()];
		mediaItems = temp.values().toArray(mediaItems);
	}
	
	/**
	 * @return the templateParams
	 */
	public TemplateParam getTemplateParams() {
		if (templateParams == null) {
			templateParams = new TemplateParam();
			templateParams.setActionButtonTitle("Play");
		}
		return templateParams;
	}
	
	/**
	 * @param templateParams
	 *            the templateParams to set
	 */
	public void setTemplateParams(TemplateParam templateParams) {
		this.templateParams = templateParams;
	}
	
	public String toJsonString() {
		return new Gson().toJson(this);
	}
}
