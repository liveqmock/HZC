package com.haozan.caipiao.adapter.guess;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.guess.GuessParty;
import com.haozan.caipiao.types.guess.MyGuessItem;

public class MyGuessAdapter
    extends BaseAdapter {
    private Context context;
    private ArrayList<MyGuessItem> ListItemRecord;
    private LayoutInflater inflater;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
    private int backGroundId = 0;

    public MyGuessAdapter(Context context, ArrayList<MyGuessItem> ListItemRecord) {
        this.context = context;
        this.ListItemRecord = ListItemRecord;
        this.inflater = LayoutInflater.from(this.context);
    }

    public final class ViewHolder {
        private TextView title;
        private TextView vote;
        private ImageView statusIcon;
// private TextView qusetion;
        private TextView status;
// private TextView date;
        private TextView betScore;
// private TextView earnedScore;
// private LinearLayout earnedScoreLinear;
        // add by vincent
        private TextView guessPartyTerm;
    }

    @Override
    public int getCount() {
        return ListItemRecord.size();
    }

    @Override
    public Object getItem(int arg0) {
        return ListItemRecord.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int arg0, View arg1, ViewGroup arg2) {
        ViewHolder viewHolder;
        if (arg1 == null) {
            // changed by vincent
            arg1 = inflater.inflate(R.layout.my_guess_record_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) arg1.findViewById(R.id.simple_guess_history_prg_name_title);
            viewHolder.vote = (TextView) arg1.findViewById(R.id.guess_history_vote);
// viewHolder.qusetion = (TextView) arg1.findViewById(R.id.simple_guess_history_name_question);
            viewHolder.statusIcon = (ImageView) arg1.findViewById(R.id.status_icon);
            viewHolder.status = (TextView) arg1.findViewById(R.id.satus);
// viewHolder.date = (TextView) arg1.findViewById(R.id.simple_guess_history_time);
            viewHolder.betScore = (TextView) arg1.findViewById(R.id.simple_guess_history_bet_score);
// viewHolder.earnedScore = (TextView) arg1.findViewById(R.id.simple_guess_history_earned_score);
// viewHolder.earnedScoreLinear = (LinearLayout) arg1.findViewById(R.id.earned_score_linear);
            // add by vincent
            viewHolder.guessPartyTerm = (TextView) arg1.findViewById(R.id.guess_party_term);

            arg1.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) arg1.getTag();
        }
        if (ListItemRecord != null) {
            final MyGuessItem newsListItem = ListItemRecord.get(arg0);
            // changed by vincent
            /*
             * if (newsListItem != null) { viewHolder.qusetion.setText(newsListItem.getQuestion());
             * viewHolder.status.setText("[" + getStatusText(newsListItem.getStatus()) + "]"); //
             * viewHolder.date
             * .setText(dateFormat.format(LotteryUtils.stringConvertToDate(newsListItem.getDate())));
             * viewHolder.date.setText(newsListItem.getDate());
             * viewHolder.betScore.setText(String.valueOf(newsListItem.getBetScore())); if
             * (newsListItem.getEarnedScore() == 0) { viewHolder.earnedScoreLinear.setVisibility(View.GONE); }
             * else { viewHolder.earnedScoreLinear.setVisibility(View.VISIBLE);
             * viewHolder.earnedScore.setText(String.valueOf(newsListItem.getEarnedScore())); } }
             */
            if (newsListItem != null) {
                // 期数
                viewHolder.guessPartyTerm.setText(String.valueOf(newsListItem.getTerm()) + "期");
                // 状态
                if (newsListItem.getStatus() == 0) {
                    viewHolder.statusIcon.setBackgroundResource(R.drawable.order_wait_icon);
                    viewHolder.statusIcon.setVisibility(View.VISIBLE);
                    viewHolder.status.setText("进行中");
                    viewHolder.status.setTextColor(context.getResources().getColor(R.color.dark_purple));
                }
                else if (newsListItem.getStatus() == 1) {
                    viewHolder.statusIcon.setBackgroundResource(R.drawable.award_sign_icon);
                    viewHolder.statusIcon.setVisibility(View.VISIBLE);
                    viewHolder.status.setText("等待返奖");
                    viewHolder.status.setTextColor(context.getResources().getColor(R.color.red));
                }
                else if (newsListItem.getStatus() == 2) {
                    viewHolder.statusIcon.setVisibility(View.GONE);
                    viewHolder.status.setText("未猜中");
                    viewHolder.status.setTextColor(context.getResources().getColor(R.color.light_purple));
                }
                else if (newsListItem.getStatus() == 3) {
                    viewHolder.statusIcon.setBackgroundResource(R.drawable.award_sign_icon);
                    viewHolder.statusIcon.setVisibility(View.VISIBLE);
                    viewHolder.status.setText("中得:" + newsListItem.getEarnedScore() + "分");
                    viewHolder.status.setTextColor(context.getResources().getColor(R.color.red));
                }
                else if (newsListItem.getStatus() == 4) {
                    viewHolder.statusIcon.setVisibility(View.GONE);
                    viewHolder.status.setText("题目无结果，积分已返");
                    viewHolder.status.setTextColor(context.getResources().getColor(R.color.light_purple));
                }
                // 题目
                viewHolder.title.setText(newsListItem.getSchemaName());
                viewHolder.vote.setText("选择：" + newsListItem.getVote_answer());
                // 押分
                viewHolder.betScore.setText("投入:" + String.valueOf(newsListItem.getBetScore()) + "分");
            }
        }
// if (arg0 == 0)
// backGroundId = R.drawable.history_item_first;
// else
// backGroundId = R.drawable.history_item;
// arg1.setBackgroundResource(backGroundId);
        arg1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("schema_name", ListItemRecord.get(arg0).getSchemaName());
                bundle.putString("schema_id", ListItemRecord.get(arg0).getSchemaId());
                bundle.putInt("earned_score", ListItemRecord.get(arg0).getEarnedScore());
                bundle.putInt("listDataType", 1);
                intent.putExtras(bundle);
                intent.setClass(context, GuessParty.class);
                context.startActivity(intent);
            }
        });
        return arg1;
    }

}
