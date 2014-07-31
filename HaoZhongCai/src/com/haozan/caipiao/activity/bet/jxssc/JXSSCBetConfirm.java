package com.haozan.caipiao.activity.bet.jxssc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import android.os.Bundle;

import com.haozan.caipiao.activity.bet.BetConfirm;
import com.haozan.caipiao.activity.bet.sd.SDActivity;
import com.haozan.caipiao.types.BetItem;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.MathUtil;

/**
 * 江西时时彩加单界面
 * 
 * @author Vincent
 * @create-time 2013-6-26 下午2:04:50
 */
public class JXSSCBetConfirm
    extends BetConfirm {

    public static ArrayList<BetItem> betLocalList = new ArrayList<BetItem>();

    public JXSSCBetConfirm() {
        super();
        betList = betLocalList;
        if (betList.size() > 0)
            lotteryType = betList.get(betList.size() - 1).getType();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (kind == null) {
            kind = "jxssc";
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
        else if (type == 402 || type == 401)
            return lotteryType = 3;
        else if (type == 362 || type == 361)
            return lotteryType = 4;
        else if (type == 332)
            return lotteryType = 5;
        else if (type == 302 || type == 301)
            return lotteryType = 6;
        else if (type == 202 || lotteryType == 201)
            return lotteryType = 7;
        else if (type == 911 || type == 912)
            return lotteryType = 8;
        else if (type == 101 || type == 102)
            return lotteryType = 9;
        else if (type == 901 || type == 902)
            return lotteryType = 10;
        else
            return lotteryType = 11;
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
        else if (lotteryType == 3)
            item.setDisplayCode(getBallsDisplayThirdKindInf(lotteryMode[0]));
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
        else if (lotteryType == 11)
            item.setDisplayCode(getBallsDisplayElevenKindInf(lotteryMode[0]));
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

    // TODO
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

    private String getBallsDisplayThirdKindInf(String lotteryMode) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[四星直选] ");
        String[] nums = lotteryMode.split(",");

        int secondLength = nums[0].length();
        for (int i = 0; i < secondLength; i++)
            betBallText.append(nums[0].substring(i, i + 1) + ",");
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("|");

        int thirdLength = nums[1].length();
        for (int i = 0; i < thirdLength; i++)
            betBallText.append(nums[1].substring(i, i + 1) + ",");
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("|");

        int forthLength = nums[2].length();
        for (int i = 0; i < forthLength; i++)
            betBallText.append(nums[2].substring(i, i + 1) + ",");
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("|");

        int fifthLength = nums[3].length();
        for (int i = 0; i < fifthLength; i++)
            betBallText.append(nums[3].substring(i, i + 1) + ",");
        betBallText.deleteCharAt(betBallText.length() - 1);
        betBallText.append("</font>");
        return betBallText.toString();
    }

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

    private String getBallsDisplayEighthKindInf(String lotteryMode) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[任选二] ");
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
        betBallText.append("[任选一] ");
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

    private String getBallsDisplayElevenKindInf(String lotteryMode) {
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
        int[] hongNumber = new int[7];
        if (lotteryType == 4 || lotteryType == 5) {
            hongNumber[0] = nums.length;
        }
        else if (lotteryType == 11) {
            hongNumber[5] = nums[0].length();
            hongNumber[6] = nums[1].length();
        }
        else if (lotteryType == 3) {
            hongNumber[1] = nums[0].length();
            hongNumber[2] = nums[1].length();
            hongNumber[3] = nums[2].length();
            hongNumber[4] = nums[3].length();
        }
        else if (lotteryType == 6) {
            hongNumber[2] = nums[0].length();
            hongNumber[3] = nums[1].length();
            hongNumber[4] = nums[2].length();
        }
        else if (lotteryType == 7) {
            hongNumber[3] = nums[0].length();
            hongNumber[4] = nums[1].length();
        }
        else if (lotteryType == 9) {
            hongNumber[4] = nums[0].length();
        }
        else {
            hongNumber[0] = nums[0].length();
            hongNumber[1] = nums[1].length();
            hongNumber[2] = nums[2].length();
            hongNumber[3] = nums[3].length();
            hongNumber[4] = nums[4].length();
        }
        if (lotteryType == 1) {
            betNumber = hongNumber[0] * hongNumber[1] * hongNumber[2] * hongNumber[3] * hongNumber[4];
        }
        else if (lotteryType == 2) {
            betNumber = hongNumber[0] * hongNumber[1] * hongNumber[2] * hongNumber[3] * hongNumber[4];
        }
        else if (lotteryType == 3) {
            betNumber = hongNumber[1] * hongNumber[2] * hongNumber[3] * hongNumber[4];
        }
        else if (lotteryType == 4) {
            betNumber = (int) (MathUtil.factorial(hongNumber[0], 3) / MathUtil.factorial(3, 3));
        }
        else if (lotteryType == 5) {
            betNumber = MathUtil.factorial(hongNumber[0], 2);
        }
        else if (lotteryType == 6) {
            betNumber = hongNumber[2] * hongNumber[3] * hongNumber[4];
        }
        else if (lotteryType == 7) {
            betNumber = hongNumber[3] * hongNumber[4];
        }
        else if (lotteryType == 8) {
            int numAll = 0;
            for (int i = 0; i < 5; i++) {
                if (!"X".equals(nums[i])) {
                    numAll += hongNumber[i];
                }
            }
            betNumber = MathUtil.combination(numAll, 2);
            for (int i = 0; i < 5; i++) {
                if (hongNumber[i] > 1) {
                    betNumber -= MathUtil.combination(hongNumber[i], 2);
                }
            }
        }
        else if (lotteryType == 9) {
            betNumber = hongNumber[4];
        }
        else if (lotteryType == 10) {
            int numAll = 0;
            for (int i = 0; i < 5; i++) {
                if (!"X".equals(nums[i])) {
                    numAll += hongNumber[i];
                }
            }
            betNumber = numAll;
        }
        else if (lotteryType == 11) {
            betNumber = hongNumber[5] * hongNumber[6];
        }
        return betNumber * 2;
    }

    // 机选
    @Override
    protected BetItem getRandomItem(int id) {
        if (lotteryType == 1)
            return jxsscwxtxBetItem(id);
        else if (lotteryType == 2)
            return jxsscwxfsBetItem(id);
        else if (lotteryType == 3)
            return jxsscsixfsBetItem(id);
        else if (lotteryType == 4)
            return jxsscsxzlBetItem(id);
        else if (lotteryType == 5)
            return jxsscsxzsBetItem(id);
        else if (lotteryType == 6)
            return jxsscsxfsBetItem(id);
        else if (lotteryType == 7)
            return jxsscexfsBetItem(id);
        else if (lotteryType == 8)
            return jxsscrxerBetItem(id);
        else if (lotteryType == 9)
            return jxsscyxfsBetItem(id);
        else if (lotteryType == 10)
            return jxsscrxyBetItem(id);
        else
            return jxsscdxdsBetItem(id);
    }

    private BetItem jxsscwxtxBetItem(int id) {
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

    public BetItem jxsscwxfsBetItem(int id) {
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

    public BetItem jxsscsixfsBetItem(int id) {
        BetItem item = new BetItem();
        Random rd = new Random();
        int num02 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
        int num03 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
        int num04 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
        int num05 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
        StringBuilder betBallText = new StringBuilder();
        betBallText.append(num02);
        betBallText.append(",");
        betBallText.append(num03);
        betBallText.append(",");
        betBallText.append(num04);
        betBallText.append(",");
        betBallText.append(num05);
        item.setLuckyNum(betBallText.toString());
        betBallText.append(":2:401:");
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[四星直选] ");
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
        item.setType(3);
        item.setMode("1005");
        return item;
    }

    public BetItem jxsscsxzlBetItem(int id) {
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

    public BetItem jxsscsxzsBetItem(int id) {
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

    public BetItem jxsscsxfsBetItem(int id) {
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

    public BetItem jxsscexfsBetItem(int id) {
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
        item.setType(7);
        item.setMode("1005");
        return item;
    }

    public BetItem jxsscrxerBetItem(int id) {
        BetItem item = new BetItem();
        Random rd = new Random();
        int num01 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
        int num02 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
        String str = "XXXXX";
        int[] temp = MathUtil.getRandomNumNotEquals(2, 5);
        char[] cs = str.toCharArray();
        cs[temp[0]] = String.valueOf(num01).toCharArray()[0];
        cs[temp[1]] = String.valueOf(num02).toCharArray()[0];

        StringBuilder betBallText = new StringBuilder();
        for (int i = 0; i < cs.length; i++) {
            betBallText.append(cs[i]);
            betBallText.append(",");
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        item.setLuckyNum(betBallText.toString());
        betBallText.append(":2:911:");
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[任选二] ");
        for (int i = 0; i < cs.length; i++) {
            betDisplayBallText.append(cs[i]);
            betDisplayBallText.append("|");
        }
        betDisplayBallText.deleteCharAt(betDisplayBallText.length() - 1);
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        item.setMoney(2);
        item.setId(id);
        item.setType(8);
        item.setMode("1005");
        return item;
    }

    public BetItem jxsscyxfsBetItem(int id) {
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

    public BetItem jxsscrxyBetItem(int id) {
        BetItem item = new BetItem();
        Random rd = new Random();
        int num01 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
        int num02 = rd.nextInt(5);
        String str = "XXXXX";
        char[] cs = str.toCharArray();
        cs[num02] = String.valueOf(num01).toCharArray()[0];

        StringBuilder betBallText = new StringBuilder();
        for (int i = 0; i < cs.length; i++) {
            betBallText.append(cs[i]);
            betBallText.append(",");
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        item.setLuckyNum(betBallText.toString());
        betBallText.append(":2:901:");
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[任选一] ");
        for (int i = 0; i < cs.length; i++) {
            betDisplayBallText.append(cs[i]);
            betDisplayBallText.append("|");
        }
        betDisplayBallText.deleteCharAt(betDisplayBallText.length() - 1);
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        item.setMoney(2);
        item.setId(id);
        item.setType(10);
        item.setMode("1005");
        return item;
    }

    public BetItem jxsscdxdsBetItem(int id) {
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
        item.setType(11);
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
