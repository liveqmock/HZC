package com.haozan.caipiao.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.haozan.caipiao.R;

/*
 * auto load data when scroll to the bottom
 */
public class AutoLoadListView
    extends ListView {
    // load states
    private static final int TO_LOAD = 0;
    private static final int STOP_LOAD = 1;

    private static final int HAS_FOOTVIEW = 0;
    private static final int NO_FOOTVIEW = 1;

    private int status;
    private boolean firstLoad = true;
    private int footviewStatus;

    private View footView;
    private TextView tvLoadAgain;
    private LinearLayout layoutProgress;

    private LoadDataListener loadDataListener;
    private GetListItemPositionListener getListItemPositionListener;
    private Context context;

    public AutoLoadListView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public AutoLoadListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    public AutoLoadListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        footviewStatus = NO_FOOTVIEW;
        status = STOP_LOAD;

        addLoadingFootView();
        // addFootView必须在setAdapter之前调用，具体原因参考源码
    }

    OnScrollListener scrollListener = new OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            // 有footview的时候itemcount+1
            if (totalItemCount > 1 && (firstVisibleItem + visibleItemCount == totalItemCount) &&
                status == TO_LOAD) {
                status = STOP_LOAD;
                loadDataListener.loadData();
            }
            if (getListItemPositionListener != null) {
                getListItemPositionListener.processListItemPosition(view, firstVisibleItem, visibleItemCount,
                                                                    totalItemCount);
            }
        }
    };

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        if (firstLoad) {
// removeLoadingFootView();
            firstLoad = false;
        }
    }

    public void setOnLoadDataListener(LoadDataListener loadDataListener) {
        this.loadDataListener = loadDataListener;
        setOnScrollListener(scrollListener);
    }

    public interface LoadDataListener {
        public void loadData();
    }

    // 监听列表滚动位置
    public void setOnGetListItemPositionListener(GetListItemPositionListener getListItemPositionListener) {
        this.getListItemPositionListener = getListItemPositionListener;
    }

    public interface GetListItemPositionListener {
        public void processListItemPosition(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                            int totalItemCount);
    }

    public void addLoadingFootView() {
        if (footviewStatus == NO_FOOTVIEW) {
            footviewStatus = HAS_FOOTVIEW;
            footView = View.inflate(context, R.layout.list_item_load_more_view, null);
            tvLoadAgain = (TextView) footView.findViewById(R.id.get_again);
            tvLoadAgain.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    layoutProgress.setVisibility(View.VISIBLE);
                    tvLoadAgain.setVisibility(View.GONE);

                    loadDataListener.loadData();
                }
            });
            layoutProgress = (LinearLayout) footView.findViewById(R.id.loading_layout);
            addFooterView(footView);
        }
    }

    public void removeLoadingFootView() {
        if (footviewStatus == HAS_FOOTVIEW) {
            footviewStatus = NO_FOOTVIEW;
            removeFooterView(footView);
            status = STOP_LOAD;
        }
    }

    public void readyToLoad() {
        status = TO_LOAD;
    }

    public void loadNoMoreData() {
        removeLoadingFootView();
    }

    public void restoreListView() {
        addLoadingFootView();
    }

    public void showFootText() {
        tvLoadAgain.setVisibility(View.VISIBLE);
        layoutProgress.setVisibility(View.GONE);
    }
}
