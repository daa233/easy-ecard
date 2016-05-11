package com.duang.easyecard.Model;

import java.io.Serializable;

/**
 * FAQ: 常见问题，ListItem
 * Created by MrD on 2016/3/28.
 */
public class FaqItem implements Serializable {
    private String title;
    private String detailAddress;
    private int type;

    public FaqItem() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }
}
