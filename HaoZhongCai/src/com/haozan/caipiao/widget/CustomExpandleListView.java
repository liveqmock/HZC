package com.haozan.caipiao.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.haozan.caipiao.R;

public class CustomExpandleListView
    extends FrameLayout {
    private RelativeLayout indicatorGroup;
    private ExpandableListView listview;

    private int layoutId;
    private int indicatorGroupId = -1;
    private int index;
    private int indicatorGroupHeight;
    private boolean showGroup = false;
    private int groupHeigh;

    private BaseExpandableListAdapter adapter;
    private Context context;

    public CustomExpandleListView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public CustomExpandleListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    public CustomExpandleListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        setWillNotDraw(false);
        listview = new ExpandableListView(context);
        listview.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,
                                                              FrameLayout.LayoutParams.FILL_PARENT));
        listview.setGroupIndicator(null);
        listview.setDivider(null);
        listview.setDividerHeight(0);
        listview.setCacheColorHint(context.getResources().getColor(R.color.transparent));
        addView(listview);
        indicatorGroup = new RelativeLayout(context);
        indicatorGroup.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,
                                                                    FrameLayout.LayoutParams.WRAP_CONTENT));
        addView(indicatorGroup);
    }

    public int getIndex() {
        return index;
    }

    public void setAdapter(BaseExpandableListAdapter adapter, int layoutId) {
        this.adapter = adapter;
        this.layoutId = layoutId;
        listview.setAdapter(adapter);
    }

// public void setInf(final BaseExpandableListAdapter adapter) {
// this.adapter = adapter;
// setOnScrollListener(new OnScrollListener() {
//
// @Override
// public void onScrollStateChanged(AbsListView view, int scrollState) {
// // TODO Auto-generated method stub
//
// }
//
// @Override
// public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
// int totalItemCount) {
// ExpandableListView listview = (ExpandableListView) view;
// int npos = view.pointToPosition(0, 0);
// if (npos != AdapterView.INVALID_POSITION) {
// long pos = listview.getExpandableListPosition(npos);
// int childPos = ExpandableListView.getPackedPositionChild(pos);
// int groupPos = ExpandableListView.getPackedPositionGroup(pos);
// if (childPos == AdapterView.INVALID_POSITION) {
// View groupView = listview.getChildAt(npos - listview.getFirstVisiblePosition());
// indicatorGroupHeight = groupView.getHeight();
// }
// // get an error data, so return now
// if (indicatorGroupHeight == 0) {
// return;
// }
// // update the data of indicator group view
// if (groupPos != indicatorGroupId) {
// adapter.getGroupView(groupPos, listview.isGroupExpanded(groupPos),
// indicatorGroup.getChildAt(0), null);
// indicatorGroupId = groupPos;
// }
// }
// if (indicatorGroupId == -1) {
// return;
// }
//
// /**
// * calculate point (0,indicatorGroupHeight)
// */
// int showHeight = indicatorGroupHeight;
// int nEndPos = listview.pointToPosition(0, indicatorGroupHeight);
// if (nEndPos != AdapterView.INVALID_POSITION) {
// long pos = listview.getExpandableListPosition(nEndPos);
// int groupPos = ExpandableListView.getPackedPositionGroup(pos);
// if (groupPos != indicatorGroupId) {
// View viewNext = listview.getChildAt(nEndPos - listview.getFirstVisiblePosition());
// showHeight = viewNext.getTop();
// }
// index = groupPos;
// }
// if (firstVisibleItem == 0 || !listview.isGroupExpanded(index)) {
// if (showGroup) {
// showGroup = false;
// indicatorGroup.setVisibility(View.GONE);
// }
// }
// else {
// if (!showGroup) {
// showGroup = true;
// indicatorGroup.setVisibility(View.VISIBLE);
// }
// }
// if (groupHeigh != indicatorGroupHeight - showHeight) {
// // update group position
// groupHeigh = indicatorGroupHeight - showHeight;
// MarginLayoutParams layoutParams = (MarginLayoutParams) indicatorGroup.getLayoutParams();
// layoutParams.topMargin = -(indicatorGroupHeight - showHeight);
// indicatorGroup.setLayoutParams(layoutParams);
// }
// }
// });
// }

    public void expandList() {
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            listview.expandGroup(i);
        }
    }

    public void refreshContent() {
        indicatorGroup.removeAllViews();
        indicatorGroupId = -1;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int length = adapter.getGroupCount();
        for (int groupPos = 0; groupPos < length; groupPos++) {
            final int index = groupPos;
            LinearLayout layout = new LinearLayout(context);
            inflater.inflate(layoutId, layout, true);
            adapter.getGroupView(groupPos, listview.isGroupExpanded(groupPos), layout, null);
            layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                                                                 LinearLayout.LayoutParams.WRAP_CONTENT));
            layout.setVisibility(View.GONE);
            layout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (listview.isGroupExpanded(index)) {
                        listview.collapseGroup(index);
                    }
                    else {
                        listview.expandGroup(index);
                    }
                }
            });
            indicatorGroup.addView(layout);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        if (adapter != null && adapter.getGroupCount() > 0) {
            int npos = listview.pointToPosition(0, 0);
            if (npos != AdapterView.INVALID_POSITION) {
                long pos = listview.getExpandableListPosition(npos);
                int childPos = ExpandableListView.getPackedPositionChild(pos);
                int groupPos = ExpandableListView.getPackedPositionGroup(pos);
                if (childPos == AdapterView.INVALID_POSITION) {
                    View groupView = listview.getChildAt(npos - listview.getFirstVisiblePosition());
                    indicatorGroupHeight = groupView.getHeight();
                }
                if (groupPos != indicatorGroupId) {
                    indicatorGroupId = groupPos;
                    int length = adapter.getGroupCount();
                    for (int i = 0; i < length; i++) {
                        indicatorGroup.getChildAt(i).setVisibility(View.INVISIBLE);
                    }
                    View view = indicatorGroup.getChildAt(groupPos);
                    view.setVisibility(View.VISIBLE);
                }
            }
            int showHeight = indicatorGroupHeight;
            int nEndPos = listview.pointToPosition(0, indicatorGroupHeight);
            if (nEndPos != AdapterView.INVALID_POSITION) {
                long pos = listview.getExpandableListPosition(nEndPos);
                int groupPos = ExpandableListView.getPackedPositionGroup(pos);
                if (groupPos != indicatorGroupId) {
                    View viewNext = listview.getChildAt(nEndPos - listview.getFirstVisiblePosition());
                    showHeight = viewNext.getTop();
                }
                index = groupPos;
            }
            if (!listview.isGroupExpanded(index)) {
                if (showGroup) {
                    showGroup = false;
                    indicatorGroup.setVisibility(View.INVISIBLE);
                }
            }
            else {
                if (!showGroup) {
                    showGroup = true;
                    indicatorGroup.setVisibility(View.VISIBLE);
                }
            }
            if (groupHeigh != indicatorGroupHeight - showHeight) {
                // update group position
                groupHeigh = indicatorGroupHeight - showHeight;
                indicatorGroup.layout(0, showHeight - indicatorGroupHeight, indicatorGroup.getWidth(),
                                      showHeight);
            }
        }
    }

}
