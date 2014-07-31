package com.haozan.caipiao.widget;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.haozan.caipiao.R;
import com.haozan.caipiao.adapter.BetHistoryTypeSelectionAdapter;

public class PopMenu
    extends PopupWindow
    implements OnClickListener {

    private static final int GRID = 1;
//    private static final int WHEEL = 2;
    private static final int LIST = 3;
    private Context context;
    private View waySwitchLayout;
    private LayoutInflater mLayoutInflater;
    private GridView menuGidView;
    private ListView menuListView;
    private BetHistoryTypeSelectionAdapter betHisTypeSeleAdapter;
    private String[] textArray;
    private PopMenuButtonClickListener popMenuButtonClickListener;
    private int kind;
    private String lotteryKind = "";
    private boolean isShowVertical = false;
    private boolean isShowAwardMoney = false;

    public PopMenu(Context context, boolean isShowVertical) {
        super(context);
        this.context = context;
        this.isShowVertical = isShowVertical;
        this.setBackgroundDrawable(new BitmapDrawable());
        this.setAnimationStyle(R.style.popup_ball);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
    }

    public void setLotteryType(String lotteryKind) {
        this.lotteryKind = lotteryKind;
    }

    public void setLayout(int layout, String[] textArray, String[] moneyArray, int kind, int width,
                          int lastClickIndex, boolean isMoney, boolean isRecordClickStatus) {
        this.textArray = textArray;
        this.kind = kind;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        waySwitchLayout = mLayoutInflater.inflate(layout, null);
        this.setContentView(waySwitchLayout);
        this.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.setOutsideTouchable(true);
        this.setFocusable(true);
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 如果显示方式为垂直需把显示宽度调窄
        if (isShowVertical)
            this.setWidth(width / 2 + 25);
        else
            this.setWidth(width - 10);
        isShowAwardMoney = isMoney;
        setupViews(moneyArray, lastClickIndex, isMoney, isRecordClickStatus);
    }

    private void setupViews(String[] moneyArray, int lastClickIndex, boolean isMoney,
                            boolean isRecordClickStatus) {
        if (kind == GRID) {
            betHisTypeSeleAdapter =
                new BetHistoryTypeSelectionAdapter(context, textArray, lastClickIndex,
                                                   R.layout.bet_method_select_item);
            betHisTypeSeleAdapter.initExtras(moneyArray, isMoney, isShowVertical, isRecordClickStatus);
            betHisTypeSeleAdapter.setLotteryType(lotteryKind);
            menuGidView = (GridView) waySwitchLayout.findViewById(R.id.menu_item_grid_view_holder);
//            if (lotteryKind.equals("ssq") || lotteryKind.equals("dlt")) {
//                menuGidView.setHorizontalSpacing(0);
//                menuGidView.setVerticalSpacing(0);
//            }
            // 判定是否垂直显示
            if (isShowVertical == false)
                menuGidView.setNumColumns(3);
            else {
                menuGidView.setNumColumns(1);
            }
            menuGidView.setAdapter(betHisTypeSeleAdapter);
            menuGidView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                    if (!textArray[arg2].equals("")) {
                        popMenuButtonClickListener.setPopMenuButtonClickListener(null, null, null, arg2,
                                                                                 textArray[arg2]);
//                        recordClickStatus(arg0, arg2);
                    }
                }
            });
        }
        else if (kind == LIST) {
            betHisTypeSeleAdapter =
                new BetHistoryTypeSelectionAdapter(context, textArray, lastClickIndex,
                                                   R.layout.bet_method_select_list_item);
            betHisTypeSeleAdapter.initExtras(moneyArray, isMoney, isShowVertical, isRecordClickStatus);
            betHisTypeSeleAdapter.setLotteryType(lotteryKind);
            menuListView = (ListView) waySwitchLayout.findViewById(R.id.menu_item_grid_view_holder);
            menuListView.setAdapter(betHisTypeSeleAdapter);
            menuListView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                    if (!textArray[arg2].equals("")) {
                        popMenuButtonClickListener.setPopMenuButtonClickListener(null, null, null, arg2,
                                                                                 textArray[arg2]);
                    }
                }
            });
        }
    }

    public void setButtonClickListener(PopMenuButtonClickListener popMenuButtonClickListener) {
        this.popMenuButtonClickListener = popMenuButtonClickListener;
    }

    public interface PopMenuButtonClickListener {
        public void setPopMenuButtonClickListener(String startDate, String endDate, String dateDuration,
                                                  int index, String tabName);
    }

    // record click status
    private void recordClickStatus(AdapterView<?> arg0, int arg2) {

    }

    @Override
    public void onClick(View v) {

    }
}
