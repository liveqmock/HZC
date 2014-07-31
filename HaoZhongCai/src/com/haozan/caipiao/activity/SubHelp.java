package com.haozan.caipiao.activity;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.umeng.analytics.MobclickAgent;

public class SubHelp
    extends BasicActivity {
    private TextView tv;
    private TextView htv;
    private Button moreInf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub_help);
        moreInf = (Button) findViewById(R.id.more_help_inf);
        tv = (TextView) findViewById(R.id.hy_text);
// htv = (TextView) findViewById(R.id.exit);
        Bundle bl = this.getIntent().getExtras();
        int str = bl.getInt("help01");
        int str1 = bl.getInt("help02");
        int str2 = bl.getInt("help03");
        int strSsq = bl.getInt("helpssq");
        int str3d = bl.getInt("help3d");
        int strQlc = bl.getInt("helpqlc");
        int strDfljy = bl.getInt("helpdfljy");
        int strSwxw = bl.getInt("helpswxw");
        int strSsl = bl.getInt("helpssl");
        int strResetPw = bl.getInt("resetPw");
        int validpay = bl.getInt("valipay");
        int pointInf = bl.getInt("pointInf");
        if (str != 0) {
            tv.setText(str);
        }
        if (str1 != 0) {
            tv.setText(str1);
        }
        if (str2 != 0) {
            tv.setText(str2);
        }
        if (strSsq != 0) {
            tv.setText(strSsq);
        }
        if (str3d != 0) {
            tv.setText(str3d);
        }
        if (strQlc != 0) {
            tv.setText(strQlc);
        }
        if (strDfljy != 0) {
            tv.setText(strDfljy);
        }
        if (strSwxw != 0) {
            tv.setText(strSwxw);
        }
        if (strSsl != 0) {
            tv.setText(strSsl);
        }
        if (strResetPw != 0) {
            tv.setText(strResetPw);
        }
        if (validpay != 0) {
            tv.setText(validpay);
        }
        if (pointInf != 0) {
            tv.setText(pointInf);
            moreInf.setVisibility(View.VISIBLE);
        }
// htv.setOnClickListener(new OnClickListener() {
//
// @Override
// public void onClick(View v) {
// SubHelp.this.finish();
// }
// });
        moreInf.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bdl = new Bundle();
                bdl.putString("helpyou", "helpyou");
                intent.putExtras(bdl);
                intent.setClass(SubHelp.this, Help.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open sub help");
        String eventName = "v2 open sub help";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_smallhelp";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            SubHelp.this.finish();
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(SubHelp.this)).overridePendingTransition(R.anim.push_to_right_in,
                                                                             R.anim.push_to_right_out);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
