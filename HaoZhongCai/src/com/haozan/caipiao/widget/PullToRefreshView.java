package com.haozan.caipiao.widget;

import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.haozan.caipiao.R;

public class PullToRefreshView
    extends LinearLayout {
    // refresh states
    private static final int PULL_TO_REFRESH = 2;
    private static final int RELEASE_TO_REFRESH = 3;
    private static final int REFRESHING = 4;
    // pull state
    private static final int PULL_DOWN_STATE = 1;

    private int startMoveSlop;
    /**
     * last y
     */
    private int mLastMotionY;
    /**
     * lock
     */
    private boolean mLock;
    /**
     * header view
     */
    private View mHeaderView;
    /**
     * list or grid
     */
    private AdapterView<?> mAdapterView;
    /**
     * scrollview
     */
    private ScrollView mScrollView;
    /**
     * header view height
     */
    private int mHeaderViewHeight;
    /**
     * header view image
     */
    private ImageView mHeaderImageView;
    // TODO add by vincent
    /**
     * footer view image
     */
    private ImageView mFooterImageView;
    /**
     * header tip text
     */
    private TextView mHeaderTextView;
    /**
     * footer tip text
     */
    private TextView mFooterTextView;
    /**
     * header refresh time
     */
    private TextView mHeaderUpdateTextView;
    /**
     * footer refresh time
     */
    // private TextView mFooterUpdateTextView;
    /**
     * header progress bar
     */
    private ProgressBar mHeaderProgressBar;
    /**
     * layout inflater
     */
    private LayoutInflater mInflater;
    /**
     * header view current state
     */
    private int mHeaderState;
    /**
     * pull state,pull up or pull down;PULL_UP_STATE or PULL_DOWN_STATE
     */
    private int mPullState;
    /**
     * 变为向下的箭头,改变箭头方向
     */
    private RotateAnimation mFlipAnimation;
    /**
     * 变为逆向的箭头,旋转
     */
    private RotateAnimation mReverseFlipAnimation;
    /**
     * footer refresh listener
     */
    private OnHeaderRefreshListener mOnHeaderRefreshListener;
    /**
     * last update time
     */
    private Date date;

    private float pullRatio = 0.5f;

    private Context context;

    public PullToRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public PullToRefreshView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    /**
     * init
     * 
     * @description
     * @param context
     */
    private void init() {
        startMoveSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        // Load all of the animations we need in code rather than through XML
        mFlipAnimation =
            new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mFlipAnimation.setInterpolator(new LinearInterpolator());
        mFlipAnimation.setDuration(250);
        mFlipAnimation.setFillAfter(true);
        mReverseFlipAnimation =
            new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
        mReverseFlipAnimation.setDuration(250);
        mReverseFlipAnimation.setFillAfter(true);

        mInflater = LayoutInflater.from(getContext());
        // header view 在此添加,保证是第一个添加到linearlayout的最上端
        addHeaderView();
        date = new Date();
    }

    private void addHeaderView() {
        // header view
        mHeaderView = mInflater.inflate(R.layout.layout_refresh, this, false);

        mHeaderImageView = (ImageView) mHeaderView.findViewById(R.id.pull_to_refresh_image);
        mHeaderTextView = (TextView) mHeaderView.findViewById(R.id.pull_to_refresh_text);
        mHeaderUpdateTextView = (TextView) mHeaderView.findViewById(R.id.pull_to_refresh_updated_at);
        mHeaderProgressBar = (ProgressBar) mHeaderView.findViewById(R.id.pull_to_refresh_progress);
        // header layout
        measureView(mHeaderView);
        mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, mHeaderViewHeight);
        // 设置topMargin的值为负的header View高度,即将其隐藏在最上方
        params.topMargin = -(mHeaderViewHeight);
        // mHeaderView.setLayoutParams(params1);
        addView(mHeaderView, params);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initContentAdapterView();
    }

    /**
     * init AdapterView like ListView,GridView and so on;or init ScrollView
     * 
     * @description
     */
    private void initContentAdapterView() {
        int count = getChildCount();
        if (count < 2) {
            throw new IllegalArgumentException(
                                               "this layout must contain 3 child views,and AdapterView or ScrollView must in the second position!");
        }
        View view = null;
        for (int i = 0; i < count; ++i) {
            view = getChildAt(i);
            if (view instanceof AdapterView<?>) {
                mAdapterView = (AdapterView<?>) view;
            }
            if (view instanceof ScrollView) {
                // finish later
                mScrollView = (ScrollView) view;
            }
        }
        if (mAdapterView == null && mScrollView == null) {
            throw new IllegalArgumentException("must contain a AdapterView or ScrollView in this layout!");
        }
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
    public boolean onInterceptTouchEvent(MotionEvent e) {
        int y = (int) e.getRawY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 首先拦截down事件,记录y坐标
                mLastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                // deltaY > 0 是向下运动,< 0是向上运动
                int deltaY = y - mLastMotionY;
                if (Math.abs(deltaY) > startMoveSlop) {
                    if (isRefreshViewScroll(deltaY)) {
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mLock) {
            return true;
        }
        int y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:// 下拉刷新?
                // onInterceptTouchEvent宸茬粡璁板綍
                // mLastMotionY = y;
                mHeaderUpdateTextView.setText("最近更新:");
                break;
            case MotionEvent.ACTION_MOVE:// 松开刷新
                int deltaY = y - mLastMotionY;
                if (mPullState == PULL_DOWN_STATE) {
                    headerPrepareToRefresh(deltaY);// 松开刷新
                    mHeaderUpdateTextView.setText("最近更新:");
                }
                mLastMotionY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:// 加载中
                int topMargin = getHeaderTopMargin();
                if (mPullState == PULL_DOWN_STATE) {
                    if (topMargin >= 0) {
                        // 寮€濮嬪埛鏂�
                        headerRefreshing();// 显示加载中
                        mHeaderUpdateTextView.setText("最近更新:");
                    }
                    else {
                        // 杩樻病鏈夋墽琛屽埛鏂帮紝閲嶆柊闅愯棌
                        setHeaderTopMargin(-mHeaderViewHeight);
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 是否应该到了父View,即PullToRefreshView滑动
     * 
     * @param deltaY , deltaY > 0 是向下运动, < 0是向上运动
     * @return
     */
    private boolean isRefreshViewScroll(int deltaY) {
        if (mHeaderState == REFRESHING) {
            return false;
        }
        // 对于ListView 以及GridView
        if (mAdapterView != null) {
            if (deltaY > 0) {

                View child = mAdapterView.getChildAt(0);
                if (child == null) {
                    // 如果mAdapterView中没有数据,不拦截
                    return false;
                }
                if (mAdapterView.getFirstVisiblePosition() == 0 && child.getTop() == 0) {
                    mPullState = PULL_DOWN_STATE;
                    return true;
                }
                int top = child.getTop();
                int padding = mAdapterView.getPaddingTop();
                if (mAdapterView.getFirstVisiblePosition() == 0 && Math.abs(top - padding) <= 8) {// 杩欓噷涔嬪墠鐢�3鍙互鍒ゆ柇,浣嗙幇鍦ㄤ笉琛�,杩樻病鎵惧埌鍘熷洜
                    mPullState = PULL_DOWN_STATE;
                    return true;
                }

            }
        }
        // 对于ScrollView
        if (mScrollView != null) {
            if (deltaY > 0 && mScrollView.getScrollY() == 0) {
                mPullState = PULL_DOWN_STATE;
                return true;
            }
        }
        return false;
    }

    /**
     * header 鍑嗗鍒锋柊,鎵嬫寚绉诲姩杩囩▼,杩樻病鏈夐噴鏀�
     * 
     * @param deltaY ,鎵嬫寚婊戝姩鐨勮窛绂�
     */
    private void headerPrepareToRefresh(int deltaY) {
        int newTopMargin = changingHeaderViewTopMargin(deltaY);
        // 褰揾eader view鐨則opMargin>=0鏃讹紝璇存槑宸茬粡瀹屽叏鏄剧ず鍑烘潵浜�,淇敼header view 鐨勬彁绀虹姸鎬�
        if (newTopMargin >= 0 && mHeaderState != RELEASE_TO_REFRESH) {
            mHeaderTextView.setText(R.string.pull_to_refresh_release_label);
            mHeaderUpdateTextView.setVisibility(View.VISIBLE);
            mHeaderImageView.clearAnimation();
            mHeaderImageView.startAnimation(mFlipAnimation);
            mHeaderState = RELEASE_TO_REFRESH;
        }
        else if (newTopMargin < 0 && newTopMargin > -mHeaderViewHeight) {// 鎷栧姩鏃舵病鏈夐噴鏀�
            mHeaderImageView.clearAnimation();
            mHeaderImageView.startAnimation(mFlipAnimation);
            // mHeaderImageView.
            mHeaderTextView.setText(R.string.pull_to_refresh_pull_label);
            mHeaderState = PULL_TO_REFRESH;
        }
    }

    /**
     * 修改Header view top margin的值
     * 
     * @description
     * @param deltaY
     * @return hylin
     */
    private int changingHeaderViewTopMargin(int deltaY) {
        LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
        float newTopMargin = params.topMargin + deltaY * pullRatio;
        if (deltaY < 0 && mPullState == PULL_DOWN_STATE && Math.abs(params.topMargin) >= mHeaderViewHeight) {
            return params.topMargin;
        }
        params.topMargin = (int) newTopMargin;
        mHeaderView.setLayoutParams(params);
        invalidate();
        return params.topMargin;
    }

    /**
     * header refreshing
     * 
     * @description
     */
    private void headerRefreshing() {
        mHeaderState = REFRESHING;
        setHeaderTopMargin(0);
        mHeaderImageView.setVisibility(View.GONE);
        mHeaderImageView.clearAnimation();
        mHeaderImageView.setImageDrawable(null);
        mHeaderProgressBar.setVisibility(View.VISIBLE);
        mHeaderTextView.setText(R.string.pull_to_refresh_refreshing_label);
        if (mOnHeaderRefreshListener != null) {
            mOnHeaderRefreshListener.onHeaderRefresh(this);
        }
    }

    /**
     * 设置header view 的topMargin的值
     * 
     * @description
     * @param topMargin 为0时，说明header view 刚好完全显示出来； 为-mHeaderViewHeight时，说明完全隐藏了
     */
    private void setHeaderTopMargin(int topMargin) {
        LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
        params.topMargin = topMargin;
        mHeaderView.setLayoutParams(params);
        invalidate();
    }

    /**
     * header view 完成更新后恢复初始状态
     * 
     * @description
     */
    public void onHeaderRefreshComplete() {
        // changed by vincent
        mHeaderUpdateTextView.setText("最近更新:");
        date = new Date();

        setHeaderTopMargin(-mHeaderViewHeight);
        mHeaderImageView.setVisibility(View.VISIBLE);
        mHeaderImageView.setImageResource(R.drawable.arrow);
        mHeaderTextView.setText(R.string.pull_to_refresh_pull_label);
        mHeaderProgressBar.setVisibility(View.GONE);
        // mHeaderUpdateTextView.setText("");
        mHeaderState = PULL_TO_REFRESH;
    }

    /**
     * Resets the list to a normal state after a refresh.
     * 
     * @param lastUpdated Last updated at.
     */
    public void onHeaderRefreshComplete(CharSequence lastUpdated) {
        setLastUpdated(lastUpdated);
        onHeaderRefreshComplete();
    }

    /**
     * Set a text to represent when the list was last updated.
     * 
     * @param lastUpdated Last updated at.
     */
    public void setLastUpdated(CharSequence lastUpdated) {
        if (lastUpdated != null) {
            mHeaderUpdateTextView.setVisibility(View.VISIBLE);
            mHeaderUpdateTextView.setText(lastUpdated);
        }
        else {
            mHeaderUpdateTextView.setVisibility(View.GONE);
        }
    }

    /**
     * 获取当前header view 的topMargin
     * 
     * @description
     * @return
     */
    private int getHeaderTopMargin() {
        LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
        return params.topMargin;
    }

    /**
     * set headerRefreshListener
     * 
     * @description
     * @param headerRefreshListener
     */
    public void setOnHeaderRefreshListener(OnHeaderRefreshListener headerRefreshListener) {
        mOnHeaderRefreshListener = headerRefreshListener;
    }

    /**
     * Interface definition for a callback to be invoked when list/grid header view should be refreshed.
     */
    public interface OnHeaderRefreshListener {
        public void onHeaderRefresh(PullToRefreshView view);
    }
}