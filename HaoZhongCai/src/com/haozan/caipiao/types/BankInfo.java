package com.haozan.caipiao.types;

public class BankInfo {
    private int id;
    private String firstChar;
    private String chineseName;
    private String key;
    private String depositCard;
    private String creditCard;
    private String lastUse;//0：未用  1：储蓄卡 2：信用卡
    private String rechargeType;

    public String getLastUse() {
        return lastUse;
    }

    public void setLastUse(String lastUse) {
        this.lastUse = lastUse;
    }

    public String getRechargeType() {
        return rechargeType;
    }

    public void setRechargeType(String rechargeType) {
        this.rechargeType = rechargeType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstChar() {
        return firstChar;
    }

    public void setFirstChar(String firstChar) {
        this.firstChar = firstChar;
    }

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDepositCard() {
        return depositCard;
    }

    public void setDepositCard(String depositCard) {
        this.depositCard = depositCard;
    }

    public String getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }

}
