package com.haozan.caipiao.util.lottery.analyse;

import com.haozan.caipiao.util.error.LocalExceptionHandler;

/**
 * 湖南快乐十分投注号码解析，样例04，06|11,13|01,02
 * 
 * @author peter_wang
 * @create-time 2013-11-19 上午10:31:58
 */
public class HNKLSFNumAnalyse
    extends DigitalNumAnalyseBasic
    implements DigitalNumInterface {
    private static final String[] BET_WAY = {"选一数投", "选一红投", "任选二", "选二连直", "选二连组", "任选三", "选三前直", "选三前组",
            "任选四", "任选五"};
    private static final String[] BET_WAY_CODE = {"101", "102", "20", "22", "21", "30", "34", "33", "40",
            "50"};

    private static final String XYST = BET_WAY[0];
    private static final String XYHT = BET_WAY[1];
    private static final String RXE = BET_WAY[2];
    private static final String XELZHI = BET_WAY[3];
    private static final String XELZU = BET_WAY[4];
    private static final String RXSAN = BET_WAY[5];
    private static final String XSQZHI = BET_WAY[6];
    private static final String XSQZU = BET_WAY[7];
    private static final String RXSI = BET_WAY[8];
    private static final String RXW = BET_WAY[9];

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

        for (int i = 0; i < BET_WAY_CODE.length; i++) {
            if (lotteryNum[1].equals(BET_WAY_CODE[i])) {
                way = BET_WAY[i];
            }
        }

        return way;
    }

    private String showNum(String betWay, String num) {
        // 前二前三是类似01,02|02,03的形式
        return showDivideRedNum(num);
    }

    private String showNum(String betWay, String num, String openNum) {
        if (num.contains("(")) {// 胆拖玩法类似(01,02,03)04,05
            return showNumDantuoWay(num, openNum);
        }
        else {// 前二前三是类似01,02|02,03的形式
// return showNumNormalWay(num, openNum);
            return showNumDantuoWay(num, openNum);
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

}
