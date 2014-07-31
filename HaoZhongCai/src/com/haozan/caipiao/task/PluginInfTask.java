package com.haozan.caipiao.task;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Handler;

import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.netbasic.AsyncConnectionBasic;
import com.haozan.caipiao.netbasic.ConnectionBasic;
import com.haozan.caipiao.requestInf.PluginInfRequestInf;
import com.haozan.caipiao.util.ActionUtil;

/**
 * 获取插件信息 ，注意防止同时调起导致返回的message相同问题
 * 
 * @author peter_wang
 * @create-time 2013-10-15 下午9:28:37
 */
public class PluginInfTask
    implements Runnable {
    private Context mContext;
    private Handler mHandler;
    private String mType;

    public PluginInfTask(Context context, Handler handler, String type) {
        this.mContext = context;
        this.mHandler = handler;
        this.mType = type;
    }

    @Override
    public void run() {
        mHandler.sendEmptyMessage(ControlMessage.SHOW_PROGRESS);

        PluginInfRequestInf requestInf = new PluginInfRequestInf();
        ConnectionBasic connect = new ConnectionBasic(mContext);
        String[] json = connect.requestGet(requestInf.getPluginUrl(mType));

        if (json != null && json.length == 2) {
            if (Integer.valueOf(json[0]) == AsyncConnectionBasic.GET_SUCCEED_STATUS) {

                Editor databaseData = ActionUtil.getEditor(mContext);
                databaseData.putString("plugin_" + mType, json[1]);
                databaseData.commit();

                mHandler.sendMessage(mHandler.obtainMessage(ControlMessage.PLUGIN_INF, json[1]));
            }
        }

        mHandler.sendEmptyMessage(ControlMessage.DISMISS_PROGRESS);
    }
}