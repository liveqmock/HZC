package com.haozan.caipiao.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.BetShowLotteryWay;
import com.haozan.caipiao.util.TextUtil;

public class PopupGridViewAdapter
    extends BaseAdapter {
    private BetShowLotteryWay betWayInf;

    private int selectedTextColor;
    private int notSelectedTextColor;

    private Context context;
    private LayoutInflater inflater;

    public PopupGridViewAdapter(Context context, BetShowLotteryWay betWayInf) {
        this.context = context;
        this.betWayInf = betWayInf;

        inflater = LayoutInflater.from(this.context);
        selectedTextColor = context.getResources().getColor(R.color.white);
        notSelectedTextColor = context.getResources().getColor(R.color.dark_purple);
    }

    @Override
    public int getCount() {
        return betWayInf.getUpsInf().length;
    }

    @Override
    public Object getItem(int position) {
        return betWayInf.getUpsInf()[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public final class ViewHolder {
        private TextView upTv;
        private TextView downTv;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.jlks_grid_view_item, null);
            viewHolder.upTv = (TextView) convertView.findViewById(R.id.up_tv);
            viewHolder.downTv = (TextView) convertView.findViewById(R.id.down_tv);

            if (betWayInf.getDownsInf() == null) {
                viewHolder.downTv.setVisibility(View.GONE);
            }
            else {
                TextUtil.setTextBold(viewHolder.downTv);
            }

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (position == betWayInf.getSelectedIndex()) {
            convertView.setBackgroundResource(R.drawable.bet_popup_item_choosed);
            viewHolder.upTv.setTextColor(selectedTextColor);
            viewHolder.downTv.setTextColor(selectedTextColor);
        }
        else {
            convertView.setBackgroundResource(R.drawable.bet_popup_item_not_choose_user_new_center);
            viewHolder.upTv.setTextColor(notSelectedTextColor);
            viewHolder.downTv.setTextColor(notSelectedTextColor);
        }
        viewHolder.upTv.setText(betWayInf.getUpsInf()[position]);
        if (betWayInf.getDownsInf() != null && betWayInf.getDownsInf().length > position &&
            betWayInf.getDownsInf()[position] != null) {
            viewHolder.downTv.setText(betWayInf.getDownsInf()[position]);
        }
        return convertView;
    }
}
