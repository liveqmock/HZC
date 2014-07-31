package com.haozan.caipiao.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.PursuitAwarddRecord;
import com.haozan.caipiao.util.LotteryUtils;

public class PursuitAwardAdapter
    extends BaseAdapter {

    private ArrayList<PursuitAwarddRecord> pursuitAwarddRecord;
    private Context context;
    private LayoutInflater inflater;

    public PursuitAwardAdapter(Context context, ArrayList<PursuitAwarddRecord> pursuitAwarddRecord) {
        this.pursuitAwarddRecord = pursuitAwarddRecord;
        this.context = context;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return pursuitAwarddRecord.size();
    }

    @Override
    public Object getItem(int arg0) {
        return pursuitAwarddRecord.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {

        final PursuitAwarddRecord lottery = pursuitAwarddRecord.get(arg0);
        if (lottery != null) {
            if (arg1 == null) {
                arg1 = inflater.inflate(R.layout.pusruit_award_item, null);
            }
            TextView textView_name = (TextView) arg1.findViewById(R.id.name_lottery);
            TextView textView_term = (TextView) arg1.findViewById(R.id.term_lottery);
            TextView textView_money = (TextView) arg1.findViewById(R.id.money_lottery);
            TextView textView_prize = (TextView) arg1.findViewById(R.id.prize_lottery);
            TextView textView_time = (TextView) arg1.findViewById(R.id.time_lottery);

            textView_name.setText(LotteryUtils.LOTTERY_NAMES[LotteryUtils.getTypeIndex(lottery.getLottId())]);
            textView_term.setText(lottery.getTerm());
            textView_money.setText(String.valueOf(lottery.getMoney()));
            textView_prize.setText(lottery.getPrize());
            textView_time.setText(lottery.getTime());
            // if (lottery.getDate() == null) {
            // term = "第 " + lottery.getTerm() + " 期";
            // } else {
            // term = "第 " + lottery.getTerm() + " 期" + "\n时间:"
            // + lottery.getDate();
            // }
            // termView.setText(term);
            // String balls = lottery.getBalls();
            // LotteryUtils.drawBalls(context, ballsView, balls);
            // convertView.setOnClickListener(new View.OnClickListener() {
            //
            // @Override
            // public void onClick(View v) {
            // if (lottery.getTerm() != null)
            // if (!lottery.getTerm().equals("         "))
            // clickListener.onActionClick(lottery.getTerm());
            // }
            //
            // });

        }
        return arg1;
    }
}
