package com.haozan.caipiao.types;

/**
 * the preferences of each editText of the lottery to display the ball
 * 
 * @author Administrator
 */
public class BallPre {

    // kind equals 0 means the ball is number,equals 1 means the ball is the any of the twelve animals
    private int kind;
    // the number to start
    private int startNumber;
    // the count of the ball
    private int count;
    // the color of the ball,0 means red,1 means blue
    private int color;
    //the unchoosen ball color
    private int initColor;
    
    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public int getStartNumber() {
        return startNumber;
    }

    public void setStartNumber(int startNumber) {
        this.startNumber = startNumber;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
    
    public int getInitColor() {
        return initColor;
    }

    public void setInitColor( int initColor) {
        this.initColor = initColor;
    }

}
