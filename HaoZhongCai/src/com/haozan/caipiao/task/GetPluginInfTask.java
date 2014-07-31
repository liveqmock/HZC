package com.haozan.caipiao.task;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;

import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.PluginInf;
import com.haozan.caipiao.util.JsonUtil;

public class GetPluginInfTask
    extends AsyncTask<String, Void, String> {

    // 插件的标示，如map
    private String type;
    // the progress of the download
    private PluginInf pluginInf;

    private Context context;
    private SharedPreferences preferences;
    private Editor databaseData;

    public GetPluginInfTask(Context context, PluginInf pluginInf) {
        this.context = context;
        this.pluginInf = pluginInf;
        preferences = context.getSharedPreferences("user", 0);
        databaseData = context.getSharedPreferences("user", 0).edit();
    }

    @Override
    protected String doInBackground(String... params) {
        type = params[0];
        String json =
            ConnectService.getURLJson(context, "http://download.haozan88.com/publish/plugin/?t=" + type);
        return json;
    }

    @Override
    protected void onPostExecute(String json) {
        super.onPostExecute(json);
        if (json != null) {
            JsonAnalyse analyse = new JsonAnalyse();
            String status = analyse.getStatus(json);
            if (status.equals("200")) {
                databaseData.putString("plugin_"+type, json);
                databaseData.commit();
                JsonUtil.analysePluginData(pluginInf, json);
            }
        }
    }
}