/**
 * Mig33 Pte. Ltd.
 *
 * Copyright (c) 2012 mig33. All rights reserved.
 */
package com.mig33.android.sdk.model;

import com.google.gson.Gson;

/**
 * PaymentReceipt.java
 * 
 * @author warrenbalcos on May 20, 2013
 * 
 */
public class PaymentReceipt {
	
	private Data	data;
	private Balance	balance;
	
	public String getReference() {
		if (data != null) {
			return data.getReference();
		}
		return null;
	}
	
	class Data {
		private long	id;
		private String	reference;
		private Amount	amount;
		
		/**
		 * @return the id
		 */
		public long getId() {
			return id;
		}
		
		/**
		 * @param id
		 *            the id to set
		 */
		public void setId(long id) {
			this.id = id;
		}
		
		/**
		 * @return the reference
		 */
		public String getReference() {
			return reference;
		}
		
		/**
		 * @param reference
		 *            the reference to set
		 */
		public void setReference(String reference) {
			this.reference = reference;
		}
		
		/**
		 * @return the amount
		 */
		public Amount getAmount() {
			return amount;
		}
		
		/**
		 * @param amount
		 *            the amount to set
		 */
		public void setAmount(Amount amount) {
			this.amount = amount;
		}
	}
	
	class Balance {
		private double	amount;
		private String	currency;
		
		/**
		 * @return the amount
		 */
		public double getAmount() {
			return amount;
		}
		
		/**
		 * @param amount
		 *            the amount to set
		 */
		public void setAmount(double amount) {
			this.amount = amount;
		}
		
		/**
		 * @return the currency
		 */
		public String getCurrency() {
			return currency;
		}
		
		/**
		 * @param currency
		 *            the currency to set
		 */
		public void setCurrency(String currency) {
			this.currency = currency;
		}
	}
	
	class Amount {
		
		private double	amount;
		private double	fundedAmount;
		private String	currency;
		
		/**
		 * @return the amount
		 */
		public double getAmount() {
			return amount;
		}
		
		/**
		 * @param amount
		 *            the amount to set
		 */
		public void setAmount(double amount) {
			this.amount = amount;
		}
		
		/**
		 * @return the fundedAmount
		 */
		public double getFundedAmount() {
			return fundedAmount;
		}
		
		/**
		 * @param fundedAmount
		 *            the fundedAmount to set
		 */
		public void setFundedAmount(double fundedAmount) {
			this.fundedAmount = fundedAmount;
		}
		
		/**
		 * @return the currency
		 */
		public String getCurrency() {
			return currency;
		}
		
		/**
		 * @param currency
		 *            the currency to set
		 */
		public void setCurrency(String currency) {
			this.currency = currency;
		}
	}
	
	/**
	 * @return the data
	 */
	public Data getData() {
		return data;
	}
	
	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(Data data) {
		this.data = data;
	}
	
	/**
	 * @return the balance
	 */
	public Balance getBalance() {
		return balance;
	}
	
	/**
	 * @param balance
	 *            the balance to set
	 */
	public void setBalance(Balance balance) {
		this.balance = balance;
	}
	
	public String toJsonString() {
		return new Gson().toJson(this);
	}
}
