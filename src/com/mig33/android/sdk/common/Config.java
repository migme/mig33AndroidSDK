/**
 * Mig33 Pte. Ltd.
 *
 * Copyright (c) 2012 mig33. All rights reserved.
 */
package com.mig33.android.sdk.common;

/**
 * Config.java
 * 
 * @author warrenbalcos on Apr 26, 2013
 * 
 */
public class Config {
	
	private static boolean		debug;
	
	private String				encoding;
	
	private boolean				useStubData;
	
	private String				userAgent;
	
	private String				domainUrl;
	
	private String				requestTokenUrl;
	
	private String				authorizeUrl;
	
	private String				accessTokenUrl;
	
	private String				consumerKey;
	
	private String				consumerSecret;
	
	private String				appName;
	
	private String				restEndPoint;
	
	private String				openSocialContentType;
	
	private static final Config	config	= new Config();
	
	private Config() {
		loadDefaultConfig();
	}
	
	public static synchronized Config getInstance() {
		return config;
	}
	
	public void loadDefaultConfig() {
		setDebug(DefaultConfig.DEBUG);
		setUseStubData(DefaultConfig.USE_STUB_DATA);
		setUserAgent(DefaultConfig.USER_AGENT);
		setDomainUrl(DefaultConfig.DOMAIN_URL);
		setRequestTokenUrl(DefaultConfig.REQUEST_TOKEN_URL);
		setAuthorizeUrl(DefaultConfig.AUTHORIZE_URL);
		setAccessTokenUrl(DefaultConfig.ACCESS_TOKEN_URL);
		setRestEndPoint(DefaultConfig.OPENSOCIAL_ENDPOINT);
		setOpenSocialContentType(DefaultConfig.OPENSOCIAL_CONTENT_TYPE);
	}
	
	public static boolean isDebug() {
		return debug;
	}
	
	public static void setDebug(boolean debug) {
		Config.debug = debug;
	}
	
	/**
	 * @return the userAgent
	 */
	public String getUserAgent() {
		return userAgent;
	}
	
	/**
	 * @param userAgent
	 *            the userAgent to set
	 */
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	
	/**
	 * @return the useStubData
	 */
	public boolean isUseStubData() {
		return useStubData;
	}
	
	/**
	 * @param useStubData
	 *            the useStubData to set
	 */
	public void setUseStubData(boolean useStubData) {
		this.useStubData = useStubData;
	}
	
	/**
	 * @return the encoding
	 */
	public String getEncoding() {
		return encoding;
	}
	
	/**
	 * @param encoding
	 *            the encoding to set
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	/**
	 * @return the domainUrl
	 */
	public String getDomainUrl() {
		return domainUrl;
	}
	
	/**
	 * @param domainUrl
	 *            the domainUrl to set
	 */
	public void setDomainUrl(String domainUrl) {
		this.domainUrl = domainUrl;
	}
	
	/**
	 * @return the requestTokenUrl
	 */
	public String getRequestTokenUrl() {
		return requestTokenUrl;
	}
	
	/**
	 * @param requestTokenUrl
	 *            the requestTokenUrl to set
	 */
	public void setRequestTokenUrl(String requestTokenUrl) {
		this.requestTokenUrl = requestTokenUrl;
	}
	
	/**
	 * @return the authorizeUrl
	 */
	public String getAuthorizeUrl() {
		return authorizeUrl;
	}
	
	/**
	 * @param authorizeUrl
	 *            the authorizeUrl to set
	 */
	public void setAuthorizeUrl(String authorizeUrl) {
		this.authorizeUrl = authorizeUrl;
	}
	
	/**
	 * @return the accessTokenUrl
	 */
	public String getAccessTokenUrl() {
		return accessTokenUrl;
	}
	
	/**
	 * @param accessTokenUrl
	 *            the accessTokenUrl to set
	 */
	public void setAccessTokenUrl(String accessTokenUrl) {
		this.accessTokenUrl = accessTokenUrl;
	}
	
	/**
	 * @return the consumerKey
	 */
	public String getConsumerKey() {
		return consumerKey;
	}
	
	/**
	 * @param consumerKey
	 *            the consumerKey to set
	 */
	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}
	
	/**
	 * @return the consumerSecret
	 */
	public String getConsumerSecret() {
		return consumerSecret;
	}
	
	/**
	 * @param consumerSecret
	 *            the consumerSecret to set
	 */
	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}
	
	/**
	 * @return the appName
	 */
	public String getAppName() {
		return appName;
	}
	
	/**
	 * @param appName
	 *            the appName to set
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}
	
	/**
	 * @return the restEndPoint
	 */
	public String getRestEndPoint() {
		return restEndPoint;
	}
	
	/**
	 * @param restEndPoint
	 *            the restEndPoint to set
	 */
	public void setRestEndPoint(String restEndPoint) {
		this.restEndPoint = restEndPoint;
	}
	
	/**
	 * @return the openSocialContentType
	 */
	public String getOpenSocialContentType() {
		return openSocialContentType;
	}
	
	/**
	 * @param openSocialContentType
	 *            the openSocialContentType to set
	 */
	public void setOpenSocialContentType(String openSocialContentType) {
		this.openSocialContentType = openSocialContentType;
	}
	
}
