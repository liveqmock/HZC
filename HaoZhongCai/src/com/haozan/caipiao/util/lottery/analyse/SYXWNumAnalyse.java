package com.haozan.caipiao.util.lottery.analyse;

import com.haozan.caipiao.util.error.LocalExceptionHandler;
/**
 * 11选5投注号码格式解析，样例任选04,05,07,10,11 前三直选02,05|01,07|08,09
 *
 * @author peter_wang
 * @create-time 2013-11-19 上午10:45:09
 */
public class SYXWNumAnalyse
    extends DigitalNumAnalyseBasic
    implements DigitalNumInterface {

    private static final String RXER = "任选二";
    private static final String RXSAN = "任选三";
    private static final String RXSI = "任选四";
    private static final String RXWU = "任选五";
    private static final String RXLIU = "任选六";
    private static final String RXQI = "任选七";
    private static final String RXBA = "任选八";
    private static final String QIANYI = "前一";
    private static final String QIANER_ZHIX_DANSHI = "前二直选单式";
    private static final String QIANER_ZHIX_FUSHI = "前二直选复式";
    private static final String QIANER_ZUX = "前二组选";
    private static final String QIANSAN_ZHIX_DANSHI = "前三直选单式";
    private static final String QIANSAN_ZHIX_FUSHI = "前三直选复式";
    private static final String QIANSAN_ZUX = "前三组选";

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

                    String betWay = betWay(eachCode[i]);
                    str.append("[" + betWay + "] ");

                    str.append("%" + lotteryNum[3] + "%");

                    if (openNum == null) {
                        str.append(showNum(betWay, lotteryNum[0]));
                    }
                    else {
                        str.append(showNum(betWay, lotteryNum[0], openNum));
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

    private String betWay(String code) {
        String[] lotteryNum = code.split("\\:");

        String way = UNKNOWN_WAY;
        if (lotteryNum[1].equals("11_RX2")) {
            way = RXER;
        }
        else if (lotteryNum[1].equals("11_RX3")) {
            way = RXSAN;
        }
        else if (lotteryNum[1].equals("11_RX4")) {
            way = RXSI;
        }
        else if (lotteryNum[1].equals("11_RX5")) {
            way = RXWU;
        }
        else if (lotteryNum[1].equals("11_RX6")) {
            way = RXLIU;
        }
        else if (lotteryNum[1].equals("11_RX7")) {
            way = RXQI;
        }
        else if (lotteryNum[1].equals("11_RX8")) {
            way = RXBA;
        }
        else if (lotteryNum[1].equals("11_RX1")) {
            way = QIANYI;
        }
        else if (lotteryNum[1].equals("11_ZXQ2_D")) {
            way = QIANER_ZHIX_DANSHI;
        }
        else if (lotteryNum[1].equals("11_ZXQ2_F")) {
            way = QIANER_ZHIX_FUSHI;
        }
        else if (lotteryNum[1].equals("11_ZXQ2")) {
            way = QIANER_ZUX;
        }
        else if (lotteryNum[1].equals("11_ZXQ3_D")) {
            way = QIANSAN_ZHIX_DANSHI;
        }
        else if (lotteryNum[1].equals("11_ZXQ3_F")) {
            way = QIANSAN_ZHIX_FUSHI;
        }
        else if (lotteryNum[1].equals("11_ZXQ3")) {
            way = QIANSAN_ZUX;
        }

        return way;
    }

    private String showNum(String betWay, String num) {
        if (num.contains("(")) {// 胆拖玩法类似(01,02,03)04,05
            return showNumDantuoWay(num);
        }
        else {// 前二前三是类似01,02|02,03的形式
            return showDivideRedNum(num);
        }
    }

    private String showNumDantuoWay(String num) {
        String[] nums = num.split(")");
        nums[0] = nums[0].substring(1, nums[0].length());

        String[] danCode = nums[0].split(",");
        String[] tuoCode = nums[1].split(",");

        StringBuilder sb = new StringBuilder();
        sb.append("<font color='" + RED_NUM_LIGHT_COLOR + "'>胆");
        for (int i = 0; i < danCode.length; i++) {
            sb.append(danCode[i] + " ");
        }

        sb.append("拖");
        for (int i = 0; i < tuoCode.length; i++) {
            sb.append(tuoCode[i] + " ");
        }
        sb.append("</font>");
        return sb.toString();
    }

    private String showNum(String betWay, String num, String openNum) {
        if (num.contains("(")) {// 胆拖玩法类似(01,02,03)04,05
            return showNumDantuoWay(num, openNum);
        }
        else {// 前二前三是类似01,02|02,03的形式
            return showNumNormalWay(num, openNum);
        }
    }

    private String showNumDantuoWay(String num, String openNum) {
        String[] nums = num.split(")");
        nums[0] = nums[0].substring(1, nums[0].length());// 11选5胆拖形式是（01,02）03,04，去除括号生成胆和拖

        String[] danCode = nums[0].split(",");
        String[] tuoCode = nums[1].split(",");

        StringBuilder sb = new StringBuilder();
        sb.append("<font color='" + RED_NUM_LIGHT_COLOR + "'>胆</font>");
        for (int i = 0; i < danCode.length; i++) {
            if (openNum.contains(danCode[i])) {
                sb.append(getDarkRedNum(danCode[i]));
            }
            else {
                sb.append(getLightRedNum(danCode[i]));
            }
        }

        sb.append("<font color='" + RED_NUM_LIGHT_COLOR + "'>拖</font>");
        for (int i = 0; i < tuoCode.length; i++) {
            if (openNum.contains(tuoCode[i])) {
                sb.append(getDarkRedNum(tuoCode[i]));
            }
            else {
                sb.append(getLightRedNum(tuoCode[i]));
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
