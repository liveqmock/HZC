package com.haozan.caipiao.request;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;

import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.netbasic.AsyncConnectionInf;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.requestInf.BannerRequestInf;
import com.haozan.caipiao.types.Banner;
import com.haozan.caipiao.util.ActionUtil;

/**
 * banner请求发起和解析数据
 * 
 * @author peter_wang
 * @create-time 2013-10-16 上午11:43:35
 */
public class BannerRequest
    extends AsyncConnectionInf {

    private Context mContext;
    private Handler mHandler;

    private ArrayList<Banner> mBannerList;

    public BannerRequest(Context context, Handler handler, ArrayList<Banner> bannerList) {
        super(context, handler);
        this.mContext = context;
        this.mHandler = handler;
        this.mBannerList = bannerList;
    }

    @Override
    public void preRun() {
        BannerRequestInf requestInf = new BannerRequestInf(mContext);
        createUrlGet(requestInf.getUrl());
    }

    private void bannerResponseData(String json) {
        try {
            JSONArray hallArray = new JSONArray(json);
            int length = hallArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject jo = hallArray.getJSONObject(i);
                addBanners(jo);
            }
        }
        catch (Exception e) {
        }
    }

    private void addBanners(JSONObject jo) {
        Banner item = new Banner();
        try {
            item.setImgUrl(jo.getString("imgUrl"));
            item.setActionType(jo.getString("actionType"));
            item.setUrl(jo.getString("url"));
            item.setTitle(jo.getString("title"));
            item.setDescription(jo.getString("descript"));
            item.setAppPackage(jo.getString("appPackage"));
            item.setAppClass(jo.getString("appClass"));
            mBannerList.add(item);
        }
        catch (Exception e) {
        }
    }

    private void dealWithFlashes(String flashes) {
        try {
            JSONArray hallArray = new JSONArray(flashes);
            if (hallArray.length() != 0) {
                JSONObject jo = hallArray.getJSONObject(0);
                saveFlashes(jo);
            }
        }
        catch (Exception e) {
        }
    }

    private void saveFlashes(JSONObject jo) {
        try {
            Banner item = new Banner();
            item.setImgUrl(jo.getString("imgUrl"));
            item.setActionType(jo.getString("actionType"));
            item.setUrl(jo.getString("url"));
            item.setTitle(jo.getString("title"));
            item.setDescription(jo.getString("descript"));
            item.setAppPackage(jo.getString("appPackage"));
            item.setAppClass(jo.getString("appClass"));

            Editor databaseData = ActionUtil.getEditor(mContext);
            databaseData.putString("flash_imgUrl", item.getImgUrl());
            try {
                databaseData.putString("flash_endTime", jo.getString("endTime"));
                databaseData.putString("flash_startTime", jo.getString("startTime"));
            }
            catch (JSONException e1) {
                e1.printStackTrace();
            }
            databaseData.putString("flash_actionType", item.getActionType());
            databaseData.putString("flash_url", item.getUrl());
            databaseData.putString("flash_title", item.getTitle());
            databaseData.putString("flash_descript", item.getDescription());
            databaseData.putString("flash_appPackage", item.getAppPackage());
            databaseData.putString("flash_appClass", item.getAppClass());
            databaseData.commit();

            Message message =
                Message.obtain(mHandler, ControlMessage.DOWNLOAD_FLASH_PIC, 0, 0, item.getImgUrl());
            message.sendToTarget();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccess(String rspContent, int statusCode) {
        JsonAnalyse analyse = new JsonAnalyse();
        String response = analyse.getData(rspContent, "response_data");
        String banner = analyse.getData(response, "banners");
        bannerResponseData(banner);

        mHandler.sendEmptyMessage(ControlMessage.HAS_BANNER_INF);

        String flashes = analyse.getData(response, "flashes");
        if (!"[]".equals(flashes))
            dealWithFlashes(flashes);
    }

    @Override
    public void onFailure(String rspContent, int statusCode) {

    }
}