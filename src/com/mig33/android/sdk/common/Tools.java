/**
 * Mig33 Pte. Ltd.
 *
 * Copyright (c) 2012 mig33. All rights reserved.
 */
package com.mig33.android.sdk.common;

/**
 * Tools.java
 * 
 * @author warrenbalcos on Apr 26, 2013
 * 
 */
public class Tools {
	
	private static FusionLogger	logger;
	
	public static void log(String tag, String message) {
		if (logger != null) {
			logger.log(tag, message);
		} else if (Config.isDebug()) {
			System.out.println(tag + ": " + message);
		}
	}
	
	public static void log(Exception e) {
		if (logger != null) {
			logger.log(e);
		} else {
			if (Config.isDebug()) {
				e.printStackTrace();
			} else {
				System.out.println(e.getMessage());
			}
		}
	}
	
	public void log(String message) {
		if (logger != null) {
			logger.log(message);
		} else if (Config.isDebug()) {
			System.out.println(message);
		}
	}
	
	public static void setCustomLogger(FusionLogger logger) {
		Tools.logger = logger;
	}
	
}
