package com.haozan.caipiao.activity;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.webbrowser.WebBrowser;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.umeng.analytics.MobclickAgent;

public class Help
    extends BasicActivity
    implements OnClickListener {
    ScrollView scrollView;
    private TextView title;
    private Button feedBack;
    private RelativeLayout maicaiLy, chongzhiLy, lingjiangLy, jifenLy, guizeLy, ssqGzLy, sdGzLy, qlcGzLy,
        swxwGzLy, dfljyGzLy, sslGzLy, dltGzLy, plwGzLy, qxcGzLy, plsGzLy, eexwGzLy, jczqGzLy, rxjGzLy,
        sfcwGzLy, xinxiLy, syxwGzLy, uniteLy, jclqGzLy, cqsscGzLy, hnklsfGzLy, givelotteryLy, jingcaiLy;
    private ImageView tubiao;
    String inf;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().setFormat(PixelFormat.RGBA_8888);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        setupViews();
        try {
            // if (this.getIntent().getExtras().getString("helpyou") != null) {
            inf = this.getIntent().getExtras().getString("helpyou");
            if (inf.equals("helpyou")) {
                scrollView.post(new Runnable() {

                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
            // }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setupViews() {
        title = (TextView) findViewById(R.id.newCmtextView);
        title.setText("帮助");
        feedBack = (Button) findViewById(R.id.title_btinit_right);
        feedBack.setText("反  馈");
        feedBack.setOnClickListener(this);
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        maicaiLy = (RelativeLayout) findViewById(R.id.maicaiLy);
        maicaiLy.setOnClickListener(this);
        chongzhiLy = (RelativeLayout) findViewById(R.id.chongzhiLy);
        chongzhiLy.setOnClickListener(this);
        lingjiangLy = (RelativeLayout) findViewById(R.id.lingjiangLy);
        lingjiangLy.setOnClickListener(this);
        jifenLy = (RelativeLayout) findViewById(R.id.jifenLy);
        jifenLy.setOnClickListener(this);
        givelotteryLy = (RelativeLayout) findViewById(R.id.givelotteryLy);
        givelotteryLy.setOnClickListener(this);
        jingcaiLy = (RelativeLayout) findViewById(R.id.jingcaiLy);
        jingcaiLy.setOnClickListener(this);
        guizeLy = (RelativeLayout) findViewById(R.id.guizeLy);
        guizeLy.setOnClickListener(this);
        xinxiLy = (RelativeLayout) findViewById(R.id.xinxiLy);
        xinxiLy.setOnClickListener(this);
        uniteLy = (RelativeLayout) findViewById(R.id.uniteLy);
        uniteLy.setOnClickListener(this);
        ssqGzLy = (RelativeLayout) findViewById(R.id.ssqGzLy);
        ssqGzLy.setOnClickListener(this);
        sdGzLy = (RelativeLayout) findViewById(R.id.sdGzLy);
        sdGzLy.setOnClickListener(this);
        qlcGzLy = (RelativeLayout) findViewById(R.id.qlcGzLy);
        qlcGzLy.setOnClickListener(this);
        swxwGzLy = (RelativeLayout) findViewById(R.id.swxwGzLy);
        swxwGzLy.setOnClickListener(this);
        dfljyGzLy = (RelativeLayout) findViewById(R.id.dfljyGzLy);
        dfljyGzLy.setOnClickListener(this);
        sslGzLy = (RelativeLayout) findViewById(R.id.sslGzLy);
        sslGzLy.setOnClickListener(this);
        dltGzLy = (RelativeLayout) findViewById(R.id.dltGzLy);
        dltGzLy.setOnClickListener(this);
        plwGzLy = (RelativeLayout) findViewById(R.id.plwGzLy);
        plwGzLy.setOnClickListener(this);
        plsGzLy = (RelativeLayout) findViewById(R.id.plsGzLy);
        plsGzLy.setOnClickListener(this);
        qxcGzLy = (RelativeLayout) findViewById(R.id.qxcGzLy);
        qxcGzLy.setOnClickListener(this);
        eexwGzLy = (RelativeLayout) findViewById(R.id.eexwGzLy);
        eexwGzLy.setOnClickListener(this);
        jczqGzLy = (RelativeLayout) findViewById(R.id.jczqGzLy);
        jczqGzLy.setOnClickListener(this);
        rxjGzLy = (RelativeLayout) findViewById(R.id.rxjGzLy);
        rxjGzLy.setOnClickListener(this);
        sfcwGzLy = (RelativeLayout) findViewById(R.id.sfcGzLy);
        sfcwGzLy.setOnClickListener(this);
        syxwGzLy = (RelativeLayout) findViewById(R.id.syxwGzLy);
        syxwGzLy.setOnClickListener(this);
        jclqGzLy = (RelativeLayout) findViewById(R.id.jclqGzLy);
        jclqGzLy.setOnClickListener(this);
        cqsscGzLy = (RelativeLayout) findViewById(R.id.cqsscGzLy);
        cqsscGzLy.setOnClickListener(this);
        hnklsfGzLy = (RelativeLayout) findViewById(R.id.hnklsfGzLy);
        hnklsfGzLy.setOnClickListener(this);
        tubiao = (ImageView) findViewById(R.id.tubiao);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.title_btinit_right){
            Intent intent = new Intent();
            intent.setClass(Help.this, Feedback.class);
            startActivity(intent);
        }
        else  if (v.getId() == R.id.maicaiLy) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("url", LotteryUtils.HELP_URL);
            bundle.putString("title", "帮助说明");
            intent.putExtras(bundle);
            intent.setClass(Help.this, WebBrowser.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.chongzhiLy) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("url", LotteryUtils.HELP_URL + "#chongzhi");
            bundle.putString("title", "帮助说明");
            intent.putExtras(bundle);
            intent.setClass(Help.this, WebBrowser.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.lingjiangLy) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("url", LotteryUtils.HELP_URL + "#lingjiang");
            bundle.putString("title", "帮助说明");
            intent.putExtras(bundle);
            intent.setClass(Help.this, WebBrowser.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.jifenLy) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("url", LotteryUtils.HELP_URL + "#jifen");
            bundle.putString("title", "帮助说明");
            intent.putExtras(bundle);
            intent.setClass(Help.this, WebBrowser.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.uniteLy) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("url", LotteryUtils.HELP_URL + "#hemai");
            bundle.putString("title", "帮助说明");
            intent.putExtras(bundle);
            intent.setClass(Help.this, WebBrowser.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.givelotteryLy) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("url", LotteryUtils.HELP_URL + "#givelottery");
            bundle.putString("title", "帮助说明");
            intent.putExtras(bundle);
            intent.setClass(Help.this, WebBrowser.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.jingcaiLy) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("url", LotteryUtils.HELP_URL + "#jingcai");
            bundle.putString("title", "帮助说明");
            intent.putExtras(bundle);
            intent.setClass(Help.this, WebBrowser.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.xinxiLy) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("url", LotteryUtils.HELP_URL + "#xinxi");
            bundle.putString("title", "帮助说明");
            intent.putExtras(bundle);
            intent.setClass(Help.this, WebBrowser.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.guizeLy) {
            if (ssqGzLy.isShown() == false) {
                ssqGzLy.setVisibility(View.VISIBLE);
                sdGzLy.setVisibility(View.VISIBLE);
                qlcGzLy.setVisibility(View.VISIBLE);
                swxwGzLy.setVisibility(View.VISIBLE);
                dfljyGzLy.setVisibility(View.VISIBLE);
                sslGzLy.setVisibility(View.VISIBLE);
                dltGzLy.setVisibility(View.VISIBLE);
                plwGzLy.setVisibility(View.VISIBLE);
                plsGzLy.setVisibility(View.VISIBLE);
                qxcGzLy.setVisibility(View.VISIBLE);
                eexwGzLy.setVisibility(View.VISIBLE);
                jczqGzLy.setVisibility(View.VISIBLE);
                rxjGzLy.setVisibility(View.VISIBLE);
                sfcwGzLy.setVisibility(View.VISIBLE);
                syxwGzLy.setVisibility(View.VISIBLE);
                jclqGzLy.setVisibility(View.VISIBLE);
                cqsscGzLy.setVisibility(View.VISIBLE);
                hnklsfGzLy.setVisibility(View.VISIBLE);
                tubiao.setImageResource(R.drawable.arrow_up);
            }
            else {
                ssqGzLy.setVisibility(View.GONE);
                sdGzLy.setVisibility(View.GONE);
                qlcGzLy.setVisibility(View.GONE);
                swxwGzLy.setVisibility(View.GONE);
                dfljyGzLy.setVisibility(View.GONE);
                sslGzLy.setVisibility(View.GONE);
                dltGzLy.setVisibility(View.GONE);
                plwGzLy.setVisibility(View.GONE);
                plsGzLy.setVisibility(View.GONE);
                qxcGzLy.setVisibility(View.GONE);
                eexwGzLy.setVisibility(View.GONE);
                jczqGzLy.setVisibility(View.GONE);
                rxjGzLy.setVisibility(View.GONE);
                sfcwGzLy.setVisibility(View.GONE);
                syxwGzLy.setVisibility(View.GONE);
                jclqGzLy.setVisibility(View.GONE);
                cqsscGzLy.setVisibility(View.GONE);
                hnklsfGzLy.setVisibility(View.GONE);
                tubiao.setImageResource(R.drawable.arrow_down);
            }
        }
        else if (v.getId() == R.id.ssqGzLy) {
            Intent intent = new Intent();
            Bundle bundel = new Bundle();
            bundel.putString("lottery_name", "双色球玩法介绍");
            bundel.putString("lottery_help", "help_new/ssq.html");
            intent.putExtras(bundel);
            intent.setClass(Help.this, LotteryWinningRules.class);
            startActivity(intent);

        }
        else if (v.getId() == R.id.sdGzLy) {
            Intent intent = new Intent();
            Bundle bundel = new Bundle();
            bundel.putString("lottery_name", "3D玩法介绍");
            bundel.putString("lottery_help", "help_new/x3d.html");
            intent.putExtras(bundel);
            intent.setClass(Help.this, LotteryWinningRules.class);
            startActivity(intent);

        }
        else if (v.getId() == R.id.qlcGzLy) {
            Intent intent = new Intent();
            Bundle bundel = new Bundle();
            bundel.putString("lottery_name", "七乐彩玩法介绍");
            bundel.putString("lottery_help", "help_new/qlc.html");
            intent.putExtras(bundel);
            intent.setClass(Help.this, LotteryWinningRules.class);
            startActivity(intent);

        }
        else if (v.getId() == R.id.swxwGzLy) {
            Intent intent = new Intent();
            Bundle bundel = new Bundle();
            bundel.putString("lottery_name", "十五选五玩法介绍");
            bundel.putString("lottery_help", "help_new/d15x5.html");
            intent.putExtras(bundel);
            intent.setClass(Help.this, LotteryWinningRules.class);
            startActivity(intent);

        }
        else if (v.getId() == R.id.dfljyGzLy) {
            Intent intent = new Intent();
            Bundle bundel = new Bundle();
            bundel.putString("lottery_name", "东方6+1玩法介绍");
            bundel.putString("lottery_help", "help_new/df6j1.html");
            intent.putExtras(bundel);
            intent.setClass(Help.this, LotteryWinningRules.class);
            startActivity(intent);

        }
        else if (v.getId() == R.id.sslGzLy) {
            Intent intent = new Intent();
            Bundle bundel = new Bundle();
            bundel.putString("lottery_name", "时时乐玩法介绍");
            bundel.putString("lottery_help", "help_new/ssc.html");
            intent.putExtras(bundel);
            intent.setClass(Help.this, LotteryWinningRules.class);
            startActivity(intent);

        }
        else if (v.getId() == R.id.dltGzLy) {
            Intent intent = new Intent();
            Bundle bundel = new Bundle();
            bundel.putString("lottery_name", "大乐透玩法介绍");
            bundel.putString("lottery_help", "help_new/dlt.html");
            intent.putExtras(bundel);
            intent.setClass(Help.this, LotteryWinningRules.class);
            startActivity(intent);

        }
        else if (v.getId() == R.id.plwGzLy) {
            Intent intent = new Intent();
            Bundle bundel = new Bundle();
            bundel.putString("lottery_name", "排列五玩法介绍");
            bundel.putString("lottery_help", "help_new/pl5.html");
            intent.putExtras(bundel);
            intent.setClass(Help.this, LotteryWinningRules.class);
            startActivity(intent);

        }
        else if (v.getId() == R.id.plsGzLy) {
            Intent intent = new Intent();
            Bundle bundel = new Bundle();
            bundel.putString("lottery_name", "排列三玩法介绍");
            bundel.putString("lottery_help", "help_new/pl3.html");
            intent.putExtras(bundel);
            intent.setClass(Help.this, LotteryWinningRules.class);
            startActivity(intent);

        }
        else if (v.getId() == R.id.qxcGzLy) {
            Intent intent = new Intent();
            Bundle bundel = new Bundle();
            bundel.putString("lottery_name", "七星彩玩法介绍");
            bundel.putString("lottery_help", "help_new/qxc.html");
            intent.putExtras(bundel);
            intent.setClass(Help.this, LotteryWinningRules.class);
            startActivity(intent);

        }
        else if (v.getId() == R.id.eexwGzLy) {
            Intent intent = new Intent();
            Bundle bundel = new Bundle();
            bundel.putString("lottery_name", "22选5玩法介绍");
            bundel.putString("lottery_help", "eexw/eexw_howto.html");
            intent.putExtras(bundel);
            intent.setClass(Help.this, LotteryWinningRules.class);
            startActivity(intent);

        }
        else if (v.getId() == R.id.jczqGzLy) {
            Intent intent = new Intent();
            Bundle bundel = new Bundle();
            bundel.putString("lottery_name", "竞技足球玩法介绍");
            bundel.putString("lottery_help", "help_new/jczq.html");
            intent.putExtras(bundel);
            intent.setClass(Help.this, LotteryWinningRules.class);
            startActivity(intent);

        }
        else if (v.getId() == R.id.rxjGzLy) {
            Intent intent = new Intent();
            Bundle bundel = new Bundle();
            bundel.putString("lottery_name", "任选九玩法介绍");
            bundel.putString("lottery_help", "help_new/f9.html");
            intent.putExtras(bundel);
            intent.setClass(Help.this, LotteryWinningRules.class);
            startActivity(intent);

        }
        else if (v.getId() == R.id.sfcGzLy) {
            Intent intent = new Intent();
            Bundle bundel = new Bundle();
            bundel.putString("lottery_name", "胜负彩玩法介绍");
            bundel.putString("lottery_help", "help_new/sfc.html");
            intent.putExtras(bundel);
            intent.setClass(Help.this, LotteryWinningRules.class);
            startActivity(intent);

        }
        else if (v.getId() == R.id.syxwGzLy) {
            Intent intent = new Intent();
            Bundle bundel = new Bundle();
            bundel.putString("lottery_name", "11选5玩法介绍");
            bundel.putString("lottery_help", "help_new/d11x5.html");
            intent.putExtras(bundel);
            intent.setClass(Help.this, LotteryWinningRules.class);
            startActivity(intent);

        }
        else if (v.getId() == R.id.jclqGzLy) {
            Intent intent = new Intent();
            Bundle bundel = new Bundle();
            bundel.putString("lottery_name", "竞彩篮球玩法介绍");
            bundel.putString("lottery_help", "help_new/jclq.html");
            intent.putExtras(bundel);
            intent.setClass(Help.this, LotteryWinningRules.class);
            startActivity(intent);

        }
        else if (v.getId() == R.id.cqsscGzLy) {
            Intent intent = new Intent();
            Bundle bundel = new Bundle();
            bundel.putString("lottery_name", "时时彩玩法介绍");
            bundel.putString("lottery_help", "help_new/ssc.html");
            intent.putExtras(bundel);
            intent.setClass(Help.this, LotteryWinningRules.class);
            startActivity(intent);

        }
        else if (v.getId() == R.id.hnklsfGzLy) {
            Intent intent = new Intent();
            Bundle bundel = new Bundle();
            bundel.putString("lottery_name", "快乐十分玩法介绍");
            bundel.putString("lottery_help", "help_new/hnkl10.html");
            intent.putExtras(bundel);
            intent.setClass(Help.this, LotteryWinningRules.class);
            startActivity(intent);

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open help");
        String eventName = "v2 open help";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_help";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Help.this.finish();
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(Help.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                          R.anim.push_to_left_out);
            }
        }
        return super.onKeyDown(keyCode, event);

    }

}