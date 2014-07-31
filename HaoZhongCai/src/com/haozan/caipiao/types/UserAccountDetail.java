package com.haozan.caipiao.types;

public class UserAccountDetail {
    private String type;
    private String money;
    private String balance;
    private String description;
    private String date;
    private String orderId;
    private int isListDiviverBgLeft;
    private int isListDiviverBgRight;
    private int isSingleBg;
    private String month = "";
    private String dayOfMonth = "";
    private String dayOfWeek = "";
    private String textOfType;
    private String inOrOut;
    private String time;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getIsDiviverLeft() {
        return isListDiviverBgLeft;
    }

    public void setIsListDiviverLeft(int isListDiviverBgLeft) {
        this.isListDiviverBgLeft = isListDiviverBgLeft;
    }

    public int getIsDiviverRight() {
        return isListDiviverBgRight;
    }

    public void setIsListDiviverRight(int isListDiviverBgRight) {
        this.isListDiviverBgRight = isListDiviverBgRight;
    }

    public int getIsSingleBg() {
        return isSingleBg;
    }

    public void setIsSingleBg(int isSingleBg) {
        this.isSingleBg = isSingleBg;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDayofMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(String dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfweek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getTextOfType() {
        return textOfType;
    }

    public void setTextOfType(String textOfType) {
        this.textOfType = textOfType;
    }

    public String getInOrOut() {
        return inOrOut;
    }

    public void setInOrOut(String inOrOut) {
        this.inOrOut = inOrOut;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
