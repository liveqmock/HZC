package com.haozan.caipiao.activity.bet.hnklsf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.os.Bundle;

import com.haozan.caipiao.activity.bet.BetConfirm;
import com.haozan.caipiao.types.BetItem;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.MathUtil;
import com.haozan.caipiao.util.StringUtil;

public class HNKLSFBetComfirm
    extends BetConfirm {

    private int getKLSFMinNum(int lotteryType) {
        if (lotteryType == 1 || lotteryType == 2)
            return HNKLSFActivity.HNKLSF_HONGQIU_MIN01;
        else if (lotteryType == 3 || lotteryType == 4 || lotteryType == 5)
            return HNKLSFActivity.HNKLSF_HONGQIU_MIN02;
        else if (lotteryType == 6 || lotteryType == 7 || lotteryType == 8)
            return HNKLSFActivity.HNKLSF_HONGQIU_MIN03;
        else if (lotteryType == 9)
            return HNKLSFActivity.HNKLSF_HONGQIU_MIN04;
        else
            return HNKLSFActivity.HNKLSF_HONGQIU_MIN05;
    }

    private int getKLSFSingleMinNum(int lotteryType) {
        if (lotteryType == 1 || lotteryType == 2)
            return HNKLSFActivity.HNKLSF_HONGQIU_MIN01;
        else if (lotteryType == 3 || lotteryType == 5 || lotteryType == 4)
            return HNKLSFActivity.HNKLSF_HONGQIU_MIN02;
        else if (lotteryType == 6 || lotteryType == 8 || lotteryType == 7)
            return HNKLSFActivity.HNKLSF_HONGQIU_MIN03;
        else if (lotteryType == 9)
            return HNKLSFActivity.HNKLSF_HONGQIU_MIN04;
        else
            return HNKLSFActivity.HNKLSF_HONGQIU_MIN05;
    }

    public static ArrayList<BetItem> betLocalList = new ArrayList<BetItem>();

    public HNKLSFBetComfirm() {
        super();
        betList = betLocalList;
        if (betList.size() > 0)
            lotteryType = betList.get(betList.size() - 1).getType();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (kind == null) {
            kind = "hnklsf";
        }
        initInf();
    }

    @Override
    protected void initLastNumsArray() {
        for (int i = 0; i < betList.size(); i++) {
            addBetItemView(betList.get(i));
        }
    }

    Map<String, Integer> map = new HashMap<String, Integer>();

    private void setRecord() {
        for (int i = 0; i < LotteryUtils.HNTypeNum.length; i++) {
            map.put(LotteryUtils.HNTypeNum[i], i + 1);
        }
    }

    @Override
    protected void initNumsArrray(String betOrgNum) {
        setRecord();
        if (betOrgNum != null) {
            String[] lotteryArray = betOrgNum.split(";");

            for (int i = 0; i < lotteryArray.length; i++) {
                String[] lotteryMode = lotteryArray[i].split("\\:");
                String[] nums = lotteryMode[0].split("\\|");
                String[] redBalls = nums[0].split("\\,");
                BetItem item = new BetItem();
                itemNum++;
                item.setId(itemNum);
                if (lotteryMode[2].equals("101"))
                    lotteryType = map.get("101");
                else if (lotteryMode[2].equals("102"))
                    lotteryType = map.get("102");
                else
                    lotteryType = map.get(lotteryMode[2].substring(0, 2));

                item.setType(lotteryType);
                item.setCode(generateCode(lotteryArray[i]));
                if (lotteryType == 4 || lotteryType == 7) {
                    StringBuilder hnklsfSpeType = new StringBuilder();
                    hnklsfSpeType.delete(0, hnklsfSpeType.length());
                    hnklsfSpeType.append("<font color='red'>");
                    for (int j = 0; j < nums.length; j++) {
                        if (j < 1)
                            hnklsfSpeType.append(lotteryDisplayCodeHeader());
                        hnklsfSpeType.append(getSpeBallsDisplayInf(nums[j].split("\\,")));
                        hnklsfSpeType.append("|");
                    }
                    hnklsfSpeType.delete(hnklsfSpeType.length() - 1, hnklsfSpeType.length());
                    hnklsfSpeType.append("</font>");
                    item.setDisplayCode(hnklsfSpeType.toString());
                }
                else
                    item.setDisplayCode(getBallsDisplayInf(redBalls));
                item.setMoney(countMoney(nums));
// item.setMoney(countMoney(redBalls.length));
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

    private long countMoney(String[] hongNumber) {
        long betNumber = 0;
        String[] redBalls = null;
        int[] numLength = null;

        if (lotteryType == 4)
            numLength = new int[2];
        else if (lotteryType == 7)
            numLength = new int[3];

        for (int i = 0; i < hongNumber.length; i++) {
            redBalls = hongNumber[i].split("\\,");
            if (lotteryType == 4) {
                numLength[i] = redBalls.length;
            }
            else if (lotteryType == 7) {
                numLength[i] = redBalls.length;
            }
        }
        if (lotteryType == 4) {
            betNumber = numLength[0] * numLength[1];
        }
        else if (lotteryType == 7) {
            betNumber = numLength[0] * numLength[1] * numLength[2];
        }
        else if (lotteryType != 4 && lotteryType != 7) {
            betNumber =
                (int) (MathUtil.factorial(redBalls.length, getKLSFMinNum(lotteryType)) / MathUtil.factorial(getKLSFMinNum(lotteryType),
                                                                                                            getKLSFMinNum(lotteryType)));
        }
        return betNumber * 2;
    }

// private long countMoney(int hongNumber) {
// long betNumber = 0;
// betNumber =
// (int) (MathUtil.factorial(hongNumber, getKLSFMinNum(lotteryType)) /
// MathUtil.factorial(getKLSFMinNum(lotteryType),
// getKLSFMinNum(lotteryType)));
// return betNumber * 2;
// }

    private String getBallsDisplayInf(String[] redBalls) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append(lotteryDisplayCodeHeader());
        int redLength = redBalls.length;
        for (int i = 0; i < redLength; i++) {
            betBallText.append(redBalls[i] + ",");
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getSpeBallsDisplayInf(String[] redBalls) {
        StringBuilder betBallText = new StringBuilder();

        int redLength = redBalls.length;
        for (int i = 0; i < redLength; i++) {
            betBallText.append(redBalls[i] + ",");
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        return betBallText.toString();
    }

    @Override
    protected BetItem getRandomItem(int id) {
        int length = 0;
        if (lotteryType == 1)
            length = HNKLSFActivity.HNKLSF_HONGQIU_LENGTH01;
        else if (lotteryType == 2)
            length = HNKLSFActivity.HNKLSF_HONGQIU_LENGTH02;
        else
            length = HNKLSFActivity.HNKLSF_HONGQIU_LENGTH03;

        BetItem item = new BetItem();
        int[] randomRedNum01 = null;
        if (lotteryType == 2) {
            Random rd = new Random();
            randomRedNum01 = new int[1];
            randomRedNum01[0] = 18 + rd.nextInt(2);
        }
        else {
            randomRedNum01 = MathUtil.getRandomNumNotEquals(getKLSFSingleMinNum(lotteryType), length);
        }

        Arrays.sort(randomRedNum01);
        StringBuilder ballText = new StringBuilder();
        ballText.append("<font color='red'>");
        ballText.append(lotteryDisplayCodeHeader());
        StringBuilder betText = new StringBuilder();
        if (lotteryType == 4 || lotteryType == 7) {
            betText.append(StringUtil.betDataTransite(randomRedNum01[0] + 1));
            betText.append(",");
        }
        else {
            for (int i = 0; i < randomRedNum01.length; i++) {
                betText.append(StringUtil.betDataTransite(randomRedNum01[i] + 1));
                betText.append(",");
            }
        }

        if (lotteryType == 4 || lotteryType == 7) {
            betText.delete(betText.length() - 1, betText.length());
            betText.append("|");
            betText.append(StringUtil.betDataTransite(randomRedNum01[1] + 1));
            betText.append(",");
        }
        if (lotteryType == 7) {
            betText.delete(betText.length() - 1, betText.length());
            betText.append("|");
            betText.append(StringUtil.betDataTransite(randomRedNum01[2] + 1));
            betText.append(",");
        }

        betText.deleteCharAt(betText.length() - 1);
        item.setLuckyNum(betText.toString());
        ballText.append(betText.toString());
        betText.append(":2:" + HNTypeNumIndex[lotteryType - 1] + ":");
        item.setCode(betText.toString());
        ballText.append("</font>");
        item.setDisplayCode(ballText.toString());
        item.setMoney(2);
        item.setId(id);
        item.setType(lotteryType);
        item.setMode("1005");
        return item;
    }

    @Override
    protected void checkExit() {
        if (betList.size() > 0)
            betLocalList = betList;
        finish();
    }

    private String[] HNTypeNumIndex = {"101", "102", "201", "221", "211", "301", "341", "331", "401", "501"};
    private String[] lotteryTypeArray = {"选一数投", "选一红投", "任选二", "选二连直", "选二连组", "任选三", "选三前直", "选三前组", "任选四",
            "任选五"};

    private String lotteryDisplayCodeHeader() {
        return lotteryTypeArray[lotteryType - 1];
    }
}
