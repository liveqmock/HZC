package com.haozan.caipiao.types.userinf;

/**
 * 提现记录每条记录数据实体类
 * 
 * @author peter_feng
 * @create-time 2013-3-15 下午4:20:34
 */
public class WithdrawHistoryData {
    // 年份
    private String year;
    // 月份
    private String month;
    // 日
    private String day;
    // 时间
    private String time;
    // 提现银行
    private String bank;
    // 提现金额
    private String money;
    // 提现手续费
    private String fee;
    // 提现状态
    private String status;
    // 是否显示日期
    private boolean showDate;
    // 是否是一天中最后一行显示行线分隔
    private boolean showLine;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isShowDate() {
        return showDate;
    }

    public void setShowDate(boolean showDate) {
        this.showDate = showDate;
    }

    public boolean isShowLine() {
        return showLine;
    }

    public void setShowLine(boolean showLine) {
        this.showLine = showLine;
    }
}
