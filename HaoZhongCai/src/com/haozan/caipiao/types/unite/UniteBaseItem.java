package com.haozan.caipiao.types.unite;

public class UniteBaseItem {
    //方案ID
    private String programId;
    //合买彩种
    private String unitekind;
    //发起人
    private String sponsor;
    //发起人nickname
    private String nickname;
    //进度
    private String scheduled;
    //保底
    private String guarantee;
    //保底比例
    private String guaRate;
    //方案总额
    private String totalMoney;
    //每份金额
    private String price;
    //剩余份数
    private String lastAmount;
    
    public String getProgramId() {
        return programId;
    }
    public void setProgramId(String programId) {
        this.programId = programId;
    }
    public String getUnitekind() {
        return unitekind;
    }
    public void setUnitekind(String unitekind) {
        this.unitekind = unitekind;
    }
    public String getSponsor() {
        return sponsor;
    }
    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getScheduled() {
        return scheduled;
    }
    public void setScheduled(String scheduled) {
        this.scheduled = scheduled;
    }
    public String getGuarantee() {
        return guarantee;
    }
    public void setGuarantee(String guarantee) {
        this.guarantee = guarantee;
    }
    public String getGuaRate() {
        return guaRate;
    }
    public void setGuaRate(String guaRate) {
        this.guaRate = guaRate;
    }
    public String getTotalMoney() {
        return totalMoney;
    }
    public void setTotalMoney(String totalMoney) {
        this.totalMoney = totalMoney;
    }
    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }
    public String getLastAmount() {
        return lastAmount;
    }
    public void setLastAmount(String lastAmount) {
        this.lastAmount = lastAmount;
    }
    
}
