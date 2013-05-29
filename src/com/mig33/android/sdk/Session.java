package com.mig33.android.sdk;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.mig33.android.sdk.common.Config;
import com.mig33.android.sdk.model.UserInfo;

public class Session {
	
	private static final Session	mInstance	= new Session();
	
	private String					userId;
	private String					username;
	private String					sessionId;
	
	private UserInfo				userInfo;
	
	/**
	 * Cookie manager
	 */
	protected CookieManager			cookieManager;
	
	/**
	 * Cookie sync manager
	 */
	protected CookieSyncManager		cookieSyncManager;
	
	private Session() {
		
	}
	
	public static Session getInstance() {
		return mInstance;
	}
	
	public void initSession(Context context) {
		this.cookieSyncManager = CookieSyncManager.createInstance(context);
		this.cookieManager = CookieManager.getInstance();
		this.cookieManager.setAcceptCookie(true);
		this.cookieManager.removeSessionCookie();
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getSessionId() {
		return sessionId;
	}
	
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
		
		// if (!TextUtils.isEmpty(sessionId) && isFacebookSession()) {
		// setFacebookSessionId(sessionId);
		// }
		setCookieSessionId(sessionId);
	}
	
	/**
	 * @return the userInfo
	 */
	public UserInfo getUserInfo() {
		return userInfo;
	}
	
	/**
	 * @param userInfo
	 *            the userInfo to set
	 */
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	
	public String getCookiesForHTTPHeader(String url) {
		return cookieManager.getCookie(url);
	}
	
	public void setCookie(String url, String value) {
		cookieManager.setCookie(url, value);
	}
	
	public void syncCookies() {
		CookieSyncManager.getInstance().sync();
	}
	
	public String getUrlPrefix() {
		return "http://www.mig33.com";
	}
	
	public String getCookiePrefix() {
		StringBuilder domainForCookie = new StringBuilder();
		String pageletServerUrl = getUrlPrefix();
		
		try {
			URL url = new URL(pageletServerUrl);
			String[] hostParts = url.getHost().split("\\.");
			int n = hostParts.length > 2 ? n = hostParts.length - 2 : 0;
			while (n < hostParts.length) {
				domainForCookie.append("." + hostParts[n]);
				n++;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		if (domainForCookie.length() == 0) {
			domainForCookie.append(".mig33.com");
		}
		
		return "; domain=" + domainForCookie.toString() + "; path=/";
	}
	
	public void setCookieSessionId(String id) {
		String url = getUrlPrefix();
		
		if (id == null || !id.trim().equalsIgnoreCase("deleted")) {
			if (!TextUtils.isEmpty(id)) {
				try {
					String eid = "eid=" + URLEncoder.encode(id, Config.getInstance().getEncoding())
							+ getCookiePrefix();
					this.cookieManager.setCookie(url, eid);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		
		// this.cookieManager.setCookie(url, "theme=" + Theme.getName() +
		// getCookiePrefix());
		// this.cookieManager.setCookie(url, "lang=" +
		// ApplicationEx.getLanguageId()
		// + getCookiePrefix());
		CookieSyncManager.getInstance().sync();
	}
	
	public boolean isMe(String userId) {
		return (userId != null && (userId.equals("@me") || userId.equals(this.userId)));
	}
}
