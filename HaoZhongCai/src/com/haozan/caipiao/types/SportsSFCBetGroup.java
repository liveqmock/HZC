package com.haozan.caipiao.types;

import java.util.ArrayList;

public class SportsSFCBetGroup {
    // 比赛日期
    private String date;
    // 比赛是星期几
    private String day;
    // 比赛数
    private String gameNumber;
    //竞彩足球
    private ArrayList<SportsItem> itemList;
    private ArrayList<SportsItem> spfitemList;
    private ArrayList<SportsItem> zjqitemList;
    private ArrayList<SportsItem> bqcitemList;
    private ArrayList<SportsItem> jczqbfitemList;
    //足球单场
    private ArrayList<SportsItem> zqdcsxdsitemList;
    private ArrayList<SportsItem> zqdcbfitemList;
  //竞彩蓝球
    private ArrayList<SportsJCLQItem> itemJCLQList;
    private ArrayList<SportsLQSFItem> jclqsfitemList;
    private ArrayList<SportsLQRFSFItem> jclqrfsfitemList;
    private ArrayList<SportsLQDXFItem> jclqdxfitemList;
    
    public ArrayList<SportsJCLQItem> getItemJCLQList() {
        return itemJCLQList;
    }

    public void setItemJCLQList(ArrayList<SportsJCLQItem> itemJCLQList) {
        this.itemJCLQList = itemJCLQList;
    }

    public ArrayList<SportsItem> getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList<SportsItem> itemList) {
        this.itemList = itemList;
    }

    public ArrayList<SportsItem> getJczqbfitemList() {
        return jczqbfitemList;
    }

    public void setJczqbfitemList(ArrayList<SportsItem> jczqbfitemList) {
        this.jczqbfitemList = jczqbfitemList;
    }

    public ArrayList<SportsItem> getZQDCSxdsitemList() {
        return zqdcsxdsitemList;
    }

    public void setZQDCSxdsitemList(ArrayList<SportsItem> sxdsitemList) {
        this.zqdcsxdsitemList = sxdsitemList;
    }

    public ArrayList<SportsItem> getZQDCBfitemList() {
        return zqdcbfitemList;
    }

    public void setZQDCBfitemList(ArrayList<SportsItem> bfitemList) {
        this.zqdcbfitemList = bfitemList;
    }

    public ArrayList<SportsItem> getZjqitemList() {
        return zjqitemList;
    }

    public void setZjqitemList(ArrayList<SportsItem> zjqitemList) {
        this.zjqitemList = zjqitemList;
    }

    public ArrayList<SportsItem> getBqcitemList() {
        return bqcitemList;
    }

    public void setBqcitemList(ArrayList<SportsItem> bqcitemList) {
        this.bqcitemList = bqcitemList;
    }

    

    public ArrayList<SportsLQSFItem> getJCLQSfitemList() {
        return jclqsfitemList;
    }

    public void setJCLQSfitemList(ArrayList<SportsLQSFItem> sfitemList) {
        this.jclqsfitemList = sfitemList;
    }

    public ArrayList<SportsLQRFSFItem> getJCLQRfsfitemList() {
        return jclqrfsfitemList;
    }

    public void setJCLQRfsfitemList(ArrayList<SportsLQRFSFItem> rfsfitemList) {
        this.jclqrfsfitemList = rfsfitemList;
    }

    public ArrayList<SportsLQDXFItem> getJCLQDxfitemList() {
        return jclqdxfitemList;
    }

    public void setJCLQDxfitemList(ArrayList<SportsLQDXFItem> dxfitemList) {
        this.jclqdxfitemList = dxfitemList;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getGameNumber() {
        return gameNumber;
    }

    public void setGameNumber(String gameNumber) {
        this.gameNumber = gameNumber;
    }

    public ArrayList<SportsItem> getSpfitemList() {
        return spfitemList;
    }

    public void setSpfitemList(ArrayList<SportsItem> itemList) {
        this.spfitemList = itemList;
    }
}
