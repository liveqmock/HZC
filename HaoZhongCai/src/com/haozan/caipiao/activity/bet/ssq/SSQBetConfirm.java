package com.haozan.caipiao.activity.bet.ssq;

import java.util.ArrayList;
import java.util.Arrays;

import android.os.Bundle;

import com.haozan.caipiao.activity.bet.BetConfirm;
import com.haozan.caipiao.types.BetItem;
import com.haozan.caipiao.util.MathUtil;
import com.haozan.caipiao.util.StringUtil;

public class SSQBetConfirm
    extends BetConfirm {

    public static ArrayList<BetItem> betLocalList = new ArrayList<BetItem>();

    public SSQBetConfirm() {
        super();
        betList = betLocalList;
        if (betList.size() > 0)
            lotteryType = betList.get(betList.size() - 1).getType();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (kind == null) {
            kind = "ssq";
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
                BetItem item = new BetItem();
                itemNum++;
                item.setId(itemNum);
                item.setCode(generateCode(lotteryArray[i]));
                String[] lotteryMode = lotteryArray[i].split("\\:");
                int type = Integer.valueOf(lotteryMode[2]);
                if (type == 5) {
                    item.setType(2);
                    item.setDisplayCode(getDantuoSSQDispalyInf(lotteryMode[0]));
                    item.setMoney(countDantuoWayMoney(lotteryMode[0]));
                }
                else {
                    item.setType(1);
                    String[] nums = lotteryMode[0].split("\\|");
                    String[] redBalls = nums[0].split("\\,");
                    String[] blueBalls = nums[1].split("\\,");
                    item.setDisplayCode(getNormalSSQDispalyInf(redBalls, blueBalls));
                    item.setMoney(countNormalWayMoney(redBalls.length, blueBalls.length));
                }
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
            invalidateMoney();
        }
    }

    // 计算一组普通投注方式的双色球号码的金额
    private long countNormalWayMoney(int hongNumber, int lanNumber) {
        long betNumber = 0;
        if (hongNumber > 7 && lanNumber >= 1) {
            betNumber =
                MathUtil.factorial(hongNumber, hongNumber - 6) /
                    MathUtil.factorial(hongNumber - 6, hongNumber - 7) * lanNumber;
        }
        else if (hongNumber == 6 && lanNumber >= 1) {
            betNumber = lanNumber;
        }
        else if (hongNumber == 7 && lanNumber >= 1) {
            betNumber = MathUtil.factorial(hongNumber, hongNumber - 6) * lanNumber;
        }
        return betNumber * 2;
    }

    // 计算一组胆拖方式的双色球号码的金额
    private long countDantuoWayMoney(String betCode) {
        String[] lotteryNum = betCode.split("\\|");
        String[] lotteryRed = lotteryNum[0].split("\\$");
        String[] dan = lotteryRed[0].split(",");
        String[] tuo = lotteryRed[1].split(",");
        String[] blue = lotteryNum[1].split(",");
        long betNumber = 0;
        betNumber = MathUtil.combination(tuo.length, 6 - dan.length) * blue.length;
        return betNumber * 2;
    }

    // 双色球普通玩法，选球后显示选球的信息
    private String getNormalSSQDispalyInf(String[] redBalls, String[] blueBalls) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        int redLength = redBalls.length;
        for (int i = 0; i < redLength; i++) {
            betBallText.append(redBalls[i] + ",");
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("</font>");

        betBallText.append("<font color='blue'>");
        int lanLength = blueBalls.length;
        if (lanLength != 0) {
            betBallText.append("|");
            for (int i = 0; i < lanLength; i++) {
                betBallText.append(blueBalls[i] + ",");
            }
            betBallText.deleteCharAt(betBallText.length() - 1);
            betBallText.append("</font>");
        }
        return betBallText.toString();
    }

    // 双色球胆拖玩法，选球后显示选球的信息
    private String getDantuoSSQDispalyInf(String betCode) {
        StringBuilder betBallText = new StringBuilder();
        String[] lotteryNum = betCode.split("\\|");
        String[] lotteryRed = lotteryNum[0].split("\\$");
        betBallText.append("<font color='red'>[胆](");
        betBallText.append(lotteryRed[0]);
        betBallText.append(")");
        betBallText.append(lotteryRed[1]);
        betBallText.append("</font>");

        betBallText.append("<font color='blue'>|");
        betBallText.append(lotteryNum[1]);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    @Override
    protected BetItem getRandomItem(int id) {
        BetItem item = new BetItem();
        int[] randomRedNum =
            MathUtil.getRandomNumNotEquals(SSQActivity.SSQ_HONGQIU_MIN,
                                               SSQActivity.SSQ_HONGQIU_LENGTH);
        Arrays.sort(randomRedNum);
        int[] randomBlueNum =
            MathUtil.getRandomNumNotEquals(SSQActivity.SSQ_LANQIU_MIN, SSQActivity.SSQ_LANQIU_LENGTH);
        StringBuilder betBallText = new StringBuilder();
        int redLength = SSQActivity.SSQ_HONGQIU_MIN;
        for (int i = 0; i < redLength; i++) {
            betBallText.append(StringUtil.betDataTransite(randomRedNum[i] + 1,
                                                          SSQActivity.SSQ_HONGQIU_LENGTH));
            betBallText.append(",");
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("|");

        int lanLength = SSQActivity.SSQ_LANQIU_MIN;
        for (int i = 0; i < lanLength; i++) {
            betBallText.append(StringUtil.betDataTransite(randomBlueNum[i] + 1,
                                                          SSQActivity.SSQ_LANQIU_LENGTH));
            betBallText.append(",");
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        item.setLuckyNum(betBallText.toString());
        betBallText.append(":1:1:");
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        for (int i = 0; i < redLength; i++) {
            betDisplayBallText.append(StringUtil.betDataTransite(randomRedNum[i] + 1,
                                                                 SSQActivity.SSQ_HONGQIU_LENGTH));
            betDisplayBallText.append(",");
        }
        betDisplayBallText.deleteCharAt(betDisplayBallText.length() - 1);
        betDisplayBallText.append("</font>");

        betDisplayBallText.append("<font color='blue'>");
        betDisplayBallText.append("|");
        for (int i = 0; i < lanLength; i++) {
            betDisplayBallText.append(StringUtil.betDataTransite(randomBlueNum[i] + 1,
                                                                 SSQActivity.SSQ_LANQIU_LENGTH));
            betDisplayBallText.append(",");
        }
        betDisplayBallText.deleteCharAt(betDisplayBallText.length() - 1);
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        item.setMoney(2);
        item.setId(id);
        item.setType(1);
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
