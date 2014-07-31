package com.haozan.caipiao.matchrecord;

import java.util.ArrayList;

import com.haozan.caipiao.lotteryrecord.SFCRecord;
import com.haozan.caipiao.types.Ball;

public class SFCRecordMatch {

    public static ArrayList<Ball> isMatch(String betStr, String openStr) {
        SFCRecord bet = new SFCRecord();
        SFCRecord open = new SFCRecord();
        // 初始化投注记录和开奖记录
        bet.setRecord(betStr, false);
        open.setRecord(openStr, true);
        // 获取投注记录和开奖记录
        ArrayList<Ball> betData = bet.getRecord();
        ArrayList<Ball> openData = open.getRecord();

        for (int i = 0; i < betData.size(); i++) {
            if (betData.get(i).getNumber().equals(openData.get(betData.get(i).getGroupIndex()).getNumber()))
                betData.get(i).setState(true);
            else if (openData.get(betData.get(i).getGroupIndex()).getNumber().equals("*"))
                betData.get(i).setState(true);
        }
        return betData;
    }

}
