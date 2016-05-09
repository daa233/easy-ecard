package com.duang.easyecard.Model;

/**
 * Created by MrD on 2016/3/19.
 */
public class MessagesListViewItem {

    private int iconResId;
    private String title;
    private String newMessagesCount;
    private int newMessageVisibility;
    private int arrowResId;

    public MessagesListViewItem() {
    }

    public MessagesListViewItem(String title) {
        this.title = title;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNewMessagesCount() {
        return newMessagesCount;
    }

    public void setNewMessagesCount(String newMessagesCount) {
        this.newMessagesCount = newMessagesCount;
    }

    public int getNewMessageVisibility() {
        return newMessageVisibility;
    }

    public void setNewMessageVisibility(int newMessageVisibility) {
        this.newMessageVisibility = newMessageVisibility;
    }

    public int getArrowResId() {
        return arrowResId;
    }

    public void setArrowResId(int arrowResId) {
        this.arrowResId = arrowResId;
    }
}
