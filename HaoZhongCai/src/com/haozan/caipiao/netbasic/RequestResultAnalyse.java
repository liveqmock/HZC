package com.haozan.caipiao.netbasic;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 解析json
 * 
 * @author peter_wang
 * @create-time 2013-10-14 下午3:47:58
 */
public class RequestResultAnalyse {

    // get the status of the json data
    public static String getStatus(String json) {
        if (json == null)
            return null;
        String status = "";
        try {
            JSONObject o = new JSONObject(json);
            status = o.getString("status");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return status;
    }

    // get the main part of the json,name means the name of the main part
    public static String getData(String json, String name) {
        if (json == null)
            return null;
        String data = "";
        try {
            JSONObject o = new JSONObject(json);
            data = o.getString(name);
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return data;
    }

    // get the main part of the json,name means the name of the main part
    public static double getDouble(String json, String name) {
        if (json == null)
            return -1;
        double data = 0;
        try {
            JSONObject o = new JSONObject(json);
            data = o.getDouble(name);
        }
        catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
        return data;
    }

    // get the main part of the json,name means the name of the main part
    public static int getInt(String json, String name) {
        if (json == null)
            return -1;
        int data = 0;
        try {
            JSONObject o = new JSONObject(json);
            data = o.getInt(name);
        }
        catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
        return data;
    }
}
