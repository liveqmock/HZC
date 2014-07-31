package com.haozan.caipiao.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;

public class SelectLuckyNumPup
    extends PopupWindow {
// private TextView firstIcon;
// private TextView secondIcon;
// private TextView thirdIcon;
// private TextView forthIcon;
// private TextView fifthIcon;
// private TextView sixthIcon;
// private RelativeLayout firstLayout;
// private RelativeLayout secondLayout;
// private RelativeLayout threeLayout;
// private RelativeLayout forthLayout;
// private RelativeLayout fifthLayout;
// private RelativeLayout sixthLayout;
    private RelativeLayout[] relas;
    private TextView[] tvs;
    private int[] relaIds = {R.id.item_first_layout, R.id.item_second_layout, R.id.item_third_layout,
            R.id.item_forth_layout, R.id.item_fifth_layout, R.id.item_sixth_layout};
    private int[] tvIds = {R.id.tv_item_first, R.id.tv_item_second, R.id.tv_item_third, R.id.tv_item_forth,
            R.id.tv_item_fifth, R.id.tv_item_sixth};
    private View waySwitchLayout;
    private ButtonClickListener buttonClickListener;
// private Context context;
    private Context context;
    private int searchLuckyType = 1;
    private Window win;

    public SelectLuckyNumPup(Context context) {
        super(context);
        this.context = context;
        LayoutInflater mLayoutInflater =
            (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        waySwitchLayout = mLayoutInflater.inflate(R.layout.lucky_num_selected_dialog, null);
        this.setContentView(waySwitchLayout);
        this.setHeight(LayoutParams.WRAP_CONTENT);
        this.setWidth(LayoutParams.WRAP_CONTENT);
        setupViews();
        init(searchLuckyType);
    }

    private void setupViews() {
        initDefault();
// firstLayout.setOnClickListener(new Button.OnClickListener() {
//
// @Override
// public void onClick(View v) {
// searchLuckyType = 1;
// buttonClickListener.setButtonClickListener(R.id.item_first_layout);
// }
// });
// secondLayout = (RelativeLayout) waySwitchLayout.findViewById(R.id.item_second_layout);
// secondLayout.setOnClickListener(new Button.OnClickListener() {
//
// @Override
// public void onClick(View v) {
// searchLuckyType = 2;
// buttonClickListener.setButtonClickListener(R.id.item_second_layout);
// }
// });
// threeLayout = (RelativeLayout) waySwitchLayout.findViewById(R.id.item_third_layout);
// threeLayout.setOnClickListener(new Button.OnClickListener() {
// public void onClick(View v) {
// searchLuckyType = 3;
// buttonClickListener.setButtonClickListener(R.id.item_third_layout);
// }
// });
// forthLayout = (RelativeLayout) waySwitchLayout.findViewById(R.id.item_forth_layout);
// forthLayout.setOnClickListener(new Button.OnClickListener() {
// public void onClick(View v) {
// searchLuckyType = 4;
// buttonClickListener.setButtonClickListener(R.id.item_forth_layout);
// }
// });
// fifthLayout = (RelativeLayout) waySwitchLayout.findViewById(R.id.item_fifth_layout);
// fifthLayout.setOnClickListener(new Button.OnClickListener() {
// public void onClick(View v) {
// searchLuckyType = 5;
// buttonClickListener.setButtonClickListener(R.id.item_fifth_layout);
// }
// });
// sixthLayout = (RelativeLayout) waySwitchLayout.findViewById(R.id.item_sixth_layout);
// sixthLayout.setOnClickListener(new Button.OnClickListener() {
// public void onClick(View v) {
// searchLuckyType = 6;
// buttonClickListener.setButtonClickListener(R.id.item_sixth_layout);
// }
// });
    }

    public void setButtonClickListener(ButtonClickListener buttonClickListener) {
        this.buttonClickListener = buttonClickListener;
    }

    public interface ButtonClickListener {
        public void setButtonClickListener(int buttonId);
    }

    private void initDefault() {
// firstIcon = (TextView) waySwitchLayout.findViewById(R.id.radio_item_first);
// secondIcon = (TextView) waySwitchLayout.findViewById(R.id.radio_item_second);
// thirdIcon = (TextView) waySwitchLayout.findViewById(R.id.radio_item_third);
// forthIcon = (TextView) waySwitchLayout.findViewById(R.id.radio_item_forth);
// fifthIcon = (TextView) waySwitchLayout.findViewById(R.id.radio_item_fifth);
// sixthIcon = (TextView) waySwitchLayout.findViewById(R.id.radio_item_sixth);
// firstLayout = (RelativeLayout) waySwitchLayout.findViewById(R.id.item_first_layout);
        relas = new RelativeLayout[6];
        tvs = new TextView[6];
        for (int i = 0; i < relas.length; i++) {
            final int j = i;
            relas[i] = (RelativeLayout) waySwitchLayout.findViewById(relaIds[i]);
            tvs[i] = (TextView) waySwitchLayout.findViewById(tvIds[i]);
            relas[i].setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    searchLuckyType = j + 1;
                    buttonClickListener.setButtonClickListener(relaIds[j]);
                }
            });
        }

    }

    public void iniSelectedItemBg() {
// firstIcon.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
// secondIcon.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
// thirdIcon.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
// forthIcon.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
// fifthIcon.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
// sixthIcon.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
        for (int i = 0; i < relas.length; i++) {
            relas[i].setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            tvs[i].setTextColor(context.getResources().getColor(R.color.dark_purple));
        }
    }

    public void init(int LuckyType) {
        for (int i = 0; i < relas.length; i++) {
            if (LuckyType == i + 1) {
                relas[i].setBackgroundResource(R.drawable.bet_popup_item_choosed);
                tvs[i].setTextColor(context.getResources().getColor(R.color.white));
            }
        }
// if (LuckyType == 1)
// firstIcon.setBackgroundResource(R.drawable.bet_popup_item_choosed);
// else if (LuckyType == 2)
// secondIcon.setBackgroundResource(R.drawable.bet_popup_item_choosed);
// else if (LuckyType == 3)
// thirdIcon.setBackgroundResource(R.drawable.bet_popup_item_choosed);
// else if (LuckyType == 4)
// forthIcon.setBackgroundResource(R.drawable.bet_popup_item_choosed);
// else if (LuckyType == 5)
// fifthIcon.setBackgroundResource(R.drawable.bet_popup_item_choosed);
// else if (LuckyType == 6)
// sixthIcon.setBackgroundResource(R.drawable.bet_popup_item_choosed);
    }
}
