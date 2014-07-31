package com.haozan.caipiao.types;

import java.util.ArrayList;

public class LotteryBallsData {
    //the digit of the ball
    private String ballType;
    //check whether the ball is choosed
    private ArrayList<Boolean> balls;
    //the count of the ball selected
    private int count;
    public String getBallType() {
        return ballType;
    }
    public void setBallType(String ballType) {
        this.ballType = ballType;
    }
    public ArrayList<Boolean> getBalls() {
        return balls;
    }
    public void setBalls(ArrayList<Boolean> balls) {
        this.balls = balls;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
}
