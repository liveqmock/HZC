package com.haozan.caipiao.types;

import java.util.ArrayList;

public class SportsHistory {
    // 比赛日期
    private String date;
    // 比赛是星期几
    private String day;
    // 截止的比赛数
    private String gameNumber;
    private ArrayList<SportsHistoryItem> itemList;

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

    public ArrayList<SportsHistoryItem> getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList<SportsHistoryItem> itemList) {
        this.itemList = itemList;
    }
}
