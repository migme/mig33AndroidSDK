package com.mig33.android.sdk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.mig33.android.sdk.Mig33;
import com.mig33.android.sdk.R;
import com.mig33.android.sdk.Session;
import com.mig33.android.sdk.R.layout;
import com.mig33.android.sdk.R.menu;
import com.projectgoth.BServiceHelper;
import com.projectgoth.b.android.RestClient;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		Intent intent = new Intent(Mig33.INTENT_ACTION_LOGIN);
		startActivityForResult(intent, Mig33.REQUEST_CODE_LOGIN);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == Mig33.REQUEST_CODE_LOGIN) {
			if (resultCode == RESULT_OK) {
				if (data != null) {
					Session session = Session.getInstance();
					session.setUserId(data.getStringExtra(Mig33.INTENT_EXTRA_USERID));
					session.setUsername(data.getStringExtra(Mig33.INTENT_EXTRA_USERNAME));
					session.setSessionId(data.getStringExtra(Mig33.INTENT_EXTRA_SESSIONID));
					
					BServiceHelper.getInstance().getRestClient().setUserId(session.getUserId());
				}
				
				Mig33.getInstance().notifyLoginSuccess();
			} else {
				Mig33.getInstance().notifyLoginError();
			}
			
			finish();
		}
	}
}
