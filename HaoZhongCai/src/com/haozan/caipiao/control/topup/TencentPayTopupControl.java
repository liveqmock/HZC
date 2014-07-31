package com.haozan.caipiao.control.topup;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.os.Handler;

import com.haozan.caipiao.activity.topup.TencentPayTopup;
import com.haozan.caipiao.netbasic.HttpConnection;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.UEDataAnalyse;

/**
 * 财付通充值控制类
 * 
 * @author peter_wang
 * @create-time 2013-10-31 下午4:32:49
 */
public class TencentPayTopupControl
    extends TopupBasic {

    public static final int MSG_PAY_RESULT = 100;

    public TencentPayTopupControl(Context context, Handler handler) {
        super(context);
    }

    public void toHelp() {

    }

    public void submitTopup(String money) {
        Editor editor = ActionUtil.getEditor(mContext);
        editor.putString("last_topup_way", TencentPayTopup.class.getName());
        editor.putBoolean("recharge_yy", false);
        editor.commit();

        tencentPayTopup(money);
    }

    private HashMap<String, String> initHashMap(String money) {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "3005161");
        parameter.put("pid", LotteryUtils.getPid(mContext));
        parameter.put("phone",
                      HttpConnectUtil.encodeParameter(ActionUtil.getLotteryApp(mContext).getUsername()));
        parameter.put("type", "WAP");
        parameter.put("money", money);
        return parameter;
    }

    public void submitDataStatisticsOpenTopup() {
        UEDataAnalyse.onEvent(mContext, "open_tencent_pay_topup");
        UEDataAnalyse.onEvent(mContext, "open_topup", "tencent_pay");
    }

    public void tencentPayTopup(String money) {
        HttpConnection connection = new HttpConnection(mContext);
        connection.getSecuryApi(true, initHashMap(money));
        ActionUtil.toWebBrowser(mContext, "财付通充值", connection.getmUrl());
    }

    public void analyseTopupResult(String strRet) {
        String statusCode = null;
        String info = null;
        String result = null;

        JSONObject jo;
        try {
            jo = new JSONObject(strRet);
            if (jo != null) {
                statusCode = jo.getString("statusCode");
                info = jo.getString("info");
                result = jo.getString("result");
            }
        }
        catch (JSONException e1) {
            e1.printStackTrace();
        }

        String ret = "statusCode = " + statusCode + ", info = " + info + ", result = " + result;

        // 按协议文档，解析并判断返回值，从而显示自定义的支付结果界面

        new AlertDialog.Builder(mContext).setTitle("支付结果").setMessage(ret).setPositiveButton("确定",
                                                                                             new DialogInterface.OnClickListener() {
                                                                                                 public void onClick(DialogInterface dialog,
                                                                                                                     int which) {

                                                                                                 }
                                                                                             }).setCancelable(false).create().show();
    }
}
