package com.mig33.android.sdk.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;

import com.mig33.android.sdk.Mig33;
import com.mig33.android.sdk.R;
import com.mig33.android.sdk.Session;
import com.mig33.android.sdk.common.Tools;
import com.mig33.android.sdk.oauth.OAuthHandler;
import com.projectgoth.BServiceHelper;

public class LoginActivity extends Activity {
	
	private static final String	TAG				= "LoginActivity";
	
	private OAuthHandler		oAuthHandler	= OAuthHandler.getInstance();
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startLoginProcess();
	}
	
	private void startLoginProcess() {
		new Thread(new Runnable() {
			public void run() {
				Intent intent = new Intent(Mig33.INTENT_ACTION_LOGIN);
				try {
					startActivityForResult(intent, Mig33.REQUEST_CODE_LOGIN);
				} catch (ActivityNotFoundException e) {
					String authUrl = oAuthHandler.getAuthUrl();
					if (authUrl != null) {
						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)));
					} else {
						Mig33.getInstance().notifyLoginError();
					}
				}
				finish();
			}
		}, "Login process Thread").start();
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Tools.log(TAG, "onActivityResult resultCode: " + resultCode);
		if (requestCode == Mig33.REQUEST_CODE_LOGIN) {
			if (resultCode == RESULT_OK) {
				if (data != null) {
					Session session = Session.getInstance();
					session.setUserId(data.getStringExtra(Mig33.INTENT_EXTRA_USER_ID));
					session.setUsername(data.getStringExtra(Mig33.INTENT_EXTRA_USERNAME));
					session.setSessionId(data.getStringExtra(Mig33.INTENT_EXTRA_SESSIONID));
					
					BServiceHelper.getInstance().getRestClient().setUserId(session.getUserId());
				}
				Mig33.getInstance().notifyLoginSuccess();
				finish();
			}
		}
	}
}
