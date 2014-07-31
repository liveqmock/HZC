package com.haozan.caipiao.activity.weibo;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.activity.Login;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.util.scroll.MyScrollLayout;
import com.haozan.caipiao.util.scroll.OnViewChangeListener;
import com.haozan.caipiao.util.weiboutil.AccessInfoHelper;
import com.umeng.analytics.MobclickAgent;

public class WeiboHallScroll
    extends BasicActivity
    implements OnViewChangeListener, OnClickListener {
    /** Called when the activity is first created. */
    private MyScrollLayout mScrollLayout;
    private ImageView[] mImageViews;
    private int mViewCount;
    private int mCurSel;
    private RelativeLayout promptLy;
    private ScrollView scrollView;
    private TextView title;
    private Button newWeibo;
    private Button searchButton;
    private RelativeLayout ssqly, sdly, qlcly, ssl, dfljy, swxw, dltly, plwly, plsly, goucaily, putongly,
        xinwenly, eexwly, qxcly, jczqly, rxjly, sfcly, zhongjiangly, syxwly, gamesly, hnklsfly, jclqly,
        klsfly, cqsscly, hemaily, jingcaily, jxsscly, jlksly;
    private ImageView imageVerticalLine;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weibo_hall_scroll);
        setupViews();
        init();
    }

    private void setupViews() {
        scrollView = (ScrollView) findViewById(R.id.scrollView02);
        title = (TextView) findViewById(R.id.newCmtextView);
        title.setText("财园广场");
        newWeibo = (Button) findViewById(R.id.title_btinit_left);
        imageVerticalLine = (ImageView) findViewById(R.id.weibo_right_button_line);
        newWeibo.setOnClickListener(new NewWeiboListener());

        searchButton = (Button) findViewById(R.id.title_btinit_right);
// searchButton.setText("   搜索   ");
        searchButton.setOnClickListener(new searchButtonListener());

        promptLy = (RelativeLayout) findViewById(R.id.layout_update);
        promptLy.setOnClickListener(new PromptLyListener());
// promptLy.setVisibility(View.VISIBLE);
        ssqly = (RelativeLayout) findViewById(R.id.ssqly);
        ssqly.setOnClickListener(this);
        sdly = (RelativeLayout) findViewById(R.id.sdly);
        sdly.setOnClickListener(this);
        qlcly = (RelativeLayout) findViewById(R.id.qlcly);
        qlcly.setOnClickListener(this);
        ssl = (RelativeLayout) findViewById(R.id.sslly);
        ssl.setOnClickListener(this);
        dfljy = (RelativeLayout) findViewById(R.id.dfljyly);
        dfljy.setOnClickListener(this);
        swxw = (RelativeLayout) findViewById(R.id.swxwly);
        swxw.setOnClickListener(this);
        dltly = (RelativeLayout) findViewById(R.id.dltly);
        dltly.setOnClickListener(this);
        plwly = (RelativeLayout) findViewById(R.id.plwly);
        plwly.setOnClickListener(this);
        plsly = (RelativeLayout) findViewById(R.id.plsly);
        plsly.setOnClickListener(this);
        goucaily = (RelativeLayout) findViewById(R.id.goucaily);
        goucaily.setOnClickListener(this);
        putongly = (RelativeLayout) findViewById(R.id.putongly);
        putongly.setOnClickListener(this);
        xinwenly = (RelativeLayout) findViewById(R.id.xinwenly);
        xinwenly.setOnClickListener(this);
        eexwly = (RelativeLayout) findViewById(R.id.eexwly);
        eexwly.setOnClickListener(this);
        qxcly = (RelativeLayout) findViewById(R.id.qxcly);
        qxcly.setOnClickListener(this);
        jczqly = (RelativeLayout) findViewById(R.id.jjzqly);
        jczqly.setOnClickListener(this);
        rxjly = (RelativeLayout) findViewById(R.id.rxjly);
        rxjly.setOnClickListener(this);
        sfcly = (RelativeLayout) findViewById(R.id.sfcly);
        sfcly.setOnClickListener(this);
        zhongjiangly = (RelativeLayout) findViewById(R.id.zhongjiangly);
        zhongjiangly.setOnClickListener(this);
        // add by vincent
        syxwly = (RelativeLayout) findViewById(R.id.syxwly);
        syxwly.setOnClickListener(this);
        gamesly = (RelativeLayout) findViewById(R.id.gamesly);
        gamesly.setOnClickListener(this);
        hnklsfly = (RelativeLayout) findViewById(R.id.square_hnklsfly);
        hnklsfly.setOnClickListener(this);
        jclqly = (RelativeLayout) findViewById(R.id.square_jclq);
        jclqly.setOnClickListener(this);
        klsfly = (RelativeLayout) findViewById(R.id.square_klsfly);
        klsfly.setOnClickListener(this);
        cqsscly = (RelativeLayout) findViewById(R.id.square_cqsscly);
        cqsscly.setOnClickListener(this);
        hemaily = (RelativeLayout) findViewById(R.id.hemaily);
        hemaily.setOnClickListener(this);
        jingcaily = (RelativeLayout) findViewById(R.id.jingcaily);
        jingcaily.setOnClickListener(this);
        jxsscly = (RelativeLayout) findViewById(R.id.jxsscly);
        jxsscly.setOnClickListener(this);
        jlksly = (RelativeLayout) findViewById(R.id.jlksly);
        jlksly.setOnClickListener(this);
    }

    private void init() {
        mScrollLayout = (MyScrollLayout) findViewById(R.id.ScrollLayout);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.llayout);

        mViewCount = mScrollLayout.getChildCount();
        mImageViews = new ImageView[mViewCount];

        for (int i = 0; i < mViewCount; i++) {
            mImageViews[i] = (ImageView) linearLayout.getChildAt(i);
            mImageViews[i].setEnabled(true);
            mImageViews[i].setOnClickListener(new ImageViewsLLitener());
            mImageViews[i].setTag(i);
        }

        mCurSel = 0;
        mImageViews[mCurSel].setEnabled(false);

        mScrollLayout.SetOnViewChangeListener(this);
    }

    private void setCurPoint(int index) {
        if (index < 0 || index > mViewCount - 1 || mCurSel == index) {
            return;
        }

        mImageViews[mCurSel].setEnabled(true);
        mImageViews[index].setEnabled(false);

        mCurSel = index;
    }

    @Override
    public void OnViewChange(int view) {
        setCurPoint(view);
    }

    private class searchButtonListener
        implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (!checkLogin()) {
                lottery("搜索");
            }
            else {
                Intent intent = new Intent();
                intent.setClass(WeiboHallScroll.this, WeiboSearch.class);
                WeiboHallScroll.this.startActivity(intent);
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                    (new AnimationModel(WeiboHallScroll.this)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                         R.anim.push_to_right_out);
                }
            }
        }
    }

    private class NewWeiboListener
        implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (!checkLogin()) {
                lottery("写动态");
            }
            else {
                Intent intent = new Intent();
                intent.setClass(WeiboHallScroll.this, NewWeiboActivity.class);
                WeiboHallScroll.this.startActivity(intent);
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                    (new AnimationModel(WeiboHallScroll.this)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                         R.anim.push_to_right_out);
                }
            }
        }
    }

    class PromptLyListener
        implements OnClickListener {

        @Override
        public void onClick(View v) {
            promptLy.setVisibility(View.GONE);
        }
    }

    // 判断是否登录
    private boolean checkLogin() {
        String userid = appState.getSessionid();
        if (userid == null) {
            return false;
        }
        else {
            return true;
        }
    }

    private void lottery(String flag) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("forwardFlag", flag);
        bundle.putBoolean("ifStartSelf", false);
        intent.putExtras(bundle);
// intent.setClass(WeiboHallScroll.this, StartUp.class);
        intent.setClass(WeiboHallScroll.this, Login.class);
        startActivity(intent);
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
            (new AnimationModel(WeiboHallScroll.this)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                 R.anim.push_to_right_out);
        }
    }

    // 取消绑定
    class EndSessionThread
        implements Runnable {
        public void run() {
            AccessInfoHelper accessDBHelper = new AccessInfoHelper(mContext);
            accessDBHelper.open();
            accessDBHelper.delete();
            accessDBHelper.close();
            endSessionHandle.sendEmptyMessage(201);
        }
    }

    Handler endSessionHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            finish();
        }
    };

    // 提交财园广场类别点击事件统计信息
    private void submitStatisticsTopupSuccess(String kind) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", kind);
        map.put("more_inf", "username [" + appState.getUsername() + "]: user click garden square item");
        String eventName = "click garden square item";
        FlurryAgent.onEvent(eventName, map);

        String eventNameMob = "click_garden_square_item";
        HashMap<String, String> map1 = new HashMap<String, String>();
        map1.put("way", kind);
        MobclickAgent.onEvent(WeiboHallScroll.this, eventNameMob, map1);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        // 微博广场选项跳转
        for (int i = 0; i < BasicWeibo.layoutIdNames.length; i++) {
            if (v.getId() == BasicWeibo.layoutIdNames[i]) {
                bundle.putString("type", BasicWeibo.weiboTypesEnglishNames[i]);
                submitStatisticsTopupSuccess(BasicWeibo.weiboTypesEnglishNames[i]);
            }
        }
        intent.putExtras(bundle);
        intent.setClass(WeiboHallScroll.this, WeiboTypesActivity.class);
        WeiboHallScroll.this.startActivity(intent);
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
            (new AnimationModel(WeiboHallScroll.this)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                 R.anim.push_to_right_out);
        }
    }

    class ImageViewsLLitener
        implements OnClickListener {
        @Override
        public void onClick(View v) {
            int pos = (Integer) (v.getTag());
            setCurPoint(pos);
            mScrollLayout.snapToScreen(pos);
        }
    }

// //////解决滑屏与ScrollView冲突
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mScrollLayout.onTouchEvent(ev); // 让GestureDetector响应触碰事件
        super.dispatchTouchEvent(ev); // 让Activity响应触碰事件
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scrollView.onTouchEvent(event); // 让ScrollView响应触碰事件
        return false;
    }

    // //////
    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open garden hall event kind select");
        String eventName = "v2 open garden hall event kind select";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_garden_hall_event_kind_select";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            WeiboHallScroll.this.finish();
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(WeiboHallScroll.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                     R.anim.push_to_left_out);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}