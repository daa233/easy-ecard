package com.duang.easyecard.Model;

/**
 * Created by MrD on 2016/2/26.
 */
public class TradingInquiry {

    private String tradingDate;  // 交易日期
    private String tradingTime;  // 交易时间
    private String merchantName;  // 商户名称
    private String tradingName;  // 交易名称
    private String transactionAmount;  // 交易金额
    private String balance;  // 卡余额

    public String getTradingDate() {
        return tradingDate;
    }
    public void setTradingDate(String tradingDate) {
        this.tradingDate = tradingDate;
    }

    public String getTradingTime() {
        return tradingTime;
    }
    public void setTradingTime(String tradingTime) {
        this.tradingTime = tradingTime;
    }
    public String getMerchantName() {
        return merchantName;
    }
    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }
    public String getTradingName() {
        return tradingName;
    }
    public void setTradingName(String tradingName) {
        this.tradingName = tradingName;
    }
    public String getTransactionAmount() {
        return transactionAmount;
    }
    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }
    public String getBalance() {
        return balance;
    }
    public void setBalance(String balance) {
        this.balance = balance;
    }
}
