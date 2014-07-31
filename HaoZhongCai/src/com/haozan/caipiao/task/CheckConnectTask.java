package com.haozan.caipiao.task;

import android.content.Context;
import android.os.Handler;

import com.haozan.caipiao.Domain;
import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.taskbasic.Task;

/**
 * 检测哪个域名可以访问
 * 
 * @author peter_wang
 * @create-time 2013-10-15 下午9:27:52
 */
public class CheckConnectTask
    extends Task {

    private Context mContext;
    private Handler mHandler;

    public CheckConnectTask(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
    }

    @Override
    public void runTask() {
        new Domain().checkHostReachable(mContext);
        mHandler.sendEmptyMessage(ControlMessage.FINISH_CHECK_CONNECTION);
    }
}