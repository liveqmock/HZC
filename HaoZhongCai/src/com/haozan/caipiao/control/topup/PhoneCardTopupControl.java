package com.haozan.caipiao.control.topup;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Handler;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.topup.PhoneCardRecharge;
import com.haozan.caipiao.netbasic.AsyncConnectionBasic;
import com.haozan.caipiao.request.topup.PhoneCardTopupRequest;
import com.haozan.caipiao.taskbasic.TaskPoolService;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.UEDataAnalyse;
import com.haozan.caipiao.util.ViewUtil;

/**
 * 手机充值卡充值控制类
 * 
 * @author peter_wang
 * @create-time 2013-11-7 下午10:23:42
 */
public class PhoneCardTopupControl
    extends TopupBasic {

    private Handler mHandler;

    public PhoneCardTopupControl(Context context, Handler handler) {
        super(context);
        this.mHandler = handler;
    }

    public void toHelp() {
        mTopupUtil.toHelp(LotteryUtils.HELP_RECHARGE_URL + "#phoneCardRecharge");
    }

    private void remenberTopupWay() {
        Editor editor = ActionUtil.getEditor(mContext);
        editor.putString("last_topup_way", PhoneCardRecharge.class.getName());
        editor.putBoolean("recharge_yy", false);
        editor.commit();
    }

    public void submitDataStatisticsOpenTopup() {
        UEDataAnalyse.onEvent(mContext, "open_phonecard_topup");
        UEDataAnalyse.onEvent(mContext, "open_topup", "phonecard");
    }

    public void toTopup(String cardType, String cardAmt, String cardNum, String cardPwd) {
        if (HttpConnectUtil.isNetworkAvailable(mContext)) {
            TaskPoolService.getInstance().requestService(new AsyncConnectionBasic(
                                                                                  new PhoneCardTopupRequest(
                                                                                                            mContext,
                                                                                                            mHandler,
                                                                                                            cardType,
                                                                                                            cardAmt,
                                                                                                            cardNum,
                                                                                                            cardPwd)));

            remenberTopupWay();

        }
        else {
            ViewUtil.showTipsToast(mContext, mContext.getString(R.string.network_not_avaliable));
        }
    }

    public void payFail(String error) {
        mTopupUtil.showFailDialog(error);
    }
}
