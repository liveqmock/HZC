package com.haozan.caipiao.types;

public class UserMessageData {
    //the id of the message
    private String id;
    //title of the message
    private String subject;
    //the time create the message
    private String time;
    //whether the message is read
    private Boolean isRead;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public Boolean getIsRead() {
        return isRead;
    }
    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

}
