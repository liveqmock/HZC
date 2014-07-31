package com.haozan.caipiao.matchrecord;

import java.util.ArrayList;

import com.haozan.caipiao.lotteryrecord.CQSSCRecord;
import com.haozan.caipiao.types.Ball;

public class CQSSCRecordMatch {

    public static ArrayList<Ball> isMatch(String betStr, String openStr) {
        CQSSCRecord bet = new CQSSCRecord();
        CQSSCRecord open = new CQSSCRecord();
        // 初始化投注记录和开奖记录
        bet.setRecord(betStr, false);
        open.setRecord(openStr, true);
        // 获取投注记录和开奖记录
        ArrayList<Ball> betData = bet.getRecord();
        ArrayList<Ball> openData = open.getRecord();

        String kind = bet.getKind();
        int openSize = openData.size();
        int betSize = betData.size();

        if (kind.equals("361") || kind.equals("362") || kind.equals("331") || kind.equals("332") ||
            kind.equals("211") || kind.equals("217") || kind.equals("901") || kind.equals("902") ||
            kind.equals("911") || kind.equals("912")) {// 三星组六单式，三星组六复式,三星组三单式,三星组三复式,二星组选单式,二星组选组合，任选一单式，任选一复式，任选二单式，任选二复式
            for (int i = getStart(kind, openSize); i < openData.size(); i++) {
                for (int j = 0; j < betSize; j++) {
                    if (betData.get(j).getNumber().equals(openData.get(i).getNumber()))
                        betData.get(j).setState(true);
                }
            }
        }

        if (kind.equals("511") || kind.equals("512") || kind.equals("501") || kind.equals("502") ||
            kind.equals("301") || kind.equals("302") || kind.equals("201") || kind.equals("202") ||
            kind.equals("701") || kind.equals("401") || kind.equals("402") || kind.equals("101") ||
            kind.equals("102")) {// 五星通选,五星通选复式,五星单式,五星复式,三星单式,三星复式,二星单式,二星复式,大小单双,四星单式,四星复式,一星单式,一星复式

            int startSize = getStart(kind, openSize);
            for (int i = startSize; i < openSize; i++) {
                for (int j = 0; j < betSize; j++) {
                    if ((betData.get(j).getGroupIndex() + startSize) == i) {
                        if (betData.get(j).getNumber().equals(openData.get(i).getNumber()))
                            betData.get(j).setState(true);
                    }
                }
            }
        }
        return betData;
    }

    private static int getStart(String kind, int size) {
        if (kind.equals("101") || kind.equals("102"))// 一星
            return size - 1;
        else if (kind.equals("211") || kind.equals("217") || kind.equals("201") || kind.equals("202"))// 二星
            return size - 2;
        else if (kind.equals("361") || kind.equals("362") || kind.equals("331") || kind.equals("332") ||
            kind.equals("301") || kind.equals("302")||kind.equals("701"))// 三星 ,大小单双
            return size - 3;
        if (kind.equals("401") || kind.equals("402"))// 四星
            return size - 4;
        else if (kind.equals("511") || kind.equals("512") || kind.equals("501") || kind.equals("502"))// 五星
            return 0;
        return 0;
    }
}
