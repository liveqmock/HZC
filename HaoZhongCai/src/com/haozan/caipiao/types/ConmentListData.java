package com.haozan.caipiao.types;

import java.io.Serializable;

public class ConmentListData
    implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -711791829617640232L;
    private String userid;
    private String weiboid;
    private String name;
    private String phone;
    private String content;
    private String time;
    private String retweetCount;
    private String replyCount;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getWeiboid() {
        return weiboid;
    }

    public void setWeiboid(String weiboid) {
        this.weiboid = weiboid;
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
