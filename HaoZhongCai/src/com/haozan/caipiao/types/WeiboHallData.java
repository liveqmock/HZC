package com.haozan.caipiao.types;

import java.io.Serializable;


public class WeiboHallData implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -5710187158906927683L;
    private int id;
    private String name;
    private String phone;
    private String content;
    private String time;
    private String avatar;
    private String retweetCount;
    private String replyCount;
    
    public int getId() {
        return id;
    }
    public void setId(int i) {
        this.id = i;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public String getRetweetCount() {
        return retweetCount;
    }
    public void setRetweetCount(String retweetCount) {
        this.retweetCount = retweetCount;
    }
    public String getReplyCount() {
        return replyCount;
    }
    public void setReplyCount(String replyCount) {
        this.replyCount = replyCount;
    }
    
}
