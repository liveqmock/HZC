package com.haozan.caipiao.widget;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.Ball;

public class SFCHistoryItemLayout
    extends LinearLayout {
    private Context context;
    private Paint paint;
    private String kind = "";

    public SFCHistoryItemLayout(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public SFCHistoryItemLayout(Context context, String kind) {
        super(context);
        this.context = context;
        this.kind = kind;
        init();
    }

    public SFCHistoryItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        if (kind.equals(""))
            setOrientation(HORIZONTAL);
        else if (kind.equals("sfc"))
            setOrientation(VERTICAL);
        setWillNotDraw(false);
        paint = new Paint();
        paint.setColor(context.getResources().getColor(R.color.light_white));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1.0F);
    }

    public void initInf(String kind, String balls) {
        String[] ball = balls.split(",");
        for (int i = 0; i < ball.length; i++) {
            TextView number = new TextView(context);
            number.setTypeface(Typeface.DEFAULT_BOLD);
            number.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            number.setGravity(Gravity.CENTER);
            number.setTextColor(context.getResources().getColor(R.color.white));
            number.setText(ball[i]);
            number.setPadding(1, 1, 1, 1);
            addView(number);
        }
    }

    public void initBetHistoryInfOpen(String kind, String balls) {
        String[] ball = balls.split(",");
        for (int i = 0; i < ball.length; i++) {
            LinearLayout.LayoutParams linParams =
                new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            TextView number = new TextView(context);
            number.setTypeface(Typeface.DEFAULT_BOLD);
            number.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            number.setGravity(Gravity.CENTER);
            number.setTextColor(context.getResources().getColor(R.color.white));
            number.setText(ball[i]);
            number.setBackgroundResource(R.drawable.sfc_bet_history_detail_code_bg_red);
            number.setPadding(2, 1, 2, 1);
            linParams.setMargins(1, 1, 1, 1);
            addView(number, linParams);
        }
    }

    private int[] numArray = {0, 3, 1, 0, 0};
    private String[][] betCodeArray = new String[15][5];
    private Ball[][] ballsArray = new Ball[15][5];

    private void initBetCodeArray(String balls) {
        StringBuilder betCodeSb = new StringBuilder();
        betCodeSb.delete(0, betCodeSb.length());
        for (int j = 0; j < 15; j++) {
            betCodeSb.append("-");
            betCodeSb.append(",");
            betCodeSb.append(balls.split("\\:")[0]);
            String betCode = betCodeSb.toString().split("\\,")[j];
            char[] codeArray = betCode.toCharArray();
            for (int i = 0; i < 5; i++) {
                if (codeArray.length == i)
                    break;
                if (codeArray[i] == '0')
                    betCodeArray[j][3] = "0";
                else if (codeArray[i] == '1')
                    betCodeArray[j][2] = "1";
                else if (codeArray[i] == '3')
                    betCodeArray[j][1] = "3";

            }
        }
    }

    public void initBetHistoryInf(String kind, String balls, String[][] teams, int screenWidth) {
        String[] betNumArray = balls.split("\\+");
        String[] ball = betNumArray[0].split(",");
        initBetCodeArray(balls);
        int colorId = context.getResources().getColor(R.color.white);
        int childWidth = 0;
        if (betNumArray.length > 1)
            childWidth = screenWidth / 18;
        else
            childWidth = screenWidth / 20;

        for (int j = 0; j < 5; j++) {
            LinearLayout linearLayout = new LinearLayout(context);
            LinearLayout.LayoutParams linParams =
                new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            linearLayout.setLayoutParams(linParams);
            linearLayout.setOrientation(HORIZONTAL);
            for (int i = 0; i < ball.length + 1; i++) {
                TextView number = new TextView(context);
                number.setWidth(childWidth);
                number.setHeight(32);
                number.setTypeface(Typeface.DEFAULT_BOLD);
                number.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                number.setGravity(Gravity.CENTER);
                number.setTextColor(colorId);
                number.setPadding(2, 1, 2, 1);
                if (j == 1 && i == 0)
                    number.setText("胜");
                else if (j == 2 && i == 0)
                    number.setText("平");
                else if (j == 3 && i == 0)
                    number.setText("负");
                else if (j == 0) {
                    number.setPadding(0, 0, 0, 0);
                    number.setText(teams[i][0].substring(0, 1));
                    number.setTextColor(context.getResources().getColor(R.color.dark_purple));
                }
                else if (j == 4) {
                    number.setPadding(0, 0, 0, 0);
                    number.setText(teams[i][1].substring(0, 1));
                    number.setTextColor(context.getResources().getColor(R.color.dark_purple));
                }
                else {
                    number.setText(String.valueOf(numArray[j]));
                }

                if (betCodeArray[i][j] == null)
                    number.setBackgroundResource(R.drawable.sfc_bet_history_detail_code_bg);
                else
                    number.setBackgroundResource(R.drawable.sfc_bet_history_detail_code_bg_red);
                linParams.setMargins(1, 1, 1, 1);
                linearLayout.addView(number, linParams);
            }
            addView(linearLayout);
        }
    }

    private void addBallList(ArrayList<Ball> ball) {
        Ball ballItem = new Ball();
        ballItem.setColor("red");
        ballItem.setGroupIndex(-1);
        ballItem.setNumber(null);
        ballItem.setState(false);
        ball.add(0, ballItem);
        for (int i = 0; i < ball.size(); i++)
            ball.get(i).setGroupIndex(ball.get(i).getGroupIndex() + 1);
    }

    private void initAwardBetCodeInf(ArrayList<Ball> ball) {
        for (int j = 0; j < ball.size(); j++) {
            if (ball.get(j).getNumber() != null) {
                if (ball.get(j).getNumber().equals("0"))
                    ballsArray[ball.get(j).getGroupIndex()][3] = ball.get(j);
                else if (ball.get(j).getNumber().equals("1"))
                    ballsArray[ball.get(j).getGroupIndex()][2] = ball.get(j);
                else if (ball.get(j).getNumber().equals("3"))
                    ballsArray[ball.get(j).getGroupIndex()][1] = ball.get(j);
            }
        }
    }

    public void initBetHistoryAwardInf(ArrayList<Ball> ball, String kind, String[][] teams, int screenWidth) {
        int resId = 0;
        int colorId=context.getResources().getColor(R.color.white);
        int textViewWidth=screenWidth / 20;
        addBallList(ball);
        initAwardBetCodeInf(ball);
        for (int j = 0; j < 5; j++) {
            LinearLayout linearLayout = new LinearLayout(context);
            LinearLayout.LayoutParams linParams =
                new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            linearLayout.setLayoutParams(linParams);
            linearLayout.setOrientation(HORIZONTAL);
            for (int i = 0; i < ballsArray.length; i++) {
                TextView number = new TextView(context);
                number.setWidth(textViewWidth);
                number.setHeight(32);
                number.setTypeface(Typeface.DEFAULT_BOLD);
                number.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                number.setGravity(Gravity.CENTER);
                number.setTextColor(colorId);
                number.setPadding(2, 1, 2, 1);

                if (ballsArray[i][j] != null) {
                    number.setBackgroundResource(R.drawable.sfc_bet_history_detail_code_bg_red);
                    if (ballsArray[i][j].isState())
                        resId = R.color.golden;
                    else
                        resId = R.color.white;
                }
                else {
                    number.setBackgroundResource(R.drawable.sfc_bet_history_detail_code_bg);
                    resId = R.color.white;
                }
                number.setTextColor(context.getResources().getColor(resId));
                if (j == 1 && i == 0)
                    number.setText("胜");
                else if (j == 2 && i == 0)
                    number.setText("平");
                else if (j == 3 && i == 0)
                    number.setText("负");
                else if (j == 0) {
                    number.setPadding(0, 0, 0, 0);
                    number.setText(teams[i][0].substring(0, 1));
                    number.setTextColor(context.getResources().getColor(R.color.dark_purple));
                }
                else if (j == 4) {
                    number.setPadding(0, 0, 0, 0);
                    number.setText(teams[i][1].substring(0, 1));
                    number.setTextColor(context.getResources().getColor(R.color.dark_purple));
                }
                else {
                    number.setText(String.valueOf(numArray[j]));
                }

                linParams.setMargins(1, 1, 1, 1);
                linearLayout.addView(number, linParams);
            }
            addView(linearLayout);
        }
        ball.remove(0);
        for (int a = 0; a < ball.size(); a++)
            ball.get(a).setGroupIndex(ball.get(a).getGroupIndex() - 1);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (kind.equals("")) {
            for (int i = 1; i < getChildCount(); i++) {
                View view = getChildAt(i);
                canvas.drawLine(view.getLeft(), view.getTop() + 4, view.getLeft(), view.getBottom() - 4,
                                paint);
            }
        }
    }
}
