package com.haozan.caipiao.matchrecord;

import java.util.ArrayList;

import com.haozan.caipiao.lotteryrecord.HNKLSFRecord;
import com.haozan.caipiao.types.Ball;

public class HNKLSFRecordMatch {

    public static ArrayList<Ball> isMatch(String betStr, String openStr) {
        HNKLSFRecord bet = new HNKLSFRecord();
        HNKLSFRecord open = new HNKLSFRecord();
        // 初始化投注记录和开奖记录
        bet.setRecord(betStr, false);
        open.setRecord(openStr, true);
        // 获取投注记录和开奖记录
        ArrayList<Ball> betData = bet.getRecord();
        ArrayList<Ball> openData = open.getRecord();

        if (bet.getKind().equals("4") || bet.getKind().equals("7")) { // 直选

            for (int i = 0; i < betData.size(); i++) {
                for (int j = 0; j < openData.size(); j++) {
//                    if (betData.get(i).getNumber().equals(openData.get(betData.get(i).getGroupIndex()).getNumber()))
//                        betData.get(i).setState(true);
                    if (betData.get(i).getNumber().equals(openData.get(j).getNumber())) {
                        betData.get(i).setState(true);
                    }
                }
            }
        }

        if (bet.getKind().equals("1") || bet.getKind().equals("2") || bet.getKind().equals("3") ||
            bet.getKind().equals("5") || bet.getKind().equals("6") || bet.getKind().equals("8") ||
            bet.getKind().equals("9") || bet.getKind().equals("10")) { // 组选三、组选六
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
