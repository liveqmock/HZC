package com.haozan.caipiao.types;

public class FootBallInstanceScoreItem {
    private String matchType;
    private String matchDate;
    private String teamName;
    private String gameAdditional;
    private String betStatus;

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

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setGameAdditional(String gameAdditional) {
        this.gameAdditional = gameAdditional;
    }

    public String getGameAdditional() {
        return gameAdditional;
    }
    
    public void setGameStatus(String betStatus) {
        this.betStatus = betStatus;
    }

    public String getGameStatus() {
        return betStatus;
    }
}
