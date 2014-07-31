package com.haozan.caipiao.types.order;

/**
 * 合买订单详情
 * 
 * @author peter_wang
 * @create-time 2013-10-30 下午3:54:16
 */
public class UniteOrderDetail {
    // 方案编号
    private String programId;
    // 方案期次
    private String term;
    // 发起人id
    private String userId;
    // 发起人昵称
    private String nickname;
    // 每份金额
    private String price;
    // 总份数
    private int allAmount;
    // 每份金额
    private double perMoney;
    // 已购份数
    private int boughtAmount;
    // 发起者自购份数
    private int buySelfAmount;
    // 进度
    private int rate;
    // 保底比例
    private int insurance;
    // 标题
    private String title;
    // 方案描述
    private String describe;
    // 方案状态 0：未满员；1：已满员下单；2：已撤单(发起者主动撤单)；3：合买失败(未达到发起条件)
    private String programStatus;
    // 订单状态 0：未中奖；1：中奖；2：未开奖
    private String winStatus;
    // 中奖金额
    private double winMoney;
    // 保密级别(1：完全公开;2：跟单可见；3：开奖后公开)
    private String secrecy;
    // 投注号码
    private String codes;
    // 佣金比例
    private int commission;
    private String uniteTime;
    // 彩种id
    private String lotteryId;

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getAllAmount() {
        return allAmount;
    }

    public void setAllAmount(int allAmount) {
        this.allAmount = allAmount;
    }

    public double getPerMoney() {
        return perMoney;
    }

    public void setPerMoney(double perMoney) {
        this.perMoney = perMoney;
    }

    public int getBoughtAmount() {
        return boughtAmount;
    }

    public void setBoughtAmount(int boughtAmount) {
        this.boughtAmount = boughtAmount;
    }

    public int getBuySelfAmount() {
        return buySelfAmount;
    }

    public void setBuySelfAmount(int buySelfAmount) {
        this.buySelfAmount = buySelfAmount;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getInsurance() {
        return insurance;
    }

    public void setInsurance(int insurance) {
        this.insurance = insurance;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getProgramStatus() {
        return programStatus;
    }

    public void setProgramStatus(String programStatus) {
        this.programStatus = programStatus;
    }

    public String getWinStatus() {
        return winStatus;
    }

    public void setWinStatus(String winStatus) {
        this.winStatus = winStatus;
    }

    public double getWinMoney() {
        return winMoney;
    }

    public void setWinMoney(double winMoney) {
        this.winMoney = winMoney;
    }

    public String getSecrecy() {
        return secrecy;
    }

    public void setSecrecy(String secrecy) {
        this.secrecy = secrecy;
    }

    public String getCodes() {
        return codes;
    }

    public void setCodes(String codes) {
        this.codes = codes;
    }

    public int getCommission() {
        return commission;
    }

    public void setCommission(int commission) {
        this.commission = commission;
    }

    public String getUniteTime() {
        return uniteTime;
    }

    public void setUniteTime(String uniteTime) {
        this.uniteTime = uniteTime;
    }

    public String getLotteryId() {
        return lotteryId;
    }

    public void setLotteryId(String lotteryId) {
        this.lotteryId = lotteryId;
    }

}
