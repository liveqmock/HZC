package com.haozan.caipiao.control.topup;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.Feedback;
import com.haozan.caipiao.activity.topup.RechargeFirstPage;
import com.haozan.caipiao.activity.userinf.UserCenter;
import com.haozan.caipiao.activity.webbrowser.WebBrowser;
import com.haozan.caipiao.netbasic.AsyncConnectionBasic;
import com.haozan.caipiao.request.topup.TopupPluginResultRequest;
import com.haozan.caipiao.taskbasic.TaskPoolService;
import com.haozan.caipiao.types.topup.TopupPluginResult;
import com.haozan.caipiao.widget.CustomDialog;

/**
 * 基础充值工具类，用组合模式加入各个充值方式control
 * 
 * @author peter_wang
 * @create-time 2013-10-31 下午3:50:37
 */
public class TopupUtil {
    private Context mContext;

    public TopupUtil(Context context) {
        this.mContext = context;
    }

    public void toHelp(String url) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("title", "帮助说明");
        intent.putExtras(bundle);
        intent.setClass(mContext, WebBrowser.class);
        mContext.startActivity(intent);
    }

    public void toFeedback() {
        Intent intent = new Intent();
        intent.setClass(mContext, Feedback.class);
        mContext.startActivity(intent);
    }

    public void toAllTopupWay() {
        Intent intent = new Intent();
        intent.setClass(mContext, RechargeFirstPage.class);
        mContext.startActivity(intent);
    }

    public void showSuccessDialog(String inf) {
        CustomDialog dlgSuccess = null;
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(mContext);
        customBuilder.setTitle("充值成功提示").setMessage(inf).setPositiveButton("个人中心",
                                                                           new DialogInterface.OnClickListener() {
                                                                               public void onClick(DialogInterface dialog,
                                                                                                   int which) {
                                                                                   dialog.dismiss();
                                                                                   Intent intent =
                                                                                       new Intent();
                                                                                   intent.setClass(mContext,
                                                                                                   UserCenter.class);
                                                                                   mContext.startActivity(intent);
                                                                                   ((Activity) mContext).finish();
                                                                               }
                                                                           }).setNegativeButton("取  消",
                                                                                                new DialogInterface.OnClickListener() {
                                                                                                    public void onClick(DialogInterface dialog,
                                                                                                                        int which) {
                                                                                                        dialog.dismiss();
                                                                                                    }
                                                                                                });
        dlgSuccess = customBuilder.create();
        dlgSuccess.show();
    }

    protected void showFailDialog(String error) {
        CustomDialog dlgFail = null;

        View failView = View.inflate(mContext, R.layout.dlg_topup_fail, null);
        TextView tvError = (TextView) failView.findViewById(R.id.text);
        tvError.setText(error);
        TextView tvContact = (TextView) failView.findViewById(R.id.contact_customer_service);
        tvContact.setText("联系客服");
        tvContact.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                toFeedback();
                ((Activity) mContext).finish();
            }
        });
        TextView tvTryOthers = (TextView) failView.findViewById(R.id.try_others);
        tvTryOthers.setText("尝试其他充值方式");
        tvTryOthers.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, RechargeFirstPage.class);
                mContext.startActivity(intent);
                ((Activity) mContext).finish();
            }
        });

        CustomDialog.Builder customBuilder = new CustomDialog.Builder(mContext);
        customBuilder.setTitle("充值失败提示").setWarning().setContentView(failView).setPositiveButton("确  定",
                                                                                                 new DialogInterface.OnClickListener() {
                                                                                                     public void onClick(DialogInterface dialog,
                                                                                                                         int which) {
                                                                                                         dialog.dismiss();
                                                                                                     }
                                                                                                 });
        dlgFail = customBuilder.create();
        dlgFail.show();
    }

    /**
     * 充值显示的金额信息
     * 
     * @param rechargeMoney
     * @return
     */
    public String createMoneyShow(String rechargeMoney) {
        return "<font color='#9D9D9D'>金额：</font>" + rechargeMoney + "元";
    }

    /**
     * 上传第三方充值插件返回结果信息
     * 
     * @param result
     */
    public void submitTopupPluginResult(TopupPluginResult result) {
        TaskPoolService.getInstance().requestService(new AsyncConnectionBasic(
                                                                              new TopupPluginResultRequest(
                                                                                                           mContext,
                                                                                                           result)));
    }
}
