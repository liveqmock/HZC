package com.haozan.caipiao.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.control.InitControl;
import com.haozan.caipiao.control.LoginControl;
import com.haozan.caipiao.control.LotteryInfControl;
import com.haozan.caipiao.fragment.MainActivity;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.igexin.slavesdk.MessageManager;
import com.umeng.analytics.MobclickAgent;

/**
 * 开机闪屏页面
 * 
 * @author peter_wang
 * @create-time 2013-10-12 下午1:46:36
 */
public class StartUp
    extends BasicActivity {
    private static final int TO_MAIN = 0;
    private static final int HALL_INIT = 1;

    private ImageView mIvTop;
    private ImageView mIvLuckyFlag;
    private ImageView mIvFlash;

    private Bitmap mFlashBitmap;

    private InitControl mInitControl;
    private LotteryInfControl mLotteryInfControl;
    private LoginControl mLoginControl;

    private MyHandler mHandler;

    private static class MyHandler
        extends WeakReferenceHandler<StartUp> {

        public MyHandler(StartUp reference) {
            super(reference);
        }

        @Override
        protected void handleMessage(StartUp activity, Message msg) {
            switch (msg.what) {
                case ControlMessage.FINISH_CHECK_CONNECTION:
                    if (HttpConnectUtil.isNetworkAvailable(activity)) {
                       if(null!=activity.mInitControl) activity.mInitControl.refreshLocalConnectionInf();

                       if(null!= activity.mLotteryInfControl) activity.mLotteryInfControl.setLotteryInf();
                    }
                    break;

                case ControlMessage.FINISH_ACTIVITY:
                    activity.finish();
                    break;

                case TO_MAIN:
                    activity.gotoMain();
                    break;

                case HALL_INIT:
                    MobclickAgent.updateOnlineConfig(activity);

                    if (HttpConnectUtil.isNetworkAvailable(activity)) {
                        activity.mLoginControl.checkAutoLogin();
                    }

                    activity.mFlashBitmap = activity.mInitControl.showFlash();
                    boolean isShow = activity.showFlash();
                    if (isShow) {
                        activity.mHandler.sendEmptyMessageDelayed(TO_MAIN, 4000);
                    }
                    else {
                        activity.mHandler.sendEmptyMessageDelayed(TO_MAIN, 1500);
                    }

                    activity.mInitControl.startPushService();
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);
        MobclickAgent.updateOnlineConfig(this);
        MessageManager.getInstance().initialize(this.getApplicationContext());
        initData();
        setupViews();
        init();
    }

    private void setupViews() {
        mIvTop = (ImageView) findViewById(R.id.img_hzc_tv);
        mIvLuckyFlag = (ImageView) findViewById(R.id.img_day_lucky);
        mIvFlash = (ImageView) findViewById(R.id.img_flash);
    }

    private void initData() {
        mHandler = new MyHandler(this);

        mInitControl = new InitControl(this, mHandler);

        mLotteryInfControl = new LotteryInfControl(this, mHandler);

        mLoginControl = new LoginControl(this, mHandler);
    }

    private void init() {
        if (HttpConnectUtil.isNetworkAvailable(mContext)) {
            mInitControl.checkConnection();
        }

        mInitControl.createShortCut();

        // 推送设置、读取闪屏等耗时操作延迟处理
        mHandler.sendEmptyMessage(HALL_INIT);
    }

    private void initChatroomUserInf() {
// Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.lucky_cat);
// // sex 0 - 男， 1 - 女， 2 - 未设置
// BaseInfo info = new BaseInfo(appState.getNickname(), 2, b, null, null);
// // 传入个人信息
// QplusSDK.getInstance().setBaseInfo(info);
// QplusSDK.getInstance().setMiniable(false);
    }

    private void startAnimation() {
        AnimationSet as = new AnimationSet(true);

        TranslateAnimation animation1 = new TranslateAnimation(0, 0, Animation.RELATIVE_TO_SELF + 80, 0);
        animation1.setDuration(500);
        animation1.setFillEnabled(true);
        animation1.setFillBefore(true);
        animation1.setFillAfter(true);

        AlphaAnimation alpha = new AlphaAnimation((float) 0.0, (float) 1.0);
        alpha.setDuration(500);

        as.addAnimation(animation1);
        as.addAnimation(alpha);
        as.setFillEnabled(true);
        as.setFillBefore(true);
        as.setFillAfter(true);
        as.setAnimationListener(goodLuck);

        mIvTop.startAnimation(as);
    }

    /**
     * 移动结束出现“好运从这里开始”
     */
    private AnimationListener goodLuck = new AnimationListener() {

        @Override
        public void onAnimationEnd(Animation animation) {
            startDayLuckyAnimation();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }

    };

    private void startDayLuckyAnimation() {
        AlphaAnimation alpha = new AlphaAnimation((float) 0.0, (float) 1.0);
        alpha.setDuration(1500);
        alpha.setFillEnabled(true);
        alpha.setFillBefore(true);
        alpha.setFillAfter(true);
        mIvLuckyFlag.startAnimation(alpha);
    }

    /**
     * 是否显示闪屏图片
     * 
     * @return
     */
    protected boolean showFlash() {
        if (mFlashBitmap != null) {
            mIvFlash.setBackgroundDrawable(new BitmapDrawable(mFlashBitmap));

            AlphaAnimation alpha = new AlphaAnimation((float) 0.0, (float) 1.0);
            alpha.setDuration(1000);
            alpha.setFillAfter(true);
            mIvFlash.setAnimation(alpha);
            return true;
        }
        else {
            startAnimation();
            return false;
        }
    }

    private void gotoMain() {
        Intent intent = new Intent();
        intent.setClass(StartUp.this, MainActivity.class);
        startActivity(intent);
        StartUp.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFlashBitmap != null) {
            mFlashBitmap.recycle();
            mFlashBitmap = null;
        }

        mHandler.removeCallbacksAndMessages(null);
        mInitControl = null;
        mLotteryInfControl = null;
    }

    @Override
    protected void submitData() {
        String eventName = "open_startup";
        MobclickAgent.onEvent(this, eventName);
    }

}