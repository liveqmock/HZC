package com.haozan.caipiao.view;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.BetBall;

public class BallView
    extends LinearLayout {

    private BetBall betBall;
    private String color;
    private Context context;

    public BallView(Context context, BetBall betBall, String color) {
        super(context);
        this.context = context;
        this.betBall = betBall;
        this.color = color;
        initViews();
    }

    private void initViews() {
        this.setOrientation(VERTICAL);
        TextView tv = new TextView(context);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(getTextColor(color));
        tv.setBackgroundResource(R.drawable.grayball);
        tv.setText(betBall.getContent());
        addView(tv);
        // 冷热门号码分析数据
        TextView analaseData = new TextView(context);
        analaseData.setGravity(Gravity.CENTER);
        analaseData.setTextSize(11);
        analaseData.setTextColor(Color.GRAY);
        analaseData.setVisibility(View.GONE);
        addView(analaseData);
        // 号码遗漏分析数据
        TextView omitData = new TextView(context);
        omitData.setGravity(Gravity.CENTER);
        omitData.setTextSize(11);
        omitData.setTextColor(Color.GRAY);
        omitData.setVisibility(View.GONE);
        addView(omitData);
    }

    private int getTextColor(String textColor) {
        int colorId = context.getResources().getColor(R.color.white);
        if (textColor.equals("red")) {
//            colorId = context.getResources().getColor(R.color.red_text);
            colorId = context.getResources().getColor(R.color.white);
        }
        else if (textColor.equals("white")) {
            colorId = context.getResources().getColor(R.color.white);
        }
        else if (textColor.equals("blue")) {
//            colorId = context.getResources().getColor(R.color.blue_text);
            colorId = context.getResources().getColor(R.color.white);
        }
        return colorId;
    }

    public BetBall getBetBall() {
        return betBall;
    }

    /**
     * 刷新球，显示球底部信息，如冷热门号码、遗漏等
     */
    public void refreshBallInf(Boolean showAnalase) {
        refreshBallInf(showAnalase, false);
    }

    /**
     * 刷新球，显示球底部信息，如冷热门号码、遗漏等
     */
    public void refreshBallInf(Boolean showAnalase, Boolean showOmit) {
        TextView analaseData = (TextView) getChildAt(1);
        if (betBall.getBallAnalase() != null && showAnalase) {
            String infStr = betBall.getBallAnalase();
            analaseData.setText(Html.fromHtml(infStr));
            analaseData.setVisibility(View.VISIBLE);
        }
        else {
            analaseData.setVisibility(View.GONE);
        }
        TextView omitData = (TextView) getChildAt(2);
        if (betBall.getBallOmit() != null && showOmit) {
            String infStr = betBall.getBallOmit();
            omitData.setText(Html.fromHtml(infStr));
            omitData.setVisibility(View.VISIBLE);
        }
        else {
            omitData.setVisibility(View.GONE);
        }
    }

    /**
     * 刷新球的状态
     */
    public void refreshBall() {
        if (!isEnabled())
            return;
        if (betBall.isChoosed()) {
            int resId = color.equals("red") ? R.drawable.redball : R.drawable.blueball;
            ((TextView) getChildAt(0)).setTextColor(getTextColor("white"));
            getChildAt(0).setBackgroundResource(resId);
        }
        else {
            ((TextView) getChildAt(0)).setTextColor(getTextColor(color));
            getChildAt(0).setBackgroundResource(R.drawable.grayball);
        }
    }

// reset the color of the ball
    public void resetBall() {
        ((TextView) getChildAt(0)).setTextColor(getTextColor(color));
        getChildAt(0).setBackgroundResource(R.drawable.grayball);
    }

// set the color of the ball to be choosed
    public void setBall(boolean status) {
        if (status) {
            int resId = color.equals("red") ? R.drawable.redball : R.drawable.blueball;
            ((TextView) getChildAt(0)).setTextColor(getTextColor("white"));
            getChildAt(0).setBackgroundResource(resId);
        }
        else {
            ((TextView) getChildAt(0)).setTextColor(getTextColor(color));
            getChildAt(0).setBackgroundResource(R.drawable.grayball);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        getChildAt(0).setEnabled(enabled);
    }
}
