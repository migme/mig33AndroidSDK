/**
 * Mig33 Pte. Ltd.
 *
 * Copyright (c) 2012 mig33. All rights reserved.
 */
package com.mig33.android.sdk.common;

/**
 * FusionLogger.java
 * 
 * @author warrenbalcos on Jan 23, 2013
 * 
 */
public interface FusionLogger {
	public void log(String tag, String message);
	
	public void log(String message);
	
	public void log(Exception e);
}
