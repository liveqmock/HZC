package com.haozan.caipiao.lotteryrecord;

import java.util.ArrayList;

import com.haozan.caipiao.types.Ball;

public class SSQRecord {

    private int divide = 0; // 红球和篮球的分界
    private ArrayList<Ball> record = new ArrayList<Ball>();

    public ArrayList<Ball> getRecord() {
        return record;
    }

    public void setRecord(String myBalls, Boolean open) {
        String[] rbballs = null;
        String[] reds = null;
        String[] blues = null;

        if (open) {
            rbballs = myBalls.split("\\|");
            reds = rbballs[0].split(",");
            blues = rbballs[1].split(",");
        }
        else {
            rbballs = myBalls.split("\\:");
            String[] record = rbballs[0].split("\\|");
            reds = record[0].split(",");
            blues = record[1].split(",");
        }

        divide = reds.length;
        for (int i = 0; i < reds.length; i++) {
            Ball b = new Ball();
            if (reds[i].length() == 1) {
                reds[i] = "0" + reds[i];
            }
            b.setNumber(reds[i]);
            b.setColor("red");
            b.setState(false);
            record.add(b);
        }

        for (int j = 0; j < blues.length; j++) {
            Ball b = new Ball();
            if (blues[j].length() == 1) {
                blues[j] = "0" + blues[j];
            }
            b.setNumber(blues[j]);
            b.setColor("blue");
            b.setState(false);
            record.add(b);
        }
    }

    public int getDivide() {
        return divide;
    }
}
