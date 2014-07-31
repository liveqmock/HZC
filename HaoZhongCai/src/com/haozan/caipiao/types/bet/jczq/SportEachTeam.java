package com.haozan.caipiao.types.bet.jczq;


/**
 * 赛事订单每场比赛选择信息
 * 
 * @author peter_wang
 * @create-time 2013-11-2 下午8:57:22
 */
public class SportEachTeam {
    // 周几
    private int day;
    // 场次
    private String index;
    // 投注
    private String betInf;

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getBetInf() {
        return betInf;
    }

    public void setBetInf(String betInf) {
        this.betInf = betInf;
    }

}
