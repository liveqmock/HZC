package com.haozan.caipiao.lotteryrecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.haozan.caipiao.types.Ball;

public class HNKLSFRecord {
    private ArrayList<Ball> record = new ArrayList<Ball>();
    private String kind = null;

    public ArrayList<Ball> getRecord() {
        return record;
    }

    private String[] hnklsfTypeArray = {"101", "102", "20", "22", "21", "30", "34", "33", "40", "50"};

    public void setRecord(String myBalls, Boolean open) {
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < hnklsfTypeArray.length; i++) {
            map.put(hnklsfTypeArray[i], String.valueOf(i + 1));
        }

        String[] rbballs = null;
        String[] ballsNum = null;

        if (open) {// 设置开奖记录
            ballsNum = myBalls.split("\\,");
            for (int i = 0; i < ballsNum.length; i++) {
                Ball b = new Ball();
                b.setNumber(ballsNum[i]);
                b.setColor("red");
                b.setState(false);
                b.setGroupIndex(i);
                record.add(b);
            }
        }
        else { // 设置投注记录
            rbballs = myBalls.split("\\:");
            if (rbballs[0].indexOf("|") == -1)
                ballsNum = rbballs[0].split("\\,");
            else
                ballsNum = rbballs[0].split("\\|");
            if (rbballs[2].equals("101"))
                kind = map.get(rbballs[2]);
            else if (rbballs[2].equals("102"))
                kind = map.get(rbballs[2]);
            else
                kind = map.get(rbballs[2].substring(0, 2));
            for (int i = 0; i < ballsNum.length; i++) {
                Ball b = new Ball();
                b.setNumber(String.valueOf((ballsNum[i])));
                b.setColor("red");
                b.setState(false);
                b.setGroupIndex(i);
                record.add(b);
            }
        }
    }

    public String getKind() {
        return kind;
    }
}
