package com.duang.easyecard.Model;

import java.io.Serializable;

/**
 * Class UserBasicInformation
 * To communicate information conveniently
 * 在丢失卡登记与招领时需要用户的学号、卡号等相关信息
 * Created by MrD on 2016/3/13.
 */
public class UserBasicInformation implements Serializable {

    private String name;  // 姓名
    private String stuId;  // 学工号
    private String cardAccount;  // 校园卡号
    private String balance;  // 校园卡余额
    private String bankAccout;  // 银行卡号（不完整）
    private String currentTransition;  // 当前过渡余额
    private String lastTransition;  // 上次过渡余额
    private String reportLossState;  // 挂失状态
    private String freezeState;  // 冻结状态
    private String identityType;  // 身份类型
    private String department;  // 部门名称

    // Constructor
    public UserBasicInformation() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStuId() {
        return stuId;
    }

    public void setStuId(String stuId) {
        this.stuId = stuId;
    }

    public String getCardAccount() {
        return cardAccount;
    }

    public void setCardAccount(String cardAccount) {
        this.cardAccount = cardAccount;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getBankAccout() {
        return bankAccout;
    }

    public void setBankAccout(String bankAccout) {
        this.bankAccout = bankAccout;
    }

    public String getCurrentTransition() {
        return currentTransition;
    }

    public void setCurrentTransition(String currentTransition) {
        this.currentTransition = currentTransition;
    }

    public String getLastTransition() {
        return lastTransition;
    }

    public void setLastTransition(String lastTransition) {
        this.lastTransition = lastTransition;
    }

    public String getReportLossState() {
        return reportLossState;
    }

    public void setReportLossState(String reportLossState) {
        this.reportLossState = reportLossState;
    }

    public String getFreezeState() {
        return freezeState;
    }

    public void setFreezeState(String freezeState) {
        this.freezeState = freezeState;
    }

    public String getIdentityType() {
        return identityType;
    }

    public void setIdentityType(String identityType) {
        this.identityType = identityType;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
