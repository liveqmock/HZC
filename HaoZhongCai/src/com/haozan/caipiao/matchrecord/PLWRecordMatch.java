package com.haozan.caipiao.matchrecord;

import java.util.ArrayList;

import com.haozan.caipiao.lotteryrecord.SDRecord;
import com.haozan.caipiao.types.Ball;

public class PLWRecordMatch {
    public static ArrayList<Ball> isMatch(String betStr, String openStr) {
        SDRecord bet = new SDRecord();
        SDRecord open = new SDRecord();
        // 初始化投注记录和开奖记录
        bet.setRecord(betStr, false);
        open.setRecord(openStr, true);
        // 获取投注记录和开奖记录
        ArrayList<Ball> betData = bet.getRecord();
        ArrayList<Ball> openData = open.getRecord();

        if (bet.getKind().equals("1")) { // 直选
            for (int i = 0; i < betData.size(); i++) {
                if (betData.get(i).getNumber().equals(openData.get(betData.get(i).getGroupIndex()).getNumber()))
                    betData.get(i).setState(true);
            }
        }
        if (bet.getKind().equals("2") || bet.getKind().equals("3")) { // 组选三、组选六
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
