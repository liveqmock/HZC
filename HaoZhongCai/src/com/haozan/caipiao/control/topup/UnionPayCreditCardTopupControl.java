package com.haozan.caipiao.control.topup;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Handler;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.topup.UnionPayCreditCardTopup;
import com.haozan.caipiao.activity.topup.UnionpayDebitCardTopup;
import com.haozan.caipiao.netbasic.AsyncConnectionBasic;
import com.haozan.caipiao.request.topup.UnionPayTopupRequest;
import com.haozan.caipiao.request.userinf.UserInfRequest;
import com.haozan.caipiao.taskbasic.TaskPoolService;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.UEDataAnalyse;
import com.haozan.caipiao.util.ViewUtil;

/**
 * 银联语音信用卡充值控制类
 * 
 * @author peter_wang
 * @create-time 2013-11-8 上午8:53:22
 */
public class UnionPayCreditCardTopupControl
    extends TopupBasic {

    private Handler mHandler;

    public UnionPayCreditCardTopupControl(Context context, Handler handler) {
        super(context);
        this.mHandler = handler;
    }

    public void toHelp() {
        mTopupUtil.toHelp(LotteryUtils.HELP_RECHARGE_URL + "#creditcardpay");
    }

    private void remenberUseAlipay() {
        Editor editor = ActionUtil.getEditor(mContext);
        editor.putString("last_topup_way", UnionPayCreditCardTopup.class.getName());
        editor.putBoolean("recharge_yy", false);
        editor.commit();
    }

    public void submitDataStatisticsOpenTopup() {
        UEDataAnalyse.onEvent(mContext, "open_union_creditcard_topup");
        UEDataAnalyse.onEvent(mContext, "open_topup", "union_creditcard");
    }

    public void toTopup(String cardNum, String money, String userAddress, String reservedPhone) {
        if (HttpConnectUtil.isNetworkAvailable(mContext)) {
            mHandler.sendEmptyMessage(UnionPayCreditCardTopup.SHOW_SUBMIT_PROGRESS_DIALOG);

            TaskPoolService.getInstance().requestService(new AsyncConnectionBasic(
                                                                                  new UnionPayTopupRequest(
                                                                                                           mContext,
                                                                                                           mHandler,
                                                                                                           cardNum,
                                                                                                           money,
                                                                                                           userAddress,
                                                                                                           reservedPhone)));

            remenberUseAlipay();

        }
        else {
            ViewUtil.showTipsToast(mContext, mContext.getString(R.string.network_not_avaliable));
        }
    }

    public void payFail(String error) {
        mTopupUtil.showFailDialog(error);
    }
}
