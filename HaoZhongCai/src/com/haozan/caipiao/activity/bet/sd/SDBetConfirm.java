package com.haozan.caipiao.activity.bet.sd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import android.os.Bundle;

import com.haozan.caipiao.activity.bet.BetConfirm;
import com.haozan.caipiao.types.BetItem;
import com.haozan.caipiao.util.MathUtil;

public class SDBetConfirm
    extends BetConfirm {
    public static ArrayList<BetItem> betLocalList = new ArrayList<BetItem>();

    // add by vincent
    private static final int[] zxhz = {1, 3, 6, 10, 15, 21, 28, 36, 45, 55, 63, 69, 73, 75};
    private static final int[] zshz = {1, 2, 1, 3, 3, 3, 4, 5, 4, 5, 5, 4, 5};
    private static final int[] zlhz = {1, 1, 2, 3, 4, 5, 7, 8, 9, 10, 10};

    public SDBetConfirm() {
        super();
        betList = betLocalList;
        if (betList.size() > 0)
            lotteryType = betList.get(betList.size() - 1).getType();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (kind == null) {
            kind = "3d";
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

    private void addBetList(String lotteryInf) {
        String[] lotteryMode = lotteryInf.split("\\:");
        BetItem item = new BetItem();
        itemNum++;
        item.setId(itemNum);
        int type = Integer.valueOf(lotteryMode[1]);
        int type1 = Integer.valueOf(lotteryMode[2]);
        // changed by vincent
        if (type1 != 4) {
            if (type == 1)
                lotteryType = type;
            else if (type == 2) {
                if (lotteryMode[2].equals("1") || lotteryMode[2].equals("2"))
                    lotteryType = 4;
                else
                    lotteryType = 2;
            }
            else if (type == 3)
                lotteryType = type;
        }
        else if (type1 == 4) {
            if (type == 1)
                lotteryType = 5;
            else if (type == 2)
                lotteryType = 6;
            else if (type == 3)
                lotteryType = 7;
        }

        item.setType(lotteryType);
        item.setCode(generateCode(lotteryInf));
        if (lotteryType == 1)
            item.setDisplayCode(getBallsDisplayFirstKindInf(lotteryMode[0]));
        else if (lotteryType == 2)
            item.setDisplayCode(getBallsDisplaySecondKindInf(lotteryMode[0]));
        else if (lotteryType == 3)
            item.setDisplayCode(getBallsDisplayThirdKindInf(lotteryMode[0]));
        else if (lotteryType == 4)
            item.setDisplayCode(getBallsDisplayForthKindInf(lotteryInf));
        // add by vincent
        else if (lotteryType == 5)
            item.setDisplayCode(getBallsDisplayFifthKindInf(lotteryMode[0]));
        else if (lotteryType == 6)
            item.setDisplayCode(getBallsDisplaySixthKindInf(lotteryMode[0]));
        else if (lotteryType == 7)
            item.setDisplayCode(getBallsDisplaySeventhKindInf(lotteryMode[0]));

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
        betBallText.append("[直选]");
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
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplaySecondKindInf(String lotteryMode) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[组三包号]");
        betBallText.append(lotteryMode);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplayThirdKindInf(String lotteryMode) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[组六包号]");
        betBallText.append(lotteryMode);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplayForthKindInf(String lotteryInf) {
        String[] lotteryMode = lotteryInf.split("\\:");
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[组三单复式]");
        if (lotteryMode[2].equals("1")) {
            String[] nums = lotteryMode[0].split(",");
            betBallText.append(nums[0] + "|" + nums[2]);
        }
        else {
            String[] nums = lotteryMode[0].split(",");
            int firstLength = nums[0].length();
            for (int i = 0; i < firstLength; i++)
                betBallText.append(nums[0].substring(i, i + 1) + ",");
            betBallText.deleteCharAt(betBallText.length() - 1);
            betBallText.append("|");
            int thirdLength = nums[1].length();
            for (int i = 0; i < thirdLength; i++)
                betBallText.append(nums[1].substring(i, i + 1) + ",");
            betBallText.deleteCharAt(betBallText.length() - 1);
        }
        betBallText.append("</font>");
        return betBallText.toString();
    }

    // add by vincent
    private String getBallsDisplayFifthKindInf(String lotteryMode) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[直选和值] ");
        betBallText.append(lotteryMode);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplaySixthKindInf(String lotteryMode) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[组三和值] ");
        betBallText.append(lotteryMode);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplaySeventhKindInf(String lotteryMode) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[组六和值] ");
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
        int index = Integer.parseInt(nums[0]);
        int hongNumber01 = 0;
        int hongNumber02 = 0;
        int hongNumber03 = 0;
        if (lotteryType == 4) {
            if (lotteryMode[2].equals("1")) {
                hongNumber01 = nums[0].length();
                hongNumber03 = nums[2].length();
            }
            else {
                hongNumber01 = nums[0].length();
                hongNumber03 = nums[1].length();
            }
        }
        else if (lotteryType == 2 || lotteryType == 3) {
            hongNumber01 = nums.length;
        }
        else if (lotteryType == 1) {
            hongNumber01 = nums[0].length();
            hongNumber02 = nums[1].length();
            hongNumber03 = nums[2].length();
        }
        if (lotteryType == 1) {
            betNumber = hongNumber01 * hongNumber02 * hongNumber03;
        }
        else if (lotteryType == 2) {
            betNumber = MathUtil.factorial(hongNumber01, 2);
        }
        else if (lotteryType == 3) {
            betNumber = (int) (MathUtil.factorial(hongNumber01, 3) / MathUtil.factorial(3, 3));
        }
        else if (lotteryType == 4) {
            if ((hongNumber03 + hongNumber01 == 2) && hongNumber01 != 0 && hongNumber03 != 0) {
                betNumber = 1;
            }
            else {
                int sameCount = findSameNum(nums[0], nums[1]);
                betNumber = hongNumber03 * hongNumber01 - sameCount;
            }
        }
        // add by vincent
        else if (lotteryType == 5) {
            if (index > 13)
                index = 27 - index;
            betNumber = zxhz[index];
        }
        else if (lotteryType == 6) {
            if (index > 12) {
                index = 25 + 1 - index;
                betNumber = zshz[index];
            }
            else {
                betNumber = zshz[index - 1];
            }
        }
        else if (lotteryType == 7) {
            if (index > 10) {
                index = 21 + 3 - index;
                betNumber = zlhz[index];
            }
            else {
                betNumber = zlhz[index - 3];
            }
        }

        return betNumber * 2;
    }

    @Override
    protected BetItem getRandomItem(int id) {
        if (lotteryType == 1)
            return sdzxBetItem(id);
        else if (lotteryType == 2)
            return sdzsBetItem(id);
        else if (lotteryType == 3)
            return sdzlBetItem(id);
        else if (lotteryType == 4)
            return sdzsdfBetItem(id);
        // add by vincent
        else if (lotteryType == 5)
            return sdzxhzBetItem(id);
        else if (lotteryType == 6)
            return sdzshzBetItem(id);
        else
            return sdzlhzBetItem(id);

    }

    public BetItem sdzxBetItem(int id) {
        BetItem item = new BetItem();
        Random rd = new Random();
        int num01 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
        int num02 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
        int num03 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
        StringBuilder betBallText = new StringBuilder();
        betBallText.append(num01);
        betBallText.append(",");
        betBallText.append(num02);
        betBallText.append(",");
        betBallText.append(num03);
        item.setLuckyNum(betBallText.toString());
        betBallText.append(":1:1:");
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[直选]");
        betDisplayBallText.append(num01);
        betDisplayBallText.append("|");
        betDisplayBallText.append(num02);
        betDisplayBallText.append("|");
        betDisplayBallText.append(num03);
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        item.setMoney(2);
        item.setId(id);
        item.setType(1);
        item.setMode("1005");
        return item;
    }

    public BetItem sdzsBetItem(int id) {
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
        betBallText.append(":2:3:");
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[组三包号]");
        for (int i = 0; i < 2; i++) {
            betDisplayBallText.append(randomRedNum[i]);
            betDisplayBallText.append(",");
        }
        betDisplayBallText.deleteCharAt(betDisplayBallText.length() - 1);
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        item.setMoney(4);
        item.setId(id);
        item.setType(2);
        item.setMode("1005");
        return item;
    }

    public BetItem sdzlBetItem(int id) {
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
        betBallText.append(":3:3:");
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[组六包号]");
        for (int i = 0; i < 3; i++) {
            betDisplayBallText.append(randomRedNum[i]);
            betDisplayBallText.append(",");
        }
        betDisplayBallText.deleteCharAt(betDisplayBallText.length() - 1);
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        item.setMoney(2);
        item.setId(id);
        item.setType(3);
        item.setMode("1005");
        return item;
    }

    public BetItem sdzsdfBetItem(int id) {
        BetItem item = new BetItem();
        Random rd = new Random();
        int num01 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
        int num03 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
        while (num01 == num03) {
            num01 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
            num03 = rd.nextInt(SDActivity.SD_HONGQIU_LENGTH);
        }
        StringBuilder betBallText = new StringBuilder();
        betBallText.append(num01 + "," + num01);
        betBallText.append(",");
        betBallText.append(num03);
        item.setLuckyNum(betBallText.toString());
        betBallText.append(":2:1:");
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[组三单复式]");
        betDisplayBallText.append(num01);
        betDisplayBallText.append("|");
        betDisplayBallText.append(num03);
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        item.setMoney(2);
        item.setId(id);
        item.setType(2);
        item.setMode("1005");
        return item;
    }

    // add by vincent
    public BetItem sdzxhzBetItem(int id) {
        BetItem item = new BetItem();
        Random rd = new Random();
        int num04 = rd.nextInt(SDActivity.SD_HONGQIU_ZHIXUAN_HEZHI);
        StringBuilder betBallText = new StringBuilder();
        betBallText.append(num04);
        item.setLuckyNum(betBallText.toString());
        betBallText.append(":1:4:");
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[直选和值] ");
        betDisplayBallText.append(num04);
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        if (num04 > 13)
            num04 = 27 - num04;
        long betMoney = zxhz[num04] * 2;
        item.setMoney(betMoney);
        item.setId(id);
        item.setType(5);
        item.setMode("1005");
        return item;
    }

    public BetItem sdzshzBetItem(int id) {
        BetItem item = new BetItem();
        Random rd = new Random();
        int num05 = rd.nextInt(SDActivity.SD_HONGQIU_ZUSAN_HEZHI);
        StringBuilder betBallText = new StringBuilder();
        betBallText.append(num05);
        item.setLuckyNum(betBallText.toString());
        betBallText.append(":2:4:");
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[组三和值] ");
        betDisplayBallText.append(num05);
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        if (num05 > 12)
            num05 = 25 - num05;
        long betMoney = zshz[num05] * 2;
        item.setMoney(betMoney);
        item.setId(id);
        item.setType(6);
        item.setMode("1005");
        return item;
    }

    public BetItem sdzlhzBetItem(int id) {
        BetItem item = new BetItem();
        Random rd = new Random();
        int num06 = rd.nextInt(SDActivity.SD_HONGQIU_ZULIU_HEZHI);
        StringBuilder betBallText = new StringBuilder();
        betBallText.append(num06);
        item.setLuckyNum(betBallText.toString());
        betBallText.append(":3:4:");
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[组六和值] ");
        betDisplayBallText.append(num06);
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        if (num06 > 10)
            num06 = 21 - num06;
        long betMoney = zlhz[num06] * 2;
        item.setMoney(betMoney);
        item.setId(id);
        item.setType(7);
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
