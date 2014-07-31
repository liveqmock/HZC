package com.haozan.caipiao.types;

public class SportsHistoryItem
    extends SportsMatchBasicItemData {
    // 联盟
    private String league;
    // 比分
    private String point;
    // 让球数|让分数
    private String concede;
    //add by vincent
    //半全场结果
    private String bqcResult;
//    private String rfResult;//如：让分主胜
//    private String dxfResult;
    //胜分差
    private String sfcResult;
    //联赛 颜色
    private String color;
    private String dxfPoint;
    private String day;
    

//    public String getRfResult() {
//        return rfResult;
//    }
//
//    public void setRfResult(String rfResult) {
//        this.rfResult = rfResult;
//    }

//    public String getDxfResult() {
//        return dxfResult;
//    }
//
//    public void setDxfResult(String dxfResult) {
//        this.dxfResult = dxfResult;
//    }

    public String getSfcResult() {
        return sfcResult;
    }

    public void setSfcResult(String sfcResult) {
        this.sfcResult = sfcResult;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDxfPoint() {
        return dxfPoint;
    }

    public void setDxfPoint(String dxfPoint) {
        this.dxfPoint = dxfPoint;
    }

    public String getBqcResult() {
        return bqcResult;
    }

    public void setBqcResult(String bqcResult) {
        this.bqcResult = bqcResult;
    }

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getConcede() {
        return concede;
    }

    public void setConcede(String concede) {
        this.concede = concede;
    }

}
