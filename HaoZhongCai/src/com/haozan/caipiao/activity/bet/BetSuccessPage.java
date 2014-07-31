package com.haozan.caipiao.activity.bet;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.activity.Feedback;
import com.haozan.caipiao.activity.LotteryGameList;
import com.haozan.caipiao.activity.PerfectInf;
import com.haozan.caipiao.activity.guess.LotteryGuessHome;
import com.haozan.caipiao.activity.userinf.UserCenter;
import com.haozan.caipiao.activity.webbrowser.WebBrowser;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.Banner;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.Logger;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.PluginUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 投注成功提示页面
 * 
 * @author Vincent
 * @create-time 2013-6-26 下午2:04:15
 */
public class BetSuccessPage
    extends BasicActivity
    implements OnClickListener {
    private static final String BET_SUCCESS_TYPE[] = {"普通投注", "发起合买", "参与合买", "送彩票"};
    private static final String[] BTN1_TEXT = {"可到个人中心查看投注信息:", "余额不多了哦"};
    private static final String BTN2_TEXT = "参与积分竞猜赢取彩金:";;
    private Button changeButton;// 从服务器拿该按钮的点击方案
    private Button toPerfectInf;
    private Button toBetAgain;
    private Button toGameCenter;
    private LinearLayout perfectInfLin;
    private String kind;
    private int type;// 类型 0,充值；1，投注信息
    private TextView text;
    private TextView tv1;
    private TextView tv2;
    private Banner item;
    private boolean ifSuccessGet = false;;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bet_success_page);
        setUpView();
        initViews();
        init();
    }

    private void initViews() {
        if ("0".equals(appState.getPerfectInf()) || !("1").equals(appState.getRegisterType()) &&
            "".equals(appState.getReservedPhone())) {
            perfectInfLin.setVisibility(View.VISIBLE);
        }
        else {
            perfectInfLin.setVisibility(View.GONE);
        }
    }

    private void init() {
        Bundle bundle = getIntent().getExtras();
        kind = bundle.getString("bet_kind");
        type = bundle.getInt("type");
        initToBetAgain();
    }

    private void initToBetAgain() {
        if (type == 0) {// 余额不足时
            toBetAgain.setText("充值");
            changeButton.setText("积分竞猜");
            tv1.setText(BTN1_TEXT[1]);
            tv2.setText(BTN2_TEXT);
        }
        else if (type == 1) {
            toBetAgain.setText("投注信息");
            tv1.setText(BTN1_TEXT[0]);
            executeGetBetSuccessInfoTask();
        }
    }

    private void executeGetBetSuccessInfoTask() {
        if (HttpConnectUtil.isNetworkAvailable(BetSuccessPage.this)) {
            GetBetSuccessInfoTask task = new GetBetSuccessInfoTask();
            task.execute();
        }
    }

    private void setUpView() {
        tv1 = (TextView) findViewById(R.id.tv01);
        tv2 = (TextView) findViewById(R.id.tv02);
        text = (TextView) findViewById(R.id.text);
        text.setText(Html.fromHtml("如有任何问题请<u><font color='blue'>联系我们</color></u>"));
        text.setOnClickListener(this);
        changeButton = (Button) findViewById(R.id.to_usercenter);
        changeButton.setOnClickListener(this);
        toBetAgain = (Button) findViewById(R.id.to_betagain);
        toBetAgain.setOnClickListener(this);
        toGameCenter = (Button) findViewById(R.id.to_gamecenter);
        toGameCenter.setOnClickListener(this);
        toPerfectInf = (Button) findViewById(R.id.to_perfect_inf);
        toPerfectInf.setOnClickListener(this);
        perfectInfLin = (LinearLayout) findViewById(R.id.ll_perfect_inf);
    }

    class GetBetSuccessInfoTask
        extends AsyncTask<Void, Object, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("提交信息...");
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(BetSuccessPage.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(9, true, initHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;

        }

        @Override
        protected void onPostExecute(String json) {
            // TODO Auto-generated method stub
            super.onPostExecute(json);
            if (json != null) {
                JsonAnalyse ja = new JsonAnalyse();
                String status = ja.getStatus(json);
                if (status.equals("200")) {
                    Logger.inf("vincent", "个性化定制信息：" + json);
                    String responsedata = ja.getData(json, "response_data");
                    doOnSuccess(responsedata);
                }
                else {
                    changeButton.setText("积分竞猜");
                    tv2.setText(BTN2_TEXT);
                }
            }
            else {
                Logger.inf("vincent", "json = null");
                changeButton.setText("积分竞猜");
                tv2.setText(BTN2_TEXT);
            }
            dismissProgressDialog();
        }
    }

    public HashMap<String, String> initHashMap() {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("service", "3007020");
        parameter.put("phone", appState.getUsername());
        parameter.put("v", LotteryUtils.getPid(BetSuccessPage.this));
        parameter.put("pid", LotteryUtils.getPid(BetSuccessPage.this));
        return parameter;
    }

    public void doOnSuccess(String responsedata) {
        if (!"[]".equals(responsedata)) {
            try {
                JSONArray hallArray = new JSONArray(responsedata);
                int length = hallArray.length();
// for (int i = 0; i < length; i++) {
// JSONObject jo = hallArray.getJSONObject(i);
// // saveFlashes(jo);
// }
                JSONObject jo = hallArray.getJSONObject(0);
                item = new Banner();
                item.setImgUrl(jo.getString("imgUrl"));
                item.setActionType(jo.getString("actionType"));
                item.setUrl(jo.getString("url"));
                item.setTitle(jo.getString("title"));
                item.setDescription(jo.getString("descript"));
                item.setAppPackage(jo.getString("appPackage"));
                item.setAppClass(jo.getString("appClass"));
                ifSuccessGet = true;
                changeButton.setText(jo.getString("title"));
                tv2.setText(jo.getString("descript"));
            }
            catch (Exception e) {
            }
        }
        else {
            changeButton.setText("积分竞猜");
            tv2.setText(BTN2_TEXT);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        if (v.getId() == R.id.to_usercenter) {
            if (type == 0) {// 没钱时跳转到积分竞猜
                intent.setClass(BetSuccessPage.this, LotteryGuessHome.class);
                startActivity(intent);
            }
            else if (type == 1) {
                if (ifSuccessGet) {
                    if ("url".equals(item.getActionType())) {
                        Bundle bundle = new Bundle();
                        bundle.putString("url", item.getUrl());
                        bundle.putString("title", item.getTitle());
                        intent.putExtras(bundle);
                        intent.setClass(BetSuccessPage.this, WebBrowser.class);
                        startActivity(intent);
                    }
                    else if ("app".equals(item.getActionType())) {
                        if (PluginUtils.checkGameExist(BetSuccessPage.this, item.getAppPackage())) {
                            Bundle bundle = new Bundle();
                            PluginUtils.goPlugin(BetSuccessPage.this, bundle, item.getAppPackage(),
                                                 item.getAppClass());
                        }
                        else {
                            PluginUtils.showPluginDownloadDialog(BetSuccessPage.this, item.getTitle(),
                                                                 item.getDescription(), item.getUrl(), false);
                        }
                    }
                    else if ("local".equals(item.getActionType())) {
                        String packName = LotteryUtils.getPackageName(BetSuccessPage.this);
                        String clsName = item.getAppClass();
                        Class<?> c = null;
                        try {
                            c = Class.forName(packName + "." + clsName);
                        }
                        catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        try {
                            intent.setClass(BetSuccessPage.this, c);
                            startActivity(intent);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            ViewUtil.showTipsToast(BetSuccessPage.this, "客户端版本较低，建议升级");
                        }
                    }
                }
                else {
                    intent.setClass(BetSuccessPage.this, LotteryGuessHome.class);
                    startActivity(intent);
                }
            }
        }
        else if (v.getId() == R.id.to_betagain) {
            if (type == 0) {
                // 跳转到充值
                ActionUtil.toTopupNew(this);
            }
            else if (type == 1) {
                // 跳转到投注信息
                intent.setClass(BetSuccessPage.this, UserCenter.class);
                BetSuccessPage.this.startActivity(intent);
            }
        }
        else if (v.getId() == R.id.to_gamecenter) {
            intent.setClass(BetSuccessPage.this, LotteryGameList.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.text) {
            intent.setClass(BetSuccessPage.this, Feedback.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.to_perfect_inf) {
            Bundle bundle = new Bundle();
            bundle.putInt("origin", 2);
            bundle.putString("commer_class", ".bet.BetSuccessPage");
            intent.putExtras(bundle);
            intent.setClass(BetSuccessPage.this, PerfectInf.class);
            startActivityForResult(intent, 0);
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                initViews();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open bet success page");
        map.put("more_inf", BET_SUCCESS_TYPE[type]);
        String eventName = "open bet success page";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        Bundle bundle = getIntent().getExtras();
        type = bundle.getInt("type");
        String eventName = "open_bet_success_page";
        MobclickAgent.onEvent(this, eventName, BET_SUCCESS_TYPE[type]);
        besttoneEventCommint(eventName);
    }
}