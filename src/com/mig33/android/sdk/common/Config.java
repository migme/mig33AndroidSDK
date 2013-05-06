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
	 * @param encoding the encoding to set
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
}
