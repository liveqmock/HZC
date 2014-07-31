package com.haozan.caipiao.lotteryrecord;

import java.util.ArrayList;

import com.haozan.caipiao.types.Ball;

public class JLK3Record {
    private ArrayList<Ball> record = new ArrayList<Ball>();
    private String kind = null;
    private int divide = 0; // 号码区间的分界

    public ArrayList<Ball> getRecord() {
        return record;
    }

    public void setRecord(String myBalls, Boolean open) {

        String[] rbballs = null;
        String[] ballsNum = null;

// String[] ballNumSection01;
// String[] ballNumSection02;

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
// if (myBalls.indexOf("\\|") == -1) {// 不是 108,109 二同号单选单式 二同号单选复式
// rbballs = myBalls.split("\\:");
// ballsNum = rbballs[0].split("\\,");
// kind = rbballs[2];
// for (int i = 0; i < ballsNum.length; i++) {
// Ball b = new Ball();
// b.setNumber(String.valueOf((ballsNum[i])));
// b.setColor("red");
// b.setState(false);
// b.setGroupIndex(i);
// record.add(b);
// }
// }
// else {// 108,109 二同号单选单式 二同号单选复式
// rbballs = myBalls.split("\\:");
// String[] recordNum = rbballs[0].split("\\|");
// ballNumSection01 = recordNum[0].split(",");
// ballNumSection02 = recordNum[1].split(",");
// divide = ballNumSection01.length;
// kind = rbballs[2];
//
// for (int i = 0; i < ballNumSection01.length; i++) {
// Ball b = new Ball();
// if (ballNumSection01[i].length() == 1) {
// ballNumSection01[i] = "0" + ballNumSection01[i];
// }
// b.setNumber(ballNumSection01[i]);
// b.setColor("red");
// b.setState(false);
// record.add(b);
// }
//
// for (int j = 0; j < ballNumSection02.length; j++) {
// Ball b = new Ball();
// if (ballNumSection02[j].length() == 1) {
// ballNumSection02[j] = "0" + ballNumSection02[j];
// }
// b.setNumber(ballNumSection02[j]);
// b.setColor("blue");
// b.setState(false);
// record.add(b);
// }
// }
            rbballs = myBalls.split("\\:");
            ballsNum = rbballs[0].split("\\,");
            kind = rbballs[2];

            if (kind.equals("103") || kind.equals("104") || kind.equals("105") || kind.equals("106") ||
                kind.equals("107") || kind.equals("108") || kind.equals("109") || kind.equals("116")) {
                for (int i = 0; i < ballsNum.length; i++) {
                    for (int j = 0; j < ballsNum[i].length(); j++) {
                        Ball b = new Ball();
                        b.setNumber(String.valueOf((ballsNum[i].charAt(j))));
                        b.setColor("red");
                        b.setState(false);
                        b.setGroupIndex(j);
                        b.setAardNumStart(0);
                        b.setAardNumEnd(2);
                        if (j == 0)
                            b.setDivideStart(true);
                        if (j == 2)
                            b.setDivideEnd(true);
                        record.add(b);
                    }
                }
            }
            else {
                for (int i = 0; i < ballsNum.length; i++) {
// for (int j = 0; j < ballsNum[i].length(); j++) {
                    Ball b = new Ball();
                    b.setNumber(ballsNum[i]);
                    b.setColor("red");
                    b.setState(false);
                    b.setGroupIndex(i);
                    if (i == 0)
                        b.setDivideStart(true);
                    if (i==ballsNum.length-1)
                        b.setDivideEnd(true);
                    record.add(b);
                }
            }
        }
    }

    public String getKind() {
        return kind;
    }

    public int getDivide() {
        return divide;
    }
}
