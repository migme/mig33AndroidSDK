package com.projectgoth.b.android;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.net.TrafficStats;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.mig33.android.sdk.Session;
import com.mig33.android.sdk.common.Config;
import com.projectgoth.b.BaseRestClient;
import com.projectgoth.b.BaseRestParams;
import com.projectgoth.b.MultipartFormData;
import com.projectgoth.b.RestType;
import com.projectgoth.b.data.Action;
import com.projectgoth.b.data.Alert;
import com.projectgoth.b.data.BadgesResponse;
import com.projectgoth.b.data.CreatePostResponse;
import com.projectgoth.b.data.DisplayImage;
import com.projectgoth.b.data.Error;
import com.projectgoth.b.data.Followers;
import com.projectgoth.b.data.Following;
import com.projectgoth.b.data.GroupResponse;
import com.projectgoth.b.data.HotTopic;
import com.projectgoth.b.data.HotTopicsResult;
import com.projectgoth.b.data.LoginResponse;
import com.projectgoth.b.data.Mentions;
import com.projectgoth.b.data.MenuConfig;
import com.projectgoth.b.data.MigAlerts;
import com.projectgoth.b.data.MigAlertsUnread;
import com.projectgoth.b.data.MigAlertsUnreadResult;
import com.projectgoth.b.data.Post;
import com.projectgoth.b.data.PostSearchResult;
import com.projectgoth.b.data.Profile;
import com.projectgoth.b.data.RecommendedProfile;
import com.projectgoth.b.data.Replies;
import com.projectgoth.b.data.Reshares;
import com.projectgoth.b.data.SearchResult;
import com.projectgoth.b.data.SessionCheckResponse;
import com.projectgoth.b.data.SuccessResponse;
import com.projectgoth.b.data.Tag;
import com.projectgoth.b.data.ThirdPartySites;
import com.projectgoth.b.data.Variable;
import com.projectgoth.b.data.VariableLabel;
import com.projectgoth.b.data.ViewURL;
import com.projectgoth.b.enums.AddFollowingResultEnum;
import com.projectgoth.b.enums.DeleteFollowingResultEnum;
import com.projectgoth.b.enums.ObjectTypeEnum;
import com.projectgoth.b.enums.PostApplicationEnum;
import com.projectgoth.b.exception.RestClientException;
import com.projectgoth.b.exception.RestErrorException;

public class RestClient extends BaseRestClient {
	
	public static final String	SSO_URL				= "https://login.mig33.com/touch/datasvc";
	public static final String	MIGBO_DATASVC_URL	= "http://www.mig33.com/touch/datasvc";
	public static final String	MULTIPART_POST_URL	= "http://www.mig33.com/touch/post/hidden_post";
	public static final String	IMAGES_URL			= "http://www.mig33.com/resources/img";
	
	private static final Gson	gson				= new Gson();
	
	private boolean				connected			= false;
	
	private RestResponseCache	restResponseCache	= RestResponseCache.getInstance();
	
	private boolean				trafficStatsFlag	= false;
	
	static {
		try {
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, new X509TrustManager[] { new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain, String authType)
						throws CertificateException {
				}
				
				public void checkServerTrusted(X509Certificate[] chain, String authType)
						throws CertificateException {
				}
				
				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}
			} }, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
		} catch (Exception e) { // should never happen
			e.printStackTrace();
		}
	}
	
	public RestClient() {
		this(SSO_URL, MIGBO_DATASVC_URL, MULTIPART_POST_URL, "");
	}
	
	public RestClient(String ssoUrl, String dataServiceUrl, String multipartPostUrl,
			String userAgent) {
		super(new AndroidPlatformLib(), ssoUrl, dataServiceUrl, multipartPostUrl, userAgent,
				PostApplicationEnum.ANDROID);
	}
	
	@Override
	public String getEId() {
		return Session.getInstance().getSessionId();
	}
	
	@Override
	public void setEId(String eId) {
		Session.getInstance().setSessionId(eId);
	}
	
	private void setDefaulHttpHeader(HttpURLConnection conn) {
		String userAgent = getUserAgent();
		if (userAgent != null) {
			conn.setRequestProperty("User-Agent", userAgent);
		}
		
		String cookie = Session.getInstance().getCookiesForHTTPHeader(
				conn.getURL().toExternalForm());
		if (!TextUtils.isEmpty(cookie)) {
			conn.setRequestProperty("Cookie", cookie);
		}
	}
	
	private void processHttpHeader(HttpURLConnection conn) {
		Map<String, List<String>> headers = conn.getHeaderFields();
		if (headers != null) {
			boolean found = false;
			for (Entry<String, List<String>> entry : headers.entrySet()) {
				if (null != entry.getKey() && entry.getKey().equalsIgnoreCase("Set-Cookie")) {
					StringBuilder valuestring = new StringBuilder(entry.getKey() + ":");
					List<String> values = entry.getValue();
					for (String value : values) {
						valuestring.append(value + "##");
						if (value.startsWith("eid=")) {
							String eid = "";
							int end = value.indexOf(";");
							if (end == -1) {
								eid = value.substring(4);
							} else {
								eid = value.substring(4, end);
							}
							try {
								setEId(URLDecoder.decode(eid, Config.getInstance().getEncoding()));
							} catch (UnsupportedEncodingException e) {
								System.out.println(e);
							}
							found = true;
							break;
						} else {
							Session.getInstance().setCookie(conn.getURL().toExternalForm(), value);
						}
					}
					Session.getInstance().syncCookies();
					// Tools.log(this, valuestring.toString());
					if (found) {
						break;
					}
				}
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
	
	public BaseRestParams createParams() {
		return new RestParams();
	}
	
	private String sendRequestWithoutTrafficStats(String url, String method, String params,
			String contentType) throws RestClientException {
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
			processHttpHeader(conn);
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
	
	@SuppressLint("NewApi")
	private String sendRequestWithTrafficStats(String url, String method, String params,
			String contentType) throws RestClientException, NoSuchMethodError {
		TrafficStats.setThreadStatsTag(0xF03D);
		try {
			return sendRequestWithoutTrafficStats(url, method, params, contentType);
		} finally {
			TrafficStats.clearThreadStatsTag();
		}
	}
	
	protected String sendRequest(String url, String method, String params, String contentType,
			RestType type, boolean force) throws RestClientException {
		
		url = resolveUrl(url);
		System.out.println(method + " " + url);
		
		if (method.equals(HttpGet.METHOD_NAME) && params != null && params.length() > 0) {
			url = url + "?" + params;
		}
		
		if (params != null) {
			System.out.println(params);
		}
		
		String response = restResponseCache.getData(url, method, params, contentType);
		if (force || response == null) {
			if (trafficStatsFlag) {
				try {
					response = sendRequestWithTrafficStats(url, method, params, contentType);
				} catch (NoSuchMethodError e) {
					trafficStatsFlag = false;
					response = sendRequestWithoutTrafficStats(url, method, params, contentType);
				}
			} else {
				response = sendRequestWithoutTrafficStats(url, method, params, contentType);
			}
			if (type != null && type.hasExpiry()) {
				Error error = getErrorFromResponse(response);
				if (error == null || !error.isError()) {
					restResponseCache.cacheData(url, method, params, contentType, response,
							type.getExpiry());
				}
			}
		}
		
		return response;
	}
	
	private Error getErrorFromResponse(String response) throws RestClientException {
		Error error = null;
		try {
			JSONObject json = new JSONObject(response);
			String errorString = json.getJSONObject(ERROR_IDENTIFIER).toString();
			error = gson.fromJson(errorString, Error.class);
		} catch (JSONException e) {
			System.out.println(e);
		}
		return error;
	}
	
	public Object getDataOrThrow(RestType type, String response) throws RestErrorException,
			RestClientException {
		try {
			JSONObject json = new JSONObject(response);
			Error error = getErrorFromResponse(response);
			if (error != null && error.isError()) {
				throw new RestErrorException(error);
			}
			return processData(type, json.get(DATA_IDENTIFIER).toString());
		} catch (JSONException e) {
			throw new RestClientException("Unable to parse response", e);
		}
	}
	
	protected Object processData(RestType type, String data) throws RestErrorException,
			RestClientException {
		try {
			if (type == RestType.CHECK_SESSION) {
				return gson.fromJson(data, SessionCheckResponse.class);
			} else if (type == RestType.CREATE_POST) {
				return gson.fromJson(data, CreatePostResponse.class);
			} else if (type == RestType.GET_HOME_FEEDS) {
				return gson.fromJson(data, Post[].class);
			} else if (type == RestType.GET_MENTIONS) {
				return gson.fromJson(data, Mentions.class);
			} else if (type == RestType.GET_POST) {
				return gson.fromJson(data, Post.class);
			} else if (type == RestType.GET_REPLIES) {
				return gson.fromJson(data, Replies.class);
			} else if (type == RestType.GET_RESHARES) {
				return gson.fromJson(data, Reshares.class);
			} else if (type == RestType.GET_POSTS) {
				return gson.fromJson(data, Post[].class);
			} else if (type == RestType.TAG_POST) {
				return data;
			} else if (type == RestType.GET_TAG_OPTIONS) {
				return gson.fromJson(data, Tag[].class);
			} else if (type == RestType.LOGIN) {
				return gson.fromJson(data, LoginResponse.class);
			} else if (type == RestType.LOGOUT) {
				
			} else if (type == RestType.GET_PROFILE) {
				return gson.fromJson(data, Profile.class);
			} else if (type == RestType.GET_BADGES) {
				return gson.fromJson(data, BadgesResponse.class);
			} else if (type == RestType.GET_FOLLOWERS) {
				return gson.fromJson(data, Followers.class);
			} else if (type == RestType.GET_FOLLOWING) {
				return gson.fromJson(data, Following.class);
			} else if (type == RestType.SEARCH_POSTS) {
				PostSearchResult object = new PostSearchResult();
				JSONObject json = new JSONObject(data);
				
				JSONObject search = json.getJSONObject("search");
				object.setTotal(search.getInt("total"));
				object.setMaxScore(search.optDouble("maxScore"));
				object.setTotalHits(search.getInt("totalHits"));
				object.setTimedOut(search.getBoolean("timedOut"));
				object.setTookInMillis(search.getLong("tookInMillis"));
				
				List<Post> posts = new ArrayList<Post>();
				JSONArray result = search.getJSONArray("result");
				if (result != null) {
					int resultLength = result.length();
					for (int n = 0; n < resultLength; n++) {
						JSONObject item = result.getJSONObject(n);
						if (item != null) {
							JSONArray names = item.names();
							if (names != null) {
								int namesLength = names.length();
								for (int m = 0; m < namesLength; m++) {
									String itemString = item.getJSONObject(names.getString(m))
											.toString();
									posts.add(gson.fromJson(itemString, Post.class));
								}
							}
						}
					}
				}
				
				Post[] postArray = new Post[posts.size()];
				posts.toArray(postArray);
				object.setResult(postArray);
				
				return object;
			} else if (type == RestType.SEARCH_USERS) {
				SearchResult object = new SearchResult();
				
				JSONObject json = new JSONObject(data);
				
				JSONObject search = json.getJSONObject("search");
				object.setTotal(search.getInt("total"));
				object.setMaxScore(search.optDouble("maxScore"));
				object.setTotalHits(search.getInt("totalHits"));
				object.setTimedOut(search.getBoolean("timedOut"));
				object.setTookInMillis(search.getLong("tookInMillis"));
				
				List<RecommendedProfile> recommendations = new ArrayList<RecommendedProfile>();
				JSONArray result = search.getJSONArray("result");
				if (result != null) {
					int resultLength = result.length();
					for (int n = 0; n < resultLength; n++) {
						JSONObject item = result.getJSONObject(n);
						if (item != null) {
							JSONArray names = item.names();
							if (names != null) {
								int namesLength = names.length();
								for (int m = 0; m < namesLength; m++) {
									String itemString = item.getJSONObject(names.getString(m))
											.toString();
									recommendations.add(gson.fromJson(itemString,
											RecommendedProfile.class));
								}
							}
						}
					}
				}
				
				RecommendedProfile[] recommendationsArray = new RecommendedProfile[recommendations
						.size()];
				recommendations.toArray(recommendationsArray);
				object.setResult(recommendationsArray);
				
				return object;
			} else if (type == RestType.SEARCH_HASHTAGS) {
				HotTopicsResult hotTopicsResult = new HotTopicsResult();
				final JSONObject jsonObj = new JSONObject(data);
				List<HotTopic> hotTopicList = new ArrayList<HotTopic>();
				
				JSONObject search = jsonObj.getJSONObject("search");
				if (search != null) {
					hotTopicsResult.setTotal(search.getInt("total"));
					hotTopicsResult.setMaxScore(search.optDouble("maxScore"));
					hotTopicsResult.setTotalHits(search.getInt("totalHits"));
					hotTopicsResult.setTimedOut(search.getBoolean("timedOut"));
					hotTopicsResult.setTookInMillis(search.getLong("tookInMillis"));
					
					JSONArray result = search.getJSONArray("result");
					if (result != null) {
						final int resultLength = result.length();
						for (int n = 0; n < resultLength; n++) {
							JSONObject item = result.getJSONObject(n);
							if (item != null) {
								JSONArray names = item.names();
								if (names != null) {
									final int namesLength = names.length();
									for (int m = 0; m < namesLength; m++) {
										final String itemString = item.getJSONObject(
												names.getString(m)).toString();
										hotTopicList.add(gson.fromJson(itemString, HotTopic.class));
									}
								}
							}
						}
					}
				}
				
				HotTopic[] hotTopicArray = new HotTopic[hotTopicList.size()];
				hotTopicList.toArray(hotTopicArray);
				hotTopicsResult.setResult(hotTopicArray);
				
				return hotTopicsResult;
			} else if (type == RestType.FOLLOW_REQUEST) {
				JSONObject json = new JSONObject(data);
				return AddFollowingResultEnum.fromValue(json.getInt("result"));
			} else if (type == RestType.UNFOLLOW_REQUEST) {
				JSONObject json = new JSONObject(data);
				return DeleteFollowingResultEnum.fromValue(json.getInt("result"));
			} else if (type == RestType.APPROVE_FOLLOW_REQUEST) {
				JSONObject json = new JSONObject(data);
				return AddFollowingResultEnum.fromValue(json.getInt("result"));
			} else if (type == RestType.REJECT_FOLLOW_REQUEST) {
				JSONObject json = new JSONObject(data);
				return AddFollowingResultEnum.fromValue(json.getInt("result"));
			} else if (type == RestType.GET_THIRD_PARTY_SITES_STATUS) {
				return gson.fromJson(data, ThirdPartySites.class);
			} else if (type == RestType.GET_THIRD_PARTY_SITES_LINKED) {
				return gson.fromJson(data, ThirdPartySites.class);
			} else if (type == RestType.GET_MIG_ALERTS) {
				// return gson.fromJson(data, MigAlerts.class);
				
				MigAlerts migAlerts = new MigAlerts();
				
				JSONObject json;
				try {
					json = new JSONObject(data);
					
					migAlerts.setUnread(json.optInt("unread"));
					
					final JSONArray jsonAlertsArr = json.optJSONArray("alerts");
					if (jsonAlertsArr == null) {
						return migAlerts;
					}
					
					final Alert[] alerts = deserializeAlerts(jsonAlertsArr);
					migAlerts.setAlerts(alerts);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				return migAlerts;
			} else if (type == RestType.GET_MIG_ALERTS_UNREAD) {
				MigAlertsUnreadResult migAlertsUnreadResult = new MigAlertsUnreadResult();
				final JSONObject json;
				try {
					json = new JSONObject(data);
					migAlertsUnreadResult.setTimestamp(json.optLong("timestamp"));
					
					final JSONArray jsonMigAlertsUnreadArr = json
							.optJSONArray("notification_destinations");
					if (jsonMigAlertsUnreadArr == null) {
						return null;
					}
					
					final int jsonMigAlertsUnreadLen = jsonMigAlertsUnreadArr.length();
					MigAlertsUnread[] migAlertsUnreadArr = new MigAlertsUnread[jsonMigAlertsUnreadLen];
					
					for (int n = 0; n < jsonMigAlertsUnreadLen; n++) {
						JSONObject jsonMigAlertsUnread = (JSONObject) jsonMigAlertsUnreadArr.get(n);
						MigAlertsUnread migAlertsUnread = new MigAlertsUnread();
						migAlertsUnread.setDestination(jsonMigAlertsUnread.optInt("type"));
						migAlertsUnread.setCount(jsonMigAlertsUnread.optInt("count"));
						
						JSONArray jsonAlertsArr = jsonMigAlertsUnread.optJSONArray("alerts");
						if (jsonAlertsArr == null) {
							return null;
						}
						
						final Alert[] alerts = deserializeAlerts(jsonAlertsArr);
						migAlertsUnread.setAlerts(alerts);
						
						migAlertsUnreadArr[n] = migAlertsUnread;
					}
					
					migAlertsUnreadResult.setAlerts(migAlertsUnreadArr);
				} catch (JSONException e) {
					e.printStackTrace();
					return null;
				}
				
				return migAlertsUnreadResult;
			} else if (type == RestType.SEND_ACTION) {
				JSONObject json;
				try {
					json = new JSONObject(data);
					return json.getString("desc");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (type == RestType.GET_MIG_ALERTS_UNREAD_COUNT) {
				return gson.fromJson(data, Integer.class);
			} else if (type == RestType.GET_UNREAD_MENTIONS_COUNT) {
				return gson.fromJson(data, Integer.class);
			} else if (type == RestType.GET_MENU_CONFIG) {
				return gson.fromJson(data, MenuConfig.class);
			} else if (type == RestType.GET_WATCHLIST) {
				return gson.fromJson(data, Post[].class);
			} else if (type == RestType.GET_WATCHLIST_COUNT) {
				return gson.fromJson(data, Integer.class);
			} else if (type == RestType.WATCH_POST) {
				return data;
			} else if (type == RestType.UNWATCH_POST) {
				return data;
			} else if (type == RestType.GET_GROUP) {
				return gson.fromJson(data, GroupResponse.class);
			} else if (type == RestType.GET_GROUP_FEEDS) {
				return gson.fromJson(data, Post[].class);
			} else if (type == RestType.JOIN_GROUP) {
				return gson.fromJson(data, SuccessResponse.class);
			} else if (type == RestType.LEAVE_GROUP) {
				return gson.fromJson(data, SuccessResponse.class);
			}
		} catch (Exception e) {
			throw new RestClientException("Unable to parse response", e);
		}
		
		return null;
	}
	
	private Alert[] deserializeAlerts(JSONArray jsonAlerts) throws JSONException {
		int jsonAlertsLen = jsonAlerts.length();
		Alert[] alerts = new Alert[jsonAlertsLen];
		for (int n = 0; n < jsonAlertsLen; n++) {
			JSONObject jsonAlert = (JSONObject) jsonAlerts.get(n);
			Alert alert = new Alert();
			alert.setMessage(jsonAlert.optString("message"));
			alert.setTimestamp(jsonAlert.optLong("timestamp"));
			alert.setId(jsonAlert.optString("id"));
			alert.setType(jsonAlert.optString("type"));
			
			JSONObject jsonImage = jsonAlert.optJSONObject("image");
			if (jsonImage != null) {
				DisplayImage image = gson.fromJson(jsonImage.toString(), DisplayImage.class);
				alert.setImage(image);
			}
			
			JSONArray jsonAction = jsonAlert.optJSONArray("actions");
			if (jsonAction != null) {
				Action[] actions = gson.fromJson(jsonAction.toString(), Action[].class);
				alert.setActions(actions);
			}
			
			JSONArray jsonVariables = jsonAlert.optJSONArray("variables");
			if (jsonVariables != null) {
				JSONObject jsonVariable = null;
				Variable variable = null;
				int len = jsonVariables.length();
				Variable[] variables = new Variable[len];
				for (int m = 0; m < len; m++) {
					jsonVariable = jsonVariables.getJSONObject(m);
					variable = new Variable();
					variable.setName(jsonVariable.optString("name"));
					variable.setType(ObjectTypeEnum.fromValue(jsonVariable.optString("type")));
					
					JSONArray jsonVarLink = jsonVariable.optJSONArray("link");
					if (jsonVarLink != null) {
						variable.setLink(gson.fromJson(jsonVarLink.toString(), ViewURL[].class));
					}
					
					JSONObject jsonVarLabel = jsonVariable.optJSONObject("label");
					if (jsonVarLabel != null) {
						variable.setLabel(gson.fromJson(jsonVarLabel.toString(),
								VariableLabel.class));
					}
					
					JSONObject jsonVarContent = jsonVariable.optJSONObject("content");
					if (jsonVarContent != null) {
						variable.setContent(jsonVarContent.toString());
					}
					
					variables[m] = variable;
				}
				
				alert.setVariables(variables);
			}
			
			alerts[n] = alert;
		}
		
		return alerts;
	}
	
	public String sendMultipartRequest(String url, MultipartFormData params)
			throws RestClientException {
		
		url = resolveUrl(url);
		System.out.println("multipart req: " + url);
		
		HttpURLConnection conn;
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
		} catch (Exception e) {
			throw new RestClientException("Unable to create connection", e);
		}
		
		setDefaulHttpHeader(conn);
		conn.setRequestProperty("Content-Type", "multipart/form-data; boundary="
				+ MultipartFormData.getBoundary());
		
		try {
			conn.setRequestMethod(HttpPost.METHOD_NAME);
			if (params != null) {
				
				byte[] data = params.getData();
				System.out.println(new String(data));
				conn.setDoOutput(true);
				conn.getOutputStream().write(data);
			}
		} catch (Exception e) {
			throw new RestClientException("Unable to send data to server", e);
		}
		
		processHttpHeader(conn);
		
		String response = null;
		try {
			response = read(conn.getInputStream());
		} catch (FileNotFoundException e) {
			try {
				throw new RestClientException(read(conn.getErrorStream()), e);
			} catch (Exception e1) {
				throw new RestClientException("Unable to read data from server", e1);
			}
		} catch (Exception e) {
			throw new RestClientException("Unable to read data from server", e);
		}
		
		// Tools.log(this, "Response:" + response);
		return response;
	}
	
	/**
	 * @return the connected
	 */
	public boolean isConnected() {
		return connected;
	}
	
	/**
	 * @param connected
	 *            the connected to set
	 */
	public void setConnected(boolean connected) {
		this.connected = connected;
	}
}
