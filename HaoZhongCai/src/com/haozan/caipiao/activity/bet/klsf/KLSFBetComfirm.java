package com.haozan.caipiao.activity.bet.klsf;

import java.util.ArrayList;
import java.util.Arrays;

import android.os.Bundle;

import com.haozan.caipiao.activity.bet.BetConfirm;
import com.haozan.caipiao.types.BetItem;
import com.haozan.caipiao.util.MathUtil;

public class KLSFBetComfirm
    extends BetConfirm {

    private int[] klsf_num_min_array = {KLSFActivity.KLSF_HONGQIU_MIN01, KLSFActivity.KLSF_HONGQIU_MIN02,
            KLSFActivity.KLSF_HONGQIU_MIN03, KLSFActivity.KLSF_HONGQIU_MIN04,
            KLSFActivity.KLSF_HONGQIU_MIN05};

// private int[] klsf_num_limit_array =
// {KLSFActivity.KLSF_HONGQIU_LIMIT01, KLSFActivity.KLSF_HONGQIU_LIMIT02,
// KLSFActivity.KLSF_HONGQIU_LIMIT03, KLSFActivity.KLSF_HONGQIU_LIMIT04};

    private int getKLSFMinNum(int lotteryType) {
        if (lotteryType == 9 || lotteryType == 5)
            return klsf_num_min_array[0];
        else if (lotteryType == 4)
            return klsf_num_min_array[1];
        else if (lotteryType == 3)
            return klsf_num_min_array[2];
        else if (lotteryType == 2)
            return klsf_num_min_array[3];
        else if (lotteryType == 1)
            return klsf_num_min_array[4];
        return 0;
    }

// private int getKLSFLimitNum(int lotteryType) {
// if (lotteryType == 1)
// return klsf_num_limit_array[0];
// else if (lotteryType == 2 || lotteryType == 3 || lotteryType == 4)
// return klsf_num_limit_array[1];
// else if (lotteryType == 5)
// return klsf_num_limit_array[2];
// else if (lotteryType == 6)
// return klsf_num_limit_array[3];
// return 0;
// }

    public static ArrayList<BetItem> betLocalList = new ArrayList<BetItem>();

    public KLSFBetComfirm() {
        super();
        betList = betLocalList;
        if (betList.size() > 0)
            lotteryType = betList.get(betList.size() - 1).getType();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (kind == null) {
            kind = "klsf";
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
                String[] lotteryMode = lotteryArray[i].split("\\:");
                String[] nums = lotteryMode[0].split("\\|");
                String[] redBalls = nums[0].split("\\,");
                BetItem item = new BetItem();
                itemNum++;
                item.setId(itemNum);
                lotteryType = Integer.valueOf(lotteryMode[1]);
                item.setType(lotteryType);
                item.setCode(generateCode(lotteryArray[i]));
                item.setDisplayCode(getBallsDisplayInf(redBalls));
                item.setMoney(countMoney(redBalls.length));
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

    private long countMoney(int hongNumber) {
        long betNumber = 0;
        if (lotteryType == 5 || lotteryType == 9)
            betNumber = hongNumber;
        else
            betNumber =
                (int) (MathUtil.factorial(hongNumber, getKLSFMinNum(lotteryType)) / MathUtil.factorial(getKLSFMinNum(lotteryType),
                                                                                                               getKLSFMinNum(lotteryType)));
        return betNumber * 2;
    }

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

    @Override
    protected BetItem getRandomItem(int id) {
        BetItem item = new BetItem();
        int[] randomRedNum =
            MathUtil.getRandomNumNotEquals(getKLSFMinNum(lotteryType), KLSFActivity.KLSF_HONGQIU_LENGTH);
        Arrays.sort(randomRedNum);
        StringBuilder ballText = new StringBuilder();
        ballText.append("<font color='red'>");
        ballText.append(lotteryDisplayCodeHeader());
        StringBuilder betText = new StringBuilder();
        for (int i = 0; i < randomRedNum.length; i++) {
            betText.append(String.valueOf(randomRedNum[i] + 1));
            betText.append(",");
        }
        betText.deleteCharAt(betText.length() - 1);
        item.setLuckyNum(betText.toString());
        ballText.append(betText.toString());
        betText.append(":" + String.valueOf(lotteryType));
        betText.append(":1:");
        item.setCode(betText.toString());
        ballText.append("</font>");
        item.setDisplayCode(ballText.toString());
        item.setMoney(2);
        item.setId(id);
        item.setType(lotteryType);
        item.setMode("1005");
        return item;
    }

// private String getBallsDisplayFirstKindInf(String klsf_lottery_code) {
// StringBuilder betBallText = new StringBuilder();
// betBallText.append("<font color='red'>");
// betBallText.append(lotteryDisplayCodeHeader());
// for (int i = 0; i < randomRedNum.length; i++) {
// betText.append(StringUtil.betDataTransite((randomRedNum[i] + 1),
// KLSFActivity.KLSF_HONGQIU_LENGTH));
// betText.append(",");
// }
// betBallText.append(klsf_lottery_code);
// betBallText.deleteCharAt(betBallText.length() - 1);
// return betBallText.toString();
// }

    @Override
    protected void checkExit() {
        if (betList.size() > 0)
            betLocalList = betList;
        finish();
    }

    private String lotteryDisplayCodeHeader() {
        if (lotteryType == 9)
            return "[好运特]";
        if (lotteryType == 5)
            return "[好运一]";
        if (lotteryType == 4)
            return "[好运二]";
        if (lotteryType == 3)
            return "[好运三]";
        if (lotteryType == 2)
            return "[好运四]";
        if (lotteryType == 1)
            return "[好运五]";
        return null;
    }
}
