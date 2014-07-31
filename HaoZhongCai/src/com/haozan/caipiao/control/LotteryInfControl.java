package com.haozan.caipiao.control;

import java.text.DecimalFormat;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.haozan.caipiao.R;
import com.haozan.caipiao.netbasic.AsyncConnectionBasic;
import com.haozan.caipiao.request.LotteryInfRequest;
import com.haozan.caipiao.taskbasic.TaskPoolService;
import com.haozan.caipiao.types.HallItem;
import com.haozan.caipiao.types.LotteryInf;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.TimeUtils;

/**
 * 彩种信息control
 * 
 * @author peter_wang
 * @create-time 2013-10-16 上午11:19:10
 */
public class LotteryInfControl {
    private Context mContext;

    private Handler mHandler;

    public LotteryInfControl(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
    }

    /**
     * @param kind 普通彩种还是高频彩，LotteryInfRequest.LOTTERY_NORMAL_KIND和LotteryInfRequest.LOTTERY_QUICK_KIND
     * @param lottery 彩种名称
     */
    public void getLotteryInf(String lotteryId) {
        if (HttpConnectUtil.isNetworkAvailable(mContext)) {
            TaskPoolService.getInstance().requestService(new AsyncConnectionBasic(
                                                                                  new LotteryInfRequest(
                                                                                                        mContext,
                                                                                                        mHandler,
                                                                                                        lotteryId)));
        }
        else {
            Message message =
                Message.obtain(mHandler, ControlMessage.REVEICE_LOTTERY_INF, 0, 405,
                               mContext.getString(R.string.network_not_avaliable));
            message.sendToTarget();
        }
    }

    /**
     * @param kind 普通彩种还是高频彩，LotteryInfRequest.LOTTERY_NORMAL_KIND和LotteryInfRequest.LOTTERY_QUICK_KIND
     * @param lottery 彩种名称
     */
    public void getLotteryInf(int kind) {
        if (HttpConnectUtil.isNetworkAvailable(mContext)) {
            TaskPoolService.getInstance().requestService(new AsyncConnectionBasic(
                                                                                  new LotteryInfRequest(
                                                                                                        mContext,
                                                                                                        mHandler,
                                                                                                        kind)));
        }
        else {
            Message message =
                Message.obtain(mHandler, ControlMessage.REVEICE_LOTTERY_INF, kind, 405,
                               mContext.getString(R.string.network_not_avaliable));
            message.sendToTarget();
        }
    }

    public void setLotteryInf() {
        if (HttpConnectUtil.isNetworkAvailable(mContext)) {
            getLotteryInf(LotteryInfRequest.LOTTERY_NORMAL_KIND);
            getLotteryInf(LotteryInfRequest.LOTTERY_QUICK_KIND);
        }
        else {
            Message message =
                Message.obtain(mHandler, ControlMessage.REVEICE_LOTTERY_INF,
                               LotteryInfRequest.LOTTERY_NORMAL_KIND, 405,
                               mContext.getString(R.string.network_not_avaliable));
            message.sendToTarget();
        }
    }

    /**
     * 倒计时检测是否要更新时间显示，时间大于1小时则一分钟更新一次
     * 
     * @param lastTimeMillis
     * @return
     */
    public boolean checkTimeChange(long lastTimeMillis) {
        int seconds = (int) lastTimeMillis / 1000;
        if (seconds < 3600 || seconds % 60 == 0) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 初始化投注彩种信息
     * 
     * @param item 需要初始化的item
     * @param lotteryId 彩种id
     * @return 是否初始化成功
     */
    public boolean creatHallItem(HallItem item, String lotteryId) {
        boolean isSuccessGetData = true;

        item.setId(lotteryId);

        if (lotteryId.equals(LotteryUtils.GUESS) == false) {
            LotteryInf lottery = HallControl.sLotteryInf.get(lotteryId);

            if (lottery != null) {
                item.setIcon(lottery.getIcon());
                item.setName(lottery.getName());
                item.setLastTerm("第" + lottery.getLastTerm() + "期");
                item.setLastNum(lottery.getLastNum());
                item.setLastOpenTime(lottery.getLastOpenTime().substring(5));

                String extra = lottery.getExtraInf();
                if (TextUtils.isEmpty(extra) == false) {
                    if (lotteryId.equals("3d")) {
                        char[] testNum = extra.toCharArray();
                        if (testNum != null && testNum.length == 3) {
                            item.setExtraInf("试机号：" + testNum[0] + "," + testNum[1] + "," + testNum[2]);
                        }
                    }
                    else {
                        if ((lotteryId.equals("ssq") && (Double.valueOf(extra) / 100000000) > 8) ||
                            (lotteryId.equals("dlt") && (Double.valueOf(extra) / 100000000) > 4)) {
                            item.setExtraInf("奖池:" + "飙升" + getLotteryJackpot(extra) + "亿");
                        }
                        else {
                            item.setExtraInf("奖池：" + getLotteryJackpot(extra) + "亿");
                        }
                    }
                }
                item.setLastTime(TimeUtils.getCountDownTime(lottery.getLastTimeMillis()));
            }
            else {
                isSuccessGetData = false;

                for (int j = 0; j < LotteryUtils.LOTTERY_ID.length; j++) {
                    if (lotteryId.equals(LotteryUtils.LOTTERY_ID[j])) {
                        item.setIcon(LotteryUtils.HALL_ITEM_ICON[j]);
                        item.setName(LotteryUtils.LOTTERY_NAMES[j]);
                        item.setLastNum(LotteryUtils.HALL_DEFALUT_CONTENT[j]);
                    }
                }

                item.setLastTerm("    期");
            }
        }

        return isSuccessGetData;
    }

    private String getLotteryJackpot(String jackpot) {
        DecimalFormat myFormatter = new DecimalFormat("#.##");
        return myFormatter.format(Double.valueOf(jackpot) / 100000000);
    }
}
