package com.haozan.caipiao.control.topup;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Xml;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.topup.ChinaUnionPaycharge;
import com.haozan.caipiao.db.BankDBHandler;
import com.haozan.caipiao.netbasic.AsyncConnectionBasic;
import com.haozan.caipiao.request.topup.ChinaUnionOldPayPluginTopupNewRequest;
import com.haozan.caipiao.request.topup.ChinaUnionOldPayPluginTopupRequest;
import com.haozan.caipiao.taskbasic.TaskPoolService;
import com.haozan.caipiao.types.topup.TopupPluginResult;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.UEDataAnalyse;
import com.haozan.caipiao.util.ViewUtil;
import com.unionpay.upomp.lthj.util.PluginHelper;

/**
 * 插件银联充值控制类
 * 
 * @author peter_wang
 * @create-time 2013-11-7 下午9:21:56
 */
public class ChinaUnionOldPayTopupControl
    extends TopupBasic {

    private Handler mHandler;

    public ChinaUnionOldPayTopupControl(Context context, Handler handler) {
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
                                                                                  new ChinaUnionOldPayPluginTopupNewRequest(
                                                                                                                         mContext,
                                                                                                                         mHandler,
                                                                                                                         money)));

            remenberUseAlipay();

        }
        else {
            ViewUtil.showTipsToast(mContext, mContext.getString(R.string.network_not_avaliable));
        }
    }

    public void callTopupPlugin(String topupXml) {
        try {
            start_upomp_pay(URLDecoder.decode(topupXml, "utf-8"));
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    /*
     * 启动插件，该方法中PluginHelper依赖于com_unionpay_upomp_lthj_lib.jar
     */
    public void start_upomp_pay(String topupXml) {

        byte[] to_upomp = topupXml.getBytes();

        Bundle mbundle = new Bundle();
        // to_upomp为商户提交的XML
        mbundle.putByteArray("xml", to_upomp);

        mbundle.putString("action_cmd", "cmd_pay_plugin");
        // 更换参数调起测试与生产插件,value为true是测试插件 ，为false是生产插件
        mbundle.putBoolean("test", false);
        PluginHelper.LaunchPlugin((Activity) mContext, mbundle);
    }

    public void payFail(String error) {
        mTopupUtil.showFailDialog(error);
    }

    public void onPayResult(String orderNo, int requestCode, int resultCode, Intent data) {
        // 插件返回支付信息的**回调函数**
        if (data != null) {
            Bundle bundle = data.getExtras();
            byte[] xml = bundle.getByteArray("xml");

            parserXML(new ByteArrayInputStream(xml));
        }
    }

    private void parserXML(InputStream in) {
        String orderNo = null;
        String tradeCode = null;
        String tradeDesc = null;
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(in, "utf-8");
            int eventType = parser.getEventType();
            boolean isDone = false;
            try {
                while ((eventType != XmlPullParser.END_DOCUMENT) && (isDone != true)) {
                    if (eventType == XmlPullParser.START_TAG) {
                        String tagName = parser.getName();
                        if (tagName != null && tagName.equals("merchantOrderId")) {
                            try {
                                orderNo = parser.nextText();
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        if (tagName != null && tagName.equals("respCode")) {
                            try {
                                tradeCode = parser.nextText();
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        if (tagName != null && tagName.equals("respDesc")) {
                            try {
                                tradeDesc = parser.nextText();
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                    eventType = parser.next();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            if (tradeCode != null && tradeCode.equals("0000")) {
                showSuccessDialog("充值成功，请留意个人中心余额更新");
            }

            TopupPluginResult pluginResult = new TopupPluginResult();
            pluginResult.setOrderNo(orderNo);
            pluginResult.setTradeCode(tradeCode);
            pluginResult.setTradeDesc(tradeDesc);
            pluginResult.setTradeId("");
            pluginResult.setTradeNo("");
            pluginResult.setTradeType("UTCL");
            mTopupUtil.submitTopupPluginResult(pluginResult);
        }
        catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }
}
