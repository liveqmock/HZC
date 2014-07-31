package com.haozan.caipiao.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

import com.haozan.caipiao.R;

public class RefreshLayout
    extends LinearLayout {
    private static final int PULL_DOWN_STATE = 1;
    private static final int PULL_TO_REFRESH = 2;
    private static final int RELEASE_TO_REFRESH = 3;
    private static final int REFRESHING = 4;

    private int refreshStatus = PULL_TO_REFRESH;

    private float factor = 0.6f;

    private int startMoveSlop;

    private View mHeaderView;
    private ImageView mHeaderImageView;
    private TextView mHeaderTextView;
    private ProgressBar mHeaderProgressBar;

    private Scroller scroller;

    private ScrollView scrollView;
    private AdapterView<?> adapterView;

    private Context context;

    private int mLastMotionX, mLastMotionY;

    private int mHeaderViewHeight;

    private OnHeaderRefreshListener mOnHeaderRefreshListener;

    private Animation mFlipAnimation;
    private Animation mReverseFlipAnimation;

    private int deltaY;

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public RefreshLayout(Context context) {
        super(context);
        this.context = context;
        init();
    }

    private void init() {
        startMoveSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        scroller = new Scroller(context);

        mFlipAnimation =
            new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mFlipAnimation.setInterpolator(new LinearInterpolator());
        mFlipAnimation.setDuration(150);
        mFlipAnimation.setFillAfter(true);

        mReverseFlipAnimation =
            new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
        mReverseFlipAnimation.setDuration(150);
        mReverseFlipAnimation.setFillAfter(true);

        setupHeadViews();
    }

    private void setupHeadViews() {
        LayoutInflater mInflater = LayoutInflater.from(getContext());
        mHeaderView = mInflater.inflate(R.layout.layout_refresh, this, false);
        mHeaderImageView = (ImageView) mHeaderView.findViewById(R.id.pull_to_refresh_image);
        mHeaderTextView = (TextView) mHeaderView.findViewById(R.id.pull_to_refresh_text);
        mHeaderProgressBar = (ProgressBar) mHeaderView.findViewById(R.id.pull_to_refresh_progress);

        measureView(mHeaderView);
        mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        LinearLayout.LayoutParams params =
            new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, mHeaderViewHeight);
        params.topMargin = -(mHeaderViewHeight);
        addView(mHeaderView, params);
    }

    private void measureView(View mHeaderView) {
        ViewGroup.LayoutParams p = mHeaderView.getLayoutParams();
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
        mHeaderView.measure(childWidthSpec, childHeightSpec);
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(0, scroller.getCurrY());
            invalidate();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 首先拦截down事件,记录y坐标
                mLastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                // deltaY > 0 是向下运动,< 0是向上运动
                int deltaY = y - mLastMotionY;
                if (Math.abs(deltaY) >= startMoveSlop) {
                    if (refreshViewCanScroll(deltaY)) {
                        return true;
                    }
                    else {
                        return false;
                    }
                }
                break;
        }
        return false;
    }

    private boolean refreshViewCanScroll(int deltaY) {
        if (scrollView != null) {
            if (scrollView.getScrollY() == 0 && deltaY > 0) {
                return true;
            }
            else {
                return false;
            }
        }
        if (adapterView != null) {
            if (adapterView.getChildCount() > 0 && adapterView.getChildAt(0).getTop() == 0 && deltaY > 0) {
                return true;
            }
            else {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (refreshStatus != REFRESHING) {
            int x = (int) event.getRawX();
            int y = (int) event.getRawY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 首先拦截down事件,记录y坐标
                    mLastMotionY = y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    // deltaY > 0 是向下运动,< 0是向上运动
                    deltaY = (int) ((y - mLastMotionY - startMoveSlop) * factor);
                    refreshViewScroll(deltaY);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (refreshStatus == RELEASE_TO_REFRESH) {
                        headerRefreshing();

                        mOnHeaderRefreshListener.onRefresh();
                    }
                    else {
                        restoreScrollView();
                    }
                    break;
            }
        }
        return true;
    }

    public void headerRefreshing() {
        mHeaderImageView.clearAnimation();
        mHeaderImageView.setVisibility(View.INVISIBLE);
        mHeaderProgressBar.setVisibility(View.VISIBLE);

        mHeaderTextView.setText("刷新中..");

        refreshStatus = REFRESHING;

        setHeaderTopMargin();
    }

    private void setHeaderTopMargin() {
        scroller.startScroll(0, -deltaY, 0, deltaY-mHeaderView.getHeight(),500);
    }

    private void refreshViewScroll(int deltaY) {
        scrollTo(0, -deltaY);

        if (deltaY > mHeaderViewHeight && refreshStatus == PULL_TO_REFRESH) {
            mHeaderImageView.startAnimation(mFlipAnimation);
            mHeaderTextView.setText("松开刷新");
            refreshStatus = RELEASE_TO_REFRESH;
        }
        else if (deltaY < mHeaderViewHeight && refreshStatus == RELEASE_TO_REFRESH) {
            mHeaderImageView.startAnimation(mReverseFlipAnimation);
            mHeaderTextView.setText("下拉刷新");
            refreshStatus = PULL_TO_REFRESH;
        }
    }

    private void restoreScrollView() {
        mHeaderTextView.setText("松开刷新");
        scroller.startScroll(0, -deltaY, 0, deltaY,500);
    }

    public void onHeaderRefreshComplete() {
        restoreScrollView();

        mHeaderImageView.setVisibility(View.VISIBLE);
        mHeaderProgressBar.setVisibility(View.INVISIBLE);

        refreshStatus = PULL_TO_REFRESH;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initContentAdapterView();
    }

    private void initContentAdapterView() {
        View view = null;
        for (int i = 0; i < getChildCount(); i++) {
            view = getChildAt(i);

            if (view instanceof ScrollView) {
                scrollView = (ScrollView) view;
            }
            if (view instanceof AdapterView<?>) {
                adapterView = (AdapterView<?>) view;
            }
        }
    }

    public void setOnHeaderRefreshListener(OnHeaderRefreshListener mOnHeaderRefreshListener) {
        this.mOnHeaderRefreshListener = mOnHeaderRefreshListener;
    }

    public interface OnHeaderRefreshListener {
        public void onRefresh();
    }
}
