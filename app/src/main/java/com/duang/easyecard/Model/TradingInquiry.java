package com.duang.easyecard.Model;

/**
 * Created by MrD on 2016/2/26.
 */
public class TradingInquiry {

    private String mTradingDate;  // 交易日期
    private String mTradingTime;  // 交易时间
    private String mMerchantName;  // 商户名称
    private String mTradingName;  // 交易名称
    private String mTransactionAmount;  // 交易金额
    private String mBalance;  // 卡余额

    public String getTradingDate() {
        return mTradingDate;
    }
    public void setTradingDate(String mTradingDate) {
        this.mTradingDate = mTradingDate;
    }

    public String getTradingTime() {
        return mTradingTime;
    }
    public void setTradingTime(String mTradingTime) {
        this.mTradingTime = mTradingTime;
    }
    public String getMerchantName() {
        return mMerchantName;
    }
    public void setMerchantName(String mMerchantName) {
        this.mMerchantName = mMerchantName;
    }
    public String getTradingName() {
        return mTradingName;
    }
    public void setTradingName(String mTradingName) {
        this.mTradingName = mTradingName;
    }
    public String getTransactionAmount() {
        return mTransactionAmount;
    }
    public void setTransactionAmount(String mTransactionAmount) {
        this.mTransactionAmount = mTransactionAmount;
    }
    public String getBalance() {
        return mBalance;
    }
    public void setBalance(String mBalance) {
        this.mBalance = mBalance;
    }
}
