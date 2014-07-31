package com.haozan.caipiao.types;

public class JingCaiBetHistoryDetailItemData {

    private String betTerm;
    private String master;
    private String guset;
    private String gameResult;
    private int concedePoints;
    private String betResultWin;
    private String betResultEqual;
    private String betResultLost;
    private String halfMatchScore="";
    private String fullMatchScore="";
    private int splitNum;
    private String betGoal;
    private String[] letScore;
    private String[] setScore;
    private String[] betResultGG;

    public void setBetTerm(String betTerm) {
        this.betTerm = betTerm;
    }

    public String getBetTerm() {
        return betTerm;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public String getMaster() {
        return master;
    }

    public void setGuest(String guset) {
        this.guset = guset;
    }

    public String getGuest() {
        return guset;
    }

    public void setGameResult(String gameResult) {
        this.gameResult = gameResult;
    }

    public String getGameResult() {
        return gameResult;
    }

    public void setConcedePoints(int concedePoints) {
        this.concedePoints = concedePoints;
    }

    public int getConcedePoints() {
        return concedePoints;
    }

    public void setBetResultWin(String betResultWin) {
        this.betResultWin = betResultWin;
    }

    public String getBetResultWin() {
        return betResultWin;
    }

    public void setBetResultEqual(String betResultEqual) {
        this.betResultEqual = betResultEqual;
    }

    public String getBetResultEqual() {
        return betResultEqual;
    }

    public void setBetResultLost(String betResultLost) {
        this.betResultLost = betResultLost;
    }

    public String getBetResultLost() {
        return betResultLost;
    }

    public void setHalfMatchScore(String halfMatchScore) {
        this.halfMatchScore = halfMatchScore;
    }

    public String getHalfMatchScore() {
        return halfMatchScore;
    }

    public void setFullMatchScore(String fullMatchScore) {
        this.fullMatchScore = fullMatchScore;
    }

    public String getFullMatchScore() {
        return fullMatchScore;
    }

    public void setSplitNum(int splitNum) {
        this.splitNum = splitNum;
    }

    public int getSplitNum() {
        return splitNum;
    }
    
    public void setBetGoal(String betGoal) {
        this.betGoal = betGoal;
    }

    public String getBetGoal() {
        return betGoal;
    }
    public void setLetScore(String[] letScore) {
        this.letScore = letScore;
    }

    public String[] getLetScore() {
        return letScore;
    }
    public void setScore(String[] setScore) {
        this.setScore = setScore;
    }

    public String[] getScore() {
        return setScore;
    }
    
    public void setResultGG(String[] betResultGG) {
        this.betResultGG = betResultGG;
    }

    public String[] getResultGG() {
        return betResultGG;
    }
}
