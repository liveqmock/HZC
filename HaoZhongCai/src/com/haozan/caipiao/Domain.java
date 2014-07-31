package com.haozan.caipiao;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences.Editor;

import com.haozan.caipiao.netbasic.AsyncConnectionBasic;
import com.haozan.caipiao.netbasic.ConnectionBasic;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.requestInf.ServerTimeRequestInf;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.TimeUtils;

public class Domain {

    private static final String SERVERDATABASE = "serverNumber";

    // 正式服务器
    public static final String[] RELEASE_ROOT_DOMAIN = {"lottery.haozan88.com", "lottery.jackypeng.com",
            "lottery.jackypeng.com", "61.129.47.83"};
    
    //欧阳服务器
    /*public static final String[] RELEASE_ROOT_DOMAIN = {"192.168.1.73", "192.168.1.73", "192.168.1.73", "192.168.1.73"};*/

    // 正式服务器,好中彩、淘彩、彩王彩票
    public static final String[] RELEASE_HZC_ROOT_DOMAIN = {"lottery.haozan88.com", "lottery.jackypeng.com", "61.129.47.83"};
    
    //郑超ip
    //public static final String[] RELEASE_HZC_ROOT_DOMAIN = {"192.168.1.51", "192.168.1.51", "192.168.1.51"};
    
    //要迁移的
    /*public static final String[] RELEASE_HZC_ROOT_DOMAIN = {"42.62.77.9", "42.62.77.9",
    "42.62.77.9"};*/
    
    // 测试服务器
    public static final String[] TEST_ROOT_DOMAIN = {"192.168.1.113", "192.168.1.113",
            "192.168.1.113"};

    // 获取相应标签数据
    public static final String GETSERVERLABELDATA = "http://download.haozan88.com/publish/keywords/?key=";

    // 独立服务器上传访问服务器时间
    public static final String PERSONAL_SERVER_DOMAIN = "http://trace.haozan88.com/TrackLogger/dialTest?";

    public static final String SCHEME_HTTP = "http://";
    public static final String SCHEME_SUFFIX = ":8080/BuKeServ/";
    public static String API_BASE_ENDPOINT = null;

    public void checkHostReachable(Context context) {
        int lastServer = ActionUtil.getSharedPreferences(context).getInt(SERVERDATABASE, 0);
        boolean isReachable = false;
        int lenght = getDomainLeght(context);
        if (lastServer >= lenght) {
            lastServer = 0;
        }
        int presentServer = lastServer;

        for (int i = 0; i < lenght; i++) {
            API_BASE_ENDPOINT = getHost(context, presentServer);

            isReachable = isHostReachable(context);

            if (isReachable) {
                break;
            }
            else {
                presentServer = getNext(context, presentServer);
            }
        }

        if (isReachable == false) {
            presentServer = 0;
            API_BASE_ENDPOINT = getHost(context, presentServer);
        }

        if (lastServer != presentServer) {
            Editor databaseData = ActionUtil.getEditor(context);
            databaseData.putInt(SERVERDATABASE, presentServer);
            databaseData.commit();
        }
    }

    private boolean isHostReachable(Context context) {
        ServerTimeRequestInf requestInf = new ServerTimeRequestInf();
        ConnectionBasic connect = new ConnectionBasic(context);
        connect.timout = 3000;
        String[] json = connect.requestGet(requestInf.getUrl(context));
        if (json != null && json.length == 2) {
            if (json[0].equals(String.valueOf(AsyncConnectionBasic.GET_SUCCEED_STATUS))) {
                JsonAnalyse ja = new JsonAnalyse();
                String time = ja.getData(json[1], "datetime");
                if (time != null) {
                    LotteryApp appState = (LotteryApp) ((Activity) context).getApplication();
                    appState.setTime(TimeUtils.convertDate(time, "yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmm"));
                    return true;
                }
            }
        }
        return false;
    }

    private int getDomainLeght(Context context) {
        int lenght;
        if (LotteryConfig.B_TEST_ENVIRONMENT) {
            lenght = TEST_ROOT_DOMAIN.length;
        }
        else {
            String appName = context.getResources().getString(R.string.app_name);
            if (appName.equals("好中彩") || appName.equals("号百彩票")) {
                lenght = RELEASE_ROOT_DOMAIN.length;
            }
            else {
                lenght = RELEASE_HZC_ROOT_DOMAIN.length;
            }
        }
        return lenght;
    }

    private String getHost(Context context, int index) {
        String reachableEndpoint;
        if (LotteryConfig.B_TEST_ENVIRONMENT) {
            reachableEndpoint = SCHEME_HTTP + TEST_ROOT_DOMAIN[index] + SCHEME_SUFFIX;
        }
        else {
            String appName = context.getResources().getString(R.string.app_name);
            if (appName.equals("好中彩") || appName.equals("号百彩票")) {
                reachableEndpoint = SCHEME_HTTP + RELEASE_HZC_ROOT_DOMAIN[index] + SCHEME_SUFFIX;
            }
            else {
                reachableEndpoint = SCHEME_HTTP + RELEASE_HZC_ROOT_DOMAIN[index] + SCHEME_SUFFIX;
            }
        }
        return reachableEndpoint;
    }

    private int getTestEndpoint(int index) {
        if (index < TEST_ROOT_DOMAIN.length - 1) {
            index++;
        }
        else {
            index = 0;
        }
        return index;
    }

    private int getZZCEndpoint(int index) {
        if (index < RELEASE_HZC_ROOT_DOMAIN.length - 1) {
            index++;
        }
        else {
            index = 0;
        }
        return index;
    }

    private int getHZCEndpoint(int index) {
        if (index < RELEASE_HZC_ROOT_DOMAIN.length - 1) {
            index++;
        }
        else {
            index = 0;
        }
        return index;
    }

    private int getNext(Context context, int indexOrg) {
        int index;
        if (LotteryConfig.B_TEST_ENVIRONMENT) {
            index = getTestEndpoint(indexOrg);
        }
        else {
            String appName = context.getResources().getString(R.string.app_name);
            if (appName.equals("好中彩") || appName.equals("号百彩票")) {
                index = getZZCEndpoint(indexOrg);
            }
            else {
                index = getHZCEndpoint(indexOrg);
            }
        }
        return index;
    }

    public static final String getHTTPURL(Context context) {
        if (API_BASE_ENDPOINT == null) {
            new Domain().checkHostReachable(context);
        }
        return API_BASE_ENDPOINT;
    }

}
