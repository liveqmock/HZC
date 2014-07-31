package com.haozan.caipiao.util;

import java.util.HashMap;

import android.content.Context;

import com.haozan.caipiao.request.UploadConnectTimeRequest;
import com.haozan.caipiao.taskbasic.TaskPoolService;
import com.umeng.analytics.MobclickAgent;

/**
 * 数据统计信息上传
 * 
 * @author peter_wang
 * @create-time 2013-10-29 下午2:16:45
 */
public class UEDataAnalyse {
    private long eventStartTime;
    private long eventEndTime;

    private Context mContext;

    public UEDataAnalyse(Context context) {
        this.mContext = context;
    }

    public static final void onEvent(Context context, String eventId) {
        MobclickAgent.onEvent(context, eventId);
    }

    public static final void onEvent(Context context, String eventId, HashMap<String, String> eventMap) {
        MobclickAgent.onEvent(context, eventId, eventMap);
    }

    public static final void onEvent(Context context, String eventId, String lable) {
        MobclickAgent.onEvent(context, eventId, lable);
    }

    /**
     * 开始统计响应时间
     */
    public void onConnectStart() {
        eventStartTime = System.currentTimeMillis();
    }

    /**
     * 结束统计响应时间
     */
    public void onConnectEnd() {
        eventEndTime = System.currentTimeMillis();
    }

    /**
     * 上传service服务器响应速度
     * 
     * @param serviceName
     */
    public void submitConnectSuccess(String serviceName) {
        onConnectEnd();

        TaskPoolService.getInstance().requestService(new UploadConnectTimeRequest(mContext, serviceName,
                                                                               eventEndTime - eventStartTime));
    }

    /**
     * 上传service服务器响应速度及访问出错信息
     * 
     * @param serviceName
     */
    public void submitConnectFail(String serviceName, String errorInf) {
        onConnectEnd();

        TaskPoolService.getInstance().requestService(new UploadConnectTimeRequest(mContext, serviceName,
                                                                               errorInf, eventEndTime -
                                                                                   eventStartTime));
    }
}