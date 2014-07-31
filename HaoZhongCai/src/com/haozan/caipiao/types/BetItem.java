package com.haozan.caipiao.types;

public class BetItem {
    private int id;
    private String term;
    private String mode;
    private String code;
    private String displayCode;
    private String luckyNum;
    private String mstar;
    private String todayLucky;
    private long money;
    private int type;//玩法

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDisplayCode() {
        return displayCode;
    }

    public void setDisplayCode(String displayCode) {
        this.displayCode = displayCode;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public String getLuckyNum() {
        return luckyNum;
    }

    public void setLuckyNum(String luckyNum) {
        this.luckyNum = luckyNum;
    }

    public String getMstar() {
        return mstar;
    }

    public void setMstar(String mstar) {
        this.mstar = mstar;
    }

    public String getTodayLucky() {
        return todayLucky;
    }

    public void setTodayLucky(String todayLucky) {
        this.todayLucky = todayLucky;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
