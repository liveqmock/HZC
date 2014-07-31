package com.haozan.caipiao.requestInf;

import android.app.Activity;
import android.content.Context;

import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.LotteryConfig;
import com.haozan.caipiao.util.CommonUtil;
import com.haozan.caipiao.util.LotteryUtils;

/**
 * banner接口url
 * 
 * @author peter_wang
 * @create-time 2013-10-16 上午11:46:20
 */
public class BannerRequestInf {
    private Context mContext;

    public BannerRequestInf(Context context) {
        this.mContext = context;
    }

    public String getUrl() {
        String url;
        LotteryApp app = (LotteryApp) ((Activity) mContext).getApplication();
        String name = app.getUsername();
        String dp = CommonUtil.getScreenWdith(mContext) + "*" + CommonUtil.getScreenHeight(mContext);
        String de = CommonUtil.getScreenResolution(mContext);

        if (LotteryConfig.B_TEST_ENVIRONMENT) {
            url =
                "http://download.haozan88.com/publish/test/?v=" + name + "&phone=" + name + "&dp=" + dp +
                    "&de=" + de;
        }
        else {
            url =
                "http://download.haozan88.com/publish/banner/?v=" + LotteryUtils.getPid(mContext) +
                    "&phone=" + name + "&dp=" + dp + "&de=" + de;
        }
        return url;
    }
}