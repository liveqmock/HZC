package com.haozan.caipiao.task;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.adapter.LotteryNewsCommentAdapter;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.NewsCommentItem;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.ViewUtil;

public class NewsCommentPostTask
    extends AsyncTask<Object, Object, Object> {
    private Context context;
    private int news_id;
    private int ding;
    private int position;
    private int num;
    private String phone_num;
    private LotteryApp app;
    private LotteryNewsCommentAdapter lncAdapter;
    private ArrayList<NewsCommentItem> newsCommentItemRecord;

    private OnStatusChangedExtraListener mOnStatusChangedExtraListener;

    public void setStatusChangeExtraListener(OnStatusChangedExtraListener mOnStatusChangedExtraListener) {
        this.mOnStatusChangedExtraListener = mOnStatusChangedExtraListener;
    }

    public interface OnStatusChangedExtraListener {
        void onStatusExtraChanged(int withDrawStatus);
    }

    public NewsCommentPostTask(Context context, TextView popupCommentTop, TextView popupCommentDown,
                               int news_id, int ding, int position, int num,
                               ArrayList<NewsCommentItem> newsCommentItemRecord,
                               LotteryNewsCommentAdapter lncAdapter) {
        this.context = context;
        this.news_id = news_id;
        this.ding = ding;
        this.position = position;
        this.num = num;
        this.newsCommentItemRecord = newsCommentItemRecord;
        this.lncAdapter = lncAdapter;
        app = ((LotteryApp) context.getApplicationContext());
        phone_num = app.getUsername();
    }

    private HashMap<String, String> initHashMap() {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "like_one");
        parameter.put("pid", LotteryUtils.getPid(context));
        parameter.put("like", "" + ding);
        parameter.put("like_id", "" + news_id);
        parameter.put("type", "2");
        parameter.put("phone", HttpConnectUtil.encodeParameter(phone_num));
        return parameter;
    }

    @Override
    protected Object doInBackground(Object... arg0) {
        ConnectService connectNet = new ConnectService(context);
        String json = null;
        try {
            json = connectNet.getJsonGet(1, true, initHashMap());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    protected void onPostExecute(Object result) {
        String inf = null;
        String json = (String) result;
        if (result == null) {
        }
        else {
            JsonAnalyse analyse = new JsonAnalyse();
            String status = analyse.getStatus(json);
            if (status.equals("200")) {

                if (ding == 1) {
// newsCommentItemRecord.get(position).setDing(true);
                    inf = "“顶”成功";
// newsCommentItemRecord.get(position).setCommentGood(num + 1);
// lncAdapter.notifyDataSetChanged();
                }
                else if (ding == -1) {
// newsCommentItemRecord.get(position).setCai(true);
                    inf = "“踩”成功";
// newsCommentItemRecord.get(position).setCommentBad(num + 1);
// lncAdapter.notifyDataSetChanged();
                }
// toast.show();
            }
            else if (status.equals("302")) {
                inf = context.getResources().getString(R.string.login_timeout);
                mOnStatusChangedExtraListener.onStatusExtraChanged(302);
            }
            else if (status.equals("304")) {
                inf = context.getResources().getString(R.string.login_again);
                mOnStatusChangedExtraListener.onStatusExtraChanged(304);
            }
            else {
                inf = analyse.getData(json, "error_desc");
            }

            if (inf != null) {
                ViewUtil.showTipsToast(context, inf);
            }
        }
        super.onPostExecute(result);
    }
}
