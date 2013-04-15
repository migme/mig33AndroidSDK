package com.projectgoth;

import com.projectgoth.b.BaseRestClient;
import com.projectgoth.b.android.RestClient;

public class BServiceHelper {

	private static BServiceHelper	instance	= null;

	private BaseRestClient				restClient	= null;

	private BServiceHelper() {
		restClient = new RestClient();
	}

	public static synchronized BServiceHelper getInstance() {
		if (instance == null) {
			instance = new BServiceHelper();
		}
		return instance;
	}

	public BaseRestClient getRestClient() {
		return restClient;
	}
}
