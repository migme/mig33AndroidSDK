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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.text.TextUtils;

import com.mig33.android.sdk.Session;
import com.mig33.android.sdk.common.Config;
import com.mig33.android.sdk.common.Tools;
import com.mig33.android.sdk.oauth.OAuthHandler;
import com.projectgoth.b.exception.RestClientException;

/**
 * HttpConnectionHandler.java
 * 
 * @author warrenbalcos on Apr 26, 2013
 * 
 */
public class HttpConnectionHandler {
	
	private static final String		TAG	= "HttpConnectionHandler";
	
	private HttpClient				httpClient;
	
	private ClientConnectionManager	tsccm;
	private HttpParams				parameters;
	
	public HttpConnectionHandler() {
		parameters = new BasicHttpParams();
		HttpProtocolParams.setVersion(parameters, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(parameters, HTTP.DEFAULT_CONTENT_CHARSET);
		HttpProtocolParams.setUseExpectContinue(parameters, false);
		HttpConnectionParams.setTcpNoDelay(parameters, true);
		HttpConnectionParams.setSocketBufferSize(parameters, 8192);
		
		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		tsccm = new ThreadSafeClientConnManager(parameters, schReg);
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
	
	public String sendApacheRequest(RestRequest request) throws ClientProtocolException,
			IOException, OAuthMessageSignerException, OAuthExpectationFailedException,
			OAuthCommunicationException {
		
		httpClient = new DefaultHttpClient(tsccm, parameters);
		
		String url = request.getUrl();
		String method = request.getMethod();
		
		OAuthConsumer consumer = OAuthHandler.getInstance().getConsumer();
		HttpUriRequest httpRequest = null;
		String params = null;
		
		if (method.equals(HttpGet.METHOD_NAME)) {
			params = request.getParamStr();
			if (!TextUtils.isEmpty(params)) {
				params = "?" + params;
			} else {
				params = "";
			}
			url = url + params;
			httpRequest = new HttpGet(url);
		} else if (method.equals(HttpPost.METHOD_NAME)) {
			
			String bodyHash = request.getBodyHash();
			if (!TextUtils.isEmpty(bodyHash)) {
				url += "?oauth_body_hash=" + URLEncoder.encode(bodyHash, "UTF-8");
			}
			
			params = request.getParamJson();
			HttpPost post = new HttpPost(url);
			if (!TextUtils.isEmpty(params)) {
				StringEntity entity = new StringEntity(params, "UTF-8");
				post.setEntity(entity);
			}
			httpRequest = post;
		}
		
		String contentType = request.getContentType();
		if (!TextUtils.isEmpty(contentType)) {
			httpRequest.addHeader("Content-Type", contentType);
		}
		
		logRequest(httpRequest);
		consumer.sign(httpRequest);
		Tools.log(TAG, "request signed");
		
		if (method.equals(HttpPost.METHOD_NAME)) {
			String bodyHash = request.getBodyHash();
			if (!TextUtils.isEmpty(bodyHash)) {
				String auth = "";
				for (Header header : httpRequest.getAllHeaders()) {
					if (header.getName().equals("Authorization")) {
						auth = header.getValue();
						break;
					}
				}
				auth += ", oauth_body_hash=\"" + URLEncoder.encode(bodyHash, "UTF-8") + "\"";
				httpRequest.addHeader("Authorization", auth);
			}
		}
		
		logRequest(httpRequest);
		
		String response = httpClient.execute(httpRequest, new BasicResponseHandler());
		Tools.log(TAG, "response: " + response);
		
		// Do whatever you want with the returned info
		// JSONObject jsonObject = new JSONObject(response);
		return response;
	}
	
	public String sendRequest(RestRequest request) throws RestClientException,
			OAuthMessageSignerException, OAuthExpectationFailedException,
			OAuthCommunicationException {
		
		String url = request.getUrl();
		String method = request.getMethod();
		
		HttpURLConnection conn = null;
		OutputStream os = null;
		InputStream is = null;
		
		if (method.equals(HttpGet.METHOD_NAME)) {
			String params = request.getParamStr();
			if (!TextUtils.isEmpty(params)) {
				url = url + "?" + params;
			} else if (method.equals(HttpPost.METHOD_NAME)) {
				try {
					String bodyHash = URLEncoder.encode(Tools.getBodyHash(params), "UTF-8");
					url = url + "?oauth_body_hash=" + bodyHash;
				} catch (UnsupportedEncodingException e) {
					Tools.log(e);
				}
			}
		}
		
		OAuthConsumer consumer = OAuthHandler.getInstance().getConsumer();
		url = consumer.sign(url);
		
		if (method.equals(HttpPost.METHOD_NAME)) {
			// adding body hash
			String bodyHash = request.getBodyHash();
			if (!TextUtils.isEmpty(bodyHash)) {
				try {
					bodyHash = URLEncoder.encode(bodyHash, "UTF-8");
					url = url + "&oauth_body_hash=" + bodyHash;
				} catch (UnsupportedEncodingException e) {
					Tools.log(e);
				}
			}
		}
		Tools.log(TAG, "sendRequest signed url: " + url);
		
		try {
			try {
				conn = (HttpURLConnection) new URL(url).openConnection();
			} catch (Exception e) {
				Tools.log(e);
				throw new RestClientException("Unable to create connection", e);
			}
			// consumer.sign(conn);
			
			// OAuthHandler.getInstance().sign(conn);
			
			setDefaulHttpHeader(conn);
			String contentType = request.getContentType();
			if (!TextUtils.isEmpty(contentType)) {
				conn.setRequestProperty("Content-Type", contentType);
			}
			
			if (!method.equals(HttpGet.METHOD_NAME)) {
				try {
					conn.setRequestMethod(method);
					String jsonParams = request.getParamJson();
					if (jsonParams != null) {
						Tools.log(TAG, "Post payload: " + jsonParams);
						conn.setDoOutput(true);
						os = conn.getOutputStream();
						os.write(jsonParams.getBytes("UTF-8"));
					}
				} catch (Exception e) {
					Tools.log(e);
					throw new RestClientException("Unable to send data to server", e);
				}
			}
			// getEid(conn);
			String response = null;
			try {
				is = conn.getInputStream();
				response = read(is);
			} catch (FileNotFoundException e) {
				Tools.log(e);
				try {
					throw new RestClientException(read(conn.getErrorStream()), e);
				} catch (Exception e1) {
					throw new RestClientException("Unable to read data from server", e1);
				}
			} catch (Exception e) {
				Tools.log(e);
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
	
	private void logRequest(HttpUriRequest request) {
		StringBuilder sb = new StringBuilder();
		sb.append("-->[request start]");
		URI uri = request.getURI();
		if (uri != null) {
			sb.append("\n [host]:" + uri.getHost());
			sb.append("\n [path]:" + uri.getPath());
			sb.append("\n [query]:" + uri.getQuery());
			sb.append("\n [method]:" + request.getMethod());
			String entityString = null;
			try {
				if (request != null && request instanceof HttpPost) {
					HttpPost post = (HttpPost) request;
					HttpEntity entity = post.getEntity();
					if (entity != null) {
						if (entity.isRepeatable()) {
							entityString = EntityUtils.toString(entity, "UTF-8");
						} else {
							entityString = " non-repeatable, skip";
						}
						sb.append("\n [entity]:" + entityString);
					}
				}
			} catch (ParseException e) {
				Tools.log(TAG, "crash at entity: " + entityString);
				Tools.log(e);
			} catch (IOException e) {
				Tools.log(TAG, "crash at entity: " + entityString);
				Tools.log(e);
			}
		}
		Header[] allHeader = request.getAllHeaders();
		if (allHeader != null) {
			for (Header header : allHeader) {
				sb.append("\n [header]k:").append(header.getName()).append(",").append("v:")
						.append(header.getValue());
			}
		}
		sb.append("\n-->[request end]");
		Tools.log(TAG, sb.toString());
	}
	
	public void onFinish() {
		// shutdown the connection manager - last bit of the apache code
		httpClient.getConnectionManager().shutdown();
	}
}
