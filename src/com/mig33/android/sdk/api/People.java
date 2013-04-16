package com.mig33.android.sdk.api;

import com.projectgoth.BServiceHelper;
import com.projectgoth.b.data.Profile;
import com.projectgoth.b.exception.RestClientException;
import com.projectgoth.b.exception.RestErrorException;

public class People {

	public static Profile getProfile(String username) throws RestErrorException, RestClientException {
		return BServiceHelper.getInstance().getRestClient().getProfileByUsername(username);
	}
}
