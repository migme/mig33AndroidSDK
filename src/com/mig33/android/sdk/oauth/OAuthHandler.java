/**
 * Mig33 Pte. Ltd.
 *
 * Copyright (c) 2012 mig33. All rights reserved.
 */
package com.mig33.android.sdk.oauth;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import android.net.Uri;

import com.mig33.android.sdk.Mig33;
import com.mig33.android.sdk.common.Config;
import com.mig33.android.sdk.common.Tools;

/**
 * OAuthHandler.java
 * 
 * @author warrenbalcos on Mar 4, 2013
 * 
 */
public class OAuthHandler {
	
	private static final String			TAG				= "OAuthHandler";
	
	// private static final Uri CALLBACK_URI = Uri.parse("mig33SDK");
	private static final Uri			CALLBACK_URI	= null;
	
	private static Config				config			= Config.getInstance();
	
	private OAuthConsumer				consumer;
	private OAuthProvider				provider;
	private String						callBackUrl;
	
	private Mig33						mig33			= Mig33.getInstance();
	
	private static final OAuthHandler	instance		= new OAuthHandler();
	
	private OAuthHandler() {
		consumer = new CommonsHttpOAuthConsumer(config.getConsumerKey(),
				config.getConsumerSecret());
		if (mig33.isAuthorized()) {
			consumer.setTokenWithSecret(mig33.getAccessToken(), mig33.getAccessSecret());
		}
		provider = new CommonsHttpOAuthProvider(config.getRequestTokenUrl(),
				config.getAccessTokenUrl(), config.getAuthorizeUrl());
		provider.setOAuth10a(true);
		callBackUrl = (CALLBACK_URI == null ? OAuth.OUT_OF_BAND : CALLBACK_URI.toString());
	}
	
	public static synchronized OAuthHandler getInstance() {
		return instance;
	}
	
	public String getAuthUrl() {
		String authUrl = null;
		try {
			// This is really important. If you were able to register your
			// real callback Uri with Twitter, and not some fake Uri
			// like I registered when I wrote this example, you need to send
			// null as the callback Uri in this function call. Then
			// Twitter will correctly process your callback redirection
			authUrl = provider.retrieveRequestToken(consumer, callBackUrl);
			// String authUrl = mProvider.retrieveRequestToken(mConsumer,
			// OAuth.OUT_OF_BAND);
			
			mig33.saveRequestInformation(consumer.getToken(), consumer.getTokenSecret());
			
		} catch (OAuthMessageSignerException e) {
			Tools.log(e);
		} catch (OAuthNotAuthorizedException e) {
			Tools.log(e);
		} catch (OAuthExpectationFailedException e) {
			Tools.log(e);
		} catch (OAuthCommunicationException e) {
			Tools.log(e);
		}
		return authUrl;
	}
	
	public boolean retrieveAccessToken(String verifier) {
		boolean result = true;
		try {
			provider.retrieveAccessToken(consumer, verifier);
			
			// Now we can retrieve the goodies
			mig33.saveAuthInformation(consumer.getToken(), consumer.getTokenSecret());
			
			Tools.log(TAG, "retrieveAccess token: " + consumer.getToken() + " secret: "
					+ consumer.getConsumerSecret());
			
			mig33.clearRequestTokenAndSecret();
			
		} catch (OAuthMessageSignerException e) {
			Tools.log(e);
			result = false;
		} catch (OAuthNotAuthorizedException e) {
			Tools.log(e);
			result = false;
		} catch (OAuthExpectationFailedException e) {
			Tools.log(e);
			result = false;
		} catch (OAuthCommunicationException e) {
			Tools.log(e);
			result = false;
		}
		return result;
	}
	
	public boolean isCallBack(Uri uri) {
		if (uri == null) {
			return false;
		}
		if (callBackUrl.equals(OAuth.OUT_OF_BAND)) {
			return true;
		}
		return Uri.parse(callBackUrl).getScheme().equals(uri.getScheme());
	}
	
	public void setConsumerTokenWithSecret(String token, String secret) {
		if (token != null || secret != null) {
			consumer.setTokenWithSecret(token, secret);
		}
	}
	
	public OAuthConsumer getConsumer() {
		return consumer;
	}
}
