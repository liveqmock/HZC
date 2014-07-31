package com.haozan.caipiao.task;

import java.util.HashMap;

import android.content.Context;
import android.os.AsyncTask;

import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.util.LotteryUtils;
/**
 * 根据gameId获取某场比赛的简单数据分析
 * 
 * @author Vimcent
 *
 */
public class SportsSimpleAnalyseTask
    extends AsyncTask<Integer, Object, String> {

    private Context context;
    private String gameId;
    private String kind;
    private OnGetSportsSimpleAnalyseInf listener;

    public SportsSimpleAnalyseTask(Context context, String gameId, String kind) {
        this.context = context;
        this.gameId = gameId;
        this.kind = kind;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onPre();
    }

    @Override
    protected String doInBackground(Integer... params) {
        ConnectService connectNet = new ConnectService(context);
        String json = null;
        try {
            //TODO
            json = connectNet.getJsonGet(5, false, initHashMap());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    private HashMap<String, String> initHashMap() {
        HashMap<String, String> parameter = new HashMap<String, String>();
        if(kind.equals("jczq")){
            parameter.put("service", "2009060");
        }
        else if(kind.equals("ctzq")){
            
        }
        
        parameter.put("pid", LotteryUtils.getPid(context));
        parameter.put("game_id", gameId);
        return parameter;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        listener.onPost(result);
    }

    public interface OnGetSportsSimpleAnalyseInf {
        public void onPre();

        public void onPost(String json);
    }

    public void setOnGetSportsSimpleAnalyseInf(OnGetSportsSimpleAnalyseInf listener) {
        this.listener = listener;
    }

}
