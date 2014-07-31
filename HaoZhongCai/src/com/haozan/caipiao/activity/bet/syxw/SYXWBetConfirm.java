package com.haozan.caipiao.activity.bet.syxw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import android.os.Bundle;

import com.haozan.caipiao.activity.bet.BetConfirm;
import com.haozan.caipiao.types.BetItem;
import com.haozan.caipiao.util.BetHistoryDetailTool;
import com.haozan.caipiao.util.MathUtil;
import com.haozan.caipiao.util.StringUtil;

public class SYXWBetConfirm
    extends BetConfirm {
    public static ArrayList<BetItem> betLocalList = new ArrayList<BetItem>();

    public SYXWBetConfirm() {
        super();
        betList = betLocalList;
        if (betList.size() > 0)
            lotteryType = betList.get(betList.size() - 1).getType();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (kind == null) {
            kind = "jx11x5";
        }
        initInf();
    }

    @Override
    protected void initLastNumsArray() {
        for (int i = 0; i < betList.size(); i++) {
            addBetItemView(betList.get(i));
        }
    }

    private String dantuoCodesConvert(String codes) {
        String[] betCodes = codes.split("\\;");
        StringBuilder sb_code = new StringBuilder();
        for (int i = 0; i < betCodes.length; i++) {
            if (betCodes[i].indexOf("(") != -1)
                sb_code.append(BetHistoryDetailTool.filterSyxwDantuoCode(betCodes[i]));
            else
                sb_code.append(betCodes[i]);
            sb_code.append(";");
        }
        sb_code.delete(sb_code.length() - 1, sb_code.length());
        return sb_code.toString();
    }

    @Override
    protected void initNumsArrray(String betOrgNum) {
        String orgNum = null;
        if (betOrgNum.indexOf(")") != -1)
            orgNum = dantuoCodesConvert(betOrgNum);
        else
            orgNum = betOrgNum;

        if (orgNum != null) {
            String[] lotteryArray = orgNum.split(";");
            for (int i = 0; i < lotteryArray.length; i++) {
                addBetList(lotteryArray[i]);
            }
            invalidateMoney();
        }
    }

    private String dantuoCodeConvertBack(String dantuoCode) {
        StringBuilder dantuo_sb = new StringBuilder();
        dantuo_sb.append("(");
        dantuo_sb.append(dantuoCode.replace("$", ")"));
        return dantuo_sb.toString().trim();
    }

    private void addBetList(String lotteryInf) {
        String[] lotteryMode = lotteryInf.split("\\:");
        BetItem item = new BetItem();
        itemNum++;
        item.setId(itemNum);
        String lm = lotteryMode[2];
        if (lm.equals("11_RX2")) {
            lotteryType = 1;
        }
        else if (lm.equals("11_RX3")) {
            lotteryType = 2;
        }
        else if (lm.equals("11_RX4")) {
            lotteryType = 3;
        }
        else if (lm.equals("11_RX5")) {
            lotteryType = 4;
        }
        else if (lm.equals("11_RX6")) {
            lotteryType = 5;
        }
        else if (lm.equals("11_RX7")) {
            lotteryType = 6;
        }
        else if (lm.equals("11_RX8")) {
            lotteryType = 7;
        }
        else if (lm.equals("11_RX1")) {
            lotteryType = 8;
        }
        else if (lm.equals("11_ZXQ2_D") || lm.equals("11_ZXQ2_F")) {
            lotteryType = 9;
        }
        else if (lm.equals("11_ZXQ2")) {
            lotteryType = 10;
        }
        else if (lm.equals("11_ZXQ3_D") || lm.equals("11_ZXQ3_F")) {
            lotteryType = 11;
        }
        else if (lm.equals("11_ZXQ3")) {
            lotteryType = 12;
        }

        item.setType(lotteryType);
        if (lotteryInf.indexOf("$") == -1) {
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
                item.setDisplayCode(getBallsDisplayEleventhKindInf(lotteryMode[0]));
            else if (lotteryType == 12)
                item.setDisplayCode(getBallsDisplayTwelfthKindInf(lotteryMode[0]));

            item.setCode(generateCode(lotteryInf));

        }
        else {
            item.setCode(generateCode(dantuoCodeConvertBack(lotteryInf)));
// item.setMoney(countMoney(dantuoCodesConvert(lotteryInf)));
            item.setDisplayCode(getDantuoSYXWDispalyInf(lotteryMode[0]));
        }
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
        betBallText.append("[任选二] ");
        betBallText.append(lotteryMode);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplaySecondKindInf(String lotteryMode) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[任选三] ");
        betBallText.append(lotteryMode);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplayThirdKindInf(String lotteryMode) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[任选四] ");
        betBallText.append(lotteryMode);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplayForthKindInf(String lotteryMode) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[任选五] ");
        betBallText.append(lotteryMode);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplayFifthKindInf(String lotteryMode) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[任选六] ");
        betBallText.append(lotteryMode);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplaySixthKindInf(String lotteryMode) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[任选七] ");
        betBallText.append(lotteryMode);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplaySeventhKindInf(String lotteryMode) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[任选八] ");
        betBallText.append(lotteryMode);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplayEighthKindInf(String lotteryMode) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[前一] ");
        betBallText.append(lotteryMode);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplayNinthKindInf(String lotteryMode) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[前二直选] ");
        betBallText.append(lotteryMode);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplayTenthKindInf(String lotteryMode) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[前二组选] ");
        betBallText.append(lotteryMode);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplayEleventhKindInf(String lotteryMode) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[前三直选] ");
        betBallText.append(lotteryMode);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getBallsDisplayTwelfthKindInf(String lotteryMode) {
        StringBuilder betBallText = new StringBuilder();
        betBallText.append("<font color='red'>");
        betBallText.append("[前三组选] ");
        betBallText.append(lotteryMode);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private String getDantuoSYXWDispalyInf(String betCode) {
        StringBuilder betBallText = new StringBuilder();
        String[] lotteryRed = betCode.split("\\$");
        betBallText.append("<font color='red'>[胆拖](");
        betBallText.append(lotteryRed[0]);
        betBallText.append(")");
        betBallText.append(lotteryRed[1]);
        betBallText.append("</font>");
        return betBallText.toString();
    }

    private long countMoney(String lotteryInf) {
        int lotteryMethodType = 1;
        long betNumber = 0;
        int hongNumber01 = 0;
        int hongNumber02 = 0;
        int hongNumber03 = 0;
        String[] lotteryMode = lotteryInf.split("\\:");

        if (lotteryType == 1 || lotteryType == 2 || lotteryType == 3 || lotteryType == 4 ||
            lotteryType == 5 || lotteryType == 6 || lotteryType == 7 || lotteryType == 8 ||
            lotteryType == 10 || lotteryType == 12) {
            String[] nums = lotteryMode[0].split(",");
            hongNumber01 = nums.length;
            if (lotteryType == 1 || lotteryType == 10) {
                if (lotteryInf.indexOf("$") == -1)
                    betNumber = MathUtil.factorial(hongNumber01, 2) / MathUtil.factorial(2, 2);
                if (lotteryType == 1)
                    lotteryMethodType = 1;
                else
                    lotteryMethodType = 1;
            }
            else if (lotteryType == 2 || lotteryType == 12) {
                if (lotteryInf.indexOf("$") == -1)
                    betNumber = MathUtil.factorial(hongNumber01, 3) / MathUtil.factorial(3, 3);
                if (lotteryType == 2)
                    lotteryMethodType = 2;
                else
                    lotteryMethodType = 2;
            }
            else if (lotteryType == 3) {
                if (lotteryInf.indexOf("$") == -1)
                    betNumber = MathUtil.factorial(hongNumber01, 4) / MathUtil.factorial(4, 4);
                if (lotteryType == 3)
                    lotteryMethodType = 3;
            }
            else if (lotteryType == 4) {
                if (lotteryInf.indexOf("$") == -1)
                    betNumber = MathUtil.factorial(hongNumber01, 5) / MathUtil.factorial(5, 5);
                if (lotteryType == 4)
                    lotteryMethodType = 4;
            }
            else if (lotteryType == 5) {
                if (lotteryInf.indexOf("$") == -1)
                    betNumber = MathUtil.factorial(hongNumber01, 6) / MathUtil.factorial(6, 6);
                if (lotteryType == 5)
                    lotteryMethodType = 5;
            }
            else if (lotteryType == 6) {
                if (lotteryInf.indexOf("$") == -1)
                    betNumber = MathUtil.factorial(hongNumber01, 7) / MathUtil.factorial(7, 7);
                if (lotteryType == 6)
                    lotteryMethodType = 6;
            }
            else if (lotteryType == 7) {
                if (lotteryInf.indexOf("$") == -1)
                    betNumber = MathUtil.factorial(hongNumber01, 8) / MathUtil.factorial(8, 8);
            }
            else if (lotteryType == 8) {
                if (lotteryInf.indexOf("$") == -1)
                    betNumber = hongNumber01;
            }

            if (lotteryInf.indexOf("$") != -1) {
                int number1 = lotteryMethodType + 1 - lotteryMode[0].split("\\$")[0].split("\\,").length;
                int number2 = lotteryMode[0].split("\\$")[1].split("\\,").length;
                betNumber = MathUtil.factorial(number2, number1) / MathUtil.factorial(number1, number1);
            }
        }
        else if (lotteryType == 9) {
            String[] li = lotteryMode[0].split("\\|");
            String[] li1 = li[0].split("\\,");
            String[] li2 = li[1].split("\\,");
            hongNumber01 = li1.length;
            hongNumber02 = li2.length;
            betNumber = hongNumber01 * hongNumber02;
        }
        else if (lotteryType == 11) {
            String[] li = lotteryMode[0].split("\\|");
            String[] li1 = li[0].split("\\,");
            String[] li2 = li[1].split("\\,");
            String[] li3 = li[2].split("\\,");
            hongNumber01 = li1.length;
            hongNumber02 = li2.length;
            hongNumber03 = li3.length;
            betNumber = hongNumber01 * hongNumber02 * hongNumber03;
        }

        return betNumber * 2;
    }

    @Override
    protected BetItem getRandomItem(int id) {
        if (lotteryType == 1)
            return syxwrxerBetItem(id);
        else if (lotteryType == 2)
            return syxwrxsanBetItem(id);
        else if (lotteryType == 3)
            return syxwrxsiBetItem(id);
        else if (lotteryType == 4)
            return syxwrxwuBetItem(id);
        else if (lotteryType == 5)
            return syxwrxliuBetItem(id);
        else if (lotteryType == 6)
            return syxwrxqiBetItem(id);
        else if (lotteryType == 7)
            return syxwrxbaBetItem(id);
        else if (lotteryType == 8)
            return syxwqyBetItem(id);
        else if (lotteryType == 9)
            return syxwqezhixBetItem(id);
        else if (lotteryType == 10)
            return syxwqezuxBetItem(id);
        else if (lotteryType == 11)
            return syxwqszhixBetItem(id);
        else
            return syxwqszuxBetItem(id);
    }

    private BetItem syxwrxerBetItem(int id) {
        BetItem item = new BetItem();
        int[] randomRedNum = MathUtil.getRandomNumNotEquals(2, SYXWActivity.SYXW_HONGQIU_LENGTH);
        Arrays.sort(randomRedNum);
        StringBuilder betBallText = new StringBuilder();
        for (int i = 0; i < 2; i++) {
            betBallText.append(StringUtil.betDataTransite((randomRedNum[i] + 1), 11));
            betBallText.append(",");
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        item.setLuckyNum(betBallText.toString());
        betBallText.append(":2:11_RX2:");
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[任选二] ");
        for (int i = 0; i < 2; i++) {
            betDisplayBallText.append(StringUtil.betDataTransite((randomRedNum[i] + 1), 11));
            betDisplayBallText.append(",");
        }
        betDisplayBallText.deleteCharAt(betDisplayBallText.length() - 1);
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        item.setMoney(2);
        item.setId(id);
        item.setType(1);
        return item;
    }

    private BetItem syxwrxsanBetItem(int id) {
        BetItem item = new BetItem();
        int[] randomRedNum = MathUtil.getRandomNumNotEquals(3, SYXWActivity.SYXW_HONGQIU_LENGTH);
        Arrays.sort(randomRedNum);
        StringBuilder betBallText = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            betBallText.append(StringUtil.betDataTransite((randomRedNum[i] + 1), 11));
            betBallText.append(",");
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        item.setLuckyNum(betBallText.toString());
        betBallText.append(":2:11_RX3:");
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[任选三] ");
        for (int i = 0; i < 3; i++) {
            betDisplayBallText.append(StringUtil.betDataTransite((randomRedNum[i] + 1), 11));
            betDisplayBallText.append(",");
        }
        betDisplayBallText.deleteCharAt(betDisplayBallText.length() - 1);
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        item.setMoney(2);
        item.setId(id);
        item.setType(2);
        return item;
    }

    private BetItem syxwrxsiBetItem(int id) {
        BetItem item = new BetItem();
        int[] randomRedNum = MathUtil.getRandomNumNotEquals(4, SYXWActivity.SYXW_HONGQIU_LENGTH);
        Arrays.sort(randomRedNum);
        StringBuilder betBallText = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            betBallText.append(StringUtil.betDataTransite((randomRedNum[i] + 1), 11));
            betBallText.append(",");
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        item.setLuckyNum(betBallText.toString());
        betBallText.append(":2:11_RX4:");
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[任选四] ");
        for (int i = 0; i < 4; i++) {
            betDisplayBallText.append(StringUtil.betDataTransite((randomRedNum[i] + 1), 11));
            betDisplayBallText.append(",");
        }
        betDisplayBallText.deleteCharAt(betDisplayBallText.length() - 1);
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        item.setMoney(2);
        item.setId(id);
        item.setType(3);
        return item;
    }

    private BetItem syxwrxwuBetItem(int id) {
        BetItem item = new BetItem();
        int[] randomRedNum = MathUtil.getRandomNumNotEquals(5, SYXWActivity.SYXW_HONGQIU_LENGTH);
        Arrays.sort(randomRedNum);
        StringBuilder betBallText = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            betBallText.append(StringUtil.betDataTransite((randomRedNum[i] + 1), 11));
            betBallText.append(",");
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        item.setLuckyNum(betBallText.toString());
        betBallText.append(":2:11_RX5:");
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[任选五] ");
        for (int i = 0; i < 5; i++) {
            betDisplayBallText.append(StringUtil.betDataTransite((randomRedNum[i] + 1), 11));
            betDisplayBallText.append(",");
        }
        betDisplayBallText.deleteCharAt(betDisplayBallText.length() - 1);
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        item.setMoney(2);
        item.setId(id);
        item.setType(4);
        return item;
    }

    private BetItem syxwrxliuBetItem(int id) {
        BetItem item = new BetItem();
        int[] randomRedNum = MathUtil.getRandomNumNotEquals(6, SYXWActivity.SYXW_HONGQIU_LENGTH);
        Arrays.sort(randomRedNum);
        StringBuilder betBallText = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            betBallText.append(StringUtil.betDataTransite((randomRedNum[i] + 1), 11));
            betBallText.append(",");
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        item.setLuckyNum(betBallText.toString());
        betBallText.append(":2:11_RX6:");
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[任选六] ");
        for (int i = 0; i < 6; i++) {
            betDisplayBallText.append(StringUtil.betDataTransite((randomRedNum[i] + 1), 11));
            betDisplayBallText.append(",");
        }
        betDisplayBallText.deleteCharAt(betDisplayBallText.length() - 1);
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        item.setMoney(2);
        item.setId(id);
        item.setType(5);
        return item;
    }

    private BetItem syxwrxqiBetItem(int id) {
        BetItem item = new BetItem();
        int[] randomRedNum = MathUtil.getRandomNumNotEquals(7, SYXWActivity.SYXW_HONGQIU_LENGTH);
        Arrays.sort(randomRedNum);
        StringBuilder betBallText = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            betBallText.append(StringUtil.betDataTransite((randomRedNum[i] + 1), 11));
            betBallText.append(",");
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        item.setLuckyNum(betBallText.toString());
        betBallText.append(":2:11_RX7:");
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[任选七] ");
        for (int i = 0; i < 7; i++) {
            betDisplayBallText.append(StringUtil.betDataTransite((randomRedNum[i] + 1), 11));
            betDisplayBallText.append(",");
        }
        betDisplayBallText.deleteCharAt(betDisplayBallText.length() - 1);
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        item.setMoney(2);
        item.setId(id);
        item.setType(6);
        return item;
    }

    private BetItem syxwrxbaBetItem(int id) {
        BetItem item = new BetItem();
        int[] randomRedNum = MathUtil.getRandomNumNotEquals(8, SYXWActivity.SYXW_HONGQIU_LENGTH);
        Arrays.sort(randomRedNum);
        StringBuilder betBallText = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            betBallText.append(StringUtil.betDataTransite((randomRedNum[i] + 1), 11));
            betBallText.append(",");
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        item.setLuckyNum(betBallText.toString());
        betBallText.append(":2:11_RX8:");
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[任选八] ");
        for (int i = 0; i < 8; i++) {
            betDisplayBallText.append(StringUtil.betDataTransite((randomRedNum[i] + 1), 11));
            betDisplayBallText.append(",");
        }
        betDisplayBallText.deleteCharAt(betDisplayBallText.length() - 1);
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        item.setMoney(2);
        item.setId(id);
        item.setType(7);
        return item;
    }

    private BetItem syxwqyBetItem(int id) {
        BetItem item = new BetItem();
        Random rd = new Random();
        int num = rd.nextInt(SYXWActivity.SYXW_HONGQIU_LENGTH);
        StringBuilder betBallText = new StringBuilder();
        betBallText.append(StringUtil.betDataTransite((num + 1), 11));
        item.setLuckyNum(betBallText.toString());
        betBallText.append(":2:11_RX1:");
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[前一] ");
        betDisplayBallText.append(StringUtil.betDataTransite((num + 1), 11));
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        item.setMoney(2);
        item.setId(id);
        item.setType(8);
        return item;
    }

    private BetItem syxwqezhixBetItem(int id) {
        BetItem item = new BetItem();
        int[] randomRedNum = MathUtil.getRandomNumNotEquals(2, SYXWActivity.SYXW_HONGQIU_LENGTH);
        StringBuilder betBallText = new StringBuilder();
        betBallText.append(StringUtil.betDataTransite((randomRedNum[0] + 1), 11) + "|" +
            StringUtil.betDataTransite((randomRedNum[1] + 1), 11));
        item.setLuckyNum(betBallText.toString());
        betBallText.append(":2:11_ZXQ2_D:");
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[前二直选] ");
        betDisplayBallText.append(StringUtil.betDataTransite((randomRedNum[0] + 1), 11));
        betDisplayBallText.append("|");
        betDisplayBallText.append(StringUtil.betDataTransite((randomRedNum[1] + 1), 11));
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        item.setMoney(2);
        item.setId(id);
        item.setType(9);
        return item;
    }

    private BetItem syxwqezuxBetItem(int id) {
        BetItem item = new BetItem();
        int[] randomRedNum = MathUtil.getRandomNumNotEquals(2, SYXWActivity.SYXW_HONGQIU_LENGTH);
        Arrays.sort(randomRedNum);
        StringBuilder betBallText = new StringBuilder();
        for (int i = 0; i < 2; i++) {
            betBallText.append(StringUtil.betDataTransite((randomRedNum[i] + 1), 11));
            betBallText.append(",");
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        item.setLuckyNum(betBallText.toString());
        betBallText.append(":2:11_ZXQ2:");
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[前二组选] ");
        for (int i = 0; i < 2; i++) {
            betDisplayBallText.append(StringUtil.betDataTransite((randomRedNum[i] + 1), 11));
            betDisplayBallText.append(",");
        }
        betDisplayBallText.deleteCharAt(betDisplayBallText.length() - 1);
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        item.setMoney(2);
        item.setId(id);
        item.setType(10);
        return item;
    }

    private BetItem syxwqszhixBetItem(int id) {
        BetItem item = new BetItem();
        int[] randomRedNum = MathUtil.getRandomNumNotEquals(3, SYXWActivity.SYXW_HONGQIU_LENGTH);
        StringBuilder betBallText = new StringBuilder();
        betBallText.append(StringUtil.betDataTransite((randomRedNum[0] + 1), 11) + "|" +
            StringUtil.betDataTransite((randomRedNum[1] + 1), 11) + "|" +
            StringUtil.betDataTransite((randomRedNum[2] + 1), 11));
        item.setLuckyNum(betBallText.toString());
        betBallText.append(":2:11_ZXQ3_D:");
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[前三直选] ");
        betDisplayBallText.append(StringUtil.betDataTransite((randomRedNum[0] + 1), 11));
        betDisplayBallText.append("|");
        betDisplayBallText.append(StringUtil.betDataTransite((randomRedNum[1] + 1), 11));
        betDisplayBallText.append("|");
        betDisplayBallText.append(StringUtil.betDataTransite((randomRedNum[2] + 1), 11));
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        item.setMoney(2);
        item.setId(id);
        item.setType(11);
        return item;
    }

    private BetItem syxwqszuxBetItem(int id) {
        BetItem item = new BetItem();
        int[] randomRedNum = MathUtil.getRandomNumNotEquals(3, SYXWActivity.SYXW_HONGQIU_LENGTH);
        Arrays.sort(randomRedNum);
        StringBuilder betBallText = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            betBallText.append(StringUtil.betDataTransite((randomRedNum[i] + 1), 11));
            betBallText.append(",");
        }
        betBallText.deleteCharAt(betBallText.length() - 1);
        item.setLuckyNum(betBallText.toString());
        betBallText.append(":2:11_ZXQ3:");
        item.setCode(betBallText.toString());
        StringBuilder betDisplayBallText = new StringBuilder();
        betDisplayBallText.append("<font color='red'>");
        betDisplayBallText.append("[前三组选] ");
        for (int i = 0; i < 3; i++) {
            betDisplayBallText.append(StringUtil.betDataTransite((randomRedNum[i] + 1), 11));
            betDisplayBallText.append(",");
        }
        betDisplayBallText.deleteCharAt(betDisplayBallText.length() - 1);
        betDisplayBallText.append("</font>");
        item.setDisplayCode(betDisplayBallText.toString());
        item.setMoney(2);
        item.setId(id);
        item.setType(12);
        return item;
    }

    @Override
    protected void checkExit() {
        if (betList.size() > 0)
            betLocalList = betList;
        finish();
    }
// @Override
// public void onClick(View v) {
    // 11选5仅支持一注投注
// if (v.getId() == R.id.add_random_operation) {
// Toast.makeText(this, "11选5暂时仅支持投注1注。", 1000).show();
// }
// else if (v.getId() == R.id.add_manual_operation) {
// Toast.makeText(this, "11选5暂时仅支持投注1注。", 1000).show();
// }

// if (v.getId() == R.id.bet_clear_button) {
// if (betList.size() > 0)
// clearWarningDialog.show();
// else
// toChooseBall(true);
// }
// else if (v.getId() == R.id.bet) {
// if (checkUserInf()) {
// goPaying();
// }
// }
// }
//
}
