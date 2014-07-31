package com.haozan.caipiao.control.bet;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;

import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.control.LotteryInfControl;
import com.haozan.caipiao.types.HallItem;
import com.haozan.caipiao.util.ActionUtil;

/**
 * 数字彩投注选球control
 * 
 * @author peter_wang
 * @create-time 2013-10-30 下午10:34:55
 */
public class BetDigitalBetControl {

    private Context mContext;
    private Handler mHandler;
    private LotteryApp mAppState;

    private Editor mEditor;
    private SharedPreferences mSharedPreferences;

    private LotteryInfControl mLotteryInfControl;

    public BetDigitalBetControl(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;

        mSharedPreferences = ActionUtil.getSharedPreferences(mContext);
        mEditor = ActionUtil.getEditor(mContext);
        mAppState = (LotteryApp) context.getApplicationContext();

        mLotteryInfControl = new LotteryInfControl(context, handler);
    }

    public boolean checkTimeChange(long lastTimeMillis) {
        return mLotteryInfControl.checkTimeChange(lastTimeMillis);
    }

    /**
     * 初始化投注彩种信息
     * 
     * @param item 需要初始化的item
     * @param lotteryId 彩种id
     * @return 是否初始化成功
     */
    public boolean creatHallItem(HallItem item, String lotteryId) {
        return mLotteryInfControl.creatHallItem(item, lotteryId);
    }

    public void getLotteryInf(String lotteryId) {
        mLotteryInfControl.getLotteryInf(lotteryId);
    }
}
