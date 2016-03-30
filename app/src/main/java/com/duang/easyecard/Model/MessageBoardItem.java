package com.duang.easyecard.Model;

/**
 * 留言板列表项
 * Created by MrD on 2016/3/30.
 */
public class MessageBoardItem {
    private String user;
    private String title;
    private String time;
    private String type;
    private String reply;

    public MessageBoardItem() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
