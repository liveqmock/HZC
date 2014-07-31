package com.haozan.caipiao.types;

import android.os.Parcel;
import android.os.Parcelable;

public class SportsLQRFSFItem
    extends SportsMatchBasicItemData
    implements Parcelable {
    // 唯一id标示
    private String id;
    // 联盟
    private String league;
    // 让分数
    private String concede;
    // 各个赔率
    private String[] odds = new String[31];
    // 投注截止时间
    private String endTime;
    // 比赛开始时间
    private String matchTime;
    // 篮球让分胜负，是否选择胜负,true代表选中，false代表没选,status[0]代表选中胜，status[1]代表负
    private boolean[] status = new boolean[31];

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public static final Parcelable.Creator<SportsLQRFSFItem> CREATOR = new Creator<SportsLQRFSFItem>() {
        public SportsLQRFSFItem createFromParcel(Parcel source) {
            SportsLQRFSFItem item = new SportsLQRFSFItem();
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

        public SportsLQRFSFItem[] newArray(int size) {
            return new SportsLQRFSFItem[size];
        }
    };
}
