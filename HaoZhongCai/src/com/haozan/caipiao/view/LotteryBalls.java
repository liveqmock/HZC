package com.haozan.caipiao.view;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.haozan.caipiao.types.LotteryBallsData;
import com.haozan.caipiao.widget.PredicateLayout;

public class LotteryBalls
    implements OnClickListener {

    private int start;
    private int limitCount;
    private Boolean alwaysShow = false;

    private Context context;
    private LotteryBallsData balls;
    private OnBallFullListener fullListener;
    private Toast toast;
    private PredicateLayout layout;
    public static final String[] animals = new String[] {"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡",
            "狗", "猪"};
    public int ballSetionIndex;

// private PopupBall popupBall;

    public LotteryBalls(Context context, Toast toast, LotteryBallsData balls, int start, int limitCount,
                        int ballSetionIndex) {
        this.context = context;
        this.toast = toast;
        this.balls = balls;
        this.start = start;
        this.limitCount = limitCount;
        this.ballSetionIndex = ballSetionIndex;
// popupBall = new PopupBall(context);
// popupBall.setPopupClickListener(this);
    }

    public void drawBalls(PredicateLayout view, int ballLength, String color) {
        layout = view;
        view.removeAllViewsInLayout();
        int length = start + ballLength;
        for (int j = start; j < length; j++)
            drawBall(view, j, color);
    }

    public void drawBall(PredicateLayout ballsView, int number, String color) {
        BallTextView tv = new BallTextView(context, number, color);
        tv.setOnClickListener(this);
        ballsView.addView(tv, new PredicateLayout.LayoutParams(0, 0));
    }

    public void drawBallsSx(PredicateLayout view, int ballLength, String color) {
        layout = view;
        view.removeAllViewsInLayout();
        int length = ballLength + start;
        for (int j = 1; j < length; j++)
            drawBallSx(view, animals[j - 1], color, j);
    }

    public void drawBallSx(PredicateLayout ballsView, String shengxiao, String color, int number) {
        BallTextView tv = new BallTextView(context, shengxiao, color, number);
        tv.setOnClickListener(this);
        ballsView.addView(tv, new PredicateLayout.LayoutParams(0, 0));
    }

    @Override
    public void onClick(View v) {
        BallTextView ball = (BallTextView) v;
        if (ball.getChoose()) {
            balls.setCount(balls.getCount() - 1);
            ball.setClickEvent();
        }
        else {
            if (balls.getCount() >= limitCount) {
                if (fullListener != null)
                    fullListener.onBallClickFull(balls.getBallType());
            }
            else {
                balls.setCount(balls.getCount() + 1);
                ball.setClickEvent();
            }
        }
        balls.getBalls().set(ball.getNumber() - start, ball.getChoose());
        if (fullListener != null)
            fullListener.onBallClickInf();
    }

    // clear the ball choosed
    public void resetBalls() {
        balls.setCount(0);
        int length = balls.getBalls().size();
        for (int i = 0; i < length; i++) {
            ((BallTextView) layout.getChildAt(i)).resetBall();
            balls.getBalls().set(i, false);
        }
    }

    // choose the ball appointed
    public void chooseBall(int i) {
        balls.setCount(balls.getCount() + 1);
        ((BallTextView) layout.getChildAt(i)).chooseBall();
    }

    public void setFullListener(OnBallFullListener fullListener) {
        this.fullListener = fullListener;
    }

    public interface OnBallFullListener {
        public void onBallClickFull(String ballType);

        public void onBallClickInf();

        public void onBallLongClickInf(String string, int i);

        public void onPopupBallClick();
    }
}
