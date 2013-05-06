/**
 * Mig33 Pte. Ltd.
 *
 * Copyright (c) 2012 mig33. All rights reserved.
 */
package com.mig33.android.sdk.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.client.methods.HttpGet;

import android.text.TextUtils;

import com.mig33.android.sdk.Session;
import com.mig33.android.sdk.common.Config;
import com.projectgoth.b.exception.RestClientException;

/**
 * HttpConnectionHandler.java
 * 
 * @author warrenbalcos on Apr 26, 2013
 * 
 */
public class HttpConnectionHandler {
	
	private static final String	TAG	= "HttpConnectionHandler";
	
	public HttpConnectionHandler() {
	}
	
	private void setDefaulHttpHeader(HttpURLConnection conn) {
		String userAgent = Config.getInstance().getUserAgent();
		if (userAgent != null) {
			conn.setRequestProperty("User-Agent", userAgent);
		}
		
		String cookie = Session.getInstance().getCookiesForHTTPHeader(
				conn.getURL().toExternalForm());
		if (!TextUtils.isEmpty(cookie)) {
			conn.setRequestProperty("Cookie", cookie);
		}
	}
	
	public String sendRequest(String url, String method, String params, String contentType)
			throws RestClientException {
		HttpURLConnection conn = null;
		OutputStream os = null;
		InputStream is = null;
		try {
			try {
				conn = (HttpURLConnection) new URL(url).openConnection();
			} catch (Exception e) {
				throw new RestClientException("Unable to create connection", e);
			}
			
			setDefaulHttpHeader(conn);
			if (contentType != null) {
				conn.setRequestProperty("Content-Type", contentType);
			}
			
			if (!method.equals(HttpGet.METHOD_NAME)) {
				try {
					conn.setRequestMethod(method);
					if (params != null) {
						conn.setDoOutput(true);
						os = conn.getOutputStream();
						os.write(params.getBytes("UTF-8"));
					}
				} catch (Exception e) {
					throw new RestClientException("Unable to send data to server", e);
				}
			}
			// getEid(conn);
			String response = null;
			try {
				is = conn.getInputStream();
				response = read(is);
			} catch (FileNotFoundException e) {
				try {
					throw new RestClientException(read(conn.getErrorStream()), e);
				} catch (Exception e1) {
					throw new RestClientException("Unable to read data from server", e1);
				}
			} catch (Exception e) {
				throw new RestClientException("Unable to read data from server", e);
			}
			return response;
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				os = null;
			}
			
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				is = null;
			}
			
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
	}
	
	private String read(InputStream in) throws Exception {
		StringBuilder sb = new StringBuilder();
		BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
		for (String line = r.readLine(); line != null; line = r.readLine()) {
			sb.append(line);
		}
		in.close();
		return sb.toString();
	}
}
