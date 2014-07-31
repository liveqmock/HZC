package com.haozan.caipiao.control.topup;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Handler;

import com.haozan.caipiao.R;
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
 * 银联语音借记卡充值控制类
 * 
 * @author peter_wang
 * @create-time 2013-11-8 上午8:53:22
 */
public class UnionPayDebitCardTopupControl
    extends TopupBasic {

    private Handler mHandler;

    public UnionPayDebitCardTopupControl(Context context, Handler handler) {
        super(context);
        this.mHandler = handler;
    }

    public void toHelp() {
        mTopupUtil.toHelp(LotteryUtils.HELP_RECHARGE_URL + "#unionpay");
    }

    public void getUserInf() {
        if (HttpConnectUtil.isNetworkAvailable(mContext)) {
            mHandler.sendEmptyMessage(UnionpayDebitCardTopup.SHOW_GET_USERINF_PROGRESS_DIALOG);

            TaskPoolService.getInstance().requestService(new AsyncConnectionBasic(
                                                                                  new UserInfRequest(
                                                                                                     mContext,
                                                                                                     mHandler,
                                                                                                     1)));
        }
        else {
            ViewUtil.showTipsToast(mContext,
                                   mContext.getResources().getString(R.string.network_not_avaliable));
        }
    }

    private void remenberUseAlipay() {
        Editor editor = ActionUtil.getEditor(mContext);
        editor.putString("last_topup_way", UnionpayDebitCardTopup.class.getName());
        editor.putBoolean("recharge_yy", false);
        editor.commit();
    }

    public void submitDataStatisticsOpenTopup() {
        UEDataAnalyse.onEvent(mContext, "open_unionpay_topup");
        UEDataAnalyse.onEvent(mContext, "open_topup", "union_bankcard");
    }

    public void toTopup(String province, String location, String cardNum, String money, String userAddress) {
        if (HttpConnectUtil.isNetworkAvailable(mContext)) {
            mHandler.sendEmptyMessage(UnionpayDebitCardTopup.SHOW_SUBMIT_PROGRESS_DIALOG);

            TaskPoolService.getInstance().requestService(new AsyncConnectionBasic(
                                                                                  new UnionPayTopupRequest(
                                                                                                           mContext,
                                                                                                           mHandler,
                                                                                                           province,
                                                                                                           location,
                                                                                                           cardNum,
                                                                                                           money,
                                                                                                           userAddress)));

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
