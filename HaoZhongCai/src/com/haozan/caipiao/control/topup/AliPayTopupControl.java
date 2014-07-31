package com.haozan.caipiao.control.topup;

import java.net.URLEncoder;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.widget.Toast;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.topup.AlipayRecharge;
import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.netbasic.AsyncConnectionBasic;
import com.haozan.caipiao.netbasic.HttpConnection;
import com.haozan.caipiao.request.topup.AliPaySecurityRequest;
import com.haozan.caipiao.taskbasic.TaskPoolService;
import com.haozan.caipiao.types.topup.TopupPluginResult;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.UEDataAnalyse;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.alipayfastlogin.AlixId;
import com.haozan.caipiao.util.alipayfastlogin.BaseHelper;
import com.haozan.caipiao.util.alipayfastlogin.MobileSecurePayHelper;
import com.haozan.caipiao.util.alipayfastlogin.MobileSecurePayer;
import com.haozan.caipiao.util.alipayfastlogin.PartnerConfig;
import com.haozan.caipiao.util.alipayfastlogin.Rsa;

/**
 * 支付宝充值控制类
 * 
 * @author peter_wang
 * @create-time 2013-11-7 下午3:27:57
 */
public class AliPayTopupControl
    extends TopupBasic {

    public static final int MSG_PAY_RESULT = 100;

    private String partner;
    private String orderNo;
    private String callUrl;
    private String rechargeMoney;

    private ProgressDialog mProgress = null;

    private Handler mHandler;

    public AliPayTopupControl(Context context, Handler handler) {
        super(context);
        this.mHandler = handler;
    }

    public void toHelp() {

    }

    private void remenberTopupWay() {
        Editor editor = ActionUtil.getEditor(mContext);
        editor.putString("last_topup_way", AlipayRecharge.class.getName());
        editor.putBoolean("recharge_yy", false);
        editor.commit();
    }

    public void toWapTopup(String money) {
        if (HttpConnectUtil.isNetworkAvailable(mContext)) {
            String eventNameMob = "alipay_recharge_click_wap_topup";
            UEDataAnalyse.onEvent(mContext, eventNameMob, money);

            remenberTopupWay();

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
        parameter.put("mode", "WAP");
        parameter.put("money", money);
        return parameter;
    }

    public void submitDataStatisticsOpenTopup() {
        UEDataAnalyse.onEvent(mContext, "open_alipay_topup");
        UEDataAnalyse.onEvent(mContext, "open_topup", "alipay");
    }

    private void aliPayWapTopup(String money) {
        HttpConnection connection = new HttpConnection(mContext);
        connection.getServicesGateway(true, initHashMap(money));
        ActionUtil.toWebBrowser(mContext, "支付宝充值", connection.getmUrl());
    }

    // 提交充值成功统计信息
    private void submitStatisticsTopupSuccess(String rechargeMoney) {
        String eventNameMob = "top_up_success";
        HashMap<String, String> map1 = new HashMap<String, String>();
        map1.put("way", "alipay");
        map1.put("money", rechargeMoney);

        UEDataAnalyse.onEvent(mContext, eventNameMob, map1);
    }

    /**
     * 发起支付宝安全支付订单信息获取
     * 
     * @param money
     */
    public void goSecurePay(String money) {
        if (HttpConnectUtil.isNetworkAvailable(mContext)) {
            String eventNameMob = "alipay_recharge_click_safety_topup";
            UEDataAnalyse.onEvent(mContext, eventNameMob, money);

            TaskPoolService.getInstance().requestService(new AsyncConnectionBasic(
                                                                                  new AliPaySecurityRequest(
                                                                                                            mContext,
                                                                                                            mHandler,
                                                                                                            money)));

            remenberTopupWay();

        }
        else {
            ViewUtil.showTipsToast(mContext, mContext.getString(R.string.network_not_avaliable));
        }
    }

    public void pay(String money, String[] payResult) {
        submitStatisticsTopupSuccess(money);

        rechargeMoney = money;
        callUrl = payResult[0];
        orderNo = payResult[1];
        partner = payResult[2];

        callAlipayPlugin();
    }

    private void callAlipayPlugin() {
        MobileSecurePayHelper mspHelper = new MobileSecurePayHelper(mContext);
        boolean isMobile_spExist = mspHelper.detectMobile_sp();
        if (!isMobile_spExist)
            return;

        if (!checkInfo()) {
            BaseHelper.showDialog((Activity) mContext,
                                  "提示",
                                  "缺少partner或者seller，请在src/com/alipay/android/appDemo4/PartnerConfig.java中增加。",
                                  R.drawable.dialog_title_icon);
            return;
        }

        try {
            String orderInfo = getOrderInfo();
            String signType = getSignType();
            String strsign = sign(signType, orderInfo);
            strsign = URLEncoder.encode(strsign);
            String info = orderInfo + "&sign=" + "\"" + strsign + "\"" + "&" + getSignType();

            // start the pay.
            MobileSecurePayer msp = new MobileSecurePayer();
            boolean bRet = msp.pay(info, mHandler, AlixId.RQF_PAY, (Activity) mContext);

            if (bRet) {
                closeProgress();
                mProgress = BaseHelper.showProgress(mContext, null, "正在支付", false, true);
            }
            else
                ;
        }
        catch (Exception ex) {
            Toast.makeText(mContext, "Failure calling remote service", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkInfo() {
        String partner1 = partner;
        String seller = partner;
        if (partner1 == null || partner1.length() <= 0 || seller == null || seller.length() <= 0)
            return false;

        return true;
    }

    private String getOrderInfo() {
        String strOrderInfo = "partner=" + "\"" + partner + "\"";
        strOrderInfo += "&";
        strOrderInfo += "seller=" + "\"" + partner + "\"";
        strOrderInfo += "&";
        strOrderInfo += "out_trade_no=" + "\"" + orderNo + "\"";
        strOrderInfo += "&";
        strOrderInfo += "subject=" + "\"" + mContext.getString(R.string.lottery_name) + "充值" + "\"";
        strOrderInfo += "&";
        strOrderInfo += "body=" + "\"" + mContext.getString(R.string.lottery_name) + "充值" + "\"";
        strOrderInfo += "&";
        strOrderInfo += "total_fee=" + "\"" + rechargeMoney + "\"";
        strOrderInfo += "&";
        strOrderInfo += "notify_url=" + "\"" + callUrl + "\"";
        return strOrderInfo;
    }

    private String getSignType() {
        String getSignType = "sign_type=" + "\"" + "RSA" + "\"";
        return getSignType;
    }

    private String sign(String signType, String content) {
        return Rsa.sign(content, PartnerConfig.RSA_PRIVATE);
    }

    /**
     * 处理插件支付返回结果
     * 
     * @param strRet
     */
    public void onPluginPayResult(String strRet) {
        try {
            String[] strRetArray = strRet.split("\\;");
            String memoText =
                strRetArray[0].substring(strRetArray[0].indexOf("{") + 1, strRetArray[0].indexOf("}"));
            String resultStatus =
                strRetArray[1].substring(strRetArray[1].indexOf("{") + 1, strRetArray[1].indexOf("}"));
            String result =
                strRetArray[2].substring(strRetArray[2].indexOf("{") + 1, strRetArray[2].indexOf("}"));

            closeProgress();
            BaseHelper.showDialog((Activity) mContext, "提示", memoText, R.drawable.dialog_title_icon);

            TopupPluginResult pluginResult = new TopupPluginResult();
            pluginResult.setOrderNo(orderNo);
            pluginResult.setTradeCode(resultStatus);
            pluginResult.setTradeDesc(memoText);
            pluginResult.setTradeId("");
            pluginResult.setTradeNo("");
            pluginResult.setTradeType("ALI");
            mTopupUtil.submitTopupPluginResult(pluginResult);

            if (resultStatus.equals("9000")) {
                mHandler.sendEmptyMessage(ControlMessage.FINISH_ACTIVITY);
            }
        }
        catch (Exception e) {
            e.printStackTrace();

            BaseHelper.showDialog((Activity) mContext, "提示", "充值失败", R.drawable.dialog_title_icon);
        }
    }

    /**
     * 支付失败
     * 
     * @param message
     */
    public void payFail(String message) {
        mTopupUtil.showFailDialog(message);
    }

    void closeProgress() {
        try {
            if (mProgress != null) {
                mProgress.dismiss();
                mProgress = null;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放资源
     */
    public void dispose() {
        try {
            if (mProgress != null) {
                mProgress.dismiss();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
