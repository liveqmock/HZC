package com.haozan.caipiao.types;

/**
 * 大厅item数据
 * 
 * @author peter_wang
 * @create-time 2013-10-21 上午11:14:00
 */
public class HallItem {
    private String id;
    private String name;
    private int icon;
    private String lastTerm;
    private String lastOpenTime;
    private String lastNum;
    private String newTerm;
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
