package com.duang.easyecard.Model;

import java.io.Serializable;

/**
 * 消息Bean
 * 收件箱和已发送都用这个原型
 * Created by MrD on 2016/3/23.
 */
public class Notice implements Serializable {
    private String id;
    private boolean type;
    private boolean unread;
    private boolean toDelete;  // 默认为false
    private boolean checked;  // 默认为false
    private String senderName;
    private String receiverName;
    private String title;
    private String content;
    private String sentTime;
    private String operationSystem;
    private String category;

    // 类型常量
    public static final boolean RECEIVED_TYPE = true;
    public static final boolean SENT_TYPE = false;

    // 构造函数
    public Notice() {
        this.toDelete = false;
        this.checked = false;
    }

    // 构造函数
    public Notice(boolean type) {
        this.type = type;
        this.toDelete = false;
        this.checked = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isReceivedType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unreadFlag) {
        this.unread = unreadFlag;
    }

    public boolean isToDelete() {
        return toDelete;
    }

    public void setToDelete(boolean toDelete) {
        this.toDelete = toDelete;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSentTime() {
        return sentTime;
    }

    public void setSentTime(String sentTime) {
        this.sentTime = sentTime;
    }

    public String getOperationSystem() {
        return operationSystem;
    }

    public void setOperationSystem(String operationSystem) {
        this.operationSystem = operationSystem;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
