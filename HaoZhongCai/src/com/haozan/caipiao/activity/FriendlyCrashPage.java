package com.haozan.caipiao.activity;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.umeng.analytics.MobclickAgent;

public class FriendlyCrashPage
    extends BasicActivity
    implements OnClickListener {

    private TextView tvCrashTips;
    private Button btnFeedback;
    private Button btnRestart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendly_crash_page);
        setupViews();
        init();
    }

    private void setupViews() {
        btnFeedback = (Button) this.findViewById(R.id.feedback);
        btnFeedback.setOnClickListener(this);
        btnRestart = (Button) this.findViewById(R.id.restart);
        btnRestart.setOnClickListener(this);
        tvCrashTips = (TextView) this.findViewById(R.id.friendly_crash_tips);
    }

    private void init() {
        tvCrashTips.setText("哎，" + getResources().getString(R.string.app_name) + "出错了\n非常抱歉给您带来不便");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.feedback) {
            Intent intent = new Intent();
            intent.setClass(this, Feedback.class);
            startActivity(intent);
            finish();
        }
        else if (v.getId() == R.id.restart) {
            Intent intent = new Intent();
            intent.setClass(this, StartUp.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open friendly crash page");
        String eventName = "open friendly crash page";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_friendly_crash_page";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }
}