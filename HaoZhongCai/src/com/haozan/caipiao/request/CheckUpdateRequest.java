package com.haozan.caipiao.request;

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;

import com.haozan.caipiao.R;
import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.netbasic.AsyncConnectionInf;
import com.haozan.caipiao.netbasic.HttpConnection;
import com.haozan.caipiao.netbasic.RequestResultAnalyse;
import com.haozan.caipiao.types.NewVersionInfo;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.UpdateUtil;

/**
 * 检测升级接口
 * 
 * @author peter_wang
 * @create-time 2013-11-12 上午10:05:22
 */
public class CheckUpdateRequest
    extends AsyncConnectionInf {

    private Context mContext;
    private Handler mHandler;

    public CheckUpdateRequest(Context mContext, Handler handler) {
        super(mContext, handler);
        this.mContext = mContext;
        this.mHandler = handler;
    }

    private HashMap<String, String> initHashMap() {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "soft_update");
        parameter.put("pid", LotteryUtils.getPid(mContext));
        TelephonyManager telephone = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String udid = telephone.getDeviceId();
        String mid = Build.MODEL;
        if (udid == null)
            udid = "0";
        else
            udid = HttpConnectUtil.encodeParameter(udid);
        if (mid == null)
            mid = "0";
        else
            mid = HttpConnectUtil.encodeParameter(mid);
        parameter.put("udid", udid);
        parameter.put("mid", mid);
        parameter.put("soft_version", LotteryUtils.fullVersion(mContext));
        parameter.put("mid", mid);
        return parameter;
    }

    @Override
    public void preRun() {
        HttpConnection connection = new HttpConnection(mContext);
        connection.getServicesGateway(false, initHashMap());
        createUrlGet(connection.getmUrl());
    }

    @Override
    public void onSuccess(String rspContent, int statusCode) {
        String updateURL = RequestResultAnalyse.getData(rspContent, "update_url");
        String updateWebURL = RequestResultAnalyse.getData(rspContent, "web_url");
        String updateContent = RequestResultAnalyse.getData(rspContent, "update_content");
        int updateForce = RequestResultAnalyse.getInt(rspContent, "force");
        String newVersionNum = RequestResultAnalyse.getData(rspContent, "update_version");
        if (newVersionNum == null) {
            newVersionNum = "未知版本";
        }
        else {
            if (newVersionNum.length() > 6) {
                newVersionNum = newVersionNum.substring(newVersionNum.length() - 6, newVersionNum.length());
            }
        }

        long fileSize = UpdateUtil.getUpdateSize(mContext, updateURL);
        if (updateURL != null && updateWebURL != null && fileSize > 0) {
            Editor mEditor = ActionUtil.getEditor(mContext);

            if (fileSize > 0) {
                mEditor.putString("update_web_url", updateWebURL);
                // add by vincent
                mEditor.putString("update_content", updateContent);
                mEditor.putString("update_version", newVersionNum);

                boolean isForceUpdate = false;
                if (updateForce == 1) {
                    isForceUpdate = true;
                }
                mEditor.putBoolean("update_force", isForceUpdate);
                mEditor.commit();

                NewVersionInfo info = new NewVersionInfo();
                info.setUpdateURL(updateURL);
                info.setUpdateWebURL(updateWebURL);
                info.setUpdateContent(updateContent);
                info.setUpdateForce(isForceUpdate);
                info.setNewVersionNum(newVersionNum);
                info.setSize(fileSize);

                Message message = Message.obtain(mHandler, ControlMessage.UPDATE_INFO_SUCCESS_RESULT, info);
                message.sendToTarget();
            }
        }
        else {
            Message message =
                Message.obtain(mHandler, ControlMessage.UPDATE_INFO_FAIL_RESULT,
                               mContext.getString(R.string.update_fail));
            message.sendToTarget();
        }
    }

    @Override
    public void onFailure(String rspContent, int statusCode) {
        Message message = Message.obtain(mHandler, ControlMessage.UPDATE_INFO_FAIL_RESULT, rspContent);
        message.sendToTarget();
    }
}