package com.haozan.caipiao.types;

public class SportsMatchBasicItemData {
    // 周几的比赛
    private String weekDay;
    // 投注格式里的编号
    private String idBetNum;
    // 主队
    private String homeTeamName;
    // 客队
    private String guessTeamName;
    // 比赛结果
    private String result;

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public void setIdBetNum(String idBetNum) {
        this.idBetNum = idBetNum;
    }

    public String getIdBetNum() {
        return idBetNum;
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
