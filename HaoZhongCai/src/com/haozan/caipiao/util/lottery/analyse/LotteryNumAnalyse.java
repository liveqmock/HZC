package com.haozan.caipiao.util.lottery.analyse;

import android.content.Context;
import android.widget.LinearLayout;

import com.haozan.caipiao.types.order.jczq.JCZQOrderDetail;
import com.haozan.caipiao.util.LotteryUtils;

public class LotteryNumAnalyse {

    /**
     * 生成相应的投注操作类
     * 
     * @param lotteryKind
     * @return
     */
    public static DigitalNumInterface createLotteryBet(String lotteryKind) {
        DigitalNumInterface lotteryBetInterface = null;
        if (lotteryKind.equals(LotteryUtils.SSQ)) {
            lotteryBetInterface = new SSQNumAnalyse();
        }
        else if (lotteryKind.equals(LotteryUtils.SD)) {
            lotteryBetInterface = new SDNumAnalyse();
        }
        else if (lotteryKind.equals(LotteryUtils.DLT)) {
            lotteryBetInterface = new DLTNumAnalyse();
        }

        return lotteryBetInterface;
    }

    /**
     * 数字彩显示订单号码
     * 
     * @param context
     * @param lotteryId
     * @param orderLayout
     * @param codes
     */
    public static void showOrderNum(Context context, String lotteryId, LinearLayout orderLayout, String codes) {
        if (LotteryUtils.isDigitalBet(lotteryId)) {
            DigitalNumInterface lotteryBetInterface = createLotteryBet(lotteryId);
            lotteryBetInterface.showLotteryOrderViews(context, orderLayout, codes);
        }
    }

    /**
     * 分解竞彩足球json数据为JCZQOrderDetail对象
     * 
     * @param context
     * @param lotteryId
     * @param orderLayout
     * @param json
     * @return
     */
    public static JCZQOrderDetail getSportOrderAnalyse(Context context, String lotteryId, String json) {
        if (LotteryUtils.JCZQ.equals(lotteryId)) {
            JCZQOrderDetail orderDetail = JCZQNumAnalyse.analyseBetCode(json);
            return orderDetail;
        }

        return null;
    }

    /**
     * 竞彩足球订单详情显示购买的比赛情况
     * 
     * @param context
     * @param lotteryId
     * @param orderLayout
     * @param orderDetail
     */
    public static void showSportOrderTeam(Context context, String lotteryId, LinearLayout orderLayout,
                                          JCZQOrderDetail orderDetail) {
        if (LotteryUtils.JCZQ.equals(lotteryId)) {
            JCZQNumAnalyse jczqBet = new JCZQNumAnalyse();
            jczqBet.showLotteryOrderViews(context, orderLayout, orderDetail);
        }
    }

}
