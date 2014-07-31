package com.haozan.caipiao.task;

import java.util.HashMap;

import android.content.Context;
import android.os.AsyncTask;

import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.util.LotteryUtils;

public class UserInfTask
    extends AsyncTask<Integer, Object, String> {

    private Context context;
    private OnGetUserInfListener listener;
    private String kind_inf;

    public UserInfTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPostExecute(String result) {
        listener.onPost(result);
    }

    @Override
    protected void onPreExecute() {
        listener.onPre();
    }

    @Override
    protected String doInBackground(Integer... kind) {
        kind_inf = String.valueOf(kind[0]);
        ConnectService connectNet = new ConnectService(context);
        String json = null;
        try {
            json = connectNet.getJsonGet(3, true, initHashMap());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public void setOnGetUserInfListener(OnGetUserInfListener listener) {
        this.listener = listener;
    }

    public interface OnGetUserInfListener {
        public void onPre();

        public void onPost(String json);
    }

    private HashMap<String, String> initHashMap() {
        LotteryApp appState = ((LotteryApp) context.getApplicationContext());
        String phone = appState.getUsername();
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "1005011");
        parameter.put("pid", LotteryUtils.getPid(context));
        parameter.put("info_type", kind_inf);
        parameter.put("phone", String.valueOf(phone));
        return parameter;
    }
}
