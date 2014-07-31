package com.haozan.caipiao.widget;

import java.util.Date;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Scroller;

import com.haozan.caipiao.R;
import com.haozan.caipiao.widget.pulltorefresh.PullToRefreshBase.Mode;
import com.haozan.caipiao.widget.pulltorefresh.internal.LoadingLayout;

/**
 * 刷新控制view
 * 
 * @author Nono
 */
public class RefreshableView
    extends LinearLayout {

    private static final String TAG = "LILITH";
    static final float FRICTION = 2.0f;
    private Scroller scroller;
    private LoadingLayout mHeaderView;
    private float mInitialMotionY;
    private float mLastMotionY;
    private int refreshTargetTop = -60;

    private OnHeaderRefreshListener refreshListener;

    private Long refreshTime = null;
    private int lastY;
    // 拉动标记
    private boolean isDragging = false;
    // 是否可刷新标记
    private boolean isRefreshEnabled = true;
    // 在刷新中标记
    private boolean isRefreshing = false;
    private int mHeaderViewHeight;

    private Context mContext;
    private Date date;
    private RotateAnimation mFlipAnimation;
    private RotateAnimation mReverseFlipAnimation;

    public RefreshableView(Context context) {
        super(context);
        mContext = context;

    }

    public RefreshableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(context, attrs);

    }

    private void init(Context context, AttributeSet attrs) {
        mFlipAnimation =
            new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mFlipAnimation.setInterpolator(new LinearInterpolator());
        mFlipAnimation.setDuration(500);
        mFlipAnimation.setFillAfter(true);
        mReverseFlipAnimation =
            new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
        mReverseFlipAnimation.setDuration(500);
        mReverseFlipAnimation.setFillAfter(true);

        // 滑动对象，
        scroller = new Scroller(mContext);
        // Styleables from XML
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PullToRefresh);
        mHeaderView = new LoadingLayout(context, Mode.PULL_DOWN_TO_REFRESH, a);
        measureView(mHeaderView);
        mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        refreshTargetTop = -mHeaderViewHeight;
        LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, mHeaderViewHeight);
        lp.topMargin = refreshTargetTop;
        lp.gravity = Gravity.CENTER;
        addView(mHeaderView, lp);
        date = new Date();
    }

    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                                           ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        }
        else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int y = (int) event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mInitialMotionY = event.getY();
                // 记录下y坐标
                lastY = y;
                break;

            case MotionEvent.ACTION_MOVE:
                mLastMotionY = event.getY();
                pullEvent();
                // y移动坐标
                int m = y - lastY;
                if (((m < 6) && (m > -1)) || (!isDragging)) {
                    doMovement(m);
                }
                // 记录下此刻y坐标
                this.lastY = y;

                break;

            case MotionEvent.ACTION_UP:
                Log.i(TAG, "ACTION_UP");

                fling();

                break;
        }
        return true;
    }

    public void pullEvent() {
        final int newHeight;
        newHeight = Math.round(Math.min(mInitialMotionY - mLastMotionY, 0) / FRICTION);
        if (newHeight != 0) {
            float scale = Math.abs(newHeight) / (float) mHeaderViewHeight;
            mHeaderView.onPullY(scale);
        }
    }

    /**
     * up事件处理
     */
    private void fling() {
        LinearLayout.LayoutParams lp = (LayoutParams) mHeaderView.getLayoutParams();
        Log.i(TAG, "fling()" + lp.topMargin);
        if (lp.topMargin > 0) {// 拉到了触发可刷新事件
            refresh();
        }
        else {
            returnInitState();
        }
    }

    private void returnInitState() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.mHeaderView.getLayoutParams();
        int i = lp.topMargin;
        scroller.startScroll(0, i, 0, refreshTargetTop);
        invalidate();
    }

    private void refresh() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.mHeaderView.getLayoutParams();
        int i = lp.topMargin;
        mHeaderView.refreshing();
        scroller.startScroll(0, i, 0, 0 - i);
        invalidate();
        if (refreshListener != null) {
            refreshListener.onHeaderRefresh(this);
            isRefreshing = true;
        }
    }

    /**
	 * 
	 */
    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            int i = this.scroller.getCurrY();
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.mHeaderView.getLayoutParams();
            int k = Math.max(i, refreshTargetTop);
            lp.topMargin = k;
            this.mHeaderView.setLayoutParams(lp);
            this.mHeaderView.invalidate();
            invalidate();
        }
    }

    /**
     * 下拉move事件处理
     * 
     * @param moveY
     */
    private void doMovement(int moveY) {
        LinearLayout.LayoutParams lp = (LayoutParams) mHeaderView.getLayoutParams();
        if (moveY > 0) {
            // 获取view的上边距
            float f1 = lp.topMargin;
            float f2 = moveY * 0.3F;
            int i = (int) (f1 + f2);
            // 修改上边距
            lp.topMargin = i;
            // 修改后刷新
            mHeaderView.setLayoutParams(lp);
            mHeaderView.invalidate();
            invalidate();
        }
        if (refreshTime != null) {
            setRefreshTime(refreshTime);
        }
        headerPrepareToRefresh(moveY);
    }

    private void headerPrepareToRefresh(int deltaY) {
        int newTopMargin = changingHeaderViewTopMargin(deltaY);
        if (newTopMargin >= 0) {
            mHeaderView.releaseToRefresh();
        }
        else if (newTopMargin < 0 && newTopMargin > -mHeaderViewHeight) {
            mHeaderView.reset();
        }
    }

    private int changingHeaderViewTopMargin(int deltaY) {
        LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
        float newTopMargin = params.topMargin + deltaY * 0.2f;
        if (deltaY < 0 && Math.abs(params.topMargin) >= mHeaderViewHeight) {
            return params.topMargin;
        }
        params.topMargin = (int) newTopMargin;
        mHeaderView.setLayoutParams(params);
        invalidate();
        return params.topMargin;
    }

    public void setRefreshEnabled(boolean b) {
        this.isRefreshEnabled = b;
    }

    public void setOnHeaderRefreshListener(OnHeaderRefreshListener listener) {
        this.refreshListener = listener;
    }

    /**
     * 刷新时间
     * 
     * @param refreshTime2
     */
    private void setRefreshTime(Long time) {

    }

    /**
     * 结束刷新事件
     */
    public void onHeaderRefreshComplete() {
        date = new Date();
        Log.i(TAG, "执行了=====finishRefresh");
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.mHeaderView.getLayoutParams();
        int i = lp.topMargin;
        scroller.startScroll(0, i, 0, refreshTargetTop);
        invalidate();
        isRefreshing = false;
        mHeaderView.reset();
        setHeaderTopMargin(-mHeaderViewHeight);
    }

    private void setHeaderTopMargin(int topMargin) {
        LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
        params.topMargin = topMargin;
        mHeaderView.setLayoutParams(params);
        invalidate();
    }

    public void onHeaderRefreshComplete(CharSequence lastUpdated) {
        setLastUpdated(lastUpdated);
        onHeaderRefreshComplete();
    }

    public void setLastUpdated(CharSequence lastUpdated) {
    }

    /*
     * 该方法一般和ontouchEvent 一起用 (non-Javadoc)
     * @see android.view.ViewGroup#onInterceptTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) { 
        int action = e.getAction();
        int y = (int) e.getRawY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = mInitialMotionY = e.getY();
                lastY = y;
                break;

            case MotionEvent.ACTION_MOVE:
                mLastMotionY = e.getY();
                // y移动坐标
                int m = y - lastY;

                // 记录下此刻y坐标
                this.lastY = y;
                if (m > 6 && canScroll()) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:

                break;

            case MotionEvent.ACTION_CANCEL:

                break;
        }
        return false;
    }

    private boolean canScroll() {
        View childView;
        if (getChildCount() > 1) {
            childView = this.getChildAt(1);
            if (childView instanceof ListView) {
                int top = ((ListView) childView).getChildAt(0).getTop();
                int pad = ((ListView) childView).getListPaddingTop();
                if ((Math.abs(top - pad)) < 3 && ((ListView) childView).getFirstVisiblePosition() == 0) {
                    return true;
                }
                else {
                    return false;
                }
            }
            else if (childView instanceof ScrollView) {
                if (((ScrollView) childView).getScrollY() == 0) {
                    return true;
                }
                else {
                    return false;
                }
            }

        }
        return false;
    }

    /**
     * 刷新监听接口
     * 
     * @author Nono
     */
    public interface OnHeaderRefreshListener {
        public void onHeaderRefresh(RefreshableView view);
    }

}
