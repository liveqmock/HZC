package com.haozan.caipiao.types;

import android.os.Parcel;
import android.os.Parcelable;

public class SportsItem
    extends SportsMatchBasicItemData
    implements Parcelable {
    // 唯一id标示
    private String id;
    // 联盟
    private String league;
    // 让球数
    private String concede;
    // 各个赔率
    private String[] odds = new String[31];
    // 投注截止时间
    private String endTime;
    // 比赛开始时间
    private String matchTime;
    // 如果是选择足球胜平负的话，是否选择胜平负,true代表选中，false代表没选,status[0]代表选中胜，以此类推
    private boolean[] status = new boolean[34];
    // 竞彩足球比分玩法是否显示分数选择区
    private boolean jczqBfIsShown;
    // 显示分数button显示内容
    private String showStr = null;
    // 竞彩足球比分玩法 哪种类型比分（胜比分、平比分、负比分）
    private boolean[] type = new boolean[3];
    // 是否显示分析内容
    private boolean ifShowAnalyse;
    // 历史交锋
    private String[] hisCom = new String[3];
    // 平均赔率
    private String[] eveOdds = new String[3];
    // 两队排名
    private String[] rank = new String[2];

    public String[] getHisCom() {
        return hisCom;
    }

    public void setHisCom(String[] hisCom) {
        this.hisCom = hisCom;
    }

    public String getHisCom(int index) {
        return hisCom[index];
    }

    public void setHisCom(int index, String hisCom) {
        this.hisCom[index] = hisCom;
    }

    public String[] getEveOdds() {
        return eveOdds;
    }

    public void setEveOdds(String[] eveOdds) {
        this.eveOdds = eveOdds;
    }

    public String getEveOdds(int index) {
        return eveOdds[index];
    }

    public void setEveOdds(int index, String eveOdds) {
        this.eveOdds[index] = eveOdds;
    }

    public String[] getRank() {
        return rank;
    }

    public void setRank(String[] rank) {
        this.rank = rank;
    }

    public String getRank(int index) {
        return rank[index];
    }

    public void setRank(int index, String rank) {
        this.rank[index] = rank;
    }

    public boolean isIfShowAnalyse() {
        return ifShowAnalyse;
    }

    public void setIfShowAnalyse(boolean ifShowAnalyse) {
        this.ifShowAnalyse = ifShowAnalyse;
    }

    public boolean getType(int index) {
        return type[index];
    }

    public void setType(int index, boolean type) {
        this.type[index] = type;
    }

    public String getShowStr() {
        return showStr;
    }

    public void setShowStr(String showStr) {
        this.showStr = showStr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isJczqBfIsShown() {
        return jczqBfIsShown;
    }

    public void setJczqBfIsShown(boolean jczqBfIsShown) {
        this.jczqBfIsShown = jczqBfIsShown;
    }

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }

    public String getConcede() {
        return concede;
    }

    public void setConcede(String concede) {
        this.concede = concede;
    }

    public String[] getOdds() {
        return odds;
    }

    public void setOdds(String[] odds) {
        this.odds = odds;
    }

    public void setOdds(int index, String odds) {
        this.odds[index] = odds;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(String matchTime) {
        this.matchTime = matchTime;
    }

    public void setStatus(boolean[] status) {
        this.status = status;
    }

    public boolean getStatus(int index) {
        return status[index];
    }

    public void setStatus(int index, boolean status) {
        this.status[index] = status;
    }

    public void reverseStatus(int index) {
        if (status[index]) {
            status[index] = false;
        }
        else {
            status[index] = true;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(getId());
        parcel.writeString(getWeekDay());
        parcel.writeString(getIdBetNum());
        parcel.writeString(getMatchHomeTeamName());
        parcel.writeString(getMatchGuessTeamName());
        parcel.writeString(concede);
        parcel.writeStringArray(odds);
        parcel.writeString(endTime);
        parcel.writeString(matchTime);
        parcel.writeBooleanArray(status);
    }

    public static final Parcelable.Creator<SportsItem> CREATOR = new Creator<SportsItem>() {
        public SportsItem createFromParcel(Parcel source) {
            SportsItem item = new SportsItem();
            item.setId(source.readString());
            item.setWeekDay(source.readString());
            item.setIdBetNum(source.readString());
            item.setMatchHomeTeamName(source.readString());
            item.setMatchGuessTeamName(source.readString());
            item.setConcede(source.readString());
            source.readStringArray(item.odds);
            item.setEndTime(source.readString());
            item.setMatchTime(source.readString());
            source.readBooleanArray(item.status);
            return item;
        }

        public SportsItem[] newArray(int size) {
            return new SportsItem[size];
        }
    };
}
