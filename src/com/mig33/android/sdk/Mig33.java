package com.mig33.android.sdk;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.mig33.android.sdk.activity.LoginActivity;
import com.mig33.android.sdk.api.ActivityAPI;
import com.mig33.android.sdk.api.Payment;
import com.mig33.android.sdk.api.People;
import com.mig33.android.sdk.common.Config;
import com.mig33.android.sdk.common.Tools;

public class Mig33 {
	
	private static final String	TAG						= "Mig33SDK";
	
	// TODO: create proper user agent for SDK tracking
	public static final String	SDK_VERSION				= "0.1.0";
	
	public static final String	INTENT_ACTION_LOGIN		= "com.mig33.intent.action.LOGIN";
	public static final String	INTENT_EXTRA_USERNAME	= "username";
	public static final String	INTENT_EXTRA_SESSIONID	= "sessionid";
	
	public static final String	PEOPLE_UPDATE_EVENT		= "com.mig33.intent.action.PEOPLE_UPDATE_EVENT";
	public static final String	PAYMENT_UPDATE_EVENT	= "com.mig33.intent.action.PAYMENT_UPDATE_EVENT";
	public static final String	ACTIVITIES_UPDATE_EVENT	= "com.mig33.intent.action.ACTIVITIES_UPDATE_EVENT";
	
	public static final String	INTENT_EXTRA_USER_ID	= "INTENT_EXTRA_USER_ID";
	public static final String	INTENT_EXTRA_GROUP_ID	= "INTENT_EXTRA_GROUP_ID";
	public static final String	INTENT_EXTRA_REFERENCE	= "INTENT_EXTRA_REFERENCE";
	public static final String	INTENT_EXTRA_POST_ID	= "INTENT_EXTRA_POST_ID";
	public static final String	INTENT_EXTRA_SUCCESSFUL	= "INTENT_EXTRA_SUCCESSFUL";
	
	public static final String	PREFS					= "Mig33SDKPrefsFile";
	
	public static final int		REQUEST_CODE_LOGIN		= 1;
	
	public static final String	USER_TOKEN				= "user_token";
	public static final String	USER_SECRET				= "user_secret";
	public static final String	REQUEST_TOKEN			= "request_token";
	public static final String	REQUEST_SECRET			= "request_secret";
	
	private SharedPreferences	preferences;
	
	private static final Mig33	mInstance				= new Mig33();
	
	private Config				config					= Config.getInstance();
	
	private LoginListener		mLoginListener;
	
	private Context				appCtx;
	
	private boolean				isReceiversRegistered	= false;
	
	@SuppressWarnings("rawtypes")
	private Class				mainActivity;
	
	//@formatter:off
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(PEOPLE_UPDATE_EVENT)) {
				String userId = intent.getStringExtra(INTENT_EXTRA_USER_ID);
				String groupId = intent.getStringExtra(INTENT_EXTRA_GROUP_ID);
				Tools.log(TAG, "USERINFO_UPDATED_EVENT: " + userId);
				handleUserInfoUpdateEvent(userId, groupId);
			} else if (action.equals(PAYMENT_UPDATE_EVENT)) {
				String reference = intent.getStringExtra(INTENT_EXTRA_REFERENCE);
				handlePaymentUpdateEvent(reference);
				Tools.log(TAG, "PAYMENT_UPDATE_EVENT: " + reference);
			} else if (action.equals(ACTIVITIES_UPDATE_EVENT)) {
				String postId = intent.getStringExtra(INTENT_EXTRA_POST_ID);
				boolean successful = intent.getBooleanExtra(INTENT_EXTRA_SUCCESSFUL, false);
				handleActivitiesUpdateEvent(postId, successful);
				Tools.log(TAG, "ACTIVITIES_UPDATE_EVENT: " + postId + " success? " + successful );
			}
		}
	};
	//@formatter:on
	
	private Mig33() {
	}
	
	public static Mig33 getInstance() {
		return mInstance;
	}
	
	public void initialize(Context context) {
		appCtx = context.getApplicationContext();
		Session.getInstance().initSession(appCtx);
		preferences = appCtx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
		if (!isReceiversRegistered) {
			appCtx.registerReceiver(receiver, new IntentFilter(PEOPLE_UPDATE_EVENT));
			appCtx.registerReceiver(receiver, new IntentFilter(PAYMENT_UPDATE_EVENT));
			appCtx.registerReceiver(receiver, new IntentFilter(ACTIVITIES_UPDATE_EVENT));
			isReceiversRegistered = true;
		}
	}
	
	public void setConsumerDetails(String key, String secret, String appName) {
		config.setConsumerKey(key);
		config.setConsumerSecret(secret);
		config.setAppName(appName);
	}
	
	public void login(Context context) {
		Intent intent = new Intent(context, LoginActivity.class);
		context.startActivity(intent);
	}
	
	public void notifyLoginSuccess() {
		if (mLoginListener != null) {
			mLoginListener.onLoginSuccess();
		}
		People.getInstance().getMyInfo();
	}
	
	public void notifyLoginError() {
		if (mLoginListener != null) {
			mLoginListener.onLoginError();
		}
	}
	
	public void showMainActivity() {
		if (mainActivity != null && appCtx != null) {
			Intent intent = new Intent(appCtx, mainActivity);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
			appCtx.startActivity(intent);
		}
	}
	
	public void broadcastIntent(Intent intent) throws Exception {
		if (appCtx != null) {
			appCtx.sendBroadcast(intent);
		} else {
			throw new Exception("application context is null");
		}
	}
	
	public void saveRequestInformation(String token, String secret) {
		// null means to clear the old values
		SharedPreferences.Editor editor = preferences.edit();
		if (token == null) {
			editor.remove(REQUEST_TOKEN);
			Tools.log(TAG, "Clearing Request Token");
		} else {
			editor.putString(REQUEST_TOKEN, token);
			Tools.log(TAG, "Saving Request Token: " + token);
		}
		if (secret == null) {
			editor.remove(REQUEST_SECRET);
			Tools.log(TAG, "Clearing Request Secret");
		} else {
			editor.putString(REQUEST_SECRET, secret);
			Tools.log(TAG, "Saving Request Secret: " + secret);
		}
		editor.commit();
	}
	
	public void saveAuthInformation(String token, String secret) {
		// null means to clear the old values
		SharedPreferences.Editor editor = preferences.edit();
		if (token == null) {
			editor.remove(USER_TOKEN);
			Tools.log(TAG, "Clearing OAuth Token");
		} else {
			editor.putString(USER_TOKEN, token);
			Tools.log(TAG, "Saving OAuth Token: " + token);
		}
		if (secret == null) {
			editor.remove(USER_SECRET);
			Tools.log(TAG, "Clearing OAuth Secret");
		} else {
			editor.putString(USER_SECRET, secret);
			Tools.log(TAG, "Saving OAuth Secret: " + secret);
		}
		editor.commit();
	}
	
	public void clearRequestTokenAndSecret() {
		saveRequestInformation(null, null);
	}
	
	public String getRequestToken() {
		String result = preferences.getString(REQUEST_TOKEN, null);
		Tools.log(TAG, "getRequestToken: " + result);
		return result;
	}
	
	public String getRequestSecret() {
		String result = preferences.getString(REQUEST_SECRET, null);
		Tools.log(TAG, "getRequestSecret: " + result);
		return result;
	}
	
	public String getAccessToken() {
		String result = preferences.getString(USER_TOKEN, null);
		Tools.log(TAG, "getAccessToken: " + result);
		return result;
	}
	
	public String getAccessSecret() {
		String result = preferences.getString(USER_SECRET, null);
		Tools.log(TAG, "getAccessSecret: " + result);
		return result;
	}
	
	// public boolean isVerify() {
	// return (getRequestToken() != null && getRequestSecret() != null);
	// }
	
	public boolean isAuthorized() {
		boolean result = (getAccessToken() != null && getAccessSecret() != null);
		// TODO: send an api call here to check if the access token is still
		// valid.
		return result;
	}
	
	/**
	 * @param mLoginListener
	 *            the mLoginListener to set
	 */
	public void setLoginListener(LoginListener loginListener) {
		mLoginListener = loginListener;
	}
	
	/**
	 * @param mainActivity
	 *            the mainActivity to set
	 */
	@SuppressWarnings("rawtypes")
	public void setMainActivity(Class mainActivity) {
		this.mainActivity = mainActivity;
	}
	
	private void handleUserInfoUpdateEvent(String userId, String groupId) {
		People.getInstance().onDataUpdated(userId, groupId);
		if (Session.getInstance().isMe(userId)) {
			Session.getInstance().setUserInfo(People.getInstance().getMyInfo());
		}
	}
	
	private void handleActivitiesUpdateEvent(String postId, boolean success) {
		ActivityAPI.getInstance().onActivityUpdate(postId, success);
	}
	
	private void handlePaymentUpdateEvent(String reference) {
		Payment.getInstance().onPaymentUpdate(reference);
	}
	
	@SuppressLint("ShowToast")
	public void showToast(Context context, String message) {
		try {
			Toast.makeText(context, message, Toast.LENGTH_LONG);
		} catch (Exception e) {
			Tools.log(e);
		}
	}
	
	public boolean showYesNoDialog(String message, Context context,
			DialogInterface.OnClickListener dialogClickListener) {
		boolean result = false;
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setMessage(message);
			builder.setPositiveButton("Yes", dialogClickListener);
			builder.setNegativeButton("No", dialogClickListener);
			builder.show();
		} catch (Exception e) {
			Tools.log(e);
		}
		return result;
	}
	
	public boolean showOkDialog(String message, Context context) {
		boolean result = false;
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setMessage(message);
			builder.setPositiveButton("OK", null);
			builder.show();
		} catch (Exception e) {
			Tools.log(e);
		}
		return result;
	}
}
