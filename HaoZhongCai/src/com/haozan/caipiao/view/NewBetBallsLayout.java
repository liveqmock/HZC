package com.haozan.caipiao.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.Html;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.BetBall;
import com.haozan.caipiao.types.BetBallsData;
import com.haozan.caipiao.types.BetBigBallViews;
import com.haozan.caipiao.view.PopupBall.ShowOnDismissListener;

public class NewBetBallsLayout
    extends ViewGroup
    implements ShowOnDismissListener {
    private static final int MOVE_LIMIT = 20;
    private BetBallsData balls;
    private BetBigBallViews bigBallViews;
    private RelativeLayout bigBallLayout;
    private TextView bigBallTv;
    private TextView hotNumTv;
    private Context context;
    private OnBallOpeListener fullListener;
    private OnTouchMoveListener touchMoveListener;
    private Activity activity;
    private int marginLeft = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == current) {

            }
        }
    };
    private GestureDetector mGestureDetector = new GestureDetector(new OnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            touchMoveListener.touchMove((int) (e1.getY() - e2.getY()));
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            notPress = false;
            onTouchEvent(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            notPress = false;
            onTouchEvent(e);
            return false;
        }

    });
    private int line_height;
    private int current = -1;
    private int scrollY;
    private boolean notPress = true;

    public static class LayoutParams
        extends ViewGroup.LayoutParams {
        public final int horizontal_spacing;
        public final int vertical_spacing;

        /**
         * @param horizontal_spacing Pixels between items, horizontally
         * @param vertical_spacing Pixels between items, vertically
         */
        public LayoutParams(int horizontal_spacing, int vertical_spacing) {
            super(0, 0);
            this.horizontal_spacing = horizontal_spacing;
            this.vertical_spacing = vertical_spacing;
        }
    }

    public NewBetBallsLayout(Context context) {
        super(context);
        this.context = context;
        init();
    }

    protected void vibarate() {
        Vibrator mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        mVibrator.vibrate(50);
    }

    public void initData(BetBallsData balls, BetBigBallViews bigBallViews, Activity activity) {
        this.balls = balls;
        this.bigBallViews = bigBallViews;
        this.activity = activity;
        bigBallLayout = bigBallViews.getBigBallLayout();
        bigBallTv = bigBallViews.getBigBallTv();
        hotNumTv = bigBallViews.getHotNumTv();
        drawBalls(balls.getBetBalls().size(), balls.getColor());
        TextPaint tp = bigBallTv.getPaint();
        tp.setFakeBoldText(true);
    }

    public void resetBalls() {
        balls.setCount(0);
        int length = balls.getBetBalls().size();
        for (int i = 0; i < length; i++) {
            ((BallView) getChildAt(i)).resetBall();
            balls.getBetBalls().get(i).setChoosed(false);
            balls.getBetBalls().get(i).setEnabled(true);
        }
    }

    public void chooseBall(int i) {
        balls.setCount(balls.getCount() + 1);
        ((BallView) getChildAt(i)).setBall(true);
    }

    public NewBetBallsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        setLongClickable(true);
    }

// 刷新球的状态显示
    public void refreshBalls() {
        int length = getChildCount();
        for (int j = 0; j < length; j++) {
            ((BallView) getChildAt(j)).refreshBall();
        }
    }

    public void drawBalls(int ballLength, String color) {
        removeAllViewsInLayout();
        int length = balls.getBetBalls().size();
        for (int j = 0; j < length; j++) {
            drawBall(balls.getBetBalls().get(j), color);
        }
    }

    public void drawBall(BetBall ball, String color) {
        BallView tv = new BallView(context, ball, color);
        addView(tv, new NewBetBallsLayout.LayoutParams(4, 4));
    }

    /**
     * 还原所有的球成可点击状态
     */
    public void enableAllBall() {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).setEnabled(true);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        assert (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED);

        final int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        final int count = getChildCount();
        int line_height = 0;

        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                child.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),
                              MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED));

                final int childw = child.getMeasuredWidth();
                line_height = Math.max(line_height, child.getMeasuredHeight() + lp.vertical_spacing);

                if (xpos + childw > width) {
                    marginLeft = Math.max(marginLeft, width - xpos);
                    xpos = getPaddingLeft();
                    ypos += line_height;
                }

                xpos += childw + lp.horizontal_spacing;
            }
        }
        this.line_height = line_height;

        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            height = ypos + line_height;

        }
        else if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            if (ypos + line_height < height) {
                height = ypos + line_height;
            }
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(1, 1); // default of 1px spacing
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        if (p instanceof LayoutParams)
            return true;
        return false;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        final int width = r - l;
        int xpos = getPaddingLeft() + marginLeft / 2;
        int ypos = getPaddingTop();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final int childw = child.getMeasuredWidth();
                final int childh = child.getMeasuredHeight();
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (xpos + childw > width) {
                    xpos = getPaddingLeft() + marginLeft / 2;
                    ypos += line_height;
                }
                child.layout(xpos, ypos, xpos + childw, ypos + childh);
                xpos += childw + lp.horizontal_spacing;
            }
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (notPress)
            return mGestureDetector.onTouchEvent(event);
        else {
            int x = (int) event.getX();
            int y = (int) event.getY();
            int count = getChildCount();
            int index = -1;
            for (int i = 0; i < count; i++) {
                int top = getChildAt(i).getTop();
                int bottom = getChildAt(i).getBottom();
                int left = getChildAt(i).getLeft();
                int right = getChildAt(i).getRight();
                if (x >= left && x <= right && y >= top && y <= bottom) {
                    index = i;
                    break;
                }
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    bigBallLayout.setVisibility(View.GONE);
                    notPress = true;
                    if (index != -1) {
                        onChooseBall(index);
                    }
                    break;
                case MotionEvent.ACTION_DOWN:
                    scrollY = y;
                    if (index != -1) {
                        showBigBall(index);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (Math.abs(scrollY - y) > MOVE_LIMIT) {
                        scrollY = y;
                    }
                    if (index != -1) {
                        if (current != index) {
                            bigBallLayout.setVisibility(View.GONE);
                            current = index;
                            showBigBall(index);
                        }
                    }
                    else {
                        bigBallLayout.setVisibility(View.GONE);
                    }
            }
            return true;
        }
    }

    private void showBigBall(int index) {
        BallView ball = (BallView) getChildAt(index);
        if (!ball.isEnabled())
            return;
        bigBallLayout.setVisibility(View.VISIBLE);
        int[] location = new int[2];
        getChildAt(index).getLocationOnScreen(location);
        // 获取状态栏高度，localRect.top就是状态栏高度，location[1]是包括状态栏的坐标
        Rect localRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        bigBallTv.setText(ball.getBetBall().getContent());
        // 如果没有冷热门号码，就居中显示，有冷热门号码，大号码靠左显示，右边显示冷热号开出次数
        if (ball.getBetBall().getBallsInf() != null) {
            if (balls.getColor().equals("red")) {
                bigBallLayout.setBackgroundResource(R.drawable.bet_number_popup_big_red);
            }
            else {
                bigBallLayout.setBackgroundResource(R.drawable.bet_number_popup_big_blue);
            }
            RelativeLayout.LayoutParams layoutparams =
                new RelativeLayout.LayoutParams(
                                                (int) getResources().getDimension(R.dimen.bet_popup_num_width),
                                                ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutparams.addRule(RelativeLayout.ALIGN_LEFT);
            layoutparams.leftMargin = 13;
            layoutparams.topMargin = 3;
            bigBallTv.setLayoutParams(layoutparams);
            hotNumTv.setText(Html.fromHtml(ball.getBetBall().getBallsInf()));
        }
        else {
            if (balls.getColor().equals("red")) {
                bigBallLayout.setBackgroundResource(R.drawable.bet_number_popup_red);
            }
            else {
                bigBallLayout.setBackgroundResource(R.drawable.bet_number_popup_blue);
            }
            RelativeLayout.LayoutParams layoutparams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutparams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            layoutparams.leftMargin = 0;
            bigBallTv.setLayoutParams(layoutparams);
        }
        AbsoluteLayout.LayoutParams localLayoutParams1 =
            (AbsoluteLayout.LayoutParams) this.bigBallLayout.getLayoutParams();
        localLayoutParams1.x = (int) (location[0] - 0.5 * (bigBallLayout.getWidth() - ball.getWidth()));
        localLayoutParams1.y =
            location[1] - localRect.top - bigBallLayout.getHeight() + ball.getChildAt(0).getHeight() + 9;
        bigBallLayout.setLayoutParams(localLayoutParams1);
    }

    public void onChooseBall(int index) {
        BallView ballView = (BallView) getChildAt(index);
        if (!ballView.isEnabled())
            return;
        vibarate();
        BetBall ball = balls.getBetBalls().get(index);
        if (ball.isChoosed()) {
            ballView.setBall(false);
            balls.setCount(balls.getCount() - 1);
            ball.setChoosed(false);
        }
        else {
            if (balls.getCount() >= balls.getLimit()) {
                if (fullListener != null) {
                    fullListener.onBallClickFull(balls.getBallType());
                    return;
                }
            }
            else {
                ballView.setBall(true);
                balls.setCount(balls.getCount() + 1);
                ball.setChoosed(true);
            }
        }
        if (fullListener != null)
            fullListener.onBallClickInf(balls.getBallType(), index);
    }

    /**
     * 刷新球组，显示每个球底部信息冷热门号码
     */
    public void refreshAllBallInf(Boolean showNum) {
        int length = getChildCount();
        for (int i = 0; i < length; i++) {
            ((BallView) getChildAt(i)).refreshBallInf(showNum);
        }
    }

    /**
     * 刷新球组，显示每个球底部信息，如冷热门号码、遗漏等
     */
    public void refreshAllBallInf(Boolean showNum, Boolean showOmit) {
        int length = getChildCount();
        for (int i = 0; i < length; i++) {
            ((BallView) getChildAt(i)).refreshBallInf(showNum, showOmit);
        }
    }

    /**
     * 刷新球组，显示每个球选中情况
     */
    public void refreshAllBall() {
        int length = getChildCount();
        for (int i = 0; i < length; i++) {
            BetBall ball = balls.getBetBalls().get(i);
            BallView ballView = ((BallView) getChildAt(i));
            if (ball.isChoosed()) {
                ballView.setBall(true);
            }
            else {
                ballView.setBall(false);
            }
            ballView.setEnabled(ball.isEnabled());
        }
    }

    @Override
    public void isDismiss() {
        current = -1;
    }

    public void setFullListener(OnBallOpeListener fullListener) {
        this.fullListener = fullListener;
    }

    public interface OnBallOpeListener {
        /**
         * 点击某个球条件受限，比如已经超过指定个数
         * 
         * @param ballType 属于哪组球
         */
        public void onBallClickFull(int kind);

        /**
         * 点击某个球事件
         * 
         * @param kind 属于哪组球
         * @param index 某组球里面的哪个球
         */
        public void onBallClickInf(int kind, int index);
    }

    public interface OnTouchMoveListener {
        public void touchMove(int move);
    }

    public void setTouchMoveListener(OnTouchMoveListener touchMoveListener) {
        this.touchMoveListener = touchMoveListener;
    }

}
