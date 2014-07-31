package com.haozan.caipiao.task;

import java.util.HashMap;

import android.content.Context;
import android.os.AsyncTask;

import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.util.LotteryUtils;

public class GetServiceContentTask
    extends AsyncTask<Void, Object, String> {

    private Context context;
    private OnGetServiceListener listener;

    public GetServiceContentTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPostExecute(String result) {
        listener.onContentPost(result);
    }

    private HashMap<String, String> initHashMap() {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "2007010");
        parameter.put("pid", LotteryUtils.getPid(context));
        parameter.put("type", "WSN");
        return parameter;
    }

    @Override
    protected String doInBackground(Void... kind) {
        ConnectService connectNet = new ConnectService(context);
        String json = null;
        try {
            json = connectNet.getJsonGet(2, false, initHashMap());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    protected void onPreExecute() {
        listener.onServicePre();
    }

    public void setOnGetServiceListener(OnGetServiceListener listener) {
        this.listener = listener;
    }

    public interface OnGetServiceListener {
        public void onServicePre();

        public void onContentPost(String json);
    }

}
