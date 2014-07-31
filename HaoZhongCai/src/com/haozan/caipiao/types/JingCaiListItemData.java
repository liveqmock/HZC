package com.haozan.caipiao.types;

public class JingCaiListItemData {
    private int idNum;
    private String matchType;
    private String matchDate;
    private String homeTeamName;
    private String guessTeamName;
    private String gameOdds;
    private boolean isClick;
    private int winButtonStatus;
    private int equalButtonStatus;
    private int lostButtonStatus;
    private String date;

    public void setIdNum(int idNum) {
        this.idNum = idNum;
    }

    public int getIdNum() {
        return idNum;
    }

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

    public void setMatchHomeTeamName(String homeTeamName) {
        this.homeTeamName = homeTeamName;
    }

    public String getMatchHomeTeamName() {
        return homeTeamName;
    }

    public void setMatchGuessTeamName(String guessTeamName) {
        this.guessTeamName = guessTeamName;
    }

    public String getMatchGuessTeamName() {
        return guessTeamName;
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
    
    public void setDate(String date) {
        this.date = date;
    }

    public String getGate() {
        return date;
    }
}