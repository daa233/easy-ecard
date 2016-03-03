package com.duang.easyecard.Model;

/**
 * Used as GridView item model.
 * Created by MrD on 2016/3/3.
 */
public class SimpleItem {

    private String mString;
    private int mResourceId;

    public String getString() {
        return mString;
    }

    public void setString(String mString) {
        this.mString = mString;
    }

    public int getResourceId() {
        return mResourceId;
    }

    public void setResourceId(int mResourceId) {
        this.mResourceId = mResourceId;
    }
}
