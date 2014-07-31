package com.haozan.caipiao.types;

/**
 * 彩种数据
 * 
 * @author peter_wang
 * @create-time 2013-10-21 上午11:14:00
 */
public class LotteryInf {
    private String id;
    private String name;
    private int icon;
    private String lastTerm;
    private String lastOpenTime;
    private String lastNum;
    private String newTerm;
    private long endTime;
    private String awardTime;
    private long lastTimeMillis = -1;// 最新一期截止倒计时,初始化-1，代表没获取到时间
    private long gapTimeMillis;// 服务器系统时间和本地时间的差距，gapTimeMillis=systemTime-localTime,用来精确计算lastTimeMillis=endtime-localtime+gaptime，因为handler的时间不准
    private String lastTime;// 最新一期截止倒计时描述语
    private String extraInf;// 额外信息，比如双色球奖池、3D试机号等信息

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

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getLastTerm() {
        return lastTerm;
    }

    public void setLastTerm(String lastTerm) {
        this.lastTerm = lastTerm;
    }

    public String getLastOpenTime() {
        return lastOpenTime;
    }

    public void setLastOpenTime(String lastOpenTime) {
        this.lastOpenTime = lastOpenTime;
    }

    public String getLastNum() {
        return lastNum;
    }

    public void setLastNum(String lastNum) {
        this.lastNum = lastNum;
    }

    public String getNewTerm() {
        return newTerm;
    }

    public void setNewTerm(String newTerm) {
        this.newTerm = newTerm;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getAwardTime() {
        return awardTime;
    }

    public void setAwardTime(String awardTime) {
        this.awardTime = awardTime;
    }

    public long getLastTimeMillis() {
        return lastTimeMillis;
    }

    public void setLastTimeMillis(long lastTimeMillis) {
        this.lastTimeMillis = lastTimeMillis;
    }

    public long getGapTimeMillis() {
        return gapTimeMillis;
    }

    public void setGapTimeMillis(long gapTimeMillis) {
        this.gapTimeMillis = gapTimeMillis;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getExtraInf() {
        return extraInf;
    }

    public void setExtraInf(String extraInf) {
        this.extraInf = extraInf;
    }

}
