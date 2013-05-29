/**
 * Mig33 Pte. Ltd.
 *
 * Copyright (c) 2012 mig33. All rights reserved.
 */
package com.mig33.android.sdk.common;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

import android.text.TextUtils;

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
	
	public static String sha1Base64Hash(String value) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte[] bytes = value.getBytes("UTF-8");
			md.update(bytes, 0, bytes.length);
			byte[] hash = md.digest();
			byte[] encodedHash = new Base64().encode(hash);
			return new String(encodedHash, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Tools.log(e);
		} catch (NoSuchAlgorithmException e) {
			Tools.log(e);
		}
		return null;
	}
	
	public static String getBodyHash(String str) {
		String result = null;
		if (!TextUtils.isEmpty(str)) {
			result = Tools.sha1Base64Hash(str);
		}
		return result;
	}
	
}
