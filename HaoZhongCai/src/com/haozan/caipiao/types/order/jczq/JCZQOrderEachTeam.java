package com.haozan.caipiao.types.order.jczq;

/**
 * 竞彩足球订单中某一场比赛详细数据
 * 
 * @author peter_wang
 * @create-time 2013-11-2 下午4:53:40
 */
public class JCZQOrderEachTeam {
    // id
    private String id;
    // 周几
    private int day;
    // 场次
    private String index;
    // 玩法
    private String betway;
    // 主队
    private String master;
    // 客队
    private String guest;
    // 投注
    private String betInf;
    // 显示结果
    private String betResult;
    // 比赛比分
    private String matchPoint;
    // 比赛半场比分
    private String matchHalfPoint;
    // 让分
    private int handicap;
    // 赔率
    private String odds;
    // 是否中奖
    private boolean isWin;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getBetway() {
        return betway;
    }

    public void setBetway(String betway) {
        this.betway = betway;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public String getGuest() {
        return guest;
    }

    public void setGuest(String guest) {
        this.guest = guest;
    }

    public String getBetInf() {
        return betInf;
    }

    public void setBetInf(String betInf) {
        this.betInf = betInf;
    }

    public String getBetResult() {
        return betResult;
    }

    public void setBetResult(String betResult) {
        this.betResult = betResult;
    }

    public String getMatchPoint() {
        return matchPoint;
    }

    public void setMatchPoint(String matchPoint) {
        this.matchPoint = matchPoint;
    }

    public String getMatchHalfPoint() {
        return matchHalfPoint;
    }

    public void setMatchHalfPoint(String matchHalfPoint) {
        this.matchHalfPoint = matchHalfPoint;
    }

    public int getHandicap() {
        return handicap;
    }

    public void setHandicap(int handicap) {
        this.handicap = handicap;
    }

    public String getOdds() {
        return odds;
    }

    public void setOdds(String odds) {
        this.odds = odds;
    }

    public boolean isWin() {
        return isWin;
    }

    public void setWin(boolean isWin) {
        this.isWin = isWin;
    }

}
