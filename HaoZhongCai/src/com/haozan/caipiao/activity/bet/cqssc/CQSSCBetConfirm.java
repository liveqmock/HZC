package com.haozan.caipiao.activity.bet.cqssc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import android.os.Bundle;

import com.haozan.caipiao.activity.bet.BetConfirm;
import com.haozan.caipiao.activity.bet.sd.SDActivity;
import com.haozan.caipiao.types.BetItem;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.MathUtil;

public class CQSSCBetConfirm
    extends BetConfirm {

    public static ArrayList<BetItem> betLocalList = new ArrayList<BetItem>();

    public CQSSCBetConfirm() {
        super();
        betList = betLocalList;
        if (betList.size() > 0)
            lotteryType = betList.get(betList.size() - 1).getType();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (kind == null) {
            kind = "cqssc";
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
        if (type == 511 || type == 512)
            return lotteryType = 1;
        else if (type == 502 || type == 501)
            return lotteryType = 2;
//        else if (type == 402 || type == 401)
//            return lotteryType = 3;
        else if (type == 362 || type == 361)
            return lotteryType = 4;
        else if (type == 332)
            return lotteryType = 5;
        else if (type == 302 || type == 301)
            return lotteryType = 6;
        else if (type == 217 || type == 211)
            return lotteryType = 7;
        else if (type == 202 || lotteryType == 201)
            return lotteryType = 8;
        else if (type == 101)
            return lotteryType = 9;
        else 
            return lotteryType = 10;

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
            item.setDisplayCode(getBallsDisplayFirstKindInf(lotteryMode[0]));
        else if (lotteryType == 2)
            item.setDisplayCode(getBallsDisplaySecondKindInf(lotteryMode[0]));
//        else if (lotteryType == 3)
//            item.setDisplayCode(getBallsDisplayThirdKindInf(lotteryMode[0]));
        else if (lotteryType == 4)
            item.setDisplayCode(getBallsDisplayForthKindInf(lotteryMode[0]));
        else if (lotteryType == 5)
            item.setDisplayCode(getBallsDisplayFifthKindInf(lotteryMode[0]));
        else if (lotteryType == 6)
            item.setDisplayCode(getBallsDisplaySixthKindInf(lotteryMode[0]));
        else if (lotteryType == 7)
            item.setDisplayCode(getBallsDisplaySeventhKindInf(lotteryMode[0]));
        else if (lotteryType == 8)
            item.setDisplayCode(getBallsDisplayEighthKindInf(lotteryMode[0]));
        else if (lotteryType == 9)
            item.setDisplayCode(getBallsDisplayNinthKindInf(lotteryMode[0]));
        else if (lotteryType == 10)
            item.setDisplayCode(getBallsDisplayTenthKindInf(lotteryMode[0]));
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

    private String getBallsDisplayFirstKindInf(String lotteryMode) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[五星通选] ");
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

    private String getBallsDisplaySecondKindInf(String lotteryMode) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[五星直选] ");
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

//    private String getBallsDisplayThirdKindInf(String lotteryMode) {
//        StringBuilder betBallText = new StringBuilder();
//        betBallText.append("<font color='red'>");
//        betBallText.append("[四星复式] ");
//        String[] nums = lotteryMode.split(",");
//
//        int secondLength = nums[0].length();
//        for (int i = 0; i < secondLength; i++)
//            betBallText.append(nums[0].substring(i, i + 1) + ",");
//        betBallText.deleteCharAt(betBallText.length() - 1);
//        betBallText.append("|");
//
//        int thirdLength = nums[1].length();
//        for (int i = 0; i < thirdLength; i++)
//            betBallText.append(nums[1].substring(i, i + 1) + ",");
//        betBallText.deleteCharAt(betBallText.length() - 1);
//        betBallText.append("|");
//
//        int forthLength = nums[2].length();
//        for (int i = 0; i < forthLength; i++)
//            betBallText.append(nums[2].substring(i, i + 1) + ",");
//        betBallText.deleteCharAt(betBallText.length() - 1);
//        betBallText.append("|");
//
//        int fifthLength = nums[3].length();
//        for (int i = 0; i < fifthLength; i++)
//            betBallText.append(nums[3].substring(i, i + 1) + ",");
//        betBallText.deleteCharAt(betBallText.length() - 1);
//        betBallText.append("</font>");
//        return betBallText.toString();
//    }

    private String getBallsDisplayForthKindInf(String lotteryMode) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[三星组六] " + lotteryMode);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplayFifthKindInf(String lotteryMode) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[三星组三] ");
        betBallText.append(lotteryMode);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplaySixthKindInf(String lotteryMode) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[三星直选] ");
        String[] nums = lotteryMode.split(",");

        int thirdLength = nums[0].length();
        for (int i = 0; i < thirdLength; i++)
            betBallText.append(nums[0].substring(i, i + 1) + ",");
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("|");

        int forthLength = nums[1].length();
        for (int i = 0; i < forthLength; i++)
            betBallText.append(nums[1].substring(i, i + 1) + ",");
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("|");

        int fifthLength = nums[2].length();
        for (int i = 0; i < fifthLength; i++)
            betBallText.append(nums[2].substring(i, i + 1) + ",");
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplaySeventhKindInf(String lotteryMode) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[二星组选] " + lotteryMode);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplayEighthKindInf(String lotteryMode) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[二星直选] ");
        String[] nums = lotteryMode.split(",");

        int forthLength = nums[0].length();
        for (int i = 0; i < forthLength; i++)
            betBallText.append(nums[0].substring(i, i + 1) + ",");
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("|");

        int fifthLength = nums[1].length();
        for (int i = 0; i < fifthLength; i++)
            betBallText.append(nums[1].substring(i, i + 1) + ",");
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplayNinthKindInf(String lotteryMode) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[一星直选] ");
        String[] nums = lotteryMode.split(",");

        int fifthLength = nums[0].length();
        for (int i = 0; i < fifthLength; i++)
            betBallText.append(nums[0].substring(i, i + 1) + ",");
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplayTenthKindInf(String lotteryMode) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[大小单双] ");
        String[] nums = lotteryMode.split(",");

        int sixthLength = nums[0].length();
        for (int i = 0; i < sixthLength; i++)
            betBallText.append(LotteryUtils.BALL_NAME[Integer.valueOf(nums[0]) - 1] + ",");
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("|");

        int seventhLength = nums[1].length();
        for (int i = 0; i < seventhLength; i++)
            betBallText.append(LotteryUtils.BALL_NAME[Integer.valueOf(nums[1]) - 1] + ",");
        betBallText.deleteCharAt(betBallText.length() - 1);
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
        int hongNumber01 = 0;
        int hongNumber02 = 0;
        int hongNumber03 = 0;
        int hongNumber04 = 0;
        int hongNumber05 = 0;
        int hongNumber06 = 0;
        int hongNumber07 = 0;
        if (lotteryType == 4 || lotteryType == 5 || lotteryType == 7) {
            hongNumber01 = nums.length;
        }
        else if (lotteryType == 10) {
            hongNumber06 = nums[0].length();
            hongNumber07 = nums[1].length();
        }
//        else if (lotteryType == 3) {
//            hongNumber02 = nums[0].length();
//            hongNumber03 = nums[1].length();
//            hongNumber04 = nums[2].length();
//            hongNumber05 = nums[3].length();
//        }
        else if (lotteryType == 6) {
            hongNumber03 = nums[0].length();
            hongNumber04 = nums[1].length();
            hongNumber05 = nums[2].length();
        }
        else if (lotteryType == 8) {
            hongNumber04 = nums[0].length();
            hongNumber05 = nums[1].length();
        }
        else if (lotteryType == 9) {
            hongNumber05 = nums[0].length();
        }
        else {
            hongNumber01 = nums[0].length();
            hongNumber02 = nums[1].length();
            hongNumber03 = nums[2].length();
            hongNumber04 = nums[3].length();
            hongNumber05 = nums[4].length();
        }
        if (lotteryType == 1) {
            betNumber = hongNumber01 * hongNumber02 * hongNumber03 * hongNumber04 * hongNumber05;
        }
        else if (lotteryType == 2) {
            betNumber = hongNumber01 * hongNumber02 * hongNumber03 * hongNumber04 * hongNumber05;
        }
//        else if (lotteryType == 3) {
//            betNumber = hongNumber02 * hongNumber03 * hongNumber04 * hongNumber05;
//        }
        else if (lotteryType == 4) {
            betNumber = (int) (MathUtil.factorial(hongNumber01, 3) / MathUtil.factorial(3, 3));
        }
        else if (lotteryType == 5) {
            betNumber = MathUtil.factorial(hongNumber01, 2);
        }
        else if (lotteryType == 6) {
            betNumber = hongNumber03 * hongNumber04 * hongNumber05;
        }
        else if (lotteryType == 7) {
            betNumber =
                (int) (MathUtil.factorial(hongNumber01, 2) / MathUtil.factorial(2, 2));
        }
        else if (lotteryType == 8) {
            betNumber = hongNumber04 * hongNumber05;
        }
        else if (lotteryType == 9) {
            betNumber = hongNumber05;
        }
        else if (lotteryType == 10) {
            betNumber = hongNumber06 * hongNumber07;
        }
        return betNumber * 2;
    }

    // 机选
    @Override
    protected BetItem getRandomItem(int id) {
        if (lotteryType == 1)
            return cqsscwxtxBetItem(id);
        else if (lotteryType == 2)
            return cqsscwxfsBetItem(id);
//        else if (lotteryType == 3)
//            return cqsscsixfsBetItem(id);
        else if (lotteryType == 4)
            return cqsscsxzlBetItem(id);
        else if (lotteryType == 5)
            return cqsscsxzsBetItem(id);
        else if (lotteryType == 6)
            return cqsscsxfsBetItem(id);
        else if (lotteryType == 7)
            return cqsscexzxBetItem(id);
        else if (lotteryType == 8)
            return cqsscexfsBetItem(id);
        else if (lotteryType == 9)
            return cqsscyxfsBetItem(id);
        else
            return cqsscdxdsBetItem(id);
    }

    private BetItem cqsscwxtxBetItem(int id) {
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
        betBallText.append(":2:511:");
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[五星通选] ");
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

    public BetItem cqsscwxfsBetItem(int id) {
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
        betBallText.append(":2:501:");
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[五星直选] ");
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
        item.setType(2);
        item.setMode("1005");
        return item;
    }

//    public BetItem cqsscsixfsBetItem(int id) {
//        BetItem item = new BetItem();
//        Random rd = new Random();
//        int num02 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
//        int num03 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
//        int num04 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
//        int num05 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
//        StringBuilder betBallText = new StringBuilder();
//        betBallText.append(num02);
//        betBallText.append(",");
//        betBallText.append(num03);
//        betBallText.append(",");
//        betBallText.append(num04);
//        betBallText.append(",");
//        betBallText.append(num05);
//        item.setLuckyNum(betBallText.toString());
//        betBallText.append(":2:401:");
//        item.setCode(betBallText.toString());
//        StringBuilder betDisplayBallText = new StringBuilder();
//        betDisplayBallText.append("<font color='red'>");
//        betDisplayBallText.append("[四星复式] ");
//        betDisplayBallText.append(num02);
//        betDisplayBallText.append("|");
//        betDisplayBallText.append(num03);
//        betDisplayBallText.append("|");
//        betDisplayBallText.append(num04);
//        betDisplayBallText.append("|");
//        betDisplayBallText.append(num05);
//        betDisplayBallText.append("</font>");
//        item.setDisplayCode(betDisplayBallText.toString());
//        item.setMoney(2);
//        item.setId(id);
//        item.setType(3);
//        item.setMode("1005");
//        return item;
//    }

    public BetItem cqsscsxzlBetItem(int id) {
        BetItem item = new BetItem();
        int[] randomRedNum = MathUtil.getRandomNumNotEquals(3, SDActivity.SD_HONGQIU_LENGTH);
        Arrays.sort(randomRedNum);
        StringBuilder betBallText = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            betBallText.append(randomRedNum[i]);
            betBallText.append(",");
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        item.setLuckyNum(betBallText.toString());
        betBallText.append(":2:361:");
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[三星组六] ");
        for (int i = 0; i < 3; i++) {
            betDisplayBallText.append(randomRedNum[i]);
            betDisplayBallText.append(",");
        }
        betDisplayBallText.deleteCharAt(betDisplayBallText.length() - 1);
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        item.setMoney(2);
        item.setId(id);
        item.setType(4);
        item.setMode("1005");
        return item;
    }

    public BetItem cqsscsxzsBetItem(int id) {
        BetItem item = new BetItem();
        int[] randomRedNum = MathUtil.getRandomNumNotEquals(2, SDActivity.SD_HONGQIU_LENGTH);
        Arrays.sort(randomRedNum);
        StringBuilder betBallText = new StringBuilder();
        for (int i = 0; i < 2; i++) {
            betBallText.append(randomRedNum[i]);
            betBallText.append(",");
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        item.setLuckyNum(betBallText.toString());
        betBallText.append(":4:332:");
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[三星组三] ");
        for (int i = 0; i < 2; i++) {
            betDisplayBallText.append(randomRedNum[i]);
            betDisplayBallText.append(",");
        }
        betDisplayBallText.deleteCharAt(betDisplayBallText.length() - 1);
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        item.setMoney(4);
        item.setId(id);
        item.setType(5);
        item.setMode("1005");
        return item;
    }

    public BetItem cqsscsxfsBetItem(int id) {
        BetItem item = new BetItem();
        Random rd = new Random();
        int num03 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
        int num04 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
        int num05 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
        StringBuilder betBallText = new StringBuilder();
        betBallText.append(num03);
        betBallText.append(",");
        betBallText.append(num04);
        betBallText.append(",");
        betBallText.append(num05);
        item.setLuckyNum(betBallText.toString());
        betBallText.append(":2:301:");
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[三星直选] ");
        betDisplayBallText.append(num03);
        betDisplayBallText.append("|");
        betDisplayBallText.append(num04);
        betDisplayBallText.append("|");
        betDisplayBallText.append(num05);
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        item.setMoney(2);
        item.setId(id);
        item.setType(6);
        item.setMode("1005");
        return item;
    }

    public BetItem cqsscexzxBetItem(int id) {
        BetItem item = new BetItem();
        int[] randomRedNum = MathUtil.getRandomNumNotEquals(2, SDActivity.SD_HONGQIU_LENGTH);
        Arrays.sort(randomRedNum);
        StringBuilder betBallText = new StringBuilder();
        for (int i = 0; i < 2; i++) {
            betBallText.append(randomRedNum[i]);
            betBallText.append(",");
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        item.setLuckyNum(betBallText.toString());
        betBallText.append(":2:211:");
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[二星组选] ");
        for (int i = 0; i < 2; i++) {
            betDisplayBallText.append(randomRedNum[i]);
            betDisplayBallText.append(",");
        }
        betDisplayBallText.deleteCharAt(betDisplayBallText.length() - 1);
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        item.setMoney(6);
        item.setId(id);
        item.setType(7);
        item.setMode("1005");
        return item;
    }

    public BetItem cqsscexfsBetItem(int id) {
        BetItem item = new BetItem();
        Random rd = new Random();
        int num04 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
        int num05 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
        StringBuilder betBallText = new StringBuilder();
        betBallText.append(num04);
        betBallText.append(",");
        betBallText.append(num05);
        item.setLuckyNum(betBallText.toString());
        betBallText.append(":2:201:");
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[二星直选] ");
        betDisplayBallText.append(num04);
        betDisplayBallText.append("|");
        betDisplayBallText.append(num05);
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        item.setMoney(2);
        item.setId(id);
        item.setType(8);
        item.setMode("1005");
        return item;
    }

    public BetItem cqsscyxfsBetItem(int id) {
        BetItem item = new BetItem();
        Random rd = new Random();
        int num05 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
        StringBuilder betBallText = new StringBuilder();
        betBallText.append(num05);
        item.setLuckyNum(betBallText.toString());
        betBallText.append(":2:101:");
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[一星直选] ");
        betDisplayBallText.append(num05);
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        item.setMoney(2);
        item.setId(id);
        item.setType(9);
        item.setMode("1005");
        return item;
    }

    public BetItem cqsscdxdsBetItem(int id) {
        BetItem item = new BetItem();
        Random rd = new Random();
        int num06 = rd.nextInt(4);
        int num07 = rd.nextInt(4);
        StringBuilder betBallText = new StringBuilder();
        betBallText.append(num06 + 1);
        betBallText.append(",");
        betBallText.append(num07 + 1);
        item.setLuckyNum(betBallText.toString());
        betBallText.append(":2:701:");
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[大小单双] ");
        betDisplayBallText.append(LotteryUtils.BALL_NAME[num06]);
        betDisplayBallText.append("|");
        betDisplayBallText.append(LotteryUtils.BALL_NAME[num07]);
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        item.setMoney(2);
        item.setId(id);
        item.setType(10);
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
