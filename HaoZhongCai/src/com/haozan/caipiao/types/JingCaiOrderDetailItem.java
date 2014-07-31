package com.haozan.caipiao.types;

public class JingCaiOrderDetailItem {

    private String jincaiBetItem;
    private String jincaiBetType;
    private int jincaiAward;
    private String jingcaiAwardMoney;
    private int jingcaiBuyMode;
    private int zhushu;
    private int betTimes;

    public void setJingCaiBetItem(String jincaiBetItem) {
        this.jincaiBetItem = jincaiBetItem;
    }

    public String getJingCaiBetItem() {
        return jincaiBetItem;
    }

    public void setJincaiBetType(String jincaiBetType) {
        this.jincaiBetType = jincaiBetType;
    }

    public String getJincaiBetType() {
        return jincaiBetType;
    }

    public void setJincaiAward(int jincaiAward) {
        this.jincaiAward = jincaiAward;
    }

    public int getJincaiAward() {
        return jincaiAward;
    }

    public void setJingcaiAwardMoney(String jingcaiAwardMoney) {
        this.jingcaiAwardMoney = jingcaiAwardMoney;
    }

    public String getJingcaiAwardMoney() {
        return jingcaiAwardMoney;
    }

    public void setJincaiBuyMode(int jingcaiBuyMode) {
        this.jingcaiBuyMode = jingcaiBuyMode;
    }

    public int getJincaiBuyMode() {
        return jingcaiBuyMode;
    }

    public void setZhuShu(int zhushu) {
        this.zhushu = zhushu;
    }

    public int getZhuShu() {
        return zhushu;
    }

    public void setBetTimes(int betTimes) {
        this.betTimes = betTimes;
    }

    public int getBetTimes() {
        return betTimes;
    }
}
