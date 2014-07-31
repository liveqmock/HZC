package com.haozan.caipiao.control;

import android.content.Context;
import android.os.Handler;

import com.haozan.caipiao.task.PluginInfTask;
import com.haozan.caipiao.taskbasic.TaskPoolService;

/**
 * 初始化操作control
 * 
 * @author peter_wang
 * @create-time 2013-10-12 下午1:40:43
 */
public class PluginControl {
    private static final String PLUGIN_MAP = "map";

    private Context mContext;
    private Handler mHandler;

    public PluginControl(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
    }

    public void getMapPluginInf() {
        getPluginInf(PLUGIN_MAP);
    }

    private void getPluginInf(String type) {
        TaskPoolService.getInstance().requestService(new PluginInfTask(mContext, mHandler, type));
    }

}
