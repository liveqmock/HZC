package com.haozan.caipiao.matchrecord;

import java.util.ArrayList;

import com.haozan.caipiao.lotteryrecord.JLK3Record;
import com.haozan.caipiao.types.Ball;

public class JLK3RecordMatch {

    public static ArrayList<Ball> isMatch(String betStr, String openStr) {
        JLK3Record bet = new JLK3Record();
        JLK3Record open = new JLK3Record();
        // 初始化投注记录和开奖记录
        bet.setRecord(betStr, false);
        open.setRecord(openStr, true);
        // 获取投注记录和开奖记录
        ArrayList<Ball> betData = bet.getRecord();
        ArrayList<Ball> openData = open.getRecord();

        if (bet.getKind().equals("103") || bet.getKind().equals("101") || bet.getKind().equals("102") ||
            bet.getKind().equals("104") || bet.getKind().equals("105") || bet.getKind().equals("106") ||
            bet.getKind().equals("107") || bet.getKind().equals("108") || bet.getKind().equals("109") ||
            bet.getKind().equals("116")) { 
// String[] jlk3OpenNumArray = openStr.split("\\,");
// String[] jlk3BetNumArray = betStr.split("\\,");
            for (int j = 0; j < betData.size(); j++) {
// for (int i = 0; i < openData.size(); i++) {
                if (betData.get(j).getNumber().equals(openData.get(betData.get(j).getGroupIndex()).getNumber())) {
                    betData.get(j).setState(true);
                }
// }
            }
        }

// if (bet.getKind().equals("116")) { // 三连号通选
// String[] jlk3OpenNumArray = openStr.split("\\,");
// String[] jlk3BetNumArray = betStr.split("\\,");
// for (int i = 0; i < jlk3BetNumArray.length; i++) {
// for (int j = 0; j <= jlk3OpenNumArray.length; j++) {
// if (String.valueOf(jlk3BetNumArray[i].charAt(j)).equals(jlk3OpenNumArray[j])) {
// betData.get(i).setState(true);
// }
// }
// }
// }
//
// if (bet.getKind().equals("101") || bet.getKind().equals("102") || bet.getKind().equals("104") ||
// bet.getKind().equals("105") || bet.getKind().equals("106") || bet.getKind().equals("107") ||
// bet.getKind().equals("108") || bet.getKind().equals("109")) {//
// 和值单式，和值复式，三同号单选单式，三同号单选复式，二同号复选单式，二同号复选复式，三不同号单式，三不同号复式，二不同号单式，二不同号复式
// for (int i = 0; i < openData.size(); i++) {
// for (int j = 0; j < betData.size(); j++) {
// if (betData.get(j).getNumber().equals(openData.get(i).getNumber())) {
// betData.get(j).setState(true);
// }
// }
// }
// }

// if (bet.getKind().equals("108") || bet.getKind().equals("109")) {
// // 区间1
// for (int i = 0; i < openData.size(); i++) {
// for (int j = 0; j < bet.getDivide(); j++) {
// if (betData.get(j).getNumber().equals(openData.get(i).getNumber())) {
// betData.get(j).setState(true);
// break;
// }
// }
// }
//
// // 区间2
// for (int i = 0; i < openData.size(); i++) {
// for (int j = bet.getDivide(); j < betData.size(); j++) {
// if (betData.get(j).getNumber().equals(openData.get(i).getNumber())) {
// betData.get(j).setState(true);
// break;
// }
// }
// }
// }

        if (bet.getKind().equals("110") || bet.getKind().equals("111") || bet.getKind().equals("113") ||
            bet.getKind().equals("114")) {
            for (int i = 0; i < openData.size(); i++) {
                for (int j = 0; j < betData.size(); j++) {
                    if (betData.get(j).getNumber().equals(openData.get(i).getNumber())) {
                        betData.get(j).setState(true);
                    }
                }
            }
        }
        return betData;
    }
}
