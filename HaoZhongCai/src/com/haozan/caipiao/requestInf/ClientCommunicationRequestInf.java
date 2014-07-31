package com.haozan.caipiao.requestInf;

import android.content.Context;

import com.haozan.caipiao.util.LotteryUtils;

/**
 * 获取版本相对应qq和手机号码接口url
 * 
 * @author peter_wang
 * @create-time 2013-10-16 上午11:46:35
 */
public class ClientCommunicationRequestInf {

    public String getUrl(Context context) {
        return "http://download.haozan88.com/publish/about/?v=" + LotteryUtils.getPid(context);
    }
}