package com.haozan.caipiao.netbasic;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

/**
 * apn工具类
 * 
 * @author peter_feng
 * @create-time 2013-7-5 下午5:33:36
 */
public class APNUtils {
    private static final String TAG = APNUtils.class.getSimpleName();
    /**
     * cmwap
     */
    public static final int MPROXYTYPE_CMWAP = 1;
    /**
     * wifi
     */
    public static final int MPROXYTYPE_WIFI = 2;
    /**
     * cmnet
     */
    public static final int MPROXYTYPE_CMNET = 4;
    /**
     * uninet服务器列表
     */
    public static final int MPROXYTYPE_UNINET = 8;
    /**
     * uniwap服务器列表
     */
    public static final int MPROXYTYPE_UNIWAP = 16;
    /**
     * net类服务器列表
     */
    public static final int MPROXYTYPE_NET = 32;
    /**
     * wap类服务器列表
     */
    public static final int MPROXYTYPE_WAP = 64;
    /**
     * 默认服务器列表
     */
    public static final int MPROXYTYPE_DEFAULT = 128;
    /**
     * cmda net
     */
    public static final int MPROXYTYPE_CTNET = 256;
    /**
     * cmda wap
     */
    public static final int MPROXYTYPE_CTWAP = 512;

    public static final String ANP_NAME_WIFI = "wifi"; // 中国移动wap APN名称
    public static final String ANP_NAME_CMWAP = "cmwap"; // 中国移动wap APN名称
    public static final String ANP_NAME_CMNET = "cmnet"; // 中国移动net APN名称
    public static final String ANP_NAME_UNIWAP = "uniwap"; // 中国联通wap APN名称
    public static final String ANP_NAME_UNINET = "uninet"; // 中国联通net APN名称
    public static final String ANP_NAME_WAP = "wap"; // 中国电信wap APN名称
    public static final String ANP_NAME_NET = "net"; // 中国电信net APN名称
    public static final String ANP_NAME_CTWAP = "中国电信ctwap服务器列表"; // wap APN名称
    public static final String ANP_NAME_CTNET = "中国电信ctnet服务器列表"; // net APN名称
    public static final String ANP_NAME_NONE = "none"; // net APN名称

    // apn地址
    private static Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");

    // apn属性类型
    public static final String APN_PROP_APN = "apn";
    // apn属性代理
    public static final String APN_PROP_PROXY = "proxy";
    // apn属性端口
    public static final String APN_PROP_PORT = "port";

    public static final byte APNTYPE_NONE = 0;// 未知类型
    public static final byte APNTYPE_CMNET = 1;// cmnet
    public static final byte APNTYPE_CMWAP = 2;// cmwap
    public static final byte APNTYPE_WIFI = 3;// WiFi
    public static final byte APNTYPE_UNINET = 4;// uninet
    public static final byte APNTYPE_UNIWAP = 5;// uniwap
    public static final byte APNTYPE_NET = 6;// net类接入点
    public static final byte APNTYPE_WAP = 7;// wap类接入点
    public static final byte APNTYPE_CTNET = 8; // ctnet
    public static final byte APNTYPE_CTWAP = 9; // ctwap

    // jce接入点类型
    public static final int JCE_APNTYPE_UNKNOWN = 0;
    public static final int JCE_APNTYPE_DEFAULT = 1;
    public static final int JCE_APNTYPE_CMNET = 2;
    public static final int JCE_APNTYPE_CMWAP = 4;
    public static final int JCE_APNTYPE_WIFI = 8;
    public static final int JCE_APNTYPE_UNINET = 16;
    public static final int JCE_APNTYPE_UNIWAP = 32;
    public static final int JCE_APNTYPE_NET = 64;
    public static final int JCE_APNTYPE_WAP = 128;
    public static final int JCE_APNTYPE_CTWAP = 512;
    public static final int JCE_APNTYPE_CTNET = 256;

    /**
     * 获取系统APN代理端口
     * 
     * @param context
     * @return
     */
    public static int getApnPortInt(Context context) {
        Cursor c = context.getContentResolver().query(PREFERRED_APN_URI, null, null, null, null);
        c.moveToFirst();
        if (c.isAfterLast()) {
            return -1;
        }
        return c.getInt(c.getColumnIndex(APN_PROP_PORT));
    }

    /**
     * 获取系统APN代理IP
     * 
     * @param context
     * @return
     */
    public static String getApnProxy(Context context) {
        Cursor c = context.getContentResolver().query(PREFERRED_APN_URI, null, null, null, null);
        c.moveToFirst();
        if (c.isAfterLast()) {
            return null;
        }
        return c.getString(c.getColumnIndex(APN_PROP_PROXY));
    }

    /**
     * 是否有网关代理
     * 
     * @param context
     * @return
     */
    public static boolean hasProxy(Context context) {
        int netType = getMProxyType(context);
        if (netType == MPROXYTYPE_CMWAP || netType == MPROXYTYPE_UNIWAP || netType == MPROXYTYPE_WAP ||
            netType == MPROXYTYPE_CTWAP) {
            return true;
        }
        return false;
    }

    /**
     * 获取自定义当前联网类型
     * 
     * @param act 当前活动Activity
     * @return 联网类型 -1表示未知的联网类型, 正确类型： MPROXYTYPE_WIFI | MPROXYTYPE_CMWAP | MPROXYTYPE_CMNET
     */
    public static int getMProxyType(Context act) {
        try {
            ConnectivityManager cm = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info == null)
                return MPROXYTYPE_DEFAULT;
            String typeName = info.getTypeName();
            if (typeName.toUpperCase().equals("WIFI")) { // wifi网络
                return MPROXYTYPE_WIFI;
            }
            else {
                String extraInfo = info.getExtraInfo().toLowerCase();
                if (extraInfo.startsWith("cmwap")) { // cmwap
                    return MPROXYTYPE_CMWAP;
                }
                else if (extraInfo.startsWith("cmnet") || extraInfo.startsWith("epc.tmobile.com")) { // cmnet
                    return MPROXYTYPE_CMNET;
                }
                else if (extraInfo.startsWith("uniwap")) {
                    return MPROXYTYPE_UNIWAP;
                }
                else if (extraInfo.startsWith("uninet")) {
                    return MPROXYTYPE_UNINET;
                }
                else if (extraInfo.startsWith("wap")) {
                    return MPROXYTYPE_WAP;
                }
                else if (extraInfo.startsWith("net")) {
                    return MPROXYTYPE_NET;
                }
                else if (extraInfo.startsWith("#777")) { // cdma
                    String proxy = getApnProxy(act);
                    if (proxy != null && proxy.length() > 0) {
                        return MPROXYTYPE_CTWAP;
                    }
                    else {
                        return MPROXYTYPE_CTNET;
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return MPROXYTYPE_DEFAULT;
    }

    /**
     * 获取网络类型
     * 
     * @param act
     * @return
     */
    public static String getNetType(Context act) {
        try {
            ConnectivityManager cm = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info == null)
                return "unknown";
            String typeName = info.getTypeName();
            if (typeName.toUpperCase().equals("WIFI")) { // wifi网络
                return ANP_NAME_WIFI;
            }
            else {
                String extraInfo = info.getExtraInfo().toLowerCase();
                if (extraInfo.startsWith(ANP_NAME_CMWAP)) { // cmwap
                    return ANP_NAME_CMWAP;
                }
                else if (extraInfo.startsWith(ANP_NAME_CMNET) || extraInfo.startsWith("epc.tmobile.com")) { // cmnet
                    return ANP_NAME_CMNET;
                }
                else if (extraInfo.startsWith(ANP_NAME_UNIWAP)) {
                    return ANP_NAME_UNIWAP;
                }
                else if (extraInfo.startsWith(ANP_NAME_UNINET)) {
                    return ANP_NAME_UNINET;
                }
                else if (extraInfo.startsWith(ANP_NAME_WAP)) {
                    return ANP_NAME_WAP;
                }
                else if (extraInfo.startsWith(ANP_NAME_NET)) {
                    return ANP_NAME_NET;
                }
                else if (extraInfo.startsWith("#777")) { // cdma
                    String proxy = getApnProxy(act);
                    if (proxy != null && proxy.length() > 0) {
                        return "cmda_net";
                    }
                    else {
                        return "cmda_wap";
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "unknown";
    }

}