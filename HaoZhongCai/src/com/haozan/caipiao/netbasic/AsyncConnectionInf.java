/*******************************************************************************
 * Copyright 2013 Zhang Zhuo(william@TinyGameX.com). Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
package com.haozan.caipiao.netbasic;

import java.util.HashMap;

import android.content.Context;
import android.os.Handler;

import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.util.Logger;

/**
 * 生成url等信息、传递网络访问状态和数据的类
 * 
 * @author peter_wang
 * @create-time 2013-10-14 下午3:05:05
 */
public abstract class AsyncConnectionInf
    implements SdkHandler {
    String mUrl;
    int mRequestMethod;
    HashMap<String, String> mPostData;

    protected Context mContext;
    private Handler mHandler;

    public AsyncConnectionInf(Context context, Handler handler) {
        super();
        this.mContext = context;
        this.mHandler = handler;
    }

    public void create(int method, String url, HashMap<String, String> postData) {
        this.mRequestMethod = method;
        this.mUrl = url;
        this.mPostData = postData;
    }

    public void createUrlGet(String url) {
        create(AsyncConnectionBasic.GET_METHOD_INDEX, url, null);
    }

    public void createUrlPost(String url, HashMap<String, String> postData) {
        create(AsyncConnectionBasic.POST_METHOD_INDEX, url, postData);
    }

    public void showProgress() {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(ControlMessage.SHOW_PROGRESS);
        }
    }

    /**
     * 执行网络数据获取前先生成url等信息
     */
    public abstract void preRun();

    /**
     * 获取到网络数据之后的解析过程  [200, {"status":"400","error_desc":"多次充值时卡内余额不足!"}]
     */
    public void afterRun(String[] data) {
        // 打印基本网络访问信息
        Logger.inf("lottery_url", mUrl);
        if (mRequestMethod == AsyncConnectionBasic.POST_METHOD_INDEX) {
// Logger.inf("lottery_url_post_map", mPostData);
        }
        Logger.inf("lottery_url_result", data[0] + "," + data[1]);

        Integer httpCode = Integer.valueOf(data[0]);
        if (httpCode.intValue() == AsyncConnectionBasic.GET_SUCCEED_STATUS) {
            onSuccess(data[1], httpCode.intValue());
        }
        else {
            onFailure(data[1], httpCode.intValue());
        }

    }

    public void dismissProgress() {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(ControlMessage.DISMISS_PROGRESS);
        }
    }

    public void dispose() {
        mUrl = null;
        mPostData = null;
    }
}
