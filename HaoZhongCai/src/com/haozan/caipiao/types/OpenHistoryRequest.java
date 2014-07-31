package com.haozan.caipiao.types;

public class OpenHistoryRequest{
    private String term;
    private String openDate;
    private String normalNum;
    private String specialNum;
    private String type;
    
    public String getTerm() {
        return term;
    }
    public void setTerm(String term) {
        this.term = term;
    }
    public String getOpenDate() {
        return openDate;
    }
    public void setOpenDate(String openDate) {
        this.openDate = openDate;
    }
    public String getNormalNum() {
        return normalNum;
    }
    public void setNormalNum(String normalNum) {
        this.normalNum = normalNum;
    }
    public String getSpecialNum() {
        return specialNum;
    }
    public void setSpecialNum(String specialNum) {
        this.specialNum = specialNum;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
