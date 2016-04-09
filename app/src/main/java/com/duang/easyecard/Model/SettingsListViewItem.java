package com.duang.easyecard.Model;

/**
 * Created by MrD on 2016/4/9.
 */
public class SettingsListViewItem {
    private int iconResId;
    private String title;
    private int arrowResId;

    public SettingsListViewItem() {
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

    public int getArrowResId() {
        return arrowResId;
    }

    public void setArrowResId(int arrowResId) {
        this.arrowResId = arrowResId;
    }
}
