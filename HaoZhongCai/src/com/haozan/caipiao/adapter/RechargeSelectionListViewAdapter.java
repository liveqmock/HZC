package com.haozan.caipiao.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.RechargeUiItem;

public class RechargeSelectionListViewAdapter
    extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<RechargeUiItem> arrays;

    public RechargeSelectionListViewAdapter(Context context, ArrayList<RechargeUiItem> arrays) {
        this.context = context;
        inflater = LayoutInflater.from(this.context);
        this.arrays = arrays;
    }

    public final class ViewHolder {
        private ImageView topUpIcon;
        private TextView topUpName;
        private TextView topUpNormalInf;
        private ImageView listDivider;
        private LinearLayout linearL;
    }

    @Override
    public int getCount() {
        return arrays.size();
    }

    @Override
    public Object getItem(int arg0) {
        return arrays.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.recharge_list_item, null);
            viewHolder.topUpIcon = (ImageView) view.findViewById(R.id.top_up_icon);
            viewHolder.topUpName = (TextView) view.findViewById(R.id.recharge_selection_bt);
            viewHolder.topUpNormalInf = (TextView) view.findViewById(R.id.recharge_selection_inf);
            viewHolder.listDivider = (ImageView) view.findViewById(R.id.recharge_list_divider);
            viewHolder.linearL = (LinearLayout) view.findViewById(R.id.linear_layout);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (position == 0) {
            viewHolder.linearL.setBackgroundResource(R.drawable.five_tab);
            if (arrays.size() == 1) {
                viewHolder.listDivider.setVisibility(View.INVISIBLE);
                viewHolder.linearL.setBackgroundResource(R.drawable.list_single_item);
            }
            else {
                viewHolder.listDivider.setVisibility(View.VISIBLE);
            }
        }
        else if (position == arrays.size() - 1) {
            viewHolder.linearL.setBackgroundResource(R.drawable.six_tab);
            viewHolder.listDivider.setVisibility(View.INVISIBLE);
        }
        else {
            viewHolder.linearL.setBackgroundResource(R.drawable.four_tab);
            viewHolder.listDivider.setVisibility(View.VISIBLE);
        }

        RechargeUiItem item = arrays.get(position);

// if (arg0 < (rechargeIconArray.length - 1)) {
        viewHolder.topUpIcon.setImageResource(item.getIcon());
        viewHolder.topUpName.setText(arrays.get(position).getName());
        if (item.isEmphasis()) {
            viewHolder.topUpName.setTextColor(context.getResources().getColor(R.color.black));
        }
        else {
            viewHolder.topUpName.setTextColor(context.getResources().getColor(R.color.gray));
        }
        viewHolder.topUpNormalInf.setText(arrays.get(position).getDescription());
        return view;
    }
}
