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
import com.haozan.caipiao.adapter.PopupGridViewK3Adapter;
import com.haozan.caipiao.types.BetShowLotteryWay;

public class SelecteTypeK3PopupWindow
    extends PopupWindow {
    private PopupSelectTypeClickListener popupSelectTypeListener;

    private BetShowLotteryWay[] betShowLotteryWayInf;

    private Context context;

    public SelecteTypeK3PopupWindow(Context context, BetShowLotteryWay[] betShowLotteryWayInf) {
        super(context);
        this.context = context;
        this.betShowLotteryWayInf = betShowLotteryWayInf;
    }

    public void init() {
        View waySwitchLayout = View.inflate(context, R.layout.pop_grid_view, null);
        waySwitchLayout.findViewById(R.id.direction_sign_up).setVisibility(View.GONE);
        waySwitchLayout.findViewById(R.id.term_popup_layout).setBackgroundResource(R.drawable.bg_k3_show_lottery_way);

        this.setContentView(waySwitchLayout);
        this.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.setOutsideTouchable(true);
        this.setFocusable(true);
        this.setHeight(LayoutParams.WRAP_CONTENT);
        this.setWidth(LayoutParams.WRAP_CONTENT);

        PopupGridViewK3Adapter betHisTypeSeleAdapter =
            new PopupGridViewK3Adapter(context, betShowLotteryWayInf[0]);
        GridView gridView = (GridView) waySwitchLayout.findViewById(R.id.menu_item_grid_view_holder);
        gridView.setNumColumns(3);
        gridView.setAdapter(betHisTypeSeleAdapter);
        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
                popupSelectTypeListener.selecteType(0, position);
            }
        });
    }

    public void setPopupSelectTypeListener(PopupSelectTypeClickListener popupSelectTypeListener) {
        this.popupSelectTypeListener = popupSelectTypeListener;
    }

    public interface PopupSelectTypeClickListener {
        public void selecteType(int type, int index);
    }
}
