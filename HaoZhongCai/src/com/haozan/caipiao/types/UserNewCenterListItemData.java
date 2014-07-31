package com.haozan.caipiao.types;

public class UserNewCenterListItemData {
    private int type;
    private String no;
    private String lot_id;
    private String term;
    private String date;
    private int bet;
    private double money;
    private int win;
    private double prize;
    private boolean isWithDate;
    private boolean isListDiviver;
    private boolean isSingle;
    private boolean isClicked;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getLotId() {
        return lot_id;
    }

    public void setLotId(String lot_id) {
        this.lot_id = lot_id;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }
    
    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public double getPrize() {
        return prize;
    }

    public void setPrize(double prize) {
        this.prize = prize;
    }
    
    public boolean getIsWithDate() {
        return isWithDate;
    }

    public void setIsWithDate(boolean isWithDate) {
        this.isWithDate = isWithDate;
    }
    
    public boolean getIsDiviver() {
        return isListDiviver;
    }

    public void setIsListDiviver(boolean isListDiviver) {
        this.isListDiviver = isListDiviver;
    }
    
    public boolean getIsSingle() {
        return isSingle;
    }

    public void setIsSingle(boolean isSingle) {
        this.isSingle = isSingle;
    }
    
    public boolean getClickStatus() {
        return isClicked;
    }

    public void setClickStatus(boolean isClicked) {
        this.isClicked = isClicked;
    }
}
