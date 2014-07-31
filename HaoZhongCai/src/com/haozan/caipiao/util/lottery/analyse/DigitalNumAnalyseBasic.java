package com.haozan.caipiao.util.lottery.analyse;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;

public abstract class DigitalNumAnalyseBasic
    implements DigitalNumInterface {

    protected static final String DANSHI = "1";
    protected static final String FUSHI = "2";

    public static final String RED_NUM_LIGHT_COLOR = "#dd8a89";
    public static final String BLUE_NUM_LIGHT_COLOR = "#9bbae4";

    public static final String RED_NUM_DARK_COLOR = "#bc1514";
    public static final String BLUE_NUM_DARK_COLOR = "#3876ca";

    public static final String UNKNOWN_WAY = "未知玩法";

    protected String getDarkRedNum(String num) {
        return "<font color='" + RED_NUM_DARK_COLOR + "'>" + num + " </font>";
    }

    protected String getLightRedNum(String num) {
        return "<font color='" + RED_NUM_LIGHT_COLOR + "'>" + num + " </font>";
    }

    protected String getDarkBlueNum(String num) {
        return "<font color='" + BLUE_NUM_DARK_COLOR + "'>" + num + " </font>";
    }

    protected String getLightBlueNum(String num) {
        return "<font color='" + BLUE_NUM_LIGHT_COLOR + "'>" + num + " </font>";
    }

    @Override
    public void showLotteryOrderViews(Context context, LinearLayout orderLayout, String codes) {
        showLotteryOrderViews(context, orderLayout, codes, null);
    }

    @Override
    public void showLotteryOrderViews(Context context, LinearLayout orderLayout, String codes, String openNum) {
        String[] eachCode = analyseBetCode(codes, openNum);
        if (eachCode != null) {
            for (int i = 0; i < eachCode.length; i++) {
                String[] inf = eachCode[i].split("%");

                View view = View.inflate(context, R.layout.list_item_bet_order_digital, null);
                TextView orderInf = (TextView) view.findViewById(R.id.order_inf);
                orderInf.setText(inf[0] + inf[1] + "倍");
                TextView orderNum = (TextView) view.findViewById(R.id.order_num);
                orderNum.setText(Html.fromHtml(inf[2]));
                orderLayout.addView(view);
            }
        }
    }

    /**
     * 匹配三星直选等直选系列号码中奖
     * 
     * @param num
     * @param openNum
     * @return
     */
    protected String showZHIXUAN(String num, String openNum) {
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

    /**
     * 匹配包号等系列选号方式中奖
     * 
     * @param num
     * @param openNum
     * @return
     */
    protected String showBAOHAO(String num, String openNum) {
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

    /**
     * 返回多少个相同,仅适用于组三组六
     * 
     * @param nums
     * @return
     */
    protected int sameNum(String[] nums) {
        if (nums.length != 3) {
            return -1;
        }

        int num = 0;
        if (nums[0].equals(nums[1])) {
            num++;
        }
        if (nums[0].equals(nums[2])) {
            num++;
        }
        if (nums[1].equals(nums[2])) {
            num++;
        }
        return num;
    }

    /**
     * 前二前三等玩法带|分割的红色球
     * 
     * @param num
     * @return
     */
    protected String showDivideRedNum(String num) {
        String[] nums = num.split("\\|");
        StringBuilder sb = new StringBuilder();
        sb.append("<font color='" + RED_NUM_LIGHT_COLOR + "'>");
        for (int i = 0; i < nums.length; i++) {
            String[] redBall = nums[i].split(",");
            for (int j = 0; j < redBall.length; j++) {
                sb.append(redBall[j] + " ");
            }

            if (i != nums.length - 1) {
                sb.append("| ");
            }
        }
        sb.append("</font>");
        return null;
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
