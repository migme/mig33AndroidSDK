/**
 * Mig33 Pte. Ltd.
 *
 * Copyright (c) 2012 mig33. All rights reserved.
 */
package com.mig33.android.sdk.activity;

import oauth.signpost.OAuth;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import com.mig33.android.sdk.Mig33;
import com.mig33.android.sdk.common.Tools;
import com.mig33.android.sdk.oauth.OAuthHandler;

/**
 * OAuthCallbackActivity.java
 * 
 * @author warrenbalcos on May 10, 2013
 * 
 */
public class OAuthCallbackActivity extends Activity {
	
	private static final String	TAG				= "OAuthCallbackActivity";
	
	private OAuthHandler		oAuthHandler	= OAuthHandler.getInstance();
	
	private Mig33				mig33			= Mig33.getInstance();
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		processCallback();
	}
	
	public void processCallback() {
		new Thread(new Runnable() {
			public void run() {
				Uri uri = getIntent().getData();
				Tools.log(TAG, "onCreate uri: " + (uri != null ? uri.toString() : ""));
				if (oAuthHandler.isCallBack(uri)) {
					String token = mig33.getRequestToken();
					String secret = mig33.getRequestSecret();
					
					Tools.log(TAG, "onresume uri: " + uri.toString() + "token: " + token
							+ " secret: " + secret);
					oAuthHandler.setConsumerTokenWithSecret(token, secret);
					
					String otoken = uri.getQueryParameter(OAuth.OAUTH_TOKEN);
					String verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
					Tools.log(TAG, "otoken: " + otoken + "verifier: " + verifier);
					
					boolean result = oAuthHandler.retrieveAccessToken(verifier);
					mig33.showMainActivity();
					if (result) {
						Mig33.getInstance().notifyLoginSuccess();
					} else {
						Mig33.getInstance().notifyLoginError();
					}
				}
				finish();
			}
		}, "Callback process Thread").start();
	}
}
