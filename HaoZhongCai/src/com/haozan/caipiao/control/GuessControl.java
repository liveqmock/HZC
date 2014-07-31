package com.haozan.caipiao.control;

import android.content.Context;
import android.os.Handler;

import com.haozan.caipiao.netbasic.AsyncConnectionBasic;
import com.haozan.caipiao.request.GuessFirstQuetionRequest;
import com.haozan.caipiao.taskbasic.TaskPoolService;

/**
 * 大厅control
 * 
 * @author peter_wang
 * @create-time 2013-10-16 上午11:19:10
 */
public class GuessControl {

    private Context mContext;

    private Handler mHandler;

    public GuessControl(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
    }

    public void setHallGuessContent() {
        TaskPoolService.getInstance().requestService(new AsyncConnectionBasic(
                                                                              new GuessFirstQuetionRequest(
                                                                                                           mContext,
                                                                                                           mHandler)));
    }
}
