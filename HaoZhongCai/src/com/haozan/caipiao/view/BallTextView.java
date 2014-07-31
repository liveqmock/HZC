package com.haozan.caipiao.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import com.haozan.caipiao.R;

public class BallTextView
    extends TextView {

    // whether the ball is choosed
    private Boolean choose;
    // the text of the ball
    private int number;
    private String openCount;
    private String word;
    private String color;

    public BallTextView(Context context, int number, String color) {
        super(context);
        this.number = number;
        this.color = color;
        this.word = null;
        this.choose = false;
        initDrawable();
    }

    public BallTextView(Context context, String word, String color, int number) {
        super(context);
        this.number = number;
        this.color = color;
        this.word = word;
        this.choose = false;
        initDrawable();
    }

    private void initDrawable() {
        setTypeface(Typeface.DEFAULT_BOLD);
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        setGravity(Gravity.CENTER);
        setTextColor(Color.WHITE);
        setBackgroundResource(R.drawable.grayball);
        if (word != null)
            setText(word);
        else {
            setText(String.valueOf(number));
        }
    }

    public BallTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public BallTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    // reset the color of the ball
    public void resetBall() {
        choose = false;
        setBackgroundResource(R.drawable.grayball);
    }

    // set the color of the ball to be choosed
    public void chooseBall() {
        choose = true;
        int resId = color.equals("red") ? R.drawable.redball : R.drawable.blueball;
        setBackgroundResource(resId);
    }

    public int getNumber() {
        return number;
    }

    public String getWord() {
        return word;
    }

    public Boolean getChoose() {
        return choose;
    }

    public String getColor() {
        return color;
    }

    public String getOpenCount() {
        return openCount;
    }

    public void setOpenCount(String openCount) {
        this.openCount = openCount;
    }

    public void setClickEvent() {
        if (choose) {
            choose = false;
            setBackgroundResource(R.drawable.grayball);
        }
        else {
            choose = true;
            int resId = color.equals("red") ? R.drawable.redball : R.drawable.blueball;
            setBackgroundResource(resId);
        }
    }
}
