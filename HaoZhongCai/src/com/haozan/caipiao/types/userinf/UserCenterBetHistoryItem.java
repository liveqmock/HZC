package com.haozan.caipiao.types.userinf;

public class UserCenterBetHistoryItem {
    // 月份
    private String month;
    // 日
    private String day;
    // 星期几
    private String dayOfWeek;
    // 彩种id
    private String id;
    // 彩种名称
    private String name;
    // 购彩金额
    private double money;
    // 类型描述
    private String typeDescription;
    // 状态
    private String status;
    // 订单状态显示的颜色
    private int statusColor;
    // 同一日期是否显示完投注
    private boolean showFinished;
    // 是否显示日期
    private boolean showDate;
    // 是否中奖
    private boolean isAward;
    // 购彩类别，1、6普通投注，2追号，3、4合买，5送彩票
    private int type;
    // 订单号
    private String orderId;
    // 期次
    private String term;

    // 中奖金额
    private double winMoney;

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getTypeDescription() {
        return typeDescription;
    }

    public void setTypeDescription(String typeDescription) {
        this.typeDescription = typeDescription;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStatusColor() {
        return statusColor;
    }

    public void setStatusColor(int statusColor) {
        this.statusColor = statusColor;
    }

    public boolean isShowFinished() {
        return showFinished;
    }

    public void setShowFinished(boolean showFinished) {
        this.showFinished = showFinished;
    }

    public boolean isShowDate() {
        return showDate;
    }

    public void setShowDate(boolean showDate) {
        this.showDate = showDate;
    }

    public boolean isAward() {
        return isAward;
    }

    public void setAward(boolean isAward) {
        this.isAward = isAward;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public double getWinMoney() {
        return winMoney;
    }

    public void setWinMoney(double winMoney) {
        this.winMoney = winMoney;
    }

}
