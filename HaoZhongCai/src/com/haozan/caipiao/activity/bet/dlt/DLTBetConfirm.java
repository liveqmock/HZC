package com.haozan.caipiao.activity.bet.dlt;

import java.util.ArrayList;
import java.util.Arrays;

import android.os.Bundle;

import com.haozan.caipiao.activity.bet.BetConfirm;
import com.haozan.caipiao.types.BetItem;
import com.haozan.caipiao.util.MathUtil;
import com.haozan.caipiao.util.StringUtil;
import com.haozan.caipiao.util.ViewUtil;

public class DLTBetConfirm
    extends BetConfirm {
    public static ArrayList<BetItem> betLocalList = new ArrayList<BetItem>();
    private String betOrgCode;

    public DLTBetConfirm() {
        super();
        betList = betLocalList;
        if (betList.size() > 0)
            lotteryType = betList.get(betList.size() - 1).getType();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (kind == null) {
            kind = "dlt";
        }
        initInf();
        setLotteryWay();
    }

    private void setLotteryWay() {
        if (lotteryType == 1) {
            databaseData.putString("dlt_way", "dlt_stanbdar");
            databaseData.commit();
        }
        else if (lotteryType == 2) {
            databaseData.putString("dlt_way", "dlt_dantuo");
            databaseData.commit();
        }
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
                betOrgCode = lotteryArray[i];// 04,12,17,33,34|06,08:1:1:1
                if (isGetDltNormalType()) {
                    BetItem item = new BetItem();
                    itemNum++;
                    item.setId(itemNum);
                    item.setCode(generateCode(lotteryArray[i]));
                    String[] lotteryMode = lotteryArray[i].split("\\:");// [04,12,17,33,34|06,08, 1, 1, 1]
                    String[] nums = lotteryMode[0].split("\\|");
                    if (!isGetDltDantuoType()) {
                        String[] redBalls = nums[0].split("\\,");
                        String[] blueBalls = {};
                        if (isGetDltNormalType())
                            blueBalls = nums[1].split("\\,");
                        item.setDisplayCode(getBallsDisplayInf(redBalls, blueBalls));
                        item.setMoney(countMoney(redBalls.length, blueBalls.length));
                    }
                    // 胆拖
                    else {
                        String[] redBalls = nums[0].split("\\$");
                        String[] redBallDan = redBalls[0].split("\\,");
                        String[] redBallTuo = redBalls[1].split("\\,");

                        String[] blueBall = nums[1].split("\\$");
// String[] blueBallDan = blueBall[0].split("\\,");
                        String[] blueBallTuo = blueBall[1].split("\\,");

                        item.setDisplayCode(getBallsDisplayInf(redBallDan, redBallTuo, blueBall[0],
                                                               blueBallTuo));
                        if (blueBall[0] != null && !blueBall[0].equals("")) {
                            item.setMoney(countMoney(redBallDan.length, redBallTuo.length, 1,
                                                     blueBallTuo.length));
                        }
                        else {
                            item.setMoney(countMoney(redBallDan.length, redBallTuo.length, 0,
                                                     blueBallTuo.length));
                        }

// item.setMoney(countMoney(redBallDan.length, redBallTuo.length, blueBall[0].length() - 1,
// blueBallTuo.length));
                    }

                    if (isGetDltDantuoType())
                        item.setType(3);
                    else if (isGetDltNormalType())
                        item.setType(1);
                    else
                        item.setType(2);
                    if (resource != null)
                        if (resource.equals("bet_history")) {
                            item.setMode("1011");
                        }
                        else if (resource.equals("bet_weibo")) {
                            item.setMode("1012");
                        }
                    betList.add(item);
                    addBetItemView(item);
                    invalidateMoney();
                }
                else {
                    ViewUtil.showTipsToast(this, "不支持生肖乐玩法");
                }
            }
        }
    }

    private boolean isHave(String src, char des) {
        if (src.indexOf(des) != -1) {
            return true;
        }
        else {
            return false;
        }
    }

    protected String generateCode(String lotteryInf) {
        String[] lotteryArray = lotteryInf.split("\\:");
        return lotteryArray[0];
    }

    private long countMoney(int hongDan, int hongTuo, int lanDan, int lanTuo) {
        long betNumber = 0;
        betNumber = MathUtil.combination(hongTuo, 5 - hongDan) * MathUtil.combination(lanTuo, 2 - lanDan);
        return betNumber * 2;
    }

    private long countMoney(int hongNumber, int lanNumber) {
        long betNumber = 0;
        if (lanNumber != 0) {
            betNumber =
                (MathUtil.factorial(hongNumber, 5) / MathUtil.factorial(5, 5)) *
                    (MathUtil.factorial(lanNumber, 2) / 2);
        }
        else if (lanNumber == 0) {
            betNumber = (int) (MathUtil.factorial(hongNumber, 2) / MathUtil.factorial(2, 2));
        }
        return betNumber * 2;
    }

    private String getBallsDisplayInf(String[] redBallDan, String[] redBallTuo, String blueBallDan,
                                      String[] blueBallTuo) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>[胆](");
        lotteryType = 2;

        // 红胆
        int redDanLength = redBallDan.length;
        for (int i = 0; i < redDanLength; i++) {
            betBallText.append(redBallDan[i] + ",");
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append(")</font>");
        // 红拖
        int redTuoLength = redBallTuo.length;
        betBallText.append("<font color='red'>");
        for (int i = 0; i < redTuoLength; i++) {
            betBallText.append(redBallTuo[i] + ",");
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("</font>");
        // 蓝胆
        betBallText.append("<font color='blue'>|");
        if (blueBallDan != null && !blueBallDan.equals("")) {
            betBallText.append("[胆](");
            betBallText.append(blueBallDan);
            betBallText.append(")</font>");
        }
        else {
            betBallText.append("—</font>");
        }
        // 蓝拖
        int blueTuoLength = blueBallTuo.length;
        betBallText.append("<font color='blue'>");
        for (int i = 0; i < blueTuoLength; i++) {
            betBallText.append(blueBallTuo[i] + ",");
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("</font>");

        return betBallText.toString();
    }

    private String getBallsDisplayInf(String[] redBalls, String[] blueBalls) {
        StringBuilder betBallText = new StringBuilder();

        if (isGetDltNormalType()) {
            betBallText.append("<font color='red'>");
            betBallText.append("[标准]");
            lotteryType = 1;
        }
        int redLength = redBalls.length;
        for (int i = 0; i < redLength; i++) {
            betBallText.append(redBalls[i] + ",");
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("</font>");
        if (isGetDltNormalType()) {
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
        }
        return betBallText.toString();
    }

    @Override
    protected BetItem getRandomItem(int id) {
        BetItem item = new BetItem();
        getRamdomItem(item, id);
        return item;
    }

    private void getRamdomItem(BetItem item, int id) {
        int[] randomRedNum =
            MathUtil.getRandomNumNotEquals(DLTActivity.DLT_HONGQIU_MIN, DLTActivity.DLT_HONGQIU_LENGTH);
        Arrays.sort(randomRedNum);
        int[] randomBlueNum =
            MathUtil.getRandomNumNotEquals(DLTActivity.DLT_LANQIU_MIN, DLTActivity.DLT_LANQIU_LENGTH);
        Arrays.sort(randomBlueNum);
        StringBuilder betBallText = new StringBuilder();
        int redLength = DLTActivity.DLT_HONGQIU_MIN;
        for (int i = 0; i < redLength; i++) {
            betBallText.append(StringUtil.betDataTransite(randomRedNum[i] + 1,
                                                          DLTActivity.DLT_HONGQIU_LENGTH));
            betBallText.append(",");
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("|");

        int lanLength = DLTActivity.DLT_LANQIU_MIN;
        for (int i = 0; i < lanLength; i++) {
            betBallText.append(StringUtil.betDataTransite(randomBlueNum[i] + 1,
                                                          DLTActivity.DLT_LANQIU_LENGTH));
            betBallText.append(",");
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[标准]");

        for (int i = 0; i < redLength; i++) {
            betDisplayBallText.append(StringUtil.betDataTransite(randomRedNum[i] + 1,
                                                                 DLTActivity.DLT_HONGQIU_LENGTH));
            betDisplayBallText.append(",");
        }
        betDisplayBallText.deleteCharAt(betDisplayBallText.length() - 1);
        betDisplayBallText.append("</font>");

        betDisplayBallText.append("<font color='blue'>");
        betDisplayBallText.append("|");
        for (int i = 0; i < lanLength; i++) {
            betDisplayBallText.append(StringUtil.betDataTransite(randomBlueNum[i] + 1,
                                                                 DLTActivity.DLT_LANQIU_LENGTH));
            betDisplayBallText.append(",");
        }
        betDisplayBallText.deleteCharAt(betDisplayBallText.length() - 1);
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        item.setMoney(2);
        item.setId(id);
        item.setType(1);
        item.setMode("1005");
    }

    private void getRamdomItemSpecial(BetItem item, int id) {
// int[] randomRedNum =
// MathUtil.getRandomNumNotEquals(DLTActivity.DLT_HONGQIU_MIN,
// DLTActivity.DLT_HONGQIU_LENGTH);
// Arrays.sort(randomRedNum);
        int[] randomBlueNum =
            MathUtil.getRandomNumNotEquals(DLTActivity.DLT_LANQIU_MIN, DLTActivity.DLT_LANQIU_LENGTH);
        Arrays.sort(randomBlueNum);
        StringBuilder betBallText = new StringBuilder();
// int redLength = DLTActivity.DLT_HONGQIU_MIN;
// for (int i = 0; i < redLength; i++) {
// betBallText.append(StringUtil.betDataTransite(randomRedNum[i] + 1,
// DLTActivity.DLT_HONGQIU_LENGTH));
// betBallText.append(",");
// }
// betBallText.deleteCharAt(betBallText.length() - 1);
// betBallText.append("|");

        int lanLength = DLTActivity.DLT_LANQIU_MIN;
        for (int i = 0; i < lanLength; i++) {
            betBallText.append(StringUtil.betDataTransite(randomBlueNum[i] + 1,
                                                          DLTActivity.DLT_LANQIU_LENGTH));
            betBallText.append(",");
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
// betDisplayBallText.append("<font color='red'>");
// for (int i = 0; i < redLength; i++) {
// betDisplayBallText.append(StringUtil.betDataTransite(randomRedNum[i] + 1,
// DLTActivity.DLT_HONGQIU_LENGTH));
// betDisplayBallText.append(",");
// }
// betDisplayBallText.deleteCharAt(betDisplayBallText.length() - 1);
// betDisplayBallText.append("</font>");

        betDisplayBallText.append("<font color='blue'>");
        betDisplayBallText.append("[生肖乐]");
// betDisplayBallText.append("|");
        for (int i = 0; i < lanLength; i++) {
            betDisplayBallText.append(StringUtil.betDataTransite(randomBlueNum[i] + 1,
                                                                 DLTActivity.DLT_LANQIU_LENGTH));
            betDisplayBallText.append(",");
        }
        betDisplayBallText.deleteCharAt(betDisplayBallText.length() - 1);
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        item.setMoney(2);
        item.setId(id);
        item.setType(2);
        item.setMode("1005");
    }

    @Override
    protected void checkExit() {
        if (betList.size() > 0)
            betLocalList = betList;
        finish();
    }

    private boolean isGetDltNormalType() {
        if (betOrgCode.indexOf('|') != -1) {
            return true;
        }
        else {
            return false;
        }
    }

    private boolean isGetDltDantuoType() {
        if (betOrgCode.indexOf('$') != -1) {
            return true;
        }
        else {
            return false;
        }
    }
}
