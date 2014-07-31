package com.haozan.caipiao.lotteryrecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.haozan.caipiao.types.Ball;

public class SYXWRecord {
    private ArrayList<Ball> record = new ArrayList<Ball>();
    private String kind = null;

    public ArrayList<Ball> getRecord() {
        return record;
    }

    private String[] xyx5TypeArray = {"11_RX1", "11_RX2", "11_RX3", "11_RX4", "11_RX5", "11_RX6", "11_RX7",
            "11_RX8", "11_ZXQ2_D", "11_ZXQ2_F", "11_ZXQ3_D", "11_ZXQ3_F", "11_ZXQ2", "11_ZXQ3"};

    public void setRecord(String myBalls, Boolean open) {
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < xyx5TypeArray.length; i++) {
            map.put(xyx5TypeArray[i], String.valueOf(i + 1));
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
            kind = map.get(rbballs[2]);
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
