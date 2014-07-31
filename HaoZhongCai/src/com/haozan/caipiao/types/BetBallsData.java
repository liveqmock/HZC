package com.haozan.caipiao.types;

import java.util.ArrayList;

public class BetBallsData {
    // 属于哪组球
    private int ballType;//
    // 每个球的信息
    private ArrayList<BetBall> betBalls;
    // 选中球个数
    private int count;
    // 球显示的颜色，red代表红色，blue代表蓝色
    private String color;
    // 球内字体颜色
    private String textColor;

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    // 最多允许选择几个球
    private int limit;

    public int getBallType() {
        return ballType;
    }

    public void setBallType(int ballType) {
        this.ballType = ballType;
    }

    public ArrayList<BetBall> getBetBalls() {
        return betBalls;
    }

    public void setBetBalls(ArrayList<BetBall> betBalls) {
        this.betBalls = betBalls;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
