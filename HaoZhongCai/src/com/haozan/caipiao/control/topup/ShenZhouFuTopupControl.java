package com.haozan.caipiao.control.topup;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Handler;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.topup.ShenZhouFuRecharge;
import com.haozan.caipiao.netbasic.AsyncConnectionBasic;
import com.haozan.caipiao.request.topup.ShenZhouFuTopupRequest;
import com.haozan.caipiao.taskbasic.TaskPoolService;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.UEDataAnalyse;
import com.haozan.caipiao.util.ViewUtil;

/**
 * 神州付充值控制类
 * 
 * @author lugq
 * @create-time 2014-7-25 上午11:23:42
 */
public class ShenZhouFuTopupControl extends TopupBasic {

    private Handler mHandler;

    public ShenZhouFuTopupControl(Context context, Handler handler) {
        super(context);
        this.mHandler = handler;
    }

    /**
     * 跳转到帮助说明页面
     */
    public void toHelp() {
        mTopupUtil.toHelp(LotteryUtils.HELP_RECHARGE_URL + "#ShenZhouFuRecharge");
    }

    /**
     * 记住上次充值的页面操作
     */
    private void remenberTopupWay() {
        Editor editor = ActionUtil.getEditor(mContext);
        editor.putString("last_topup_way", ShenZhouFuRecharge.class.getName());
        editor.putBoolean("recharge_yy", false);
        editor.commit();
    }

    /**
     * 友盟的数据统计操作
     */
    public void submitDataStatisticsOpenTopup() {
        UEDataAnalyse.onEvent(mContext, "open_shenzhou_topup");
        UEDataAnalyse.onEvent(mContext, "open_topup", "shenzhou");
    }

    /**
     * 充值操作
     * @param cardType 充值卡类型(中国移动、联通、电信) 
     * @param cardAmt 卡面金额
     * @param payMoney 实际到账金额
     * @param cardNum 充值卡卡号
     * @param cardPwd 充值卡密码
     */
    public void toTopup(String cardType, String cardAmt, String payMoney, String cardNum, String cardPwd) {
        if (HttpConnectUtil.isNetworkAvailable(mContext)) {
            TaskPoolService.getInstance().requestService(new AsyncConnectionBasic(new ShenZhouFuTopupRequest(mContext,mHandler,cardType,cardAmt, payMoney,cardNum,cardPwd)));
            remenberTopupWay();
        }
        else {
            ViewUtil.showTipsToast(mContext, mContext.getString(R.string.network_not_avaliable));
        }
    }

    /**
     * 充值失败的时候弹出失败对话框
     * @param error 充值失败信息
     */
    public void payFail(String error) {
        mTopupUtil.showFailDialog(error);
    }
}
