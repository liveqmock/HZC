package com.haozan.caipiao.types;

public class PursuitHistoryDetailItem {
    private String lottId;
    private String orderId;
    private String term;
    private double money;
    private int win;
    private String openCodes;
    private double prize;
    private int pursuitBuyMode;

    public void setLotteryId(String lottId) {
        this.lottId = lottId;
    }

    public String getLotteryId() {
        return lottId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getOpenCodes() {
        return openCodes;
    }

    public void setOpenCodes(String openCodes) {
        this.openCodes = openCodes;
    }

    public double getPrize() {
        return prize;
    }

    public void setPrize(double prize) {
        this.prize = prize;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public void setPursuitBuyMode(int pursuitBuyMode) {
        this.pursuitBuyMode = pursuitBuyMode;
    }

    public int getPursuitBuyMode() {
        return pursuitBuyMode;
    }

}
