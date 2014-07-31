package com.haozan.caipiao.control.topup;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.Uri;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.topup.BankCardNetRecharge;
import com.haozan.caipiao.db.BankDBHandler;
import com.haozan.caipiao.netbasic.HttpConnection;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.UEDataAnalyse;
import com.haozan.caipiao.util.ViewUtil;

/**
 * 支付宝网页版充值控制类
 * 
 * @author peter_wang
 * @create-time 2013-11-7 下午9:09:18
 */
public class BankCardNetTopupControl
    extends TopupBasic {

    public BankCardNetTopupControl(Context context) {
        super(context);
    }

    public void toHelp() {

    }

    private void remenberUseAlipay() {
        Editor editor = ActionUtil.getEditor(mContext);
        editor.putString("last_topup_way", BankCardNetRecharge.class.getName());
        editor.putBoolean("recharge_yy", false);
        editor.commit();
    }

    public void toWapTopup(String bankName, String type, String money) {
        if (HttpConnectUtil.isNetworkAvailable(mContext)) {
            if (null != bankName && null != type) {
                BankDBHandler db = new BankDBHandler(mContext);
                db.updateOneData(bankName, type);
            }

            String eventNameMob = "alipay_recharge_click_WebBankCard_topup";
            UEDataAnalyse.onEvent(mContext, eventNameMob, money);

            remenberUseAlipay();

            aliPayWapTopup(money);
        }
        else {
            ViewUtil.showTipsToast(mContext, mContext.getString(R.string.network_not_avaliable));
        }
    }

    private HashMap<String, String> initHashMap(String money) {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "replenish_account_by_alipay");
        parameter.put("pid", LotteryUtils.getPid(mContext));
        parameter.put("phone",
                      HttpConnectUtil.encodeParameter(ActionUtil.getLotteryApp(mContext).getUsername()));
        parameter.put("mode", "PRE");
        parameter.put("money", money);
        return parameter;
    }

    public void submitDataStatisticsOpenTopup() {
        UEDataAnalyse.onEvent(mContext, "open_alipay_topup");
        UEDataAnalyse.onEvent(mContext, "open_topup", "alipay_web");
    }

    private void aliPayWapTopup(String money) {
        HttpConnection connection = new HttpConnection(mContext);
        connection.getServicesGateway(true, initHashMap(money));
        ActionUtil.toWebBrowser(mContext, "支付宝网页充值", connection.getmUrl());
    }

    public void callAlipay() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:0571-88156688"));
        mContext.startActivity(intent);
    }
}
