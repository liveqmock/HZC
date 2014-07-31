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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.weibo.BasicWeibo;
import com.haozan.caipiao.activity.weibo.MyProfileActivity;
import com.haozan.caipiao.activity.weibo.UserProfileActivity;
import com.haozan.caipiao.activity.weibo.WeiboZhengWen;
import com.haozan.caipiao.types.WeiboData;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.util.weiboutil.TextUtil;
import com.haozan.caipiao.util.weiboutil.TimeUtil;

public class WeiboAdapter
    extends BaseAdapter {
    private ArrayList<WeiboData> weibo;
    private Context context;
    private LayoutInflater inflater;

    public WeiboAdapter(Context context, ArrayList<WeiboData> weibo) {
        super();
        this.context = context;
        this.weibo = weibo;
        inflater = LayoutInflater.from(this.context);
    }

    private final class ViewHolder {
        private TextView niceName;
        private TextView content;
        private TextView creattime;
        private TextView conmentCount;
        private TextView forwardCount;
        private LinearLayout weiboLayout;
        private TextView newsTitle;
        private TextView weiboFrom;
        private ImageView locationPic;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder = new ViewHolder();
        final WeiboData data = weibo.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.weibo_list, null);
            viewHolder.niceName = (TextView) convertView.findViewById(R.id.niceName);
            viewHolder.content = (TextView) convertView.findViewById(R.id.tvItemContent);
            viewHolder.creattime = (TextView) convertView.findViewById(R.id.tvItemDate);
            viewHolder.conmentCount = (TextView) convertView.findViewById(R.id.conment_count);
            viewHolder.forwardCount = (TextView) convertView.findViewById(R.id.forward_count);
            viewHolder.weiboLayout = (LinearLayout) convertView.findViewById(R.id.weibolistLayout);
            viewHolder.newsTitle = (TextView) convertView.findViewById(R.id.news_title);
            viewHolder.weiboFrom = (TextView) convertView.findViewById(R.id.weibo_from);
            viewHolder.locationPic = (ImageView) convertView.findViewById(R.id.location_pic);
            convertView.setTag(viewHolder);

        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ImageView avatar = (ImageView) convertView.findViewById(R.id.ivItemPortrait);
        if (data.getBitmap() != null) {

            avatar.setImageBitmap(data.getBitmap());
        }
        else {
            avatar.setImageResource(R.drawable.lucky_cat);
        }

        final String title = data.getTitle();
        String name = data.getName();
        if (name == null) {
            viewHolder.niceName.setText("匿名彩友");
        }
        else {
            if (data.getName().equals("null") || data.getName().equals("")) {
                viewHolder.niceName.setText("匿名彩友");
            }
            else {
                viewHolder.niceName.setText(data.getName());
            }
        }

        // String name1=data.getContent().substring(0,9);
        // StringBuilder text = new StringBuilder();
        // text.append("<font color='blue'>" + name1);
        // text.append("</font>");
        // content.setText(Html.fromHtml(text.toString()));

        if (title.equals("dfljy")) {
            String money = data.getContent();
            String money1 = money.replaceFirst(" ", "+");
            viewHolder.content.setText(money1);
        }
        else {
            viewHolder.content.setText(TextUtil.formatContent(data.getContent(), context));
        }

        // 如果包含"投注位置"则显示位置图标
        BasicWeibo.locationPicShow(data.getContent(), viewHolder.locationPic);

        viewHolder.creattime.setText(TimeUtil.getTimeStr(BasicWeibo.stringDate(data.getTime(),
                                                                               "yyyy-MM-dd HH:mm:ss")));
        // 评论
        viewHolder.conmentCount.setText(String.valueOf(data.getReplyCount()));
        // 转发
        viewHolder.forwardCount.setText(String.valueOf(data.getRetweetCount()));

        String preview = data.getPreview();
        int type = data.getType();
        String version = data.getSource();

        // 子内容显示
        BasicWeibo.subContentShow(context, viewHolder.newsTitle, type, title, preview, null, viewHolder.content, data.getContent());
        // 财园判断发自哪个版本以及投注跳转、新闻跳转控制
        BasicWeibo.weiboFrom(context, type, version, title, viewHolder.weiboFrom, viewHolder.newsTitle,
                             data.getAttachid(), preview);

        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClass(context, WeiboZhengWen.class);
                Bundle b = new Bundle();
                b.putString("weiboId", String.valueOf(data.getId()));
                intent.putExtras(b);
                context.startActivity(intent);
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                    (new AnimationModel((Activity)context)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                 R.anim.push_to_right_out);
                }
                Jump();
            }
        });

//        viewHolder.weiboLayout.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                Intent intent = new Intent();
//                intent.setClass(context, WeiboZhengWen.class);
//                Bundle b = new Bundle();
//                b.putString("weiboId", String.valueOf(data.getId()));
//                intent.putExtras(b);
//                context.startActivity(intent);
//                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
//                    (new AnimationModel((Activity)context)).overridePendingTransition(R.anim.push_to_right_in,
//                                                                                 R.anim.push_to_right_out);
//                }
//                Jump();
//            }
//        });

        avatar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LotteryApp appState = ((LotteryApp) context.getApplicationContext());
                if (appState.getUserid().equals(data.getUserid()) || appState.getUserid() == data.getUserid()) {
                    Intent intent = new Intent();
                    intent.setClass(context, MyProfileActivity.class);
                    context.startActivity(intent);
                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                        (new AnimationModel((Activity)context)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                     R.anim.push_to_right_out);
                    }
                    Jump();
                }
                else {
                    Intent intent1 = new Intent();
                    intent1.setClass(context, UserProfileActivity.class);
                    Bundle b = new Bundle();
                    b.putInt("userId", Integer.valueOf(data.getUserid()));
                    intent1.putExtras(b);
                    context.startActivity(intent1);
                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                        (new AnimationModel((Activity)context)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                     R.anim.push_to_right_out);
                    }
                    Jump();
                }
            }
        });
        return convertView;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return weibo.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return weibo.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    protected void Jump() {
// ((Activity) context).overridePendingTransition(R.anim.push_to_right_in, R.anim.push_to_right_out);
    }
}
