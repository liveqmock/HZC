package com.haozan.caipiao.util.lottery.analyse;

import com.haozan.caipiao.util.error.LocalExceptionHandler;
/**
 * 双色球投注号码格式解析，样例01,04,06,10,11,14,16|06,10 胆拖01,04$03,07,08,11|13
 *
 * @author peter_wang
 * @create-time 2013-11-19 上午10:42:12
 */
public class SSQNumAnalyse
    extends DigitalNumAnalyseBasic
    implements DigitalNumInterface {

    private static final String SSQ_DANTUO = "5";

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
        String[] danCode = lotteryRed[0].split(",");
        String[] tuoCode = lotteryRed[1].split(",");
        String[] blueBall = lotteryNum[1].split(",");

        StringBuilder sb = new StringBuilder();
        sb.append("<font color='" + RED_NUM_LIGHT_COLOR + "'>胆");
        for (int i = 0; i < danCode.length; i++) {
            sb.append(danCode[i] + " ");
        }

        sb.append(" 拖");
        for (int i = 0; i < tuoCode.length; i++) {
            sb.append(tuoCode[i] + " ");
        }
        sb.append("</font>");

        sb.append("<font color='" + BLUE_NUM_LIGHT_COLOR + "'> ");
        for (int i = 0; i < blueBall.length; i++) {
            sb.append(blueBall[i] + " ");
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

                    if (lotteryNum[2].equals(DANSHI)) {
                        str.append("[单式] ");
                    }
                    else if (lotteryNum[2].equals(FUSHI)) {
                        str.append("[复式] ");
                    }
                    else if (lotteryNum[2].equals(SSQ_DANTUO)) {
                        str.append("[胆拖]");
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
        String[] blueBall = lotteryNum[1].split(",");

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
