package com.mig33.android.sdk.api;

import com.projectgoth.BServiceHelper;
import com.projectgoth.b.MultipartFormData.FormData;
import com.projectgoth.b.data.CreatePostResponse;
import com.projectgoth.b.exception.RestClientException;
import com.projectgoth.b.exception.RestErrorException;

public class Message {

	public static CreatePostResponse createPost(String text, byte[] imageData, String mimeType, String filename, boolean facebook, boolean twitter)
			throws RestErrorException, RestClientException {
		FormData photo = new FormData();
		photo.name = "media";
		photo.filename = filename;
		photo.mimeType = mimeType;
		photo.data = imageData;
		return BServiceHelper.getInstance().getRestClient().createNewPost(text, photo, facebook, twitter);
	}
	
	public static CreatePostResponse createPost(String text, boolean facebook, boolean twitter)
			throws RestErrorException, RestClientException {
		return BServiceHelper.getInstance().getRestClient().createNewPost(text, facebook, twitter);
	}
}
