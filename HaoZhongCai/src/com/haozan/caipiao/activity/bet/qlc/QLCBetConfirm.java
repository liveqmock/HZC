package com.haozan.caipiao.activity.bet.qlc;

import java.util.ArrayList;
import java.util.Arrays;

import android.os.Bundle;

import com.haozan.caipiao.activity.bet.BetConfirm;
import com.haozan.caipiao.types.BetItem;
import com.haozan.caipiao.util.MathUtil;
import com.haozan.caipiao.util.StringUtil;

public class QLCBetConfirm
    extends BetConfirm {
    public static ArrayList<BetItem> betLocalList = new ArrayList<BetItem>();

    public QLCBetConfirm() {
        super();
        betList = betLocalList;
        if (betList.size() > 0)
            lotteryType = betList.get(betList.size() - 1).getType();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (kind == null) {
            kind = "qlc";
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
        betNumber = (int) (MathUtil.factorial(hongNumber, 7) / MathUtil.factorial(7, 7));
        return betNumber * 2;
    }

    private String getBallsDisplayInf(String[] redBalls) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
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
            MathUtil.getRandomNumNotEquals(QLCActivity.QLC_HONGQIU_MIN,
                                               QLCActivity.QLC_HONGQIU_LENGTH);
        Arrays.sort(randomRedNum);
        StringBuilder ballText = new StringBuilder();
        ballText.append("<font color='red'>");
        StringBuilder betText = new StringBuilder();
        for (int i = 0; i < randomRedNum.length; i++) {
            betText.append(StringUtil.betDataTransite((randomRedNum[i] + 1), QLCActivity.QLC_HONGQIU_LENGTH));
            betText.append(",");
        }
        betText.deleteCharAt(betText.length() - 1);
        item.setLuckyNum(betText.toString());
        ballText.append(betText.toString());
        betText.append(":1:1:");
        item.setCode(betText.toString());
        ballText.append("</font>");
        item.setDisplayCode(ballText.toString());
        item.setMoney(2);
        item.setId(id);
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
