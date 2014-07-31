package com.haozan.caipiao.activity.bet.jlks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import android.os.Bundle;

import com.haozan.caipiao.activity.bet.BetConfirm;
import com.haozan.caipiao.types.BetItem;
import com.haozan.caipiao.util.MathUtil;

/**
 * 新快三加单界面
 * 
 * @author Vincent
 * @create-time 2013-6-26 下午2:04:50
 */
public class JLKSBetConfirm
    extends BetConfirm {
    private static final int RAMDOM_MAX_NUM = 6;
    private static final String[] hzList1 = {"3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14",
            "15", "16", "17", "18"};
    private static final String[] sthList = {"111", "222", "333", "444", "555", "666"};

    private static final String[] eth_dx_thList = {"11", "22", "33", "44", "55", "66"};
    private static final String[] eth_dx_bthList = {"1", "2", "3", "4", "5", "6"};
    private static final String[] eth_fx_List = {"11*", "22*", "33*", "44*", "55*", "66*"};
    public static ArrayList<BetItem> betLocalList = new ArrayList<BetItem>();

    public JLKSBetConfirm() {
        super();
        betList = betLocalList;
        if (betList.size() > 0)
            lotteryType = betList.get(betList.size() - 1).getType();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (kind == null) {
            kind = "jlk3";
        }
        initInf();
    }

    @Override
    protected void initLastNumsArray() {
        for (int i = 0; i < betList.size(); i++) {
            addBetItemView(betList.get(i));
        }
    }

    @Override
    protected void initNumsArrray(String betOrgNum) {
        if (betOrgNum != null) {
            String[] lotteryArray = betOrgNum.split(";");
            for (int i = 0; i < lotteryArray.length; i++) {
                addBetList(lotteryArray[i]);
            }
            invalidateMoney();
        }
    }

// 把获取到的投注格式类型转换
    private int typeConversion(int type) {
        if (type == 101 || type == 102)
            return lotteryType = 1;
        else if (type == 103 || type == 104 || type == 105)
            return lotteryType = 2;
        else if (type == 106 || type == 107 || type == 108 || type == 109)
            return lotteryType = 3;
        else if (type == 110 || type == 111 || type == 116)
            return lotteryType = 4;
        else
            return lotteryType = 5;
    }

    private void addBetList(String lotteryInf) {
        String[] lotteryMode = lotteryInf.split("\\:");
        BetItem item = new BetItem();
        itemNum++;
        item.setId(itemNum);
        lotteryType = Integer.valueOf(lotteryMode[2]);
        typeConversion(lotteryType);
        item.setType(lotteryType);
        item.setCode(generateCode(lotteryInf));
        if (lotteryType == 1)
            item.setDisplayCode(getBallsDisplayHZInf(lotteryMode[0]));
        else if (lotteryType == 2)
            item.setDisplayCode(getBallsDisplaySTHInf(lotteryMode[0]));
        else if (lotteryType == 3)
            item.setDisplayCode(getBallsDisplayETHInf(lotteryMode[0]));
        else if (lotteryType == 4)
            item.setDisplayCode(getBallsDisplaySBTHInf(lotteryMode[0]));
        else if (lotteryType == 5)
            item.setDisplayCode(getBallsDisplayEBTHInf(lotteryMode[0]));
        item.setMoney(countMoney(lotteryInf));
        if (resource != null)
            if (resource.equals("bet_history")) {
                item.setMode("1011");
            }
            else if (resource.equals("bet_weibo")) {
                item.setMode("1012");
            }
        betList.add(item);
        addBetItemView(item);
    }

    private String getBallsDisplayHZInf(String lotteryMode) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[和值] ");
        betBallText.append(lotteryMode);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplaySTHInf(String lotteryMode) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        if (lotteryMode.equals("777")) {
            betBallText.append("[三同号通选] ");
            betBallText.append("三同号通选");
        }
        else {
            betBallText.append("[三同号] ");
            betBallText.append(lotteryMode);
        }
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplayETHInf(String lotteryMode) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[二同号] ");
        betBallText.append(lotteryMode);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplaySBTHInf(String lotteryMode) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        if (lotteryMode.equals("789")) {
            betBallText.append("[三连号通选] ");
            betBallText.append("三连号通选");
        }
        else {
            betBallText.append("[三不同号] ");
            betBallText.append(lotteryMode);
        }
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplayEBTHInf(String lotteryMode) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[二不同号] ");
        betBallText.append(lotteryMode);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private int findSameNum(String s1, String s2) {
        int n = 0;
        int length = s1.length();
        for (int i = 0; i < length; i++) {
            String s = s1.substring(i, i + 1);
            if (s2.contains(s))
                n++;
        }
        return n;
    }

    private long countMoney(String lotteryInf) {
        long betNumber = 0;
        String[] lotteryMode = lotteryInf.split("\\:");
        String[] nums = lotteryMode[0].split(",");

        if (lotteryType == 1 || lotteryType == 2) {
            betNumber = nums.length;
        }
        else if (lotteryType == 3) {
            if (lotteryMode[0].indexOf("|") < 0) {
                betNumber = nums.length;
            }
            else {
                String[] subCodes = lotteryMode[0].split("\\|");
                int num1 = subCodes[0].split(",").length;
                int num2 = subCodes[1].split(",").length;
                betNumber = num1 * num2;
            }
        }
        else if (lotteryType == 4) {
            int length = lotteryMode[0].split(",").length;
            if (length == 1) {
                betNumber = 1;
            }
            else {
                betNumber = MathUtil.combination((int) length, 3);
            }
        }
        else if (lotteryType == 5) {
            int length = lotteryMode[0].split(",").length;
            betNumber = MathUtil.combination((int) length, 2);
        }
        return betNumber * 2;
    }

    // 机选
    @Override
    protected BetItem getRandomItem(int id) {
        if (lotteryType == 1)
            return jlksHZBetItem(id);
        else if (lotteryType == 2)
            return jlksSTHBetItem(id);
        else if (lotteryType == 3)
            return jlksETHBetItem(id);
        else if (lotteryType == 4)
            return jlksSBTHBetItem(id);
        else
            return jlksEBTHBetItem(id);
    }

    private BetItem jlksHZBetItem(int id) {
        BetItem item = new BetItem();
        Random rd = new Random();
        int num01 = rd.nextInt(RAMDOM_MAX_NUM);
        int num02 = rd.nextInt(RAMDOM_MAX_NUM);
        int num03 = rd.nextInt(RAMDOM_MAX_NUM);
        int num = num01 + num02 + num03;
        StringBuilder betBallText = new StringBuilder();
        betBallText.append(hzList1[num]);
        item.setLuckyNum(betBallText.toString());
        betBallText.append(":2:101:");
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[和值] ");
        betDisplayBallText.append(hzList1[num]);
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        item.setMoney(2);
        item.setId(id);
        item.setType(1);
        item.setMode("1005");
        return item;
    }

    public BetItem jlksSTHBetItem(int id) {
        BetItem item = new BetItem();
        StringBuilder betBallText = new StringBuilder();
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        Random rd = new Random();
        int flag = rd.nextInt(2);
        if (flag == 0) {
            int num = rd.nextInt(RAMDOM_MAX_NUM);
            betBallText.append(sthList[num]);
            item.setLuckyNum(betBallText.toString());
            betBallText.append(":2:104:");
            betDisplayBallText.append("[三同号] ");
            betDisplayBallText.append(sthList[num]);
        }
        else {
            betBallText.append("777");
            item.setLuckyNum(betBallText.toString());
            betBallText.append(":2:103:");
            betDisplayBallText.append("[三同号通选] ");
            betDisplayBallText.append("三同号通选");
        }
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        item.setCode(betBallText.toString());
        item.setMoney(2);
        item.setId(id);
        item.setType(2);
        item.setMode("1005");
        return item;
    }

    public BetItem jlksETHBetItem(int id) {
        BetItem item = new BetItem();
        StringBuilder betBallText = new StringBuilder();
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[二同号] ");
        Random rd = new Random();
        int flag = rd.nextInt(2);
        if (flag == 0) {
            int[] temp = MathUtil.getRandomNumNotEquals(2, RAMDOM_MAX_NUM);
            Arrays.sort(temp);
            betBallText.append(eth_dx_thList[temp[0]]);
            betBallText.append("|");
            betBallText.append(eth_dx_bthList[temp[1]]);
            item.setLuckyNum(betBallText.toString());
            betBallText.append(":2:108:");
            betDisplayBallText.append(eth_dx_thList[temp[0]]);
            betDisplayBallText.append("|");
            betDisplayBallText.append(eth_dx_bthList[temp[1]]);

        }
        else {
            int num = rd.nextInt(RAMDOM_MAX_NUM);
            betBallText.append(eth_dx_thList[num]);
            item.setLuckyNum(betBallText.toString());
            betBallText.append(":2:106:");
            betDisplayBallText.append(eth_fx_List[num]);
        }
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        item.setCode(betBallText.toString());
        item.setMoney(2);
        item.setId(id);
        item.setType(3);
        item.setMode("1005");
        return item;
    }

    public BetItem jlksSBTHBetItem(int id) {
        BetItem item = new BetItem();
        StringBuilder betBallText = new StringBuilder();
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        Random rd = new Random();
        int flag = rd.nextInt(2);
        if (flag == 0) {
            int[] temp = MathUtil.getRandomNumNotEquals(3, RAMDOM_MAX_NUM);
            Arrays.sort(temp);
            betDisplayBallText.append("[三不同号] ");
            for (int i = 0; i < temp.length; i++) {
                betBallText.append(eth_dx_bthList[temp[i]]);
                betBallText.append(",");
                betDisplayBallText.append(eth_dx_bthList[temp[i]]);
                betDisplayBallText.append(",");
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
            betDisplayBallText.deleteCharAt(betDisplayBallText.length() - 1);
            item.setLuckyNum(betBallText.toString());
            betBallText.append(":2:110:");
        }
        else {
            betBallText.append("789");
            item.setLuckyNum(betBallText.toString());
            betBallText.append(":2:116:");
            betDisplayBallText.append("[三连号通选] ");
            betDisplayBallText.append("三连号通选");
        }
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        item.setCode(betBallText.toString());
        item.setMoney(2);
        item.setId(id);
        item.setType(4);
        item.setMode("1005");
        return item;
    }

    public BetItem jlksEBTHBetItem(int id) {
        BetItem item = new BetItem();
        int[] temp = MathUtil.getRandomNumNotEquals(2, RAMDOM_MAX_NUM);
        Arrays.sort(temp);
        StringBuilder betBallText = new StringBuilder();
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[二不同号] ");
        for (int i = 0; i < 2; i++) {
            betBallText.append(temp[i]);
            betBallText.append(",");
            betDisplayBallText.append(temp[i]);
            betDisplayBallText.append(",");
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        item.setLuckyNum(betBallText.toString());
        betBallText.append(":2:113:");
        item.setCode(betBallText.toString());
        betDisplayBallText.deleteCharAt(betDisplayBallText.length() - 1);
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        item.setMoney(2);
        item.setId(id);
        item.setType(5);
        item.setMode("1005");
        return item;
    }

    @Override
    protected void checkExit() {
        if (betList.size() > 0)
            betLocalList = betList;
        finish();
    }
}
