package com.haozan.caipiao.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.umeng.analytics.MobclickAgent;

public class NoteListExtention
    extends BasicActivity {

    private static final long SIZE = 10;
    private int page = 1;
    private TextView textView;
    private TextView warningInf;
    private ProgressBar loding;
    private WebView web;
    private int rid;
    private String href;
    private String int_type;
    private LinearLayout ll_mess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_list_extendtion);
        setUpViews();
        init();

    }

    private void init() {
        rid = this.getIntent().getExtras().getInt("rid");
        href = this.getIntent().getExtras().getString("href");
        int_type = this.getIntent().getExtras().getString("img");
        if (int_type.equals("3")) {
            ll_mess.setVisibility(View.GONE);
            web.setVisibility(View.VISIBLE);
            web.loadUrl(href);
        }
        else if (int_type.equals("2")) {
            ll_mess.setVisibility(View.VISIBLE);
            web.setVisibility(View.GONE);
            GetLotteryHistoryTask rlh = new GetLotteryHistoryTask();
            rlh.execute();
        }
        /*
         * if(href != null && !href.equals("")){ ll_mess.setVisibility(View.GONE);
         * web.setVisibility(View.VISIBLE); web.loadUrl(href); } else{ ll_mess.setVisibility(View.VISIBLE);
         * web.setVisibility(View.GONE); GetLotteryHistoryTask rlh = new GetLotteryHistoryTask();
         * rlh.execute(); }
         */
    }

    private void setUpViews() {
        loding = (ProgressBar) findViewById(R.id.progress_large);
        textView = (TextView) findViewById(R.id.message_detail);
        warningInf = (TextView) findViewById(R.id.warning_message);
        web = (WebView) findViewById(R.id.web);
        ll_mess = (LinearLayout) findViewById(R.id.ll_mess);
    }

    /*
     * public boolean onKeyDown(int keyCode, KeyEvent event) { if (keyCode == KeyEvent.KEYCODE_BACK &&
     * event.getRepeatCount() == 0) { Intent intent = new Intent(); intent.setClass(NoteListExtention.this,
     * Hall.class); startActivity(intent); finish(); if (android.os.Build.VERSION.SDK_INT >
     * android.os.Build.VERSION_CODES.DONUT) { (new
     * AnimationModel(NoteListExtention.this)).overridePendingTransition(R .anim.push_to_left_in,
     * R.anim.push_to_left_out); } } return false; }
     */

    class GetLotteryHistoryTask
        extends AsyncTask<String, Object, String> {

        private HashMap<String, String> initHashMap() {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2001040");
            parameter.put("pid", LotteryUtils.getPid(NoteListExtention.this));
            parameter.put("version", LotteryUtils.fullVersion(NoteListExtention.this));
            parameter.put("page_no", "" + page);
            parameter.put("num", "" + SIZE);
            parameter.put("notice_id", "" + rid);
            return parameter;
        }

        @Override
        protected String doInBackground(String... para) {
            ConnectService connectNet = new ConnectService(NoteListExtention.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(2, true, initHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            // refreshBt.setVisibility(View.VISIBLE);
            // reloadProgress.setVisibility(View.GONE);
            // loadMore.setVisibility(View.VISIBLE);
            // loadMoreProgress.setVisibility(View.GONE);
            String inf = null;
            if (json != null) {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    String response_data = analyse.getData(json, "response_data");
                    loding.setVisibility(View.GONE);
                    // footView.setVisibility(View.VISIBLE);
                    // if (response_data.equals("[]")) {
                    // inf = "已无更多数据";
                    // }
                    if (response_data.equals("[]") == false) {
                        // page++;
                        // setLotteryData(response_data, historyDiaplay);
                        // int size = historyDiaplay.size();
                        // if (size % 10 != 0) {
                        // ViewUtil.showTipsToast(this,"没有更多记录");
                        // lv.removeFooterView(footView);
                        // }
                        // adapter.notifyDataSetChanged();
                    }
                    // else {
                    try {
                        JSONArray hallArray = new JSONArray(response_data);
                        JSONObject jobject = hallArray.getJSONObject(0);

                        // int length = hallArray.length();
                        // long size = SIZE;
                        // if (!firstFromServer)
                        // size = SIZE + 1;
                        // if (length < size) {
// ViewUtil.showTipsToast(this,noMoreData);
                        // lv.removeFooterView(footView);
                        // removeFoot = true;
                        // }
                        textView.setText(jobject.getString("content"));
                    }
                    catch (JSONException e) {
                        ViewUtil.showTipsToast(NoteListExtention.this, "网络数据有误");
                        // e.printStackTrace();
                    }
                    // if (firstFromServer)
                    // historyDiaplay.clear();
                    // // whether click the refresh button
                    // if (reload) {
                    // historyDiaplay.clear();
                    // }
                    // setLotteryData(response_data, historyDiaplay);
                    // adapter.notifyDataSetChanged();
                    // }
                }
                else {
                    inf = "数据查询失败";
                }
            }
            else {
                inf = "数据查询失败";
            }
            // reload = false;
            // if (inf != null) {
            // if (firstLoad) {
            // message.setVisibility(View.VISIBLE);
            // message.setText(inf);
            // }
            // ViewUtil.showTipsToast(this,inf);
            // }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // refreshBt.setVisibility(View.GONE);
            // reloadProgress.setVisibility(View.VISIBLE);
            // loadMore.setVisibility(View.GONE);
            // loadMoreProgress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open notice extention");
        String eventName = "open notice extention";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_notice_extention";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }
}
