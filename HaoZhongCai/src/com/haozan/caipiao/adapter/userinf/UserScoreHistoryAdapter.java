package com.haozan.caipiao.adapter.userinf;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.userinf.UserScoreDetailData;

public class UserScoreHistoryAdapter
    extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<UserScoreDetailData> scoreInfList;

    public UserScoreHistoryAdapter(Context context, ArrayList<UserScoreDetailData> scoreInfList) {
        this.context = context;
        this.scoreInfList = scoreInfList;
        inflater = LayoutInflater.from(this.context);
    }

    public final class ViewHolder {
        private TextView scoreMonth;
        private TextView scoreDay;
        private LinearLayout leftLayout;
        private TextView scoreInf;
        private TextView scoreVariation;
    }

    @Override
    public int getCount() {
        return scoreInfList.size();
    }

    @Override
    public Object getItem(int index) {
        return scoreInfList.get(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.user_score_history_adapter_item, null);
            viewHolder.scoreMonth = (TextView) convertView.findViewById(R.id.score_month);
            viewHolder.scoreDay = (TextView) convertView.findViewById(R.id.score_day);
            viewHolder.leftLayout = (LinearLayout) convertView.findViewById(R.id.list_left_layout);
            viewHolder.scoreInf = (TextView) convertView.findViewById(R.id.score_inf);
            viewHolder.scoreVariation = (TextView) convertView.findViewById(R.id.score_variation);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        UserScoreDetailData scoreData = scoreInfList.get(position);
        if (scoreData.isShowLine()) {
            viewHolder.leftLayout.setBackgroundResource(R.drawable.list_left_bg_with_line);
        }
        else {
            viewHolder.leftLayout.setBackgroundResource(R.drawable.list_left_bg);
        }
        if (scoreData.isShowDate()) {
            viewHolder.scoreMonth.setText(scoreData.getMonth() + "月");
            viewHolder.scoreDay.setText(scoreData.getDay());
        }
        else {
            viewHolder.scoreMonth.setText("");
            viewHolder.scoreDay.setText("");
        }
        viewHolder.scoreInf.setText(scoreData.getInf());
        String v = scoreData.getVariation();
        if (Integer.valueOf(v) < 0) {
            viewHolder.scoreVariation.setTextColor(context.getResources().getColor(R.color.bottle_green));
            viewHolder.scoreVariation.setText(v);
        }
        else {
            viewHolder.scoreVariation.setTextColor(context.getResources().getColor(R.color.red_text));
            viewHolder.scoreVariation.setText("+" + v);
        }
        return convertView;
    }

}
