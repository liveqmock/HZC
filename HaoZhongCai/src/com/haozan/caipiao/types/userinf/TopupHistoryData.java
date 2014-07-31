package com.haozan.caipiao.types.userinf;

/**
 * 充值记录每项数据实体类
 * 
 * @author peter_feng
 * @create-time 2013-3-15 上午11:59:18
 */

public class TopupHistoryData {

    // 年份
    private String year;
    // 月份
    private String month;
    // 日
    private String day;
    // 时间
    private String time;
    // 充值描述信息
    private String inf;
    // 充值金额
    private String money;
    // 充值方式
    private String way;
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

    public String getInf() {
        return inf;
    }

    public void setInf(String inf) {
        this.inf = inf;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getWay() {
        return way;
    }

    public void setWay(String way) {
        this.way = way;
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
