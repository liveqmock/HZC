package com.haozan.caipiao.types;

public class AthleticsListItemData
    extends SportsMatchBasicItemData {
    // 唯一id标示
    private String id;
    private String matchType;
    private String matchDate;
    private String gameOdds;
    private boolean isClick;
    private int winButtonStatus;
    private int equalButtonStatus;
    private int lostButtonStatus;
    private String matchId;

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // 是否显示分析内容
    private boolean ifShowAnalyse;
    // 历史交锋
    private String[] hisCom = new String[3];
    // 平均赔率
    private String[] eveOdds = new String[3];
    // 两队排名
    private String[] rank = new String[2];

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }

    public String getMatchDate() {
        return matchDate;
    }

    public void setGameOdds(String gameOdds) {
        this.gameOdds = gameOdds;
    }

    public String getGameOdds() {
        return gameOdds;
    }

    public void setClickStatus(boolean isClick) {
        this.isClick = isClick;
    }

    public boolean getClickStatus() {
        return isClick;
    }

    public void setWinButtonStatus(int winButtonStatus) {
        this.winButtonStatus = winButtonStatus;
    }

    public int getWinButtonStatus() {
        return winButtonStatus;
    }

    public void setLostButtonStatus(int lostButtonStatus) {
        this.lostButtonStatus = lostButtonStatus;
    }

    public int getLostButtonStatus() {
        return lostButtonStatus;
    }

    public void setEqualButtonStatus(int equalButtonStatus) {
        this.equalButtonStatus = equalButtonStatus;
    }

    public int getEqualButtonStatus() {
        return equalButtonStatus;
    }

    public boolean isIfShowAnalyse() {
        return ifShowAnalyse;
    }

    public void setIfShowAnalyse(boolean ifShowAnalyse) {
        this.ifShowAnalyse = ifShowAnalyse;
    }

    public String[] getHisCom() {
        return hisCom;
    }

    public void setHisCom(String[] hisCom) {
        this.hisCom = hisCom;
    }

    public String getHisCom(int index) {
        return hisCom[index];
    }

    public void setHisCom(int index, String hisCom) {
        this.hisCom[index] = hisCom;
    }

    public String[] getEveOdds() {
        return eveOdds;
    }

    public void setEveOdds(String[] eveOdds) {
        this.eveOdds = eveOdds;
    }

    public String getEveOdds(int index) {
        return eveOdds[index];
    }

    public void setEveOdds(int index, String eveOdds) {
        this.eveOdds[index] = eveOdds;
    }

    public String[] getRank() {
        return rank;
    }

    public void setRank(String[] rank) {
        this.rank = rank;
    }

    public String getRank(int index) {
        return rank[index];
    }

    public void setRank(int index, String rank) {
        this.rank[index] = rank;
    }
}
