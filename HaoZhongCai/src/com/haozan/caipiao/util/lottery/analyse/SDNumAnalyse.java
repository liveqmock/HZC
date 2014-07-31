package com.haozan.caipiao.util.lottery.analyse;

import com.haozan.caipiao.util.error.LocalExceptionHandler;
/**
 * 3D投注号码格式解析，样例直选23,4,07 组三包号3,4,6
 *
 * @author peter_wang
 * @create-time 2013-11-19 上午10:38:43
 */
public class SDNumAnalyse
    extends DigitalNumAnalyseBasic
    implements DigitalNumInterface {

    private static final String ZXDFS = "直选单复式";
    private static final String ZXHZ = "直选和值";
    private static final String ZSBH = "组三包号";
    private static final String ZSDFS = "组三单复式";
    private static final String ZSHZ = "组三和值";
    private static final String ZLDS = "组六单式";
    private static final String ZLBH = "组六包号";
    private static final String ZLHZ = "组六和值";

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
        if (betWay.equals(ZXDFS)) {
            return showZXDFS(num, openNum);
        }
        else if (betWay.equals(ZXHZ)) {
            return showZXHZ(num, openNum);
        }
        else if (betWay.equals(ZSBH)) {
            return showZSBH(num, openNum);
        }
        else if (betWay.equals(ZSDFS)) {
            return showZSDFS(num, openNum);
        }
        else if (betWay.equals(ZSHZ)) {
            return showZSHZ(num, openNum);
        }
        else if (betWay.equals(ZLDS)) {
            return showZLDS(num, openNum);
        }
        else if (betWay.equals(ZLBH)) {
            return showZLBH(num, openNum);
        }
        else if (betWay.equals(ZLHZ)) {
            return showZLHZ(num, openNum);
        }

        return null;
    }

    private String showZXDFS(String num, String openNum) {
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

    private String showZXHZ(String num, String openNum) {
        StringBuilder sb = new StringBuilder();

        String[] nums = num.split(",");
        String[] openNums = openNum.split(",");
        int sum = 0;
        for (int i = 0; i < openNums.length; i++) {
            sum += Integer.valueOf(openNums[i]);
        }

        for (int i = 0; i < nums.length; i++) {
            if (Integer.valueOf(nums[i]) == sum) {
                sb.append(getDarkRedNum(nums[i]));
            }
            else {
                sb.append(getLightRedNum(nums[i]));
            }

            if (i != nums.length - 1) {
                sb.append("<font color='" + RED_NUM_LIGHT_COLOR + "'>");
                sb.append("| ");
                sb.append("</font>");
            }
        }

        return sb.toString();
    }

    private String showZSBH(String num, String openNum) {
        String[] openNums = openNum.split(",");
        if (sameNum(openNums) != 1) {
            return showNum(ZSBH, num);
        }

        return createZSZL(num, openNum);
    }

    private String[] divideZSOpenNum(String[] openNums) {
        String same, different;
        if (openNums[0].equals(openNums[1])) {
            same = openNums[0];
            different = openNums[2];
        }
        else if (openNums[0].equals(openNums[2])) {
            same = openNums[0];
            different = openNums[1];
        }
        else {
            same = openNums[1];
            different = openNums[0];
        }

        return new String[] {same, different};
    }

    private String showZSDFS(String num, String openNum) {
        String[] openNums = openNum.split(",");
        if (sameNum(openNums) != 1) {
            return showNum(ZSDFS, num);
        }

        String[] openDivideNum = divideZSOpenNum(openNums);// 两个string，代表相同和不同的号码
        char[] charOpenNums = new char[openDivideNum.length];
        for (int i = 0; i < openDivideNum.length; i++) {
            charOpenNums[i] = openDivideNum[i].charAt(0);
        }

        StringBuilder sb = new StringBuilder();

        String[] nums = num.split(",");

        if (charOpenNums[0] == nums[0].charAt(0)) {
            sb.append(getDarkRedNum(nums[0]));
        }
        else {
            sb.append(getLightRedNum(nums[0]));
        }

        if (charOpenNums[0] == nums[1].charAt(0)) {
            sb.append(getDarkRedNum(nums[1]));
        }
        else {
            sb.append(getLightRedNum(nums[1]));
        }

        if (charOpenNums[1] == nums[2].charAt(0)) {
            sb.append(getDarkRedNum(nums[0]));
        }
        else {
            sb.append(getLightRedNum(nums[0]));
        }

        return sb.toString();
    }

    private String showZSHZ(String num, String openNum) {
        String[] openNums = openNum.split(",");
        if (sameNum(openNums) != 1) {
            return showNum(ZSHZ, num);
        }

        return showZXHZ(num, openNum);
    }

    private String showZLDS(String num, String openNum) {
        String[] openNums = openNum.split(",");
        if (sameNum(openNums) != 0) {
            return showNum(ZLDS, num);
        }

        return createZSZL(num, openNum);
    }

    private String showZLBH(String num, String openNum) {
        String[] openNums = openNum.split(",");
        if (sameNum(openNums) != 0) {
            return showNum(ZLBH, num);
        }

        return createZSZL(num, openNum);
    }

    private String createZSZL(String num, String openNum) {
        StringBuilder sb = new StringBuilder();

        String[] nums = num.split(",");

        for (int i = 0; i < nums.length; i++) {
            if (openNum.contains(nums[i])) {
                sb.append(getDarkRedNum(nums[i]));
            }
            else {
                sb.append(getLightRedNum(nums[i]));
            }
        }

        return sb.toString();
    }

    private String showZLHZ(String num, String openNum) {
        String[] openNums = openNum.split(",");
        if (sameNum(openNums) != 0) {
            return showNum(ZLHZ, num);
        }

        return showZXHZ(num, openNum);
    }

    private String betWay(String code) {
        String[] lotteryNum = code.split("\\:");

        if (lotteryNum[1].equals("1")) {
            if (lotteryNum[2].equals("1") || lotteryNum[2].equals("2")) {
                return ZXDFS;
            }
            else if (lotteryNum[2].equals("4")) {
                return ZXHZ;
            }
        }
        else if (lotteryNum[1].equals("2")) {
            if (lotteryNum[2].equals("3")) {
                return ZSBH;
            }
            else if (lotteryNum[2].equals("2") || lotteryNum[2].equals("1")) {
                return ZSDFS;
            }
            else if (lotteryNum[2].equals("4")) {
                return ZSHZ;
            }
        }
        else if (lotteryNum[1].equals("3")) {
            if (lotteryNum[2].equals("1")) {
                return ZLDS;
            }
            else if (lotteryNum[2].equals("3")) {
                return ZLBH;
            }
            else if (lotteryNum[2].equals("4")) {
                return ZLHZ;
            }
        }

        return UNKNOWN_WAY;
    }

    private String showNum(String betWay, String num) {
        StringBuilder sb = new StringBuilder();

        String[] nums = num.split(",");
        sb.append("<font color='" + RED_NUM_LIGHT_COLOR + "'>");
        if (betWay.equals(ZXHZ) || betWay.equals(ZSHZ) || betWay.equals(ZLHZ)) {
            for (int i = 0; i < nums.length; i++) {
                sb.append(nums[i] + " ");

                if (i != nums.length - 1) {
                    sb.append("| ");
                }
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
}
