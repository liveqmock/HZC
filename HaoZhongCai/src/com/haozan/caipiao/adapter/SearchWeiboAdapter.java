package com.haozan.caipiao.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.weibo.BasicWeibo;
import com.haozan.caipiao.activity.weibo.MyProfileActivity;
import com.haozan.caipiao.activity.weibo.UserProfileActivity;
import com.haozan.caipiao.activity.weibo.WeiboZhengWen;
import com.haozan.caipiao.types.SearchWeiboDate;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.util.weiboutil.TextUtil;
import com.haozan.caipiao.util.weiboutil.TimeUtil;

public class SearchWeiboAdapter
    extends BaseAdapter {
    private ArrayList<SearchWeiboDate> sweiboDate;
    private Context context;
    private EditText searchET;
    private LayoutInflater inflater;

    public SearchWeiboAdapter(Context context, ArrayList<SearchWeiboDate> sweiboDate, EditText searchET) {
        super();
        this.context = context;
        this.sweiboDate = sweiboDate;
        this.searchET = searchET;
        inflater = LayoutInflater.from(this.context);
    }

    private static class ViewHolder {
        private TextView niceName;
        private TextView content;
        private TextView creattime;
        private TextView conmentCount;
        private TextView forwardCount;
        private LinearLayout ly;
        private TextView newsTitle;
        private TextView weiboFrom;
        private ImageView avatar;
        private ImageView locationPic;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder;
        final SearchWeiboDate Data = sweiboDate.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.weibohall_item, null);
            viewHolder = new ViewHolder();
            viewHolder.niceName = (TextView) convertView.findViewById(R.id.niceName);
            viewHolder.content = (TextView) convertView.findViewById(R.id.tvItemContent);
            viewHolder.conmentCount = (TextView) convertView.findViewById(R.id.conment_count);
            viewHolder.forwardCount = (TextView) convertView.findViewById(R.id.forward_count);
            viewHolder.creattime = (TextView) convertView.findViewById(R.id.tvItemDate);
            viewHolder.ly = (LinearLayout) convertView.findViewById(R.id.lyRightLayout);
            viewHolder.newsTitle = (TextView) convertView.findViewById(R.id.news_title);
            viewHolder.weiboFrom = (TextView) convertView.findViewById(R.id.weibo_from);
            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.ivItemPortrait);
            viewHolder.locationPic = (ImageView) convertView.findViewById(R.id.location_pic);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (Data.getBitmap() != null) {
            viewHolder.avatar.setImageBitmap(Data.getBitmap());
        }
        else {
            viewHolder.avatar.setImageResource(R.drawable.lucky_cat);
        }
        final String title = Data.getTitle();
        String name = Data.getName();
        if (name == null) {
            viewHolder.niceName.setText("匿名彩友");
        }
        else {
            if (Data.getName().equals("null") || Data.getName().equals("")) {
                viewHolder.niceName.setText("匿名彩友");
            }
            else {
                LotteryApp appState = ((LotteryApp) context.getApplicationContext());
                if (appState.getUserid().equals(Data.getUserid()) || appState.getUserid() == Data.getUserid()) {
                    viewHolder.niceName.setText("我");
                }
                else {
                    viewHolder.niceName.setText(Data.getName());
                }
            }
        }

        // 第一个空格替换为“+”号
        if (title.equals("dfljy")) {
            String money = Data.getContent();
            String money1 = money.replaceFirst(" ", "+");
            viewHolder.content.setText(money1);
        }
        else {
            viewHolder.content.setText(TextUtil.formatContent(Data.getContent(), context));
        }

        // 如果包含"投注位置"则显示位置图标
        BasicWeibo.locationPicShow(Data.getContent(), viewHolder.locationPic);

        viewHolder.creattime.setText(TimeUtil.getTimeStr(BasicWeibo.stringDate(Data.getTime(),
                                                                               "yyyy-MM-dd HH:mm:ss")));
        viewHolder.conmentCount.setText(String.valueOf(Data.getReplyCount()));
        viewHolder.forwardCount.setText(String.valueOf(Data.getRetweetCount()));

        String preview = Data.getPreview();
        int type = Data.getType();
        String version = Data.getSource();

        // 子内容显示
        BasicWeibo.subContentShow(context, viewHolder.newsTitle, type, title, preview, viewHolder.ly, viewHolder.content, Data.getContent());

        // 财园判断发自哪个版本以及投注跳转、新闻跳转控制
        BasicWeibo.weiboFrom(context, type, version, title, viewHolder.weiboFrom, viewHolder.newsTitle,
                             Data.getAttachid(), preview);

        viewHolder.ly.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClass(context, WeiboZhengWen.class);
                Bundle b = new Bundle();
                b.putString("weiboId", Data.getWeiboid());
                intent.putExtras(b);
                context.startActivity(intent);
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                    (new AnimationModel((Activity)context)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                     R.anim.push_to_left_out);
                }
            }
        });

        viewHolder.avatar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LotteryApp appState = ((LotteryApp) context.getApplicationContext());
                if (appState.getUserid().equals(Data.getUserid()) || appState.getUserid() == Data.getUserid()) {
                    Intent intent = new Intent();
                    intent.setClass(context, MyProfileActivity.class);
                    context.startActivity(intent);
                }
                else {
                    Intent intent1 = new Intent();
                    intent1.setClass(context, UserProfileActivity.class);
                    Bundle b = new Bundle();
                    b.putInt("data_type", 1);
                    b.putInt("userId", Integer.valueOf(Data.getUserid()));
                    intent1.putExtras(b);
                    context.startActivity(intent1);
                }
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                    (new AnimationModel((Activity)context)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                     R.anim.push_to_left_out);
                }
            }
        });

        BasicWeibo.hightKeyWords(context, viewHolder.content, Data.getContent(),
                                 searchET.getText().toString());
        return convertView;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return sweiboDate.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return sweiboDate.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

}
