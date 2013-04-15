package com.mig33.android.sdk;

import com.mig33.android.sdk.activity.LoginActivity;

import android.content.Context;
import android.content.Intent;


public class Mig33 {

	public static final String SDK_VERSION = "0.1.0";
	public static final String DEFAULT_ENCODING	= "UTF-8";
	
	public static final String INTENT_ACTION_LOGIN = "com.mig33.intent.action.LOGIN";
	public static final String INTENT_EXTRA_USERID = "userid";
	public static final String INTENT_EXTRA_USERNAME = "username";
	public static final String INTENT_EXTRA_SESSIONID = "sessionid";
	
	public static final int REQUEST_CODE_LOGIN = 1;
	
	private static final Mig33 mInstance = new Mig33();
	
	private LoginListener mLoginListener;
	
	private Mig33() {
		
	}
	
	public static Mig33 getInstance() {
		return mInstance;
	}
	
	public void initialize(Context context) {
		Session.getInstance().initSession(context.getApplicationContext());
	}
	
	public void login(Context context, LoginListener listener) {
		mLoginListener = listener;
		
		Intent intent = new Intent(context, LoginActivity.class);
		context.startActivity(intent);
	}
	
	public void notifyLoginSuccess() {
		if (mLoginListener != null) {
			mLoginListener.onLoginSuccess();
		}
	}
	
	public void notifyLoginError() {
		if (mLoginListener != null) {
			mLoginListener.onLoginError();
		}
	}
}
