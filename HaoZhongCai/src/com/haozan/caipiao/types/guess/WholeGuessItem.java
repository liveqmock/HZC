package com.haozan.caipiao.types.guess;

public class WholeGuessItem {
    private String schemaName;
    private String date;// begintime
    private String endDate;
    private String nowTime;
    private String publishTime;
    private String allScore;
    private String answer;
    private String schemaId;
    private int status;
    private String guessTerm;

    public String getAllScore() {
        return allScore;
    }

    public void setAllScore(String allScore) {
        this.allScore = allScore;
    }

    public String getNowTime() {
        return nowTime;
    }

    public void setNowTime(String nowTime) {
        this.nowTime = nowTime;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getSchemaId() {
        return schemaId;
    }

    public void setschemaId(String schemaId) {
        this.schemaId = schemaId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTerm() {
        return guessTerm;
    }

    public void setTerm(String guessTerm) {
        this.guessTerm = guessTerm;
    }
}
