package com.haozan.caipiao.adapter.guess;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
import com.haozan.caipiao.types.guess.WholeGuessItem;
import com.haozan.caipiao.util.TimeUtils;

public class WholeGuessAdapter
    extends BaseAdapter {
    private Context context;
    private ArrayList<WholeGuessItem> ListItemRecord;
    private LayoutInflater inflater;

    public WholeGuessAdapter(Context context, ArrayList<WholeGuessItem> ListItemRecord) {
        this.context = context;
        this.ListItemRecord = ListItemRecord;
        this.inflater = LayoutInflater.from(this.context);
    }

    public final class ViewHolder {
        private TextView status;
        private ImageView statusImg;
        private TextView allScore;
        private TextView title;
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
            arg1 = inflater.inflate(R.layout.guess_list_item_view_new, null);
            viewHolder = new ViewHolder();
            viewHolder.status = (TextView) arg1.findViewById(R.id.status);
            viewHolder.statusImg = (ImageView) arg1.findViewById(R.id.img_guess_status_flag);
            viewHolder.allScore = (TextView) arg1.findViewById(R.id.allScore);
            viewHolder.title = (TextView) arg1.findViewById(R.id.guess_title);
            viewHolder.guessPartyTerm = (TextView) arg1.findViewById(R.id.guess_party_term);
            arg1.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) arg1.getTag();
        }
        if (ListItemRecord != null) {
            final WholeGuessItem newsListItem = ListItemRecord.get(arg0);
            if (newsListItem != null) {
                // changed by vincent
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if (newsListItem.getStatus() == 3) {
                    viewHolder.status.setText(getStatusText(newsListItem.getStatus()));
                }
                else {
                    try {
                        Date beginDate = format.parse(newsListItem.getDate());
                        Date endDate = format.parse(newsListItem.getEndDate());
                        Date nowDate = format.parse(newsListItem.getNowTime());
                        long millis0 = beginDate.getTime() - nowDate.getTime();
                        long millis1 = endDate.getTime() - nowDate.getTime();

                        if (millis0 < 0 && millis1 > 0 || millis0 == 0) {// 进行中
                            viewHolder.status.setText("剩:" + TimeUtils.getCountDownTime(millis1));
                            viewHolder.status.setTextColor(context.getResources().getColor(R.color.dark_purple));
                            viewHolder.statusImg.setImageResource(R.drawable.img_guess_flag_being);
                            viewHolder.allScore.setTextColor(context.getResources().getColor(R.color.red_text));
                        }
                        else if (newsListItem.getStatus() == GuessParty.NO_OPEN_LOTTERY) {// 待公布
                            viewHolder.status.setTextColor(context.getResources().getColor(R.color.dark_purple));
                            viewHolder.status.setText(getStatusText(GuessParty.NO_OPEN_LOTTERY));
                            viewHolder.statusImg.setImageResource(R.drawable.img_guess_flag_todeclare);
                            viewHolder.allScore.setTextColor(context.getResources().getColor(R.color.yellow_text));
                        }
                        else if (newsListItem.getStatus() == GuessParty.OPEN_LOTTERY) {// 已开奖
                            viewHolder.status.setTextColor(context.getResources().getColor(R.color.light_purple));
                            viewHolder.status.setText(getStatusText(GuessParty.OPEN_LOTTERY));
                            viewHolder.statusImg.setImageResource(R.drawable.img_guess_flag_underline);
                            viewHolder.allScore.setTextColor(context.getResources().getColor(R.color.light_purple));
                        }
                        else if (newsListItem.getStatus() == GuessParty.INVALID_LOTTERY) {// 已截止
                            viewHolder.status.setTextColor(context.getResources().getColor(R.color.light_purple));
                            viewHolder.status.setText(getStatusText(GuessParty.INVALID_LOTTERY));
                            viewHolder.statusImg.setImageResource(R.drawable.img_guess_flag_underline);
                            viewHolder.allScore.setTextColor(context.getResources().getColor(R.color.light_purple));
                        }
                        else {
                            viewHolder.status.setTextColor(context.getResources().getColor(R.color.dark_purple));
                            viewHolder.status.setText(getStatusText(GuessParty.NO_OPEN_LOTTERY));// 待公布
                            viewHolder.statusImg.setImageResource(R.drawable.img_guess_flag_todeclare);
                            viewHolder.allScore.setTextColor(context.getResources().getColor(R.color.yellow_text));
                        }
                    }
                    catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                viewHolder.title.setTextSize(16);
                viewHolder.title.setText(newsListItem.getSchemaName());
                viewHolder.guessPartyTerm.setText(String.valueOf(newsListItem.getTerm()) + "期");
                viewHolder.allScore.setText(newsListItem.getAllScore());
            }
        }
        arg1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                Bundle bundle = new Bundle();

                bundle.putInt("status", ListItemRecord.get(arg0).getStatus());
                bundle.putString("schema_name", ListItemRecord.get(arg0).getSchemaName());
                bundle.putString("schema_id", ListItemRecord.get(arg0).getSchemaId());
                bundle.putInt("listDataType", 0);
                intent.putExtras(bundle);
                intent.setClass(context, GuessParty.class);
                context.startActivity(intent);

            }
        });
        return arg1;
    }

    private String getStatusText(int status) {
        if (status == GuessParty.NO_OPEN_LOTTERY)
            return "等待开奖";
        else if (status == GuessParty.OPEN_LOTTERY)
            return "已开奖";
        else if (status == GuessParty.INVALID_LOTTERY)
            return "题目无结果";
        else
            return "系统出错";
    }

}
