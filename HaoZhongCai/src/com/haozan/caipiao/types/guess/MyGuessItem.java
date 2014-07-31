package com.haozan.caipiao.types.guess;

public class MyGuessItem {
    private String answer_id;
    private int betScore;
    private int earnedScore;
    private String guess_id;
    private int status;
    private String date;
    private String schema_id;
    private String question;
    private String user_id;
    private String vote_id;
    private String vote_answer;
    //add by vincent
    private String guessTerm;
    private String schemaName;

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getTerm() {
        return guessTerm;
    }

    public void setTerm(String guessTerm) {
        this.guessTerm = guessTerm;
    }

    public String getAnwserId() {
        return answer_id;
    }

    public void setAnwserId(String answer_id) {
        this.answer_id = answer_id;
    }

    public int getBetScore() {
        return betScore;
    }

    public void setBetScore(int betScore) {
        this.betScore = betScore;
    }

    public int getEarnedScore() {
        return earnedScore;
    }

    public void setEarnedScore(int earnedScore) {
        this.earnedScore = earnedScore;
    }

    public String getGuessId() {
        return guess_id;
    }

    public void setGuessId(String guess_id) {
        this.guess_id = guess_id;
    }

    public String getVote_answer() {
        return vote_answer;
    }

    public void setVote_answer(String vote_answer) {
        this.vote_answer = vote_answer;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    
    public String getSchemaId() {
        return schema_id;
    }

    public void setSchemaId(String schema_id) {
        this.schema_id = schema_id;
    }
    
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
    
    public String getUserId() {
        return user_id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }
    
    public String getVoteId() {
        return vote_id;
    }

    public void setVoteId(String vote_id) {
        this.vote_id = vote_id;
    }
}
