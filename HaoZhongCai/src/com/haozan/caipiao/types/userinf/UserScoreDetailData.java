package com.haozan.caipiao.types.userinf;

/**
 * 积分明细每项数据实体类
 * 
 * @author peter_feng
 * @create-time 2013-3-14 下午3:49:32
 */
public class UserScoreDetailData {
    // 月份
    private String month;
    // 日
    private String day;
    // 星期几
    private String dayOfWeek;
    // 积分变化描述信息
    private String inf;
    // 积分变化数
    private String variation;
    // 是否显示月份
    private boolean showDate;
    // 是否是最后一行显示行线分隔
    private boolean showLine;

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

    public String getInf() {
        return inf;
    }

    public void setInf(String inf) {
        this.inf = inf;
    }

    public String getVariation() {
        return variation;
    }

    public void setVariation(String variation) {
        this.variation = variation;
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
