package com.haozan.caipiao.matchrecord;

import java.util.ArrayList;

import com.haozan.caipiao.lotteryrecord.SYXWRecord;
import com.haozan.caipiao.types.Ball;

public class SYXRecordMatch {

    public static ArrayList<Ball> isMatch(String betStr, String openStr) {
        SYXWRecord bet = new SYXWRecord();
        SYXWRecord open = new SYXWRecord();
        // 初始化投注记录和开奖记录
        bet.setRecord(betStr, false);
        open.setRecord(openStr, true);
        // 获取投注记录和开奖记录
        ArrayList<Ball> betData = bet.getRecord();
        ArrayList<Ball> openData = open.getRecord();

        if (bet.getKind().equals("11") || bet.getKind().equals("9")||bet.getKind().equals("10") || bet.getKind().equals("12")) { // 直选
            for (int i = 0; i < betData.size(); i++) {
                if (betData.get(i).getNumber().equals(openData.get(betData.get(i).getGroupIndex()).getNumber()))
                    betData.get(i).setState(true);
            }
        }

        if (bet.getKind().equals("1") || bet.getKind().equals("2") || bet.getKind().equals("3") ||
            bet.getKind().equals("4") || bet.getKind().equals("5") || bet.getKind().equals("6") ||
            bet.getKind().equals("7") || bet.getKind().equals("8") || bet.getKind().equals("13") ||
            bet.getKind().equals("14")) { // 组选三、组选六
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
