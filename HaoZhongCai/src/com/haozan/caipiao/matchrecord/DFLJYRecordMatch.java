package com.haozan.caipiao.matchrecord;

import java.util.ArrayList;

import com.haozan.caipiao.lotteryrecord.DFLJYRecord;
import com.haozan.caipiao.types.Ball;

public class DFLJYRecordMatch {

    public static ArrayList<Ball> isMatch(String betStr, String openStr) {
        DFLJYRecord bet = new DFLJYRecord();
        DFLJYRecord open = new DFLJYRecord();
        // 初始化投注记录和开奖记录
        bet.setRecord(betStr, false);
        open.setRecord(openStr, true);
        // 获取投注记录和开奖记录
        ArrayList<Ball> betData = bet.getRecord();
        ArrayList<Ball> openData = open.getRecord();

        // 基本球匹配

        for (int i = 0; i < bet.getDivide(); i++) {
            if (betData.get(i).getNumber().equals(openData.get(betData.get(i).getGroupIndex()).getNumber()))
                betData.get(i).setState(true);
        }

        // 生肖球匹配
        for (int i = bet.getDivide(); i < betData.size(); i++) {
            if (i >= 6) {
                if (betData.get(i).getNumber().equals(openData.get(6).getNumber()))
                    betData.get(i).setState(true);
            }
            else {
                if (betData.get(i).getNumber().equals(openData.get(betData.get(i).getGroupIndex()).getNumber()))
                    betData.get(i).setState(true);
            }
        }
        return betData;
    }
}
