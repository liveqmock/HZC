package com.haozan.caipiao.widget;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.Ball;
import com.haozan.caipiao.util.StringUtil;

/**
 * 绘制球工具类
 * 
 * @author peter_feng
 * @create-time 2013-5-28 下午5:27:46
 */
public class DrawBalls {

    private boolean isBigBall = false;

    public DrawBalls() {
        super();
    }

    public boolean isBigBall() {
        return isBigBall;
    }

    public void setBigBall(boolean isBigBall) {
        this.isBigBall = isBigBall;
    }

    /**
     * 根据彩种和开奖数据画球
     * 
     * @param context 上下文情景
     * @param ballsLayout 画球的容器(the layout for drawing balls)
     * @param kind 彩种英文简写id(the id of the lottery)
     */
    public void drawBallsLayout(Context context, PredicateLayout ballsLayout, String kind, String balls) {
        if (kind.equals("sfc") || kind.equals("r9")) {
            drawSFC(context, ballsLayout, kind, balls);
        }
        else if (kind.equals("cqssc") || kind.equals("jxssc")) {
            drawSSC(context, ballsLayout, kind, balls);
        }
        else {
            drawNormalBalls(context, ballsLayout, balls);
        }
    }

    private void drawSSC(Context context, PredicateLayout ballsLayout, String kind, String balls) {
        if (balls == null)
            return;
        ballsLayout.removeAllViewsInLayout();
        String[] rbballs = balls.split("\\|");
        String[] reds = rbballs[0].split(",");
        for (String r : reds) {
            drawBall(context, ballsLayout, r, "red");
        }
        if (rbballs.length > 1) {
            String[] blues = rbballs[1].split(",");
            for (String r : blues) {
                drawBall(context, ballsLayout, r, "blue");
            }
        }
        String ssc = StringUtil.getSSCInf(balls);
        if (ssc != null) {
            PredicateLayout.LayoutParams layoutParams = new PredicateLayout.LayoutParams(0, 0);
            TextView number = new TextView(context);
            number.setTypeface(Typeface.DEFAULT_BOLD);
            number.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            number.setGravity(Gravity.CENTER);
            number.setTextColor(context.getResources().getColor(R.color.light_purple));
            number.setText(ssc);
            number.setPadding(1, 1, 1, 1);
            ballsLayout.addView(number, layoutParams);
        }
    }

    private void drawSFC(Context context, PredicateLayout ballsLayout, String kind, String balls) {
        if (balls == null)
            return;
        ballsLayout.removeAllViewsInLayout();
        SFCHistoryItemLayout layout = new SFCHistoryItemLayout(context);
        layout.setBackgroundResource(R.drawable.hall_sports_bg);
        layout.initInf(kind, balls);
        ballsLayout.addView(layout, new PredicateLayout.LayoutParams(2, 2));
    }

    public void drawSFCHistoryBallOpen(Context context, PredicateLayout ballsLayout, String kind, String balls) {
        if (balls == null)
            return;
        ballsLayout.removeAllViewsInLayout();
        SFCHistoryItemLayout layout = new SFCHistoryItemLayout(context, "");
        layout.initBetHistoryInfOpen(kind, balls);
        ballsLayout.addView(layout, new PredicateLayout.LayoutParams(2, 2));
    }

    public void drawSFCHistoryBall(Context context, PredicateLayout ballsLayout, String kind, String balls,
                                   String[][] teams, int screenWidth) {
        if (balls == null)
            return;
        ballsLayout.removeAllViewsInLayout();
        SFCHistoryItemLayout layout = new SFCHistoryItemLayout(context, "sfc");
        layout.initBetHistoryInf(kind, balls, teams, screenWidth);
        ballsLayout.addView(layout, new PredicateLayout.LayoutParams(2, 2));
    }

    public void drawSFCHistoryBall(Context context, LinearLayout ballsLayout, String kind, String balls,
                                   String[][] teams, int screenWidth) {
        if (balls == null)
            return;
        ballsLayout.removeAllViewsInLayout();
        SFCHistoryItemLayout layout = new SFCHistoryItemLayout(context, "sfc");
        layout.initBetHistoryInf(kind, balls, teams, screenWidth);
        ballsLayout.addView(layout);
    }

    public void drawSFCBetHistoryAwardBall(Context context, LinearLayout ballsLayout, String kind,
                                           ArrayList<Ball> ball, String balls, String[][] teams,
                                           int screenWidth) {
        if (ball == null)
            return;
        ballsLayout.removeAllViewsInLayout();
        SFCHistoryItemLayout layout = new SFCHistoryItemLayout(context, "sfc");
        layout.initBetHistoryAwardInf(ball, kind, teams, screenWidth);
        ballsLayout.addView(layout);
    }

    private void drawNormalBalls(Context context, PredicateLayout ballsLayout, String balls) {
        if (balls == null)
            return;
        ballsLayout.removeAllViewsInLayout();
        String[] rbballs = balls.split("\\|");
        String[] reds = rbballs[0].split(",");
        if (rbballs.length < 3 && reds.length > 1) {
            for (String r : reds) {
                drawBall(context, ballsLayout, r, "red");
            }
            if (rbballs.length > 1) {
                String[] blues = rbballs[1].split(",");
                for (String r : blues) {
                    drawBall(context, ballsLayout, r, "blue");
                }
            }
        }
        else {
            for (String r : rbballs) {
                drawBall(context, ballsLayout, r, "red");
            }
        }
    }

    public void drawBall(Context context, PredicateLayout ballsView, String r, String color) {
        TextView tv = new TextView(context);
        String s = r.equals("?") ? "?" : r;
        tv.setText(s);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(context.getResources().getColor(R.color.white));
        int resId;
        if (isBigBall) {
            resId = color.equals("red") ? R.drawable.redball : R.drawable.blueball;
        }
        else {
            resId = color.equals("red") ? R.drawable.smallredball : R.drawable.smallblueball;
        }
        tv.setBackgroundResource(resId);
        ballsView.addView(tv);
    }
}
