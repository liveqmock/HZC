package com.haozan.caipiao.types;

public class TempData {
    private String tName;
    private String tPhoneNum;
    private int groupPosition;
    private int childPosition;
    private boolean ifFromContacts = false;
    private String shortUrl;

    public String gettName() {
        return tName;
    }

    public void settName(String tName) {
        this.tName = tName;
    }

    public String gettPhoneNum() {
        return tPhoneNum;
    }

    public void settPhoneNum(String tPhoneNum) {
        this.tPhoneNum = tPhoneNum;
    }

    public int getGroupPosition() {
        return groupPosition;
    }

    public void setGroupPosition(int groupPosition) {
        this.groupPosition = groupPosition;
    }

    public int getChildPosition() {
        return childPosition;
    }

    public void setChildPosition(int childPosition) {
        this.childPosition = childPosition;
    }

    public boolean isIfFromContacts() {
        return ifFromContacts;
    }

    public void setIfFromContacts(boolean ifFromContacts) {
        this.ifFromContacts = ifFromContacts;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

}
