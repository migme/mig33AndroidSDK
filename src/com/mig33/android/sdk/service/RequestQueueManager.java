/**
 * Mig33 Pte. Ltd.
 *
 * Copyright (c) 2012 mig33. All rights reserved.
 */
package com.mig33.android.sdk.service;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.AsyncTask;

import com.mig33.android.sdk.cache.DataCache;
import com.mig33.android.sdk.common.Tools;
import com.projectgoth.b.data.Error;
import com.projectgoth.b.exception.RestClientException;
import com.projectgoth.b.exception.RestErrorException;

/**
 * RequestQueueManager.java
 * 
 * @author warrenbalcos on May 2, 2013
 * 
 */
public class RequestQueueManager {


	private static final String TAG = "RequestQueueManager";
	
	private static final int MAX_EXECUTOR_THREADS = 2;
	
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
	
	private HttpConnectionHandler				httpHandler;
	
	private DataCache							cache					= DataCache.getInstance();
	
	private boolean								mPauseWork				= false;
	private final Object						mPauseWorkLock			= new Object();
	
	private ArrayList<RestRequest>				requestQueue;
		
	private static final RequestQueueManager	instance				= new RequestQueueManager();
	
	private RequestQueueManager() {
		requestQueue = new ArrayList<RestRequest>();
		httpHandler = new HttpConnectionHandler();
	}
	
	public static synchronized RequestQueueManager getInstance() {
		return instance;
	}
	
	public void queueRequest(RestRequest request) {
		if (request != null) {
			requestQueue.add(request);
		}
	}
	
	private class RequestWorkerTask extends AsyncTask<RestRequest, Void, String> {
		private RestRequest	request;
		
		protected String doInBackground(RestRequest... params) {
			request = params[0];
			
			Tools.log(TAG, "request: " + request.getMethod() + " " + request.getUrl() + " "
					+ request.getParams());
			
			// Wait here if work is paused and the task is not cancelled
			synchronized (mPauseWorkLock) {
				while (mPauseWork && !isCancelled()) {
					try {
						mPauseWorkLock.wait();
					} catch (InterruptedException e) {
					}
				}
			}
			
			boolean fail = false;
			String response = null;
			try {
				response = httpHandler.sendRequest(request.getUrl(), request.getMethod(),
						request.getParams(), request.getContentType());
				Tools.log(TAG, "respose: " + response);
				
				request.incrementRetries();
				
				Error error = RestRequest.getErrorFromResponse(response);
				String data = RestRequest.getStringData(response);
				if (error != null || data == null) {
					throw new RestErrorException(error);
				}
				
				// try parsing the data, if it is correct..
				RestRequest.parseData(request.getType(), data);
				cache.cacheData(request.getKey(), data);
			} catch (RestErrorException e) {
				Tools.log(e);
				fail = true;
			} catch (RestClientException e) {
				Tools.log(e);
				fail = true;
			}
			if (fail) {
				if(request.canRetry()) {
					// TODO: retry this request
				}
				// TODO: handle failed request (store and forward)
			}
			return response;
		}
		
		@Override
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
}
