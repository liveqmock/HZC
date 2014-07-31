package com.haozan.caipiao.util.alipayfastlogin;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class Result {
    private static final Map<String, String> sError;

    static {
        sError = new HashMap<String, String>();
        sError.put("9000", "操作成功");
        sError.put("4000", "系统异常");
        sError.put("4001", "数据格式不正确");
        sError.put("4003", "该用户绑定的支付宝账户被冻结或不允许支付");
        sError.put("4004", "该用户已解除绑定");
        sError.put("4005", "绑定失败或没有绑定");
        sError.put("4006", "订单支付失败");
        sError.put("4010", "重新绑定账户");
        sError.put("6000", "支付服务正在进行升级操作");
        sError.put("6001", "用户中途取消支付操作");
    }

    public static String getResult(String result) {
        String src = result.replace("{", "");
        src = src.replace("}", "");
        return getContent(src, "memo=", ";result");
    }

    public static String getToken(String result) {
        String src = result.replace("{", "");
        src = src.replace("}", "");
        return getContent(src, "token=", "&userid");
    }

    public static String getAppUserId(String result) {
        String src = result.replace("{", "");
        src = src.replace("}", "");
        return getContent(src, "userid=", null);
    }

    private static String getContent(String src, String startTag, String endTag) {
        String content = src;
        int start = src.indexOf(startTag);
        Log.i("start", "start = " + start);
        if (start == -1) {
            return null;
        }
        start += startTag.length();

        try {
            if (endTag != null) {
                int end = src.indexOf(endTag);
                content = src.substring(start, end);
                return content;
            }
            else {
                content = src.substring(start);
                return content;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
