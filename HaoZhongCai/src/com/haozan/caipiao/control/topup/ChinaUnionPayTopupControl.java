package com.haozan.caipiao.control.topup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Handler;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.topup.ChinaUnionPaycharge;
import com.haozan.caipiao.db.BankDBHandler;
import com.haozan.caipiao.netbasic.AsyncConnectionBasic;
import com.haozan.caipiao.request.topup.ChinaUnionPayPluginTopupRequest;
import com.haozan.caipiao.taskbasic.TaskPoolService;
import com.haozan.caipiao.types.topup.TopupPluginResult;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.UEDataAnalyse;
import com.haozan.caipiao.util.ViewUtil;

/**
 * 插件银联充值控制类
 * 
 * @author peter_wang
 * @create-time 2013-11-7 下午9:21:56
 */
public class ChinaUnionPayTopupControl
    extends TopupBasic {

    private Handler mHandler;

    public ChinaUnionPayTopupControl(Context context, Handler handler) {
        super(context);
        this.mHandler = handler;
    }

    public void toHelp() {
        mTopupUtil.toHelp(LotteryUtils.HELP_RECHARGE_URL + "#chinaUnioncardRecharge");
    }

    private void remenberUseAlipay() {
        Editor editor = ActionUtil.getEditor(mContext);
        editor.putString("last_topup_way", ChinaUnionPaycharge.class.getName());
        editor.putBoolean("recharge_yy", false);
        editor.commit();
    }

    public void submitDataStatisticsOpenTopup() {
        UEDataAnalyse.onEvent(mContext, "open_new_unionpay_topup");
        UEDataAnalyse.onEvent(mContext, "open_topup", "new_union_bankcard");
    }

    public void toTopup(String bankName, String type, String money) {
        if (HttpConnectUtil.isNetworkAvailable(mContext)) {
            if (null != bankName && null != type) {
                BankDBHandler db = new BankDBHandler(mContext);
                db.updateOneData(bankName, type);
            }

            TaskPoolService.getInstance().requestService(new AsyncConnectionBasic(
                                                                                  new ChinaUnionPayPluginTopupRequest(
                                                                                                                mContext,
                                                                                                                mHandler,
                                                                                                                money)));

            remenberUseAlipay();

        }
        else {
            ViewUtil.showTipsToast(mContext, mContext.getString(R.string.network_not_avaliable));
        }
    }

    public void callTopupPlugin(String orderId) {
//        UPPayAssistEx.startPayByJAR((Activity) mContext, PayActivity.class, null, null, orderId, "01");

    }

    public void payFail(String error) {
        mTopupUtil.showFailDialog(error);
    }

    public void onPayResult(String orderNo, int requestCode, int resultCode, Intent data) {
        if (data != null) {
            String msg = "";
            /*
             * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
             */
            String str = data.getExtras().getString("pay_result");
            if (str.equalsIgnoreCase("success")) {
                msg = "支付成功";
                showSuccessDialog("充值成功，请留意个人中心余额更新");
            }
            else if (str.equalsIgnoreCase("fail")) {
                msg = "支付失败！";
            }
            else if (str.equalsIgnoreCase("cancel")) {
                msg = "用户取消了支付";
            }

            TopupPluginResult pluginResult = new TopupPluginResult();
            pluginResult.setOrderNo(orderNo);
            pluginResult.setTradeCode("");
            pluginResult.setTradeDesc(msg);
            pluginResult.setTradeId("");
            pluginResult.setTradeNo("");
            pluginResult.setTradeType("UTCL");
            mTopupUtil.submitTopupPluginResult(pluginResult);
        }
    }
}
