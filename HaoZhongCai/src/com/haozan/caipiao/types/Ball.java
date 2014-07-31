package com.haozan.caipiao.types;

import android.os.Parcel;
import android.os.Parcelable;

public class Ball
    implements Parcelable {
    private static final long serialVersionUID = 11212L;
    private String number;
    private String color;
    private boolean state;
    private int groupIndex;
    private String danTuo;
    private int awardNumStartNum = -1;
    private int awardNumEndNum = -1;
    private boolean diviedStart=false;
    private boolean diviedEnd=false;

    public int getGroupIndex() {
        return groupIndex;
    }

    public void setGroupIndex(int groupIndex) {
        this.groupIndex = groupIndex;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String string) {
        this.number = string;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getDantou() {
        return danTuo;
    }

    public void setDantuo(String danTuo) {
        this.danTuo = danTuo;
    }
    
    public int getAardNumStart() {
        return awardNumStartNum;
    }

    public void setAardNumStart(int awardNumStartNum) {
        this.awardNumStartNum = awardNumStartNum;
    }
    
    public int getAardNumEnd() {
        return awardNumEndNum;
    }

    public void setAardNumEnd(int awardNumEndNum) {
        this.awardNumEndNum = awardNumEndNum;
    }

    public boolean getDevideStart() {
        return diviedStart;
    }

    public void setDivideStart(boolean diviedStart) {
        this.diviedStart = diviedStart;
    }
    
    public boolean getDevideEnd() {
        return diviedEnd;
    }

    public void setDivideEnd(boolean diviedEnd) {
        this.diviedEnd = diviedEnd;
    }
    
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(number);
        dest.writeString(color);
        if (state)
            dest.writeInt(1);
        else
            dest.writeInt(0);
        dest.writeInt(groupIndex);
    }

    // 实现Parcelable接口的类型中，必须有一个实现了Parcelable.Creator接口的静态常量成员字段，
    // 并且它的名字必须为CREATOR的
    public static final Creator<Ball> CREATOR = new Creator<Ball>() {
        // From Parcelable.Creator
        @Override
        public Ball createFromParcel(Parcel in) {
            Ball b = new Ball();
            // 从包裹中读出数据
            b.number = in.readString();
            b.color = in.readString();
            if (in.readInt() == 0)
                b.state = false;
            else
                b.state = true;
            b.groupIndex = in.readInt();
            return b;
        }

        @Override
        public Ball[] newArray(int size) {
            return new Ball[size];
        }

    };
}
