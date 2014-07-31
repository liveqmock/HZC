package com.haozan.caipiao.requestInf;

import android.content.Context;

import com.haozan.caipiao.util.LotteryUtils;

/**
 * 插件信息接口url，包括下载插件和游戏插件
 * 
 * @author peter_wang
 * @create-time 2013-10-16 上午11:47:08
 */
public class PluginInfRequestInf {

    public static final String PLUGIN_MAP = "map";

    public String getPluginUrl(String type) {
        return "http://download.haozan88.com/publish/plugin/?t=" + type;
    }

    public String getGameUrl(Context context) {
        return "http://download.haozan88.com/publish/game/?v=" + LotteryUtils.getPid(context);
    }
}