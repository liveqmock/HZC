package com.haozan.caipiao.util.lottery.analyse;

import com.haozan.caipiao.util.error.LocalExceptionHandler;
/**
 * 七星彩投注号码格式解析，样例345,1,35,1,08,5,25
 *
 * @author peter_wang
 * @create-time 2013-11-19 上午10:37:42
 */
public class QXCNumAnalyse
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

    /**
     * 显示投注号码，跟开奖号码做匹配，解析后格式：玩法%倍数%号码，号码中中奖号码会做特殊显示
     * 
     * @param codes
     * @param openNum
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
        StringBuilder sb = new StringBuilder();

        String[] nums = num.split(",");
        String[] openNums = openNum.split(",");
        char[] charOpenNums = new char[openNums.length];
        for (int i = 0; i < openNums.length; i++) {
            charOpenNums[i] = openNums[i].charAt(0);
        }

        for (int i = 0; i < nums.length; i++) {
            for (int j = 0; j < nums[i].length(); j++) {
                if (nums[i].charAt(j) == charOpenNums[i]) {
                    sb.append(getDarkRedNum(nums[i].charAt(j) + ""));
                }
                else {
                    sb.append(getLightRedNum(nums[i].charAt(j) + ""));
                }
            }

            if (i != nums.length - 1) {
                sb.append("<font color='" + RED_NUM_LIGHT_COLOR + "'>");
                sb.append("| ");
                sb.append("</font>");
            }
        }

        return sb.toString();
    }

    private String showNum(String num) {
        StringBuilder sb = new StringBuilder();

        String[] nums = num.split(",");
        sb.append("<font color='" + RED_NUM_LIGHT_COLOR + "'>");
        for (int i = 0; i < nums.length; i++) {
            for (int j = 0; j < nums[i].length(); j++) {
                sb.append(nums[i].charAt(j) + " ");
            }

            if (i != nums.length - 1) {
                sb.append("| ");
            }
        }
        sb.append("</font>");

        return sb.toString();
    }
}
