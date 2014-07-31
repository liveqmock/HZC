package com.haozan.caipiao.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.LotteryHistoryDetail;
import com.haozan.caipiao.types.Lottery;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.widget.DrawBalls;
import com.haozan.caipiao.widget.PredicateLayout;

public class LotteryHistoryAdapter
    extends BaseAdapter {
    private ArrayList<Lottery> list;
    private Context context;
    private LayoutInflater inflater;
    private String term;
    private String time;

    private OnHistoryItemListener clickListener;

    public LotteryHistoryAdapter(Context context, ArrayList<Lottery> list) {
        this.context = context;
        inflater = LayoutInflater.from(this.context);
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (list != null) {
            final Lottery lottery = list.get(position);
            if (lottery != null) {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.history_item_view, null);
                }
                TextView termView = (TextView) convertView.findViewById(R.id.term);
                TextView timeView = (TextView) convertView.findViewById(R.id.time);
                PredicateLayout ballsView = (PredicateLayout) convertView.findViewById(R.id.balls);
                ballsView.removeAllViewsInLayout();
                if (lottery.getDate() == null) {
                    term = "第 " + lottery.getTerm() + " 期";
                }
                else {
                    term = "第 " + lottery.getTerm() + " 期";
                    time = lottery.getDate();
                }
                termView.setText(term);
                timeView.setText(time);
                String balls = lottery.getBalls();
                DrawBalls drawBalls = new DrawBalls();
//                drawBalls.setBigBall(true);
                drawBalls.drawBallsLayout(context, ballsView, lottery.getId(), balls);
                ImageView arrowIcon = (ImageView) convertView.findViewById(R.id.icon);
                if (LotteryUtils.isFrequentLottery(lottery.getId())) {
                    arrowIcon.setVisibility(View.GONE);
                }
                else {
                    final int positionPre = position;
                    convertView.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (lottery.getTerm() != null)
                                if (!lottery.getTerm().equals("         ")) {
                                    Intent it = new Intent(context, LotteryHistoryDetail.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("kind", list.get(positionPre).getId());
                                    bundle.putString("term", list.get(positionPre).getTerm());
                                    bundle.putString("balls", list.get(positionPre).getBalls());
                                    bundle.putString("time", list.get(positionPre).getDate());
                                    it.putExtras(bundle);
                                    context.startActivity(it);
                                }
                        }
                    });
                }
            }
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void setItemClickListener(OnHistoryItemListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface OnHistoryItemListener {
        public void onActionClick(String term);
    }
}
