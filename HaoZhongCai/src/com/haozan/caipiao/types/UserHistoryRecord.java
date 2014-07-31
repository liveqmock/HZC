package com.haozan.caipiao.types;

public class UserHistoryRecord {

    private String id;
    private String term;
    private String balls;
    private String openBalls;
    private String win;
    private double money;
    private double prize;
    private String betTime;
    private String orderId;
    private String betMode;
    private int buyStatus;
    private boolean isClisk;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getBalls() {
        return balls;
    }

    public void setBalls(String balls) {
        this.balls = balls;
    }

    public String getOpenBalls() {
        return openBalls;
    }

    public void setOpenBalls(String openBalls) {
        this.openBalls = openBalls;
    }

    public String getWin() {
        return win;
    }

    public void setWin(String win) {
        this.win = win;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public double getPrize() {
        return prize;
    }

    public void setPrize(double prize) {
        this.prize = prize;
    }

    public String getBetTime() {
        return betTime;
    }

    public void setBetTime(String betTime) {
        this.betTime = betTime;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getBetMode() {
        return betMode;
    }

    public void setBetMode(String betMode) {
        this.betMode = betMode;
    }

    public int getBuyMode() {
        return buyStatus;
    }

    public void setBuyMode(int buyStatus) {
        this.buyStatus = buyStatus;
    }

    public boolean getClickStatus() {
        return isClisk;
    }

    public void setClickStatus(boolean isClisk) {
        this.isClisk = isClisk;
    }
}
