package com.haozan.caipiao.types;

public class PursuitBetHistory {
    private String lot_id;
    private String start_term;
    private String codes;
    private int isStopBet;
    private double money;
    private int status;
    private String betTime;
    private int pursuitId;
    private int num;
    private int doNum;
    private boolean isClick;

    public String getId() {
        return lot_id;
    }

    public void setId(String lot_id) {
        this.lot_id = lot_id;
    }

    public String getTerm() {
        return start_term;
    }

    public void setTerm(String start_term) {
        this.start_term = start_term;
    }

    public String getCodes() {
        return codes;
    }

    public void setCodes(String codes) {
        this.codes = codes;
    }

    public int getStopBet() {
        return isStopBet;
    }

    public void setStopBet(int isStopBet) {
        this.isStopBet = isStopBet;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getBetTime() {
        return betTime;
    }

    public void setBetTime(String betTime) {
        this.betTime = betTime;
    }

    public int getPursuitId() {
        return pursuitId;
    }

    public void setPursuitId(int pursuitId) {
        this.pursuitId = pursuitId;
    }

    public int getPursuitNum() {
        return num;
    }

    public void setPursuitNum(int num) {
        this.num = num;
    }

    public int getPursuitDoNum() {
        return doNum;
    }

    public void setPursuitDoNum(int doNum) {
        this.doNum = doNum;
    }

    public boolean getClickStatus() {
        return isClick;
    }

    public void setClickStatus(boolean isClick) {
        this.isClick = isClick;
    }
}
