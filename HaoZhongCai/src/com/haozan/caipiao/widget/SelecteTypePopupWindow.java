package com.haozan.caipiao.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.PopupWindow;

import com.haozan.caipiao.R;
import com.haozan.caipiao.adapter.PopupGridViewAdapter;
import com.haozan.caipiao.types.BetShowLotteryWay;

public class SelecteTypePopupWindow
    extends PopupWindow {
    private PopupSelectTypeClickListener popupSelectTypeListener;

    private BetShowLotteryWay[] betShowLotteryWayInf;
    
    private int column=3;

    private Context context;

    public SelecteTypePopupWindow(Context context, BetShowLotteryWay[] betShowLotteryWayInf) {
        super(context);
        this.context = context;
        this.betShowLotteryWayInf = betShowLotteryWayInf;
    }

    public void init() {
        View waySwitchLayout = View.inflate(context, R.layout.pop_grid_view, null);
        waySwitchLayout.findViewById(R.id.direction_sign_up).setVisibility(View.GONE);

        this.setContentView(waySwitchLayout);
        this.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.setOutsideTouchable(true);
        this.setFocusable(true);
        this.setHeight(LayoutParams.WRAP_CONTENT);
        this.setWidth(LayoutParams.WRAP_CONTENT);

        PopupGridViewAdapter betHisTypeSeleAdapter =
            new PopupGridViewAdapter(context, betShowLotteryWayInf[0]);
        GridView gridView = (GridView) waySwitchLayout.findViewById(R.id.menu_item_grid_view_holder);
        gridView.setNumColumns(column);
        gridView.setAdapter(betHisTypeSeleAdapter);
        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
                popupSelectTypeListener.selecteType(0, position);
            }
        });
    }

    /**
     * 若要使用此方法，一定要在init()方法之前调用
     * @param column
     */
    public void setColumn(int column) {
        this.column = column;
    }

    public void setPopupSelectTypeListener(PopupSelectTypeClickListener popupSelectTypeListener) {
        this.popupSelectTypeListener = popupSelectTypeListener;
    }

    public interface PopupSelectTypeClickListener {
        public void selecteType(int type, int index);
    }
}
