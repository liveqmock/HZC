package com.haozan.caipiao.task;

import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;

import com.haozan.caipiao.Domain;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;

public class GetLabelDataTask
    extends AsyncTask<String, Integer, String> {

    private Context context;
    private String key;
    private Editor packagetClassSaveEditor;

    public GetLabelDataTask(Context context, String key, Editor packagetClassSaveEditor) {
        this.context = context;
        if (key.contains("+"))
            this.key = key.replace("+", "%2B");
        else
            this.key = key;
        this.packagetClassSaveEditor = packagetClassSaveEditor;
    }

    @Override
    protected String doInBackground(String... params) {
        String json = null;
        try {
            json =
                ConnectService.getURLJson(context,
                                          Domain.GETSERVERLABELDATA +
                                              URLEncoder.encode(key.replaceAll("#", "").trim()));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    protected void onPostExecute(String json) {
        super.onPostExecute(json);
// String inf = null;
        if (json != null) {
            JsonAnalyse analyse = new JsonAnalyse();
            String status = analyse.getStatus(json);
            if (status.equals("200")) {
                String data = analyse.getData(json, "response_data");
                try {
                    JSONObject jo = new JSONObject(data);
                    String packName = jo.getString("package");
                    String clsName = jo.getString("class");
                    String dlkUrl = jo.getString("url");
                    packagetClassSaveEditor.putString(key, packName + "|" + clsName + "|" + dlkUrl);
                    packagetClassSaveEditor.commit();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
            }
        }
    }
}
