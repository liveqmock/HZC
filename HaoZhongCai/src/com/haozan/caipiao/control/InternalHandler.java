package com.haozan.caipiao.control;

import android.os.Handler;

/**
 * 传递handler之前先做处理
 * 
 * @author peter_wang
 * @create-time 2013-10-14 上午11:48:35
 */
public class InternalHandler
    extends Handler {
    private Handler mParentHandler = null;

    public InternalHandler(Handler parentHandler) {
        this.mParentHandler = parentHandler;
    }

    public Handler getParentHandler() {
        return mParentHandler;
    }
}
