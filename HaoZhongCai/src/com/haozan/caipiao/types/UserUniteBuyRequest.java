package com.haozan.caipiao.types;

public class UserUniteBuyRequest {
    private String programId;
    private String buyOrderId;
    private double perAmount;
    private int allNum;
    private int boughtNum;
    private int rate;
    private double reward;
    private int paulNum;
    private int isOpen;
    private String createTime;
    private int proStatus;
    private String catId;
    private String term;
    private int winStatus;
    private int selfBougthNum;
    private boolean isClick;
    private double prize;

    public String getProgramId() {
        return programId;
    }
    public void setProgramId(String programId) {
        this.programId = programId;
    }
    
    public String getBuyOrderId() {
        return buyOrderId;
    }
    public void setBuyOrderId(String buyOrderId) {
        this.buyOrderId = buyOrderId;
    }

    public double getPerAmount() {
        return perAmount;
    }

    public void setPerAmount(double perAmount) {
        this.perAmount = perAmount;
    }
    
    public int getAllNum() {
        return allNum;
    }
    public void setAllNum(int allNum) {
        this.allNum = allNum;
    }
    
    public int getBoughtNum() {
        return boughtNum;
    }
    public void setBoughtNum(int boughtNum) {
        this.boughtNum = boughtNum;
    }
    
    public int getRate() {
        return rate;
    }
    public void setRate(int rate) {
        this.rate = rate;
    }
    
    public double getReward() {
        return reward;
    }
    public void setReward(double reward) {
        this.reward = reward;
    }
    
    public int getPaulNum() {
        return paulNum;
    }
    public void setPaulNum(int paulNum) {
        this.paulNum = paulNum;
    }
    
    public int getIsOpen() {
        return isOpen;
    }
    public void setIsOpen(int isOpen) {
        this.isOpen = isOpen;
    }

    public String getCreateTime() {
        return createTime;
    }
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
    
    public int getProStatus() {
        return proStatus;
    }
    public void setProStatus(int proStatus) {
        this.proStatus = proStatus;
    }
    
    public String getCatId() {
        return catId;
    }
    public void setCatId(String catId) {
        this.catId = catId;
    }
    
    public String getTerm() {
        return term;
    }
    public void setTerm(String term) {
        this.term = term;
    }
    
    public int getWinStatus() {
        return winStatus;
    }
    public void setWinStatus(int winStatus) {
        this.winStatus = winStatus;
    }
    
    public int getSelfBougthNum() {
        return selfBougthNum;
    }
    public void setSelfBougthNum(int selfBougthNum) {
        this.selfBougthNum = selfBougthNum;
    }
    
    public boolean getClickStatus() {
        return isClick;
    }
    public void setClickStatus(boolean isClick) {
        this.isClick = isClick;
    }
    
    public double getPrize() {
        return prize;
    }
    public void setPrize(double prize) {
        this.prize = prize;
    }
}
