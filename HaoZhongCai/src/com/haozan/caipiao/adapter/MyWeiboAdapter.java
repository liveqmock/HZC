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
import android.widget.ImageView;
import android.widget.TextView;

import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.weibo.BasicWeibo;
import com.haozan.caipiao.activity.weibo.MyWeiboZhengwen;
import com.haozan.caipiao.activity.weibo.UserWeiboZhengwen;
import com.haozan.caipiao.types.MyWeiboData;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.util.weiboutil.TextUtil;
import com.haozan.caipiao.util.weiboutil.TimeUtil;

public class MyWeiboAdapter
    extends BaseAdapter {
    private ArrayList<MyWeiboData> myweibo;
    private Context context;
    private LayoutInflater inflater;

    public MyWeiboAdapter(Context context, ArrayList<MyWeiboData> myweibo) {
        super();
        this.context = context;
        this.myweibo = myweibo;
        inflater = LayoutInflater.from(this.context);
    }

    private static class ViewHolder {
        private TextView niceName;
        private TextView content;
        private TextView creattime;
        private TextView conmentCount;
        private TextView forwardCount;
        private TextView newsTitle;
        private TextView weiboFrom;
        private ImageView avatar;
        private ImageView locationPic;
        private ImageView unitePic;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder;
        final MyWeiboData wb = myweibo.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.myweibo_list, null);
            viewHolder = new ViewHolder();
            viewHolder.niceName = (TextView) convertView.findViewById(R.id.niceName);
            viewHolder.content = (TextView) convertView.findViewById(R.id.tvItemContent);
            viewHolder.conmentCount = (TextView) convertView.findViewById(R.id.conment_count);
            viewHolder.forwardCount = (TextView) convertView.findViewById(R.id.forward_count);
            viewHolder.creattime = (TextView) convertView.findViewById(R.id.tvItemDate);
            viewHolder.newsTitle = (TextView) convertView.findViewById(R.id.news_title);
            viewHolder.weiboFrom = (TextView) convertView.findViewById(R.id.weibo_from);
            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.ivItemPortrait);
            viewHolder.locationPic = (ImageView) convertView.findViewById(R.id.location_pic);
            viewHolder.unitePic = (ImageView) convertView.findViewById(R.id.unite_pic);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (wb.getBitmap() != null) {

            viewHolder.avatar.setImageBitmap(wb.getBitmap());
        }
        else {
            viewHolder.avatar.setImageResource(R.drawable.lucky_cat);
        }
        final String title = wb.getTitle();
        String name = wb.getName();
        if (name == null) {
            viewHolder.niceName.setText("匿名彩友");
        }
        else {
            if (wb.getName().equals("null") || wb.getName().equals("")) {
                viewHolder.niceName.setText("匿名彩友");
            }
            else {
                viewHolder.niceName.setText(wb.getName());
            }
        }
        if (title.equals("dfljy")) {
            String money = wb.getContent();
            String money1 = money.replaceFirst(" ", "+");
            viewHolder.content.setText(money1);
        }
        else {
            viewHolder.content.setText(TextUtil.formatContent(wb.getContent(), context));
        }

        // 如果包含"投注位置"则显示位置图标
        BasicWeibo.locationPicShow(wb.getContent(), viewHolder.locationPic);
        //如果包含"合买"，则显示合买图标
        BasicWeibo.unitePicShow(wb.getContent(), viewHolder.unitePic);

        viewHolder.creattime.setText(TimeUtil.getTimeStr(BasicWeibo.stringDate(wb.getTime(),
                                                                               "yyyy-MM-dd HH:mm:ss")));
        viewHolder.conmentCount.setText(String.valueOf(wb.getReplyCount()));
        viewHolder.forwardCount.setText(String.valueOf(wb.getRetweetCount()));

        String preview = wb.getPreview();
        int type = wb.getType();
        String version = wb.getSource();
        // 子内容显示
        BasicWeibo.subContentShow(context, viewHolder.newsTitle, type, title, preview, null, viewHolder.content, wb.getContent());

        // 财园判断发自哪个版本以及投注跳转、新闻跳转控制
        BasicWeibo.weiboFrom(context, type, version, title, viewHolder.weiboFrom, viewHolder.newsTitle,
                             wb.getAttachid(), preview);

        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LotteryApp appState = ((LotteryApp) context.getApplicationContext());
                if (appState.getUserid().equals(wb.getUserId()) || appState.getUserid() == wb.getUserId()) {
                    Intent intent = new Intent();
                    intent.setClass(context, MyWeiboZhengwen.class);
                    Bundle b = new Bundle();
                    b.putSerializable("weibodata", wb);
                    intent.putExtras(b);
                    context.startActivity(intent);
//                    ((Activity) context).overridePendingTransition(R.anim.push_to_right_in, R.anim.push_to_right_out);
                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                        (new AnimationModel((Activity)context)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                     R.anim.push_to_right_out);
                    }
                }
                else {
                    Intent intent = new Intent();
                    intent.setClass(context, UserWeiboZhengwen.class);
                    Bundle b = new Bundle();
                    // 把动态对象传到NewConmentsActivity
                    b.putInt("data_type", 3);
                    b.putSerializable("userWeiboData", wb);
                    b.putString("weiboId", wb.getId());
                    intent.putExtras(b);
                    context.startActivity(intent);
//                    ((Activity) context).overridePendingTransition(R.anim.push_to_right_in, R.anim.push_to_right_out);
                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                        (new AnimationModel((Activity)context)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                     R.anim.push_to_right_out);
                    }
                }
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return myweibo.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return myweibo.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

}
