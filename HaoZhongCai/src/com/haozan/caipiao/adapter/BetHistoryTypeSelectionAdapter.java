package com.haozan.caipiao.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;

public class BetHistoryTypeSelectionAdapter
    extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private String[] buttonName;
    private String[] buttonAwardMoney;
    private int pos;
    private int layout;
    private boolean isWithAwardMoney = false;
    private boolean isShowVertical = false;
    private boolean isRecordClickStatus = false;
    private String lotteryType = "";

    public final class ViewHolder {
        private TextView button;
        private TextView awardMoney;
        private ImageView listItemIcon;
        private LinearLayout gridItem;
    }

    public BetHistoryTypeSelectionAdapter(Context context, String[] buttonName, int pos, int layout) {
        this.context = context;
        inflater = LayoutInflater.from(this.context);
        this.buttonName = buttonName;
        this.pos = pos;
        this.layout = layout;
    }

    public void setLotteryType(String lotteryType) {
        this.lotteryType = lotteryType;
    }

    public void initExtras(String[] buttonAwardMoney, boolean isWithAwardMoney, boolean isShowVertical,
                           boolean isRecordClickStatus) {
        this.buttonAwardMoney = buttonAwardMoney;
        this.isWithAwardMoney = isWithAwardMoney;
        this.isShowVertical = isShowVertical;
        this.isRecordClickStatus = isRecordClickStatus;
    }

    @Override
    public int getCount() {
        return buttonName.length;
    }

    @Override
    public Object getItem(int position) {
        return buttonName[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(layout, null);
            viewHolder.gridItem = (LinearLayout) convertView.findViewById(R.id.grid_item_layout);
            viewHolder.button = (TextView) convertView.findViewById(R.id.grid_view_item_click);
            viewHolder.awardMoney = (TextView) convertView.findViewById(R.id.grid_item_money);
            if (layout == R.layout.bet_method_select_list_item)
                viewHolder.listItemIcon =
                    (ImageView) convertView.findViewById(R.id.user_new_center_pop_list_icon);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (isShowVertical || buttonAwardMoney == null || isWithAwardMoney == false)
            viewHolder.awardMoney.setVisibility(View.GONE);

        if (isWithAwardMoney && isRecordClickStatus) {
            if (position == pos) {
                if (buttonName.length == 3) {
                    if (lotteryType.equals("ssq") || lotteryType.equals("dlt"))
                        viewHolder.gridItem.setBackgroundResource(R.drawable.bet_popup_item_choosed);
                    else
                        viewHolder.gridItem.setBackgroundResource(R.drawable.bet_popup_item_choosed);
                    viewHolder.awardMoney.setTextColor(context.getResources().getColor(R.color.dark_purple));
                }
                else if (buttonName.length == 2) {
                    if (lotteryType.equals("ssq") || lotteryType.equals("dlt"))
                        viewHolder.gridItem.setBackgroundResource(R.drawable.bet_popup_item_choosed);
                    else
                        viewHolder.gridItem.setBackgroundResource(R.drawable.bet_popup_item_choosed);
                    viewHolder.awardMoney.setTextColor(context.getResources().getColor(R.color.dark_purple));
                }
                else {
                    if (lotteryType.equals("ssq") || lotteryType.equals("dlt"))
                        viewHolder.gridItem.setBackgroundResource(R.drawable.bet_popup_item_choosed);
                    else
                        viewHolder.gridItem.setBackgroundResource(R.drawable.bet_popup_item_choosed);
                    viewHolder.awardMoney.setTextColor(context.getResources().getColor(R.color.white));
                }
                viewHolder.button.setTextColor(Color.WHITE);
            }
        }
        else if (isRecordClickStatus) {
            if (position == pos) {
                viewHolder.gridItem.setBackgroundResource(R.drawable.bet_popup_item_choosed);
                viewHolder.button.setTextColor(Color.WHITE);
            }
            else {
                viewHolder.gridItem.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
                viewHolder.button.setTextColor(context.getResources().getColor(R.color.light_purple));
            }
        }

        viewHolder.button.setText(buttonName[position]);
        if (isWithAwardMoney)
            viewHolder.awardMoney.setText(buttonAwardMoney[position]);

        if (!isWithAwardMoney && !isRecordClickStatus && lotteryType.equals("unc")) {
            viewHolder.listItemIcon.setImageResource(R.drawable.icon_user_center_message);
            String[] buttonNameArray = buttonName[position].split("\\|");
            // 新个人中心下拉菜单增加消息数，财园回复数，中奖短信通知功能状态
            if (buttonName[position].indexOf("|") != -1) {
                viewHolder.awardMoney.setVisibility(View.VISIBLE);
                if (!buttonNameArray[1].equals("0"))
                    viewHolder.awardMoney.setText(buttonNameArray[1]);
                else
                    viewHolder.awardMoney.setText("");
                viewHolder.button.setText(buttonNameArray[0]);
            }
            else
                viewHolder.button.setText(buttonName[position]);
        }
        return convertView;
    }
}
