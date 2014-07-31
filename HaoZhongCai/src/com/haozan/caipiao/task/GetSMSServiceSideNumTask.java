package com.haozan.caipiao.task;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.util.ViewUtil;

public class GetSMSServiceSideNumTask
    extends AsyncTask<String, Object, String> {

    private Context context;

    public GetSMSServiceSideNumTask(Context context) {
        this.context = context;
    }

    private String smsNum;

    @Override
    protected String doInBackground(String... params) {
        ConnectService connectNet = new ConnectService(context);
        String json = null;
        try {
            json = connectNet.getURLJson(context, params[0]);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        JSONObject jsonObject = null;
        if (result != null) {
            try {
                jsonObject = new JSONObject(result);
                smsNum = jsonObject.getString("sms_gateway");
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            ViewUtil.showTipsToast(context, "网络异常");
        }
        listener.onContentPost(smsNum);
    }

    public void setOnGetServiceListener(OnGetServiceSideSMSNumListener listener) {
        this.listener = listener;
    }

    public interface OnGetServiceSideSMSNumListener {
        public void onContentPost(String smsNum);
    }

    private OnGetServiceSideSMSNumListener listener;
}
