package com.haozan.caipiao.util.lottery.analyse;

import com.haozan.caipiao.util.error.LocalExceptionHandler;
/**
 * 时时彩号码格式解析，样例同3D
 *
 * @author peter_wang
 * @create-time 2013-11-19 上午10:39:43
 */
public class SSCNumAnalyse
    extends DigitalNumAnalyseBasic
    implements DigitalNumInterface {

    private static final String WXTXDS = "五星通选单式";
    private static final String WXTXFS = "五星通选复式";
    private static final String WXZXDS = "五星直选单式";
    private static final String WXZXFS = "五星直选复式";
    private static final String SIXZXDS = "四星直选单式";
    private static final String SIXZXFS = "四星直选复式";
    private static final String SXZLDS = "三星组六单式";
    private static final String SXZLFS = "三星组六复式";
    private static final String SXZS = "三星组三";
    private static final String SANXZXDS = "三星直选单式";
    private static final String SANXZXFS = "三星直选复式";
    private static final String EXZXDS = "二星直选单式";
    private static final String EXZXFS = "二星直选复式";
    // TODO 适用于旧版时时彩
    private static final String EXZX = "二星组选";
    private static final String RXEDS = "任选二单式";
    private static final String RXEFS = "任选二复式";
    private static final String YXZXDS = "一星直选单式";
    private static final String YXZXFS = "一星直选复式";
    private static final String RXYDS = "任选一单式";
    private static final String RXYFS = "任选一复式";
    private static final String DXDS = "大小单双";

    private static final String[] DXDS_DESC = {"大", "小", "单", "双"};

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

    private String showNum(String betWay, String num, String openNum) {
        if (betWay.equals(WXTXDS) || betWay.equals(WXTXFS) || betWay.equals(WXZXDS) || betWay.equals(WXTXFS)) {
            return showWX(num, openNum);
        }
        else if (betWay.equals(SIXZXDS) || betWay.equals(SIXZXFS)) {
            return showSIXZX(num, openNum);
        }
        else if (betWay.equals(SANXZXDS) || betWay.equals(SANXZXFS)) {
            return showSANXZX(num, openNum);
        }
        else if (betWay.equals(EXZXDS) || betWay.equals(EXZXFS)) {
            return showEXZX(num, openNum);
        }
        else if (betWay.equals(YXZXDS) || betWay.equals(YXZXFS)) {
            return showYXZX(num, openNum);
        }
        else if (betWay.equals(SXZLDS) || betWay.equals(SXZLFS)) {
            return showZLBH(betWay, num, openNum);
        }
        else if (betWay.equals(SXZS)) {
            return showZSBH(betWay, num, openNum);
        }
        else if (betWay.equals(EXZX)) {
            return showEXBH(betWay, num, openNum);
        }
        else if (betWay.equals(RXEDS) || betWay.equals(RXEFS) || betWay.equals(RXYDS) || betWay.equals(RXYFS)) {
            return showRENXUAN(num, openNum);
        }
        else if (betWay.equals(DXDS)) {
            return showDXDS(num, openNum);
        }

        return null;
    }

    /**
     * 五星玩法
     * 
     * @param num 如01,249,3,4,5
     * @param openNum 如0,3,4,2,3
     * @return
     */
    private String showWX(String num, String openNum) {
        return showZHIXUAN(num, openNum);
    }

    private String showSIXZX(String num, String openNum) {
        openNum = openNum.substring(2, openNum.length());
        return showZHIXUAN(num, openNum);
    }

    private String showSANXZX(String num, String openNum) {
        openNum = openNum.substring(4, openNum.length());
        return showZHIXUAN(num, openNum);
    }

    private String showEXZX(String num, String openNum) {
        openNum = openNum.substring(6, openNum.length());
        return showZHIXUAN(num, openNum);
    }

    private String showYXZX(String num, String openNum) {
        openNum = openNum.substring(8, openNum.length());
        return showZHIXUAN(num, openNum);
    }

    private String showZSBH(String betWay, String num, String openNum) {
        openNum = openNum.substring(4, openNum.length());
        String[] openNums = openNum.split(",");
        if (sameNum(openNums) != 1) {
            return showNum(betWay, num);
        }

        return showBAOHAO(num, openNum);
    }

    private String showZLBH(String betWay, String num, String openNum) {
        openNum = openNum.substring(4, openNum.length());
        String[] openNums = openNum.split(",");
        if (sameNum(openNums) != 0) {
            return showNum(betWay, num);
        }

        return showBAOHAO(num, openNum);
    }

    private String showEXBH(String betWay, String num, String openNum) {
        openNum = openNum.substring(6, openNum.length());
        String[] openNums = openNum.split(",");
        if (openNums[0] == openNums[1]) {
            return showNum(betWay, num);
        }

        return showBAOHAO(num, openNum);
    }

    private String showRENXUAN(String num, String openNum) {
        return showZHIXUAN(num, openNum);
    }

    private String showDXDS(String num, String openNum) {
        openNum = openNum.substring(6, openNum.length());

        StringBuilder sb = new StringBuilder();

        String[] nums = num.split(",");
        String[] openNums = openNum.split(",");
        int shiwei = Integer.valueOf(openNums[0]);
        int gewei = Integer.valueOf(openNums[1]);

        for (int j = 0; j < nums[0].length(); j++) {
            int choose = Integer.valueOf(nums[0].charAt(j)) - 1;
            if (detectWinDXDS(choose, shiwei)) {
                sb.append(getDarkRedNum(DXDS_DESC[choose]));
            }
            else {
                sb.append(getLightRedNum(DXDS_DESC[choose]));
            }
        }

        sb.append("<font color='" + RED_NUM_LIGHT_COLOR + "'>");
        sb.append("| ");
        sb.append("</font>");

        for (int j = 0; j < nums[1].length(); j++) {
            int choose = Integer.valueOf(nums[1].charAt(j)) - 1;
            if (detectWinDXDS(choose, gewei)) {
                sb.append(getDarkRedNum(DXDS_DESC[choose]));
            }
            else {
                sb.append(getLightRedNum(DXDS_DESC[choose]));
            }
        }

        return sb.toString();
    }

    /**
     * 判断是否大小单双
     * 
     * @param choose 0-3代表大小单双
     * @param result 出来的结果，0-9
     * @return
     */
    private boolean detectWinDXDS(int choose, int result) {
        if (choose == 0) {
            return result >= 5;
        }
        else if (choose == 1) {
            return result < 5;
        }
        else if (choose == 2) {
            return result % 2 == 1;
        }
        else if (choose == 3) {
            return result % 2 == 0;
        }
        return false;
    }

    private String betWay(String code) {
        String[] lotteryNum = code.split("\\:");

        String betWay = UNKNOWN_WAY;
        if (lotteryNum[1].equals("511")) {
            betWay = WXTXDS;
        }
        else if (lotteryNum[1].equals("512")) {
            betWay = WXTXFS;
        }
        else if (lotteryNum[1].equals("501")) {
            betWay = WXZXDS;
        }
        else if (lotteryNum[1].equals("502")) {
            betWay = WXZXFS;
        }
        else if (lotteryNum[1].equals("401")) {
            betWay = SIXZXDS;
        }
        else if (lotteryNum[1].equals("402")) {
            betWay = SIXZXFS;
        }
        else if (lotteryNum[1].equals("361")) {
            betWay = SANXZXDS;
        }
        else if (lotteryNum[1].equals("362")) {
            betWay = SANXZXFS;
        }
        else if (lotteryNum[1].equals("332")) {
            betWay = SXZS;
        }
        else if (lotteryNum[1].equals("301")) {
            betWay = SANXZXDS;
        }
        else if (lotteryNum[1].equals("302")) {
            betWay = SANXZXFS;
        }
        else if (lotteryNum[1].equals("201")) {
            betWay = EXZXDS;
        }
        else if (lotteryNum[1].equals("202")) {
            betWay = EXZXFS;
        }
        else if (lotteryNum[1].equals("911")) {
            betWay = RXEDS;
        }
        else if (lotteryNum[1].equals("912")) {
            betWay = RXEFS;
        }
        else if (lotteryNum[1].equals("101")) {
            betWay = YXZXDS;
        }
        else if (lotteryNum[1].equals("102")) {
            betWay = YXZXFS;
        }
        else if (lotteryNum[1].equals("901")) {
            betWay = RXYDS;
        }
        else if (lotteryNum[1].equals("902")) {
            betWay = RXYFS;
        }
        else if (lotteryNum[1].equals("701")) {
            betWay = DXDS;
        }

        return betWay;
    }

    private String showNum(String betWay, String num) {
        StringBuilder sb = new StringBuilder();

        String[] nums = num.split(",");
        sb.append("<font color='" + RED_NUM_LIGHT_COLOR + "'>");
        if (betWay.equals(DXDS)) {
            for (int i = 0; i < nums.length; i++) {
                for (int j = 0; j < nums[i].length(); j++) {
                    sb.append(DXDS_DESC[Integer.valueOf(nums[i].charAt(j))] + " ");
                }

                if (i != nums.length - 1) {
                    sb.append("| ");
                }
            }
        }
        else if (isExistDivide(betWay)) {
            for (int i = 0; i < nums.length; i++) {
                sb.append(nums[i] + " ");
            }
        }
        else {
            for (int i = 0; i < nums.length; i++) {
                for (int j = 0; j < nums[i].length(); j++) {
                    sb.append(nums[i].charAt(j) + " ");
                }

                if (i != nums.length - 1) {
                    sb.append("| ");
                }
            }
        }
        sb.append("</font>");

        return sb.toString();
    }

    private boolean isExistDivide(String betWay) {
        if (betWay.equals(SXZLDS) || betWay.equals(SXZLFS) || betWay.equals(SXZS) || betWay.equals(EXZX) ||
            betWay.equals(YXZXDS) || betWay.equals(YXZXFS)) {
            return true;
        }
        else {
            return false;
        }
    }
}
