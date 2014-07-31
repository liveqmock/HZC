package com.haozan.caipiao.control.bet;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.Login;
import com.haozan.caipiao.activity.webbrowser.WebBrowser;
import com.haozan.caipiao.netbasic.AsyncConnectionBasic;
import com.haozan.caipiao.request.bet.UniteJoinOrderRequest;
import com.haozan.caipiao.request.order.OrderSportDetailRequest;
import com.haozan.caipiao.taskbasic.TaskPoolService;
import com.haozan.caipiao.types.bet.UniteJoinSubmitOrder;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.UEDataAnalyse;
import com.haozan.caipiao.util.ViewUtil;

/**
 * 投注支付control
 * 
 * @author peter_wang
 * @create-time 2013-10-30 下午10:34:55
 */
public class BetPayControl {

    private Context mContext;
    private Handler mHandler;
    private LotteryApp mAppState;

    private Editor mEditor;
    private SharedPreferences mSharedPreferences;

    private boolean isReadProtocol = true;
    private boolean isShare = true;

    private double mLatitude;
    private double mLongitude;
    private String mLocation;;

    public BetPayControl(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;

        mSharedPreferences = ActionUtil.getSharedPreferences(mContext);
        mEditor = ActionUtil.getEditor(mContext);
        mAppState = (LotteryApp) context.getApplicationContext();
    }

    public double getmLatitude() {
        return mLatitude;
    }

    public double getmLongitude() {
        return mLongitude;
    }

    public String getmLocation() {
        return mLocation;
    }

    /**
     * 显示投注协议内容
     */
    public void toBetProtocol() {
        Intent inte = new Intent();
        Bundle bun = new Bundle();
        bun.putString("url", LotteryUtils.BET_PROTOCOL_URL);
        bun.putString("title", "委托投注协议");
        inte.putExtras(bun);
        inte.setClass(mContext, WebBrowser.class);
        mContext.startActivity(inte);
    }

    /**
     * 是否已阅读投注协议
     * 
     * @return
     */
    public boolean isReadProtocol() {
        if (isReadProtocol) {
            ViewUtil.showTipsToast(mContext, "请勾选委托投注协议");
        }

        return isReadProtocol;
    }

    /**
     * 点击协议勾选按钮
     * 
     * @param bet
     * @param check
     */
    public void clickProtocol(Button bet, ImageView check) {
        if (isReadProtocol) {
            bet.setEnabled(false);
            isReadProtocol = false;
            check.setBackgroundResource(R.drawable.choosing_not_select);
        }
        else {
            bet.setEnabled(true);
            isReadProtocol = true;
            check.setBackgroundResource(R.drawable.choosing_select);
        }
    }

    public void initLastShareSetting(ImageView ivShareFlag) {
        isShare = mSharedPreferences.getBoolean("bet_transpond", true);
        setSelectIcon(ivShareFlag, isShare);
    }

    /**
     * 点击分享勾选按钮
     * 
     * @param ivTranspondFlag
     */
    public void clickShareContent(ImageView ivTranspondFlag) {
        if (!isShare) {
            isShare = true;
        }
        else {
            isShare = false;
        }
        setSelectIcon(ivTranspondFlag, isShare);

        mEditor.putBoolean("bet_transpond", isShare);
        mEditor.commit();
    }

    private void setSelectIcon(ImageView ivTranspondFlag, boolean isSelect) {
        if (isSelect) {
            ivTranspondFlag.setBackgroundResource(R.drawable.choosing_select);
        }
        else {
            ivTranspondFlag.setBackgroundResource(R.drawable.choosing_not_select);
        }
    }

    /**
     * 投注按钮判断是否可以投注，不能投注执行相应操作
     * 
     * @param accountStatus
     * @return
     */
    public boolean canBet(double betMoney) {
        if (mAppState.getUsername() == null) {
            Intent intent = new Intent();
            intent.setClass(mContext, Login.class);
            mContext.startActivity(intent);
            return false;
        }
        else if (mAppState.getAccount() < betMoney) {
            ActionUtil.toTopupNew(mContext);
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * 投注状态显示
     * 
     * @param accountStatus
     * @return
     */
    public void betStatusView(TextView account, Button bet, double betMoney) {
        if (mAppState.getUsername() == null) {
            bet.setText("登录");
            account.setText("");
        }
        else if (mAppState.getAccount() < betMoney) {
            bet.setText("充值");
            account.setText("余额：" + mAppState.getAccount() + "元");
        }
        else {
            bet.setText("购彩");
            account.setText("余额：" + mAppState.getAccount() + "元");
        }
    }

    /**
     * 跟单
     * 
     * @param order
     */
    public void submitJoinUnit(UniteJoinSubmitOrder order, String kind) {
        String eventNameMobNew = "join_group_pay_click_submit";
        HashMap<String, String> mapUmeng = new HashMap<String, String>();
        mapUmeng.put("kind", kind);
        mapUmeng.put("submit_way", "detail");

        UEDataAnalyse.onEvent(mContext, eventNameMobNew, mapUmeng);

        TaskPoolService.getInstance().requestService(new AsyncConnectionBasic(
                                                                              new UniteJoinOrderRequest(
                                                                                                        mContext,
                                                                                                        mHandler,
                                                                                                        order)));
    }

    /**
     * 获取购买比赛信息
     * 
     * @param kind
     * @param orderId
     */
    public void setUniteSportOrderDetail(String kind, String orderId) {
        TaskPoolService.getInstance().requestService(new AsyncConnectionBasic(
                                                                              new OrderSportDetailRequest(
                                                                                                          mContext,
                                                                                                          mHandler,
                                                                                                          kind,
                                                                                                          orderId,
                                                                                                          true)));
    }
}
