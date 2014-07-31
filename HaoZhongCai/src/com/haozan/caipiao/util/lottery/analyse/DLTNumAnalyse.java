package com.haozan.caipiao.util.lottery.analyse;

import android.text.TextUtils;

import com.haozan.caipiao.util.error.LocalExceptionHandler;
/**
 * 大乐透投注号码解析，投注样例03,05,09,11,14|02,05，胆拖02,04$01,06,09,10|$12
 *
 * @author peter_wang
 * @create-time 2013-11-19 上午10:28:51
 */
public class DLTNumAnalyse
    extends DigitalNumAnalyseBasic
    implements DigitalNumInterface {

    /**
     * 解析后格式：玩法%倍数%号码
     * 
     * @param codes
     * @return
     */
    @Override
    public String[] analyseBetCode(String codes) {
        return analyseBetCode(codes, null);
    }

    private String showNum(String num) {
        if (num.contains("$")) {
            return showNumDantuoWay(num);
        }
        else {
            return showNumNormalWay(num);
        }
    }

    private String showNumDantuoWay(String num) {
        String[] lotteryNum = num.split("\\|");
        String[] lotteryRed = lotteryNum[0].split("\\$");
        String[] redDanCode = lotteryRed[0].split(",");
        String[] redTuoCode = lotteryRed[1].split(",");

        StringBuilder sb = new StringBuilder();
        sb.append("<font color='" + RED_NUM_LIGHT_COLOR + "'>胆");
        for (int i = 0; i < redDanCode.length; i++) {
            sb.append(redDanCode[i] + " ");
        }

        sb.append(" 拖");
        for (int i = 0; i < redTuoCode.length; i++) {
            sb.append(redTuoCode[i] + " ");
        }
        sb.append("</font>");

        sb.append("<font color='" + BLUE_NUM_LIGHT_COLOR + "'>");
        String[] lotteryBlue = lotteryNum[1].split("\\$");
        String[] blueTuoCode = lotteryBlue[1].split(",");
        // 胆为空代表蓝球没选胆，只是普通选择方式
        if (TextUtils.isEmpty(lotteryBlue[0])) {
            sb.append(" ");
            for (int i = 0; i < blueTuoCode.length; i++) {
                sb.append(blueTuoCode[i] + " ");
            }
        }
        else {
            String[] blueDanCode = lotteryBlue[0].split(",");
            sb.append(" 胆");
            for (int i = 0; i < blueDanCode.length; i++) {
                sb.append(blueDanCode[i] + " ");
            }

            sb.append(" 拖");
            for (int i = 0; i < blueTuoCode.length; i++) {
                sb.append(blueTuoCode[i] + " ");
            }
        }
        sb.append("</font>");

        return sb.toString();
    }

    private String showNumNormalWay(String num) {
        String[] nums = num.split("\\|");
        String[] redBall = nums[0].split(",");
        String[] blueBall = nums[1].split(",");

        StringBuilder sb = new StringBuilder();
        sb.append("<font color='" + RED_NUM_LIGHT_COLOR + "'>");
        for (int i = 0; i < redBall.length; i++) {
            sb.append(redBall[i] + " ");
        }
        sb.append("</font>");

        sb.append("<font color='" + BLUE_NUM_LIGHT_COLOR + "'> ");
        for (int i = 0; i < blueBall.length; i++) {
            sb.append(blueBall[i] + " ");
        }
        sb.append("</font>");

        return sb.toString();
    }

    /**
     * 显示投注号码，跟开奖号码做匹配，解析后格式：玩法%倍数%号码，号码中中奖号码会做特殊显示
     * 
     * @param codes
     * @return
     */
    @Override
    public String[] analyseBetCode(String codes, String openNum) {
        String[] analyseResult = null;
        try {
            if (codes != null) {
                String[] eachCode = codes.split(";");
                analyseResult = new String[eachCode.length];

                StringBuilder str = new StringBuilder();
                for (int i = 0; i < eachCode.length; i++) {
                    String[] lotteryNum = eachCode[i].split("\\:");

                    if (lotteryNum[1].equals("3")) {
                        str.append("[生肖乐] ");
                    }
                    else {
                        if (lotteryNum[2].equals("5")) {
                            str.append("[胆拖] ");
                        }
                        else if (lotteryNum[2].equals("1")) {
                            str.append("[单式] ");
                        }
                        else if (lotteryNum[2].equals("2")) {
                            str.append("[复式] ");
                        }
                        if (lotteryNum[1].equals("2")) {
                            str.append("追加");
                        }
                    }

                    str.append("%" + lotteryNum[3] + "%");

                    if (openNum == null) {
                        str.append(showNum(lotteryNum[0]));
                    }
                    else {
                        str.append(showNum(lotteryNum[0], openNum));
                    }

                    analyseResult[i] = str.toString();

                    str.delete(0, str.length());
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            LocalExceptionHandler.exportExceptionInf(e);
            return null;
        }

        return analyseResult;
    }

    private String showNum(String num, String openNum) {
        if (num.contains("$")) {
            return showNumDantuoWay(num, openNum);
        }
        else {
            return showNumNormalWay(num, openNum);
        }
    }

    private String showNumDantuoWay(String num, String openNum) {
        String[] openNums = openNum.split("\\|");

        String[] lotteryNum = num.split("\\|");
        String[] lotteryRed = lotteryNum[0].split("\\$");
        String[] danCode = lotteryRed[0].split(",");
        String[] tuoCode = lotteryRed[1].split(",");

        StringBuilder sb = new StringBuilder();
        sb.append("<font color='" + RED_NUM_LIGHT_COLOR + "'>胆</font>");
        for (int i = 0; i < danCode.length; i++) {
            if (openNums[0].contains(danCode[i])) {
                sb.append(getDarkRedNum(danCode[i]));
            }
            else {
                sb.append(getLightRedNum(danCode[i]));
            }
        }

        sb.append("<font color='" + RED_NUM_LIGHT_COLOR + "'>拖</font>");
        for (int i = 0; i < tuoCode.length; i++) {
            if (openNums[0].contains(tuoCode[i])) {
                sb.append(getDarkRedNum(tuoCode[i]));
            }
            else {
                sb.append(getLightRedNum(tuoCode[i]));
            }
        }

        String[] lotteryBlue = lotteryNum[1].split("\\$");
        String[] blueTuoCode = lotteryBlue[1].split(",");
        // 胆为空代表蓝球没选胆，只是普通选择方式
        if (TextUtils.isEmpty(lotteryBlue[0])) {
            sb.append(" ");
            for (int i = 0; i < blueTuoCode.length; i++) {
                if (openNums[1].contains(blueTuoCode[i])) {
                    sb.append(getDarkBlueNum(blueTuoCode[i]));
                }
                else {
                    sb.append(getLightBlueNum(blueTuoCode[i]));
                }
            }
        }
        else {
            String[] blueDanCode = lotteryBlue[0].split(",");
            sb.append("<font color='" + RED_NUM_LIGHT_COLOR + "'>胆</font>");
            for (int i = 0; i < blueDanCode.length; i++) {
                if (openNums[1].contains(blueDanCode[i])) {
                    sb.append(getDarkBlueNum(blueDanCode[i]));
                }
                else {
                    sb.append(getLightBlueNum(blueDanCode[i]));
                }
            }

            sb.append("<font color='" + RED_NUM_LIGHT_COLOR + "'>拖</font>");
            for (int i = 0; i < blueTuoCode.length; i++) {
                if (openNums[1].contains(blueTuoCode[i])) {
                    sb.append(getDarkBlueNum(blueTuoCode[i]));
                }
                else {
                    sb.append(getLightBlueNum(blueTuoCode[i]));
                }
            }
        }

        return sb.toString();
    }

    private String showNumNormalWay(String num, String openNum) {
        String[] openNums = openNum.split("\\|");

        String[] nums = num.split("\\|");
        String[] redBall = nums[0].split(",");
        String[] blueBall = nums[1].split(",");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < redBall.length; i++) {
            if (openNums[0].contains(redBall[i])) {
                sb.append(getDarkRedNum(redBall[i]));
            }
            else {
                sb.append(getLightRedNum(redBall[i]));
            }
        }

        for (int i = 0; i < blueBall.length; i++) {
            if (openNums[1].contains(blueBall[i])) {
                sb.append(getDarkBlueNum(blueBall[i]));
            }
            else {
                sb.append(getLightBlueNum(blueBall[i]));
            }
        }

        return sb.toString();
    }
}
