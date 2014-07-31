package com.haozan.caipiao.util;

import java.util.HashMap;
import java.util.Map;

public class BetHistoryDetailTool {

    public static Map<Integer, String> cqsscType;
    public static Map<String, String> jxxyx5cType;
    private static Map<String, String> huKlsfWay;

    private static final String[] arrayTips = {"五星通选", "五星通选", "五星直选", "五星直选", "五星组合", "四星单式", "四星复式",
            "三星组六", "三星组六", "三星组三", "三星组三", "三星直选", "三星直选", "二星组选", "二星组选", "二星直选", "二星直选", "一星直选", "大小单双",
            "三星和值", "二星和值", "一星复式", "任选一单式", "任选一复式", "任选二单式", "任选二单式", "任选二复式"};
    private static final int[] arrayNum = {511, 512, 501, 502, 505, 401, 402, 361, 362, 331, 332, 301, 302,
            211, 217, 201, 202, 101, 701, 309, 209, 102, 901, 902, 911, 912};
    private static final String[] xyx5arrayTipsText = {"前一", "任选二", "任选三", "任选四", "任选五", "任选六", "任选七", "任选八",
            "前二直选单式", "前二直选复式", "前三直选单式", "前三直选复式", "前二组选", "前三组选"};
    private static final String[] xyx5arrayTipsNum = {"11_RX1", "11_RX2", "11_RX3", "11_RX4", "11_RX5",
            "11_RX6", "11_RX7", "11_RX8", "11_ZXQ2_D", "11_ZXQ2_F", "11_ZXQ3_D", "11_ZXQ3_F", "11_ZXQ2",
            "11_ZXQ3"};

    public static void initAnalyseData() {
        jxxyx5cType = new HashMap<String, String>();
        for (int j = 0; j < xyx5arrayTipsText.length; j++)
            jxxyx5cType.put(xyx5arrayTipsNum[j], xyx5arrayTipsText[j]);

        cqsscType = new HashMap<Integer, String>();
        for (int j = 0; j < arrayNum.length; j++)
            cqsscType.put(arrayNum[j], arrayTips[j]);

        huKlsfWay = new HashMap<String, String>();
        for (int i = 0; i < LotteryUtils.HNTypeNum.length; i++)
            huKlsfWay.put(LotteryUtils.HNTypeNum[i], LotteryUtils.textArrayHNKLSF[i]);
    }

    public static String subName(String lottery, String ballSubType01, String ballSubType02) {

        StringBuilder lotteryInfParent = new StringBuilder();
        if (lottery.equals("3d") || lottery.equals("pls")) {
            if (ballSubType01.equals("1")) {
                if (ballSubType02.equals("4"))
                    lotteryInfParent.append("[直选和值]");
                else
                    lotteryInfParent.append("[单选三]");
            }
            else if (ballSubType01.equals("2")) {
                if (ballSubType02.equals("3")) {
                    if (ballSubType02.equals("4"))
                        lotteryInfParent.append("[组三和值]");
                    else
                        lotteryInfParent.append("[组三包号]");
                }
                else if (ballSubType02.equals("1") || ballSubType02.equals("2"))
                    lotteryInfParent.append("[组三单复式]");
            }
            else if (ballSubType01.equals("3")) {
                if (ballSubType02.equals("3"))
                    lotteryInfParent.append("[组六包号]");
                if (ballSubType02.equals("1"))
                    lotteryInfParent.append("[组六单式]");
                if (ballSubType02.equals("4"))
                    lotteryInfParent.append("[组六和值]");
            }
        }
        else if (lottery.equals("ssl")) {
            if (ballSubType01.equals("1")) {
                lotteryInfParent.append("[直选三]");
            }
            else if (ballSubType01.equals("2")) {
                lotteryInfParent.append("[组选三]");
            }
            else if (ballSubType01.equals("3")) {
                if (ballSubType02.equals("3"))
                    lotteryInfParent.append("[组六包号]");
            }
            else if (ballSubType01.equals("4")) {
                lotteryInfParent.append("[前二]");
            }
            else if (ballSubType01.equals("5")) {
                lotteryInfParent.append("[后二]");
            }
            else if (ballSubType01.equals("6")) {
                lotteryInfParent.append("[前一]");
            }
            else if (ballSubType01.equals("7")) {
                lotteryInfParent.append("[后一]");
            }
        }
        else if (lottery.equals("dlt")) {
            if (ballSubType01.equals("1") || ballSubType01.equals("2"))
                lotteryInfParent.append("[标准]");
            else if (ballSubType01.equals("3"))
                lotteryInfParent.append("[生肖乐]");
        }
// else if (lottery.equals("klsf")) {
// lotteryInfParent.append(lotteryDisplayCodeHeader("klsf", Integer.valueOf(ballSubType01)));
// }
        else if (lottery.equals("cqssc") || lottery.equals("jxssc")) {
            lotteryInfParent.append("["+cqsscType.get(Integer.valueOf(ballSubType02))+"]");
        }
        else if (lottery.equals("jx11x5")) {
            lotteryInfParent.append("["+jxxyx5cType.get(ballSubType02)+"]");
        }
        else if (lottery.equals("hnklsf")) {
            if (ballSubType02.equals("101"))
                lotteryInfParent.append("["+huKlsfWay.get(ballSubType02)+"]");
            else if (ballSubType02.equals("102"))
                lotteryInfParent.append("["+huKlsfWay.get(ballSubType02)+"]");
            else
                lotteryInfParent.append("["+huKlsfWay.get(ballSubType02.substring(0, 2))+"]");
        }
        else if (lottery.equals("klsf")) {
            if (ballSubType01.equals("9"))
                lotteryInfParent.append("[好运特]");
            if (ballSubType01.equals("5"))
                lotteryInfParent.append("[好运一]");
            if (ballSubType01.equals("4"))
                lotteryInfParent.append("[好运二]");
            if (ballSubType01.equals("3"))
                lotteryInfParent.append("[好运三]");
            if (ballSubType01.equals("2"))
                lotteryInfParent.append("[好运四]");
            if (ballSubType01.equals("1"))
                lotteryInfParent.append("[好运五]");
        }
        else if (lottery.equals("jlk3")) {
            if (ballSubType02.equals("101"))
                lotteryInfParent.append("[和值单式]");
            if (ballSubType02.equals("102"))
                lotteryInfParent.append("[和值复式]");
            if (ballSubType02.equals("103"))
                lotteryInfParent.append("[三同号通选]");
            if (ballSubType02.equals("104"))
                lotteryInfParent.append("[三同号单选单式]");
            if (ballSubType02.equals("105"))
                lotteryInfParent.append("[三同号单选复式]");
            if (ballSubType02.equals("106"))
                lotteryInfParent.append("[二同号复选单式]");
            if (ballSubType02.equals("107"))
                lotteryInfParent.append("[二同号复选复式]");
            if (ballSubType02.equals("108"))
                lotteryInfParent.append("[二同号单选单式]");
            if (ballSubType02.equals("109"))
                lotteryInfParent.append("[二同号单选复式]");
            if (ballSubType02.equals("110"))
                lotteryInfParent.append("[三不同号单式]");
            if (ballSubType02.equals("111"))
                lotteryInfParent.append("[三不同号复式]");
            if (ballSubType02.equals("113"))
                lotteryInfParent.append("[二不同号单式]");
            if (ballSubType02.equals("114"))
                lotteryInfParent.append("[二不同号复式]");
            if (ballSubType02.equals("116"))
                lotteryInfParent.append("[三连号通选]");
        }
        return lotteryInfParent.toString();
    }

    private static String lotteryDisplayCodeHeader(String headerType, int lotteryType) {
        if (headerType.equals("ssc")) {
            if (lotteryType == 511)
                return "[五星通选]";
            if (lotteryType == 501 || lotteryType == 502)
                return "[五星直选]";
            if (lotteryType == 401 || lotteryType == 402)
                return "[四星直选]";
            if (lotteryType == 201 || lotteryType == 202)
                return "[三星直选]";
            if (lotteryType == 311 || lotteryType == 332)
                return "[三星组三]";
            if (lotteryType == 311 || lotteryType == 362)
                return "[三星组六]";
            if (lotteryType == 611)
                return "[任选二]";
            if (lotteryType == 601)
                return "[任选一]";
            if (lotteryType == 701)
                return "[大小单双]";
        }
        return null;
    }

    public static String getDanTuoCodeNormal(String originCode, String lotteryPlayType) {
        StringBuilder originCodeString = new StringBuilder();
        if (lotteryPlayType.equals("5")) {
            originCodeString.append(annalyseDanTuoCode(originCode));
        }
        else {
            originCodeString.append(originCode);
        }
        return originCodeString.toString();
    }

    public static String getDanTuoCodeOpen(String openCode, int boundIndex, String lotteryPlayType) {
        StringBuilder originCodeString = new StringBuilder();
        if (lotteryPlayType.equals("5")) {
// originCodeString.append("none,");
// originCodeString.append(openCode.subSequence(0, boundIndex - 2));
// originCodeString.append("none,");
// originCodeString.append(openCode.subSequence(boundIndex - 2, openCode.length()));
            originCodeString.append(analyseDantuoCodeOpen(openCode, boundIndex));
        }
        else {
            originCodeString.append(openCode);
        }
        return originCodeString.toString();
    }

    public static String analyseDantuoCodeOpen(String openCode, int boundIndex) {
        StringBuilder originCodeString = new StringBuilder();
        originCodeString.append("none,");
        originCodeString.append(openCode.subSequence(0, boundIndex - 2));
        originCodeString.append("none,");
        originCodeString.append(openCode.subSequence(boundIndex - 2, openCode.length()));
        return originCodeString.toString();
    }

    public static String annalyseDanTuoCode(String originCode) {
        String[] originCodeArray = originCode.split("\\|");
        StringBuilder originCodeString = new StringBuilder();
        originCodeString.append("#,");
        if (originCodeArray[0].indexOf("$") != -1) {
            for (int j = 0; j < originCodeArray[0].split("\\$").length; j++) {
                originCodeString.append(originCodeArray[0].split("\\$")[j]);
                originCodeString.append(",$,");
            }
            originCodeString.delete(originCodeString.length() - 2, originCodeString.length());
        }
        else {
            originCodeString.append(originCodeArray[0]);
        }

        if (originCode.indexOf("|") != -1) {
            if (originCodeArray[1].indexOf("$") != -1) {
                if (originCodeArray[1].indexOf("$") != 0) {
                    originCodeString.append("|#,");
                    for (int j = 0; j < originCodeArray[1].split("\\$").length; j++) {
                        originCodeString.append(originCodeArray[1].split("\\$")[j]);
                        originCodeString.append(",$,");
                    }
                    originCodeString.delete(originCodeString.length() - 2, originCodeString.length());
                }
                else {
                    originCodeString.append("|" + originCodeArray[1].split("\\$")[1]);
                }
            }
            else {
                originCodeString.append("|" + originCodeArray[1]);
            }
        }
        return originCodeString.toString();
    }

    public static String filterSyxwDantuoCode(String betDanTuoCode) {
        return betDanTuoCode.replace("(", "").replace(")", "$").trim();
    }

    public static String filterSyxwDantuoCodeBack(String betDanTuoCode) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(betDanTuoCode);
        return sb.toString().replace("$", ")").trim();
    }

    public static String reConstructBetCode(String betCode, String type) {
        String num = "1,2,3,4,5,6";
        if (type.equals("103")) {
            return "111,222,333,444,555,666";
        }
        else if (type.equals("104") || type.equals("105")) {
            return betCode;
        }
        else if (type.equals("106")) {
            String a = betCode.substring(0, 1);
            if (a.equals("6"))
                num = num.replaceAll(a, "").toString();
            else
                num = num.replaceAll(a + ",", "");
            String[] numArray = num.split("\\,");
            return (betCode + numArray[0]) + "," + (betCode + numArray[1]) + "," + (betCode + numArray[2]) +
                "," + (betCode + numArray[3]) + "," + (betCode + numArray[4]);
        }
        else if (type.equals("107")) {
            StringBuilder sbBetCode = new StringBuilder();
            String[] betCodeArray = betCode.split("\\,");
         
            for (int i = 0; i < betCodeArray.length; i++) {
                num = "1,2,3,4,5,6";
                String a = betCodeArray[i].substring(0, 1);
                if (a.equals("6"))
                    num = num.replaceAll(a, "").toString();
                else
                    num = num.replaceAll(a + ",", "").toString();
                String[] numArray = num.split("\\,");
                sbBetCode.append((betCodeArray[i] + numArray[0]) + "," + (betCodeArray[i] + numArray[1]) +
                    "," + (betCodeArray[i] + numArray[2]) + "," + (betCodeArray[i] + numArray[3]) + "," +
                    (betCodeArray[i] + numArray[4]));
                sbBetCode.append(",");
            }
            return sbBetCode.delete(sbBetCode.length() - 1, sbBetCode.length()).toString();
        }
        else if (type.equals("108")) {
            String[] betCodeArray = betCode.split("\\|");
            return betCodeArray[0] + betCodeArray[1];
        }
        else if (type.equals("109")) {
            StringBuilder sbBetCode = new StringBuilder();
            String[] betCodeArray = betCode.split("\\|");
// for (int i = 0; i < betCodeArray.length; i++) {
            String[] betCodeArraySub01 = betCodeArray[0].split("\\,");
            String[] betCodeArraySub02 = betCodeArray[1].split("\\,");
            for (int j = 0; j < betCodeArraySub01.length; j++) {
                sbBetCode.append(betCodeArraySub01[j] + betCodeArraySub02[j]);
                sbBetCode.append(",");
            }
// }
            return sbBetCode.delete(sbBetCode.length() - 1, sbBetCode.length()).toString();
        }
        else if (type.equals("116")) {
            return "123,234,345,456";
        }
        else
            return betCode;
    }

    public static String reConstructBetCode(String betCode) {
        String[] betCodeArrayF = betCode.split("\\:");
        String type = betCodeArrayF[2];
        String codeExtra = ":" + betCodeArrayF[1] + ":" + betCodeArrayF[2] + ":" + betCodeArrayF[3];
        String num = "1,2,3,4,5,6";
        if (type.equals("103")) {
            return "111,222,333,444,555,666" + codeExtra;
        }
        else if (type.equals("104")) {
            return betCode;
        }
        else if (type.equals("105")) {
            String[] codeArray = betCodeArrayF[0].split("\\,");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < codeArray.length; i++) {
                sb.append(codeArray[i] + codeExtra);
                sb.append(";");
            }
            return sb.delete(sb.length() - 1, sb.length()).toString();
        }
        else if (type.equals("106")) {
            String a = betCode.substring(0, 1);
            if (a.equals("6"))
                num = num.replaceAll(a, "").toString();
            else
                num = num.replaceAll(a + ",", "").toString();
            String[] numArray = num.split("\\,");
            return (betCodeArrayF[0] + numArray[0]) + "," + (betCodeArrayF[0] + numArray[1]) + "," +
                (betCodeArrayF[0] + numArray[2]) + "," + (betCodeArrayF[0] + numArray[3]) + "," +
                (betCodeArrayF[0] + numArray[4]) + codeExtra;
        }
        else if (type.equals("107")) {
            StringBuilder sbBetCode = new StringBuilder();
            String[] betCodeArray = betCodeArrayF[0].split("\\,");
            String a = betCodeArray[0].substring(0, 1);
            if (a.equals("6"))
                num = num.replaceAll(a, "").toString();
            else
                num = num.replaceAll(a + ",", "").toString();
            for (int i = 0; i < betCodeArray.length; i++) {
                String[] numArray = num.split("\\,");
                sbBetCode.append((betCodeArray[i] + numArray[0]) + "," + (betCodeArray[i] + numArray[1]) +
                    "," + (betCodeArray[i] + numArray[2]) + "," + (betCodeArray[i] + numArray[3]) + "," +
                    (betCodeArray[i] + numArray[4]));
                sbBetCode.append(",");
            }
            return sbBetCode.delete(sbBetCode.length() - 1, sbBetCode.length()).toString() + codeExtra;
        }
        else if (type.equals("108")) {
            String[] betCodeArray = betCode.split("\\|");
            return betCodeArray[0] + betCodeArray[1] + codeExtra;
        }
        else if (type.equals("109")) {
            StringBuilder sbBetCode = new StringBuilder();
            String[] betCodeArray = betCode.split("\\|");
// for (int i = 0; i < betCodeArray.length; i++) {
            String[] betCodeArraySub01 = betCodeArray[0].split("\\,");
            String[] betCodeArraySub02 = betCodeArray[1].split("\\,");
            for (int j = 0; j < betCodeArraySub01.length; j++) {
                sbBetCode.append(betCodeArraySub01[j] + betCodeArraySub02[j]);
                sbBetCode.append(",");
            }
// }
            return sbBetCode.delete(sbBetCode.length() - 1, sbBetCode.length()).toString() + codeExtra;
        }
        else if (type.equals("116")) {
            return "123,234,345,456" + codeExtra;
        }
        else
            return betCode;
    }
}
