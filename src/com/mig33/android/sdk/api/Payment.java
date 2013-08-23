/**
 * Mig33 Pte. Ltd.
 *
 * Copyright (c) 2012 mig33. All rights reserved.
 */
package com.mig33.android.sdk.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.mig33.android.sdk.Mig33;
import com.mig33.android.sdk.model.PaymentItem;
import com.mig33.android.sdk.model.PaymentReceipt;
import com.mig33.android.sdk.model.PaymentResponse;
import com.mig33.android.sdk.service.RestService;

/**
 * Payment.java
 * 
 * @author warrenbalcos on May 10, 2013
 * 
 */
public class Payment {
	
	private static final Payment				instance	= new Payment();
	
	// TODO: hard cache paymentItems
	private HashMap<String, PaymentItem>		paymentItems;
	
	private HashMap<String, PaymentResponse>	paymentResponses;
	
	private List<PaymentListener>				listeners;
	
	private Payment() {
		paymentItems = new HashMap<String, PaymentItem>();
		paymentResponses = new HashMap<String, PaymentResponse>();
		listeners = Collections.synchronizedList(new ArrayList<PaymentListener>());
	}
	
	public interface PaymentListener {
		public void onPaymentCanceled(String reference);
		
		public void onPaymentSucceeded(String reference);
		
		public void onPaymentError(String reference);
	}
	
	public static synchronized Payment getInstance() {
		return instance;
	}
	
	/**
	 * price is in USD as default
	 * 
	 * @param description
	 * @param price
	 */
	public String buy(final Context context, final String description, final int price) {
		if (price <= 0) {
			// TODO: throw error, cannot buy something with no price
			return null;
		}
		final PaymentItem item = createPaymentItem(description, price);
		Mig33.getInstance().showYesNoDialog("Are you sure you want to buy item? - " + description,
				context, new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case DialogInterface.BUTTON_POSITIVE:
							paymentItems.put(item.getReference(), item);
							RestService.getInstance().processPayment(item);
							break;
						case DialogInterface.BUTTON_NEGATIVE:
							
							break;
						}
					}
				});
		return item.getReference();
	}
	
	private PaymentItem createPaymentItem(String description, float price) {
		PaymentItem item = new PaymentItem(description, String.valueOf(price));
		while (paymentItems.containsKey(item.getReference())) {
			item = new PaymentItem(description, String.valueOf(price));
		}
		return item;
	}
	
	/**
	 * @return the paymentItems
	 */
	public HashMap<String, PaymentItem> getPaymentItems() {
		return paymentItems;
	}
	
	/**
	 * @return the paymentResponses
	 */
	public HashMap<String, PaymentResponse> getPaymentResponses() {
		return paymentResponses;
	}
	
	/**
	 * @param paymentResponses
	 *            the paymentResponses to set
	 */
	public void addPaymentResponse(PaymentResponse paymentResponse) {
		if (paymentResponse != null) {
			PaymentReceipt receipt = paymentResponse.getPaymentReceipt();
			if (receipt != null) {
				String reference = receipt.getReference();
				if (reference != null) {
					this.paymentResponses.put(reference, paymentResponse);
				}
			}
		}
	}
	
	public void addOrUpdatePaymentItem(PaymentItem paymentItem) {
		paymentItems.put(paymentItem.getReference(), paymentItem);
	}
	
	public void addListener(PaymentListener listener) {
		listeners.add(listener);
	}
	
	public void onPaymentCanceled(String reference) {
		cleanListeners();
		for (PaymentListener listener : listeners) {
			if (listener != null) {
				listener.onPaymentCanceled(reference);
			}
		}
	}
	
	public void onPaymentUpdate(String reference) {
		cleanListeners();
		for (PaymentListener listener : listeners) {
			if (listener != null) {
				PaymentItem item = paymentItems.get(reference);
				if (item != null) {
					if (item.isSuccessful()) {
						listener.onPaymentSucceeded(reference);
					} else if (item.isFail()) {
						listener.onPaymentError(reference);
					}
				} else {
					listener.onPaymentError(reference);
				}
			}
		}
	}
	
	private void cleanListeners() {
		for (int i = 0; i < listeners.size(); i++) {
			PaymentListener temp = listeners.get(i);
			if (temp == null) {
				listeners.remove(i);
			}
		}
	}
}
