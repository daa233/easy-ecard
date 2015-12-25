package com.duang.easyecard.Models;

public class TradingInquiry {

	private String mTradingTime;  // 交易时间
	private String mMerchantName;  // 商户名称
	private String mTradingName;  // 交易名称
	private String mTransactionAmount;  // 交易金额
	private String mBalance;  // 卡余额
	
	public String getmTradingTime() {
		return mTradingTime;
	}
	public void setmTradingTime(String mTradingTime) {
		this.mTradingTime = mTradingTime;
	}
	public String getmMerchantName() {
		return mMerchantName;
	}
	public void setmMerchantName(String mMerchantName) {
		this.mMerchantName = mMerchantName;
	}
	public String getmTradingName() {
		return mTradingName;
	}
	public void setmTradingName(String mTradingName) {
		this.mTradingName = mTradingName;
	}
	public String getmTransactionAmount() {
		return mTransactionAmount;
	}
	public void setmTransactionAmount(String mTransactionAmount) {
		this.mTransactionAmount = mTransactionAmount;
	}
	public String getmBalance() {
		return mBalance;
	}
	public void setmBalance(String mBalance) {
		this.mBalance = mBalance;
	}
}
