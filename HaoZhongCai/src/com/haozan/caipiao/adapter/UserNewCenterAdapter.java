package com.haozan.caipiao.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.bethistory.GiveLotteryDetail;
import com.haozan.caipiao.activity.bethistory.NormalOrderDetail;
import com.haozan.caipiao.activity.bethistory.PursuitHistoryDetail;
import com.haozan.caipiao.activity.unite.UniteHallDetail;
import com.haozan.caipiao.types.userinf.UserCenterBetHistoryItem;

public class UserNewCenterAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<UserCenterBetHistoryItem> userNewCenterListData;

    public UserNewCenterAdapter(Context context, ArrayList<UserCenterBetHistoryItem> userNewCenterListData) {
        this.context = context;
        this.userNewCenterListData = userNewCenterListData;
    }

    public final class ViewHolder {
        private TextView userDetailInfMonth;
        private TextView userDetailInfDay;
        private TextView userDetailInfDayOfweek;
        private TextView name;
        private TextView status;
        private TextView cost;
        private TextView orderType;
        private LinearLayout leftLayout;
        private ImageView awardIcon;
        private RelativeLayout userDetailInfContainer;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public int getCount() {
        return userNewCenterListData.size();
    }

    @Override
    public Object getItem(int index) {
        return userNewCenterListData.get(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int index, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(context, R.layout.user_new_center_list_item, null);
            viewHolder.userDetailInfContainer =
                (RelativeLayout) convertView.findViewById(R.id.user_new_center_children_container);
            viewHolder.userDetailInfMonth = (TextView) convertView.findViewById(R.id.user_new_center_month);
            viewHolder.userDetailInfDay = (TextView) convertView.findViewById(R.id.use_new_center_day);
            viewHolder.userDetailInfDayOfweek =
                (TextView) convertView.findViewById(R.id.user_new_center_day_of_week);
            viewHolder.name = (TextView) convertView.findViewById(R.id.user_new_center_lottery_name);
            viewHolder.status =
                (TextView) convertView.findViewById(R.id.user_new_center_lottery_order_status);
            viewHolder.cost = (TextView) convertView.findViewById(R.id.user_new_center_lottery_order_cost);
            viewHolder.orderType =
                (TextView) convertView.findViewById(R.id.user_new_center_lottery_order_type);
            viewHolder.leftLayout = (LinearLayout) convertView.findViewById(R.id.list_left_layout);
            viewHolder.awardIcon = (ImageView) convertView.findViewById(R.id.award_icon);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        UserCenterBetHistoryItem item = userNewCenterListData.get(index);
        if (item.isShowDate()) {
            viewHolder.userDetailInfMonth.setText(item.getMonth());
            viewHolder.userDetailInfDay.setText(item.getDay());
            viewHolder.userDetailInfDayOfweek.setText(item.getDayOfWeek());
        }
        else {
            viewHolder.userDetailInfMonth.setText("");
            viewHolder.userDetailInfDay.setText("");
            viewHolder.userDetailInfDayOfweek.setText("");
        }

        if (item.isShowFinished()) {
            viewHolder.leftLayout.setBackgroundResource(R.drawable.list_left_bg_with_line);
        }
        else {
            viewHolder.leftLayout.setBackgroundResource(R.drawable.list_left_bg);
        }

        viewHolder.name.setText(item.getName());
        viewHolder.cost.setText("￥" + item.getMoney());
        viewHolder.status.setTextColor(item.getStatusColor());
        viewHolder.status.setText(item.getStatus());
        viewHolder.orderType.setText(item.getTypeDescription());
        if (item.isAward()) {
            viewHolder.awardIcon.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.awardIcon.setVisibility(View.GONE);
        }

        viewHolder.userDetailInfContainer.setOnClickListener(new ListChildItemClickListener(index));
        return convertView;
    }

    class ListChildItemClickListener
        implements OnClickListener {
        private int index;

        public ListChildItemClickListener(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.user_new_center_children_container) {
                UserCenterBetHistoryItem item = userNewCenterListData.get(index);

                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("lottery_id", item.getId());
                bundle.putString("term", item.getTerm());
                bundle.putString("order_id", item.getOrderId());
                bundle.putString("program_id", item.getOrderId());
                bundle.putString("pursuit_id", item.getOrderId());
                bundle.putDouble("bet_money", item.getMoney());
                bundle.putBoolean("is_win", item.isAward());
                bundle.putDouble("win_money", item.getWinMoney());
                bundle.putString("order_status", item.getStatus());
                bundle.putBoolean("is_unite_bet_historoy", true);
                intent.putExtras(bundle);
                if (item.getType() == 1 || item.getType() == 6)
                    intent.setClass(context, NormalOrderDetail.class);
                else if (item.getType() == 2) {
                    intent.setClass(context, PursuitHistoryDetail.class);
                }
                else if (item.getType() == 3 || item.getType() == 4) {
                    intent.setClass(context, UniteHallDetail.class);
                }
                else if (item.getType() == 5) {
                    intent.setClass(context, GiveLotteryDetail.class);
                }
                context.startActivity(intent);
            }
        }
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

}
