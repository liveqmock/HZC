package com.haozan.caipiao.activity;

import java.util.HashMap;
import java.util.Map;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.umeng.analytics.MobclickAgent;

public class Compass
    extends BasicActivity {

    private float presentOrentation = 0.0f;

    private ImageView ImgCompass;
    private TextView OrientText;
    private RotateAnimation myAni = null;
    private float DegressQuondam = 0.0f;

    // add by vincent
// private TextView east;
// private TextView south;
// private TextView west;
// private TextView north;

    private SensorManager sm;
    private Sensor orienttationSensor;
    private String direction = null;
    private float lastOrentation = 0.0f;
    private SensorEventListener orienttationListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            presentOrentation = event.values[0];
            if (Math.abs(presentOrentation - lastOrentation) >= 2) {
                lastOrentation = presentOrentation;
                float range = (float) 22.5;
                // 指向正北
                if (presentOrentation > 360 - range && presentOrentation < 360 + range) {
                    direction = "正北";
                }
                // 指向正东
                if (presentOrentation > 90 - range && presentOrentation < 90 + range) {
                    direction = "正东";
                }
                // 指向正南
                if (presentOrentation > 180 - range && presentOrentation < 180 + range) {
                    direction = "正南";
                }
                // 指向正西
                if (presentOrentation > 270 - range && presentOrentation < 270 + range) {
                    direction = "正西";
                }
                // 指向东北
                if (presentOrentation > 45 - range && presentOrentation < 45 + range) {
                    direction = "东北";
                }
                // 指向东南
                if (presentOrentation > 135 - range && presentOrentation < 135 + range) {
                    direction = "东南";
                }
                // 指向西南
                if (presentOrentation > 225 - range && presentOrentation < 225 + range) {
                    direction = "西南";
                }
                // 指向西北
                if (presentOrentation > 315 - range && presentOrentation < 315 + range) {
                    direction = "西北";
                }
            }

            if (direction != null) {
                OrientText.setText(direction + "　" + (int) presentOrentation + "°");
            }

            ((TextView) findViewById(R.id.OrientValue)).setText(String.valueOf(presentOrentation));

            if (DegressQuondam != -presentOrentation)
                AniRotateImage(-presentOrentation);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }

    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.compass);
        OrientText = (TextView) findViewById(R.id.OrientText);
        ImgCompass = (ImageView) findViewById(R.id.ivCompass);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                             WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        orienttationSensor = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    }

    @Override
    public void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open compass");
        String eventName = "v2 open compass";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sm.registerListener(orienttationListener, orienttationSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sm.unregisterListener(orienttationListener);
    }

    private void AniRotateImage(float fDegress) {
        myAni =
            new RotateAnimation(DegressQuondam, fDegress, Animation.RELATIVE_TO_SELF, 0.5f,
                                Animation.RELATIVE_TO_SELF, 0.5f);
        myAni.setDuration(300);
        myAni.setFillAfter(true);

        ImgCompass.startAnimation(myAni);

        DegressQuondam = fDegress;
    }

    @Override
    protected void submitData() {
        String eventName = "open_compass";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Compass.this.finish();
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(Compass.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                             R.anim.push_to_left_out);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}