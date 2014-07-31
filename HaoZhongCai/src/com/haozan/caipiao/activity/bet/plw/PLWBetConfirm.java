package com.haozan.caipiao.activity.bet.plw;

import java.util.ArrayList;
import java.util.Random;

import android.os.Bundle;

import com.haozan.caipiao.activity.bet.BetConfirm;
import com.haozan.caipiao.activity.bet.sd.SDActivity;
import com.haozan.caipiao.types.BetItem;

public class PLWBetConfirm
    extends BetConfirm {
    public static ArrayList<BetItem> betLocalList = new ArrayList<BetItem>();

    public PLWBetConfirm() {
        super();
        betList = betLocalList;
        if (betList.size() > 0)
            lotteryType = betList.get(betList.size() - 1).getType();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (kind == null) {
            kind = "plw";
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
                item.setDisplayCode(getBallsDisplayInf(nums[0]));
                item.setMoney(countMoney(redBalls));
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

    private long countMoney(String[] redBalls) {
        int hongNumber01 = redBalls[0].length();
        int hongNumber02 = redBalls[1].length();
        int hongNumber03 = redBalls[2].length();
        int hongNumber04 = redBalls[3].length();
        int hongNumber05 = redBalls[4].length();
        long betNumber = 0;
        betNumber = hongNumber01 * hongNumber02 * hongNumber03 * hongNumber04 * hongNumber05;
        return betNumber * 2;
    }

    private String getBallsDisplayInf(String lotteryMode) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        String[] nums = lotteryMode.split(",");
        int firstLength = nums[0].length();
        for (int i = 0; i < firstLength; i++)
            betBallText.append(nums[0].substring(i, i + 1) + ",");
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("|");
        int secondLength = nums[1].length();
        for (int i = 0; i < secondLength; i++)
            betBallText.append(nums[1].substring(i, i + 1) + ",");
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("|");
        int thirdLength = nums[2].length();
        for (int i = 0; i < thirdLength; i++)
            betBallText.append(nums[2].substring(i, i + 1) + ",");
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("|");
        int forthLength = nums[3].length();
        for (int i = 0; i < forthLength; i++)
            betBallText.append(nums[3].substring(i, i + 1) + ",");
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("|");
        int fifthLength = nums[4].length();
        for (int i = 0; i < fifthLength; i++)
            betBallText.append(nums[4].substring(i, i + 1) + ",");
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    @Override
    protected BetItem getRandomItem(int id) {
        BetItem item = new BetItem();
        Random rd = new Random();
        int num01 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
        int num02 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
        int num03 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
        int num04 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
        int num05 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
        StringBuilder betBallText = new StringBuilder();
        betBallText.append(num01);
        betBallText.append(",");
        betBallText.append(num02);
        betBallText.append(",");
        betBallText.append(num03);
        betBallText.append(",");
        betBallText.append(num04);
        betBallText.append(",");
        betBallText.append(num05);
        item.setLuckyNum(betBallText.toString());
        betBallText.append(":1:1:");
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append(num01);
        betDisplayBallText.append("|");
        betDisplayBallText.append(num02);
        betDisplayBallText.append("|");
        betDisplayBallText.append(num03);
        betDisplayBallText.append("|");
        betDisplayBallText.append(num04);
        betDisplayBallText.append("|");
        betDisplayBallText.append(num05);
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
