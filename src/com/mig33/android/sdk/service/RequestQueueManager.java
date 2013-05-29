/**
 * Mig33 Pte. Ltd.
 *
 * Copyright (c) 2012 mig33. All rights reserved.
 */
package com.mig33.android.sdk.service;

import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Intent;
import android.os.AsyncTask;

import com.mig33.android.sdk.Mig33;
import com.mig33.android.sdk.api.Payment;
import com.mig33.android.sdk.cache.DataCache;
import com.mig33.android.sdk.common.Tools;
import com.mig33.android.sdk.common.UIUtils;
import com.mig33.android.sdk.model.ActivityResponse;
import com.mig33.android.sdk.model.ActivityResponse.Data;
import com.mig33.android.sdk.model.ApiResponse;
import com.mig33.android.sdk.model.PaymentItem;
import com.mig33.android.sdk.model.PaymentResponse;
import com.projectgoth.b.data.Error;
import com.projectgoth.b.exception.RestErrorException;

/**
 * RequestQueueManager.java
 * 
 * @author warrenbalcos on May 2, 2013
 */
public class RequestQueueManager {

	private static final String TAG = "RequestQueueManager";
	
	private static final int MAX_EXECUTOR_THREADS = 4;
	
	private HttpConnectionHandler httpConnectionHandler;
	
	//@formatter:off
	private static final ThreadFactory sThreadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

	    public Thread newThread(Runnable r) {
	    	return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
	    }
	};
	
	// Dual thread executor for main AsyncTask
	public static final Executor THREAD_EXECUTOR = 
			Executors.newFixedThreadPool(MAX_EXECUTOR_THREADS, sThreadFactory);
	//@formatter:on
	
	private DataCache							cache					= DataCache.getInstance();
	
	private boolean								mPauseWork				= false;
	private final Object						mPauseWorkLock			= new Object();
	
	private HashMap<String, RestRequest>		requestMap;
	private HashMap<String, RestRequest>		retryMap;
	
	private static final RequestQueueManager	instance				= new RequestQueueManager();
	
	private RequestQueueManager() {
		requestMap = new HashMap<String, RestRequest>();
		retryMap = new HashMap<String, RestRequest>();
		httpConnectionHandler = new HttpConnectionHandler();
	}
	
	public static synchronized RequestQueueManager getInstance() {
		return instance;
	}
	
	public void queueRequest(RestRequest request) {
		if (request != null) {
			if (!requestMap.containsKey(request.getKey())) {
				Tools.log(TAG, "queueRequest: " + request.getKey());
				requestMap.put(request.getKey(), request);
				sendRequest(request);
			}
		}
	}
	
	private void doRetry() {
		new Thread(new Runnable() {
			public void run() {
				if (!retryMap.isEmpty()) {
					RestRequest request = null;
					for (String key : retryMap.keySet()) {
						request = retryMap.get(key);
						break;
					}
					if (request != null) {
						sendRequest(request);
					}
				}
			}
		}).start();
	}
	
	private void sendRequest(RestRequest request) {
		if (cancelPotentialWork(request)) {
			try {
				RequestWorkerTask task = new RequestWorkerTask(request);
				request.setRequestWorkerTaskReference(task);
				if (UIUtils.hasHoneycomb()) {
					task.executeOnExecutor(THREAD_EXECUTOR);
				} else {
					THREAD_EXECUTOR.execute(task);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public class RequestWorkerTask extends AsyncTask<Void, Void, Void> implements Runnable {
		
		private RestRequest	request;
		
		public RequestWorkerTask(RestRequest request) throws Exception {
			if (request == null) {
				throw new Exception("Request object cannot be set to null");
			}
			this.request = request;
		}
		
		public void run() {
			doInBackground();
		}
		
		protected Void doInBackground(Void... params) {
			
			boolean success = true;
			String key = request.getKey();
			
			Tools.log(TAG, "requesting: " + key);
			
			// Wait here if work is paused and the task is not cancelled
			synchronized (mPauseWorkLock) {
				while (mPauseWork && !isCancelled()) {
					try {
						mPauseWorkLock.wait();
					} catch (InterruptedException e) {
					}
				}
			}
			
			try {
				// String response = httpConnectionHandler.sendRequest(request);
				String response = httpConnectionHandler.sendApacheRequest(request);
				request.incrementRetries();
				
				ApiResponse apiResponse = new ApiResponse(response);
				
				Error error = apiResponse.getError();
				String entry = apiResponse.getEntry();
				if (error != null || entry == null) {
					throw new RestErrorException(error);
				}
				apiResponse.setResponseIds(key, request.getUserId(), request.getGroupId(),
						request.getAppId());
				apiResponse.setType(request.getType());
				request.setApiResponse(apiResponse);
				
				// try parsing the data, if it is correct..
				cache.cacheData(key, apiResponse);
				Tools.log(TAG, "response: " + response);
			} catch (UnsupportedOperationException e) {
				// TODO: handle http exceptions properly
				success = false;
				Tools.log(e);
				// } catch (RestErrorException e) {
				// Tools.log(e);
				// success = false;
				// } catch (IOException e) {
				// Tools.log(e);
				// success = false;
			} catch (Exception e) {
				Tools.log(e);
				success = false;
			}
			if (success) {
				handleRequestSuccess(request);
				requestMap.remove(key);
				retryMap.remove(key);
			} else {
				if (!request.canRetry()) {
					requestMap.remove(key);
					retryMap.remove(key);
					handleRequestFail(request);
				} else {
					scheduleRetry(request);
				}
			}
			if (requestMap.isEmpty()) {
				doRetry();
			}
			return null;
		}
		
		public void handleRequestFail(RestRequest request) {
			switch (request.getType()) {
			case PEOPLE:
				break;
			case PAYMENT:
				PaymentResponse resp = new PaymentResponse(request.getApiResponse());
				PaymentItem item = (PaymentItem) request.getRequestItem();
				item.setFailed();
				Payment.getInstance().addOrUpdatePaymentItem(item);
				Payment.getInstance().addPaymentResponse(resp);
				broadcastPaymentApiResults(request);
				break;
			case ACTIVITIES:
				// TODO: handle activities broadcast
				break;
			}
		}
		
		public void handleRequestSuccess(RestRequest request) {
			switch (request.getType()) {
			case PEOPLE:
				broadcastPeopleApiResults(request.getUserId(), request.getGroupId());
				break;
			case PAYMENT:
				PaymentResponse resp = new PaymentResponse(request.getApiResponse());
				PaymentItem item = (PaymentItem) request.getRequestItem();
				item.setSuccessful();
				Payment.getInstance().addOrUpdatePaymentItem(item);
				Payment.getInstance().addPaymentResponse(resp);
				broadcastPaymentApiResults(request);
				break;
			case ACTIVITIES:
				// TODO: handle activities broadcast
				break;
			}
		}
		
		protected void onCancelled() {
			super.onCancelled();
			synchronized (mPauseWorkLock) {
				mPauseWorkLock.notifyAll();
			}
		}
	}
	
	public void setPauseWork(boolean pauseWork) {
		synchronized (mPauseWorkLock) {
			mPauseWork = pauseWork;
			if (!mPauseWork) {
				mPauseWorkLock.notifyAll();
			}
		}
	}
	
	public static boolean cancelPotentialWork(RestRequest request) {
		if (request != null) {
			final RequestWorkerTask requestWorkerTask = request.getRequestWorkerTaskReference();
			
			if (requestWorkerTask != null) {
				final Object workerData = requestWorkerTask.request;
				if (workerData == null || !workerData.equals(request)) {
					requestWorkerTask.cancel(true);
					Tools.log(TAG, "cancelPotentialWork - cancelled work for " + request);
				} else {
					// The same work is already in progress.
					return false;
				}
			}
		}
		return true;
	}
	
	private void scheduleRetry(RestRequest request) {
		if (!retryMap.containsKey(request)) {
			retryMap.put(request.getKey(), request);
		}
	}
	
	public void broadcastPeopleApiResults(String userId, String groupId) {
		Intent intent = new Intent(Mig33.PEOPLE_UPDATE_EVENT);
		intent.putExtra(Mig33.INTENT_EXTRA_USER_ID, userId);
		try {
			Mig33.getInstance().broadcastIntent(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void broadcastPaymentApiResults(RestRequest request) {
		Intent intent = new Intent(Mig33.PAYMENT_UPDATE_EVENT);
		PaymentItem item = (PaymentItem) request.getRequestItem();
		intent.putExtra(Mig33.INTENT_EXTRA_REFERENCE, item.getReference());
		try {
			Mig33.getInstance().broadcastIntent(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void broadcastActivityApiResults(RestRequest request) {
		Intent intent = new Intent(Mig33.PAYMENT_UPDATE_EVENT);
		ActivityResponse response = new ActivityResponse(request.getApiResponse());
		Data data = response.getData();
		intent.putExtra(Mig33.INTENT_EXTRA_POST_ID, data.getId());
		intent.putExtra(Mig33.INTENT_EXTRA_SUCCESSFUL, data.isSuccessful());
		try {
			Mig33.getInstance().broadcastIntent(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
