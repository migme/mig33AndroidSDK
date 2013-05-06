package com.mig33.android.sdk.api;

import java.util.ArrayList;

import com.mig33.android.sdk.model.UserInfo;
import com.projectgoth.BServiceHelper;
import com.projectgoth.b.data.Profile;
import com.projectgoth.b.exception.RestClientException;
import com.projectgoth.b.exception.RestErrorException;

public class People {
	
	public static Profile getProfile(String username) throws RestErrorException,
			RestClientException {
		return BServiceHelper.getInstance().getRestClient().getProfileByUsername(username);
	}
	
	// GET /api/rest/people/117/@friends
	//  
	// ?fields=username%2Cid%2CdisplayName%2Ccountry%2CstatusMessage%2CthumbnailUrl
	// &oauth_nonce=add7f1c3ec5424ec1ab2a3e11703eaba
	// &oauth_version=1.0
	// &oauth_timestamp=1304051441
	// &oauth_consumer_key=2e3432ec5f13685c569253ac26350f0304db67ad1
	// &xoauth_requestor_id=117
	// &oauth_signature_method=HMAC-SHA1
	// &oauth_signature=eYefAuAGX3v06Y%2F%2FPYo3r4I5fdY%3D
	
	public enum PeopleField {
		ID("id"),
		USERNAME("username"),
		DISPLAY_NAME("displayName"),
		COUNTRY("country"),
		STATUS_MESSAGE("statusMessage"),
		THUMBNAIL_URL("thumbnailUrl");
		
		private String	fieldValue;
		
		private PeopleField(String fieldValue) {
			this.fieldValue = fieldValue;
		}
		
		public static String getAllFields() {
			String fields = "";
			int ctr = PeopleField.values().length;
			for (PeopleField field : PeopleField.values()) {
				ctr--;
				fields += field.fieldValue;
				if (ctr > 0) {
					fields += ",";
				}
			}
			return fields;
		}
		
		public String getFieldValue() {
			return fieldValue;
		}
	}
	
	public static UserInfo getUserInfo(String id) {
		UserInfo user = null;
		return user;
	}
	
	public static UserInfo getMyInfo() {
		UserInfo user = null;
		return user;
	}
	
	public static ArrayList<UserInfo> getFriendsInfo(String id) {
		ArrayList<UserInfo> friends = new ArrayList<UserInfo>();
		return friends;
	}
	
	public static ArrayList<UserInfo> getMyFriendsInfo() {
		ArrayList<UserInfo> friends = new ArrayList<UserInfo>();
		return friends;
	}

}
