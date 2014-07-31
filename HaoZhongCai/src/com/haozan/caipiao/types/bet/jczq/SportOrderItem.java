package com.haozan.caipiao.types.bet.jczq;

import java.util.ArrayList;

/**
 * 赛事订单信息
 * 
 * @author peter_wang
 * @create-time 2013-11-2 下午8:58:24
 */
public class SportOrderItem {
    // 串数
    private String bunch;
    // 倍数
    private int times;
    // 每场比赛信息
    private ArrayList<SportEachTeam> teamInfList;

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

    public ArrayList<SportEachTeam> getTeamInfList() {
        return teamInfList;
    }

    public void setTeamInfList(ArrayList<SportEachTeam> teamInfList) {
        this.teamInfList = teamInfList;
    }

}
