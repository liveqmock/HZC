package com.haozan.caipiao.types.order.jczq;

import java.util.ArrayList;

/**
 * 竞彩足球订单详细
 * 
 * @author peter_wang
 * @create-time 2013-11-2 下午5:11:36
 */
public class JCZQOrderDetail {
    // 订单编号
    private String orderId;
    // 玩法
    private String betWay;
    // 串数
    private String bunch;
    // 倍数
    private int times;
    // 拆分场数
    private int splitNum;
    // 场次信息
    private ArrayList<JCZQOrderEachTeam> teamInfList;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getBetway() {
        return betWay;
    }

    public void setBetway(String betWay) {
        this.betWay = betWay;
    }

    public String getBunch() {
        return bunch;
    }

    public void setBunch(String bunch) {
        this.bunch = bunch;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public int getSplitNum() {
        return splitNum;
    }

    public void setSplitNum(int splitNum) {
        this.splitNum = splitNum;
    }

    public ArrayList<JCZQOrderEachTeam> getTeamInfList() {
        return teamInfList;
    }

    public void setTeamInfList(ArrayList<JCZQOrderEachTeam> teamInfList) {
        this.teamInfList = teamInfList;
    }

}
