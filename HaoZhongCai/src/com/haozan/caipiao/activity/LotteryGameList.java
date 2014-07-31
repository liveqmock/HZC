package com.haozan.caipiao.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.adapter.HallGameListAdapter;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.GameDownloadInf;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.JsonUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 获取游戏列表
 * 
 * @author peter
 */
public class LotteryGameList
    extends BasicActivity {

    private ListView lv;
    private TextView message;
    private ProgressBar progress;

    private HallGameListAdapter adapter;
    private ArrayList<GameDownloadInf> gameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hall_game_list);
        setupViews();
        init();
    }

    private void setupViews() {
        message = (TextView) this.findViewById(R.id.message);
        progress = (ProgressBar) this.findViewById(R.id.progressBar);
        lv = (ListView) this.findViewById(R.id.game_list);
        gameList = new ArrayList<GameDownloadInf>();
        adapter = new HallGameListAdapter(LotteryGameList.this, gameList);
        lv.setAdapter(adapter);
    }

    private void init() {
        getOldGameList();
        if (HttpConnectUtil.isNetworkAvailable(LotteryGameList.this)) {
            GetGameListTask gameListTask = new GetGameListTask();
            gameListTask.execute();
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
        }
    }

    // 获取上次保存的游戏列表，初始化界面
    private void getOldGameList() {
        String gameListJson = preferences.getString("game_list", null);
        if (gameListJson != null) {
            JsonUtil.analyseGameListData(gameList, gameListJson);
        }
    }

    /**
     * 获取游戏列表线程
     * 
     * @author peter_feng
     * @create-time 2012-8-14 下午04:24:58
     */
    class GetGameListTask
        extends AsyncTask<Void, Object, String> {

        @Override
        protected String doInBackground(Void... para) {
            String json =
                ConnectService.getURLJson(LotteryGameList.this,
                                          "http://download.haozan88.com/publish/game/index.php?v=" +
                                              LotteryUtils.getPid(LotteryGameList.this));
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            progress.setVisibility(View.GONE);
            if (json != null) {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    String response_data = analyse.getData(json, "response_data");
                    databaseData.putString("game_list", response_data);
                    databaseData.commit();
                    JsonUtil.analyseGameListData(gameList, response_data);
                    adapter.notifyDataSetChanged();
                }
            }
            if (gameList.size() == 0) {
                message.setText("游戏将在后续推出，敬请期待");
                message.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open game list");
        String eventName = "open game list";
        FlurryAgent.onEvent(eventName, map);
    }

    @Override
    protected void submitData() {
        String eventName = "open_game_list";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }
}
