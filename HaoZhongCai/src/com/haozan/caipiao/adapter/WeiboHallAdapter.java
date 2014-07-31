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
import com.haozan.caipiao.activity.Login;
import com.haozan.caipiao.activity.weibo.BasicWeibo;
import com.haozan.caipiao.activity.weibo.MyProfileActivity;
import com.haozan.caipiao.activity.weibo.UserProfileActivity;
import com.haozan.caipiao.activity.weibo.WeiboZhengWen;
import com.haozan.caipiao.types.WeiboData;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.util.weiboutil.TextUtil;
import com.haozan.caipiao.util.weiboutil.TimeUtil;

public class WeiboHallAdapter
    extends BaseAdapter {
    private ArrayList<WeiboData> weibo;
    private Context context;
    private LayoutInflater inflater;
    WeiboData data1;

    public WeiboHallAdapter(Context context, ArrayList<WeiboData> weibo) {
        super();
        this.context = context;
        this.weibo = weibo;
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
        private ImageView unitePic;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder;
        final WeiboData data = weibo.get(position);
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
            viewHolder.unitePic = (ImageView) convertView.findViewById(R.id.unite_pic);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (data.getBitmap() != null) {
            viewHolder.avatar.setImageBitmap(data.getBitmap());
        }
        else {
// Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.lucky_cat);
// viewHolder.avatar.setImageBitmap(BasicWeibo.toRoundCorner(bmp, 0));
            viewHolder.avatar.setImageResource(R.drawable.lucky_cat);
// avatar.setImageResource(getRandom());
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

        int type = data.getType();
        // 第一个空格替换为“+”号
        if (title.equals("dfljy")) {
            String money = data.getContent();
            String money1 = money.replaceFirst(" ", "+");
            viewHolder.content.setText(money1);
        }
        else {
            if (type != 6) {
                viewHolder.content.setText(TextUtil.formatContent(data.getContent(), context));
            }
            else {
                viewHolder.newsTitle.setText(TextUtil.formatContent(data.getContent(), context));
            }
        }

        // 如果包含"投注位置"则显示位置图标
        BasicWeibo.locationPicShow(data.getContent(), viewHolder.locationPic);
        // 如果包含"合买"，则显示合买图标
        BasicWeibo.unitePicShow(data.getContent(), viewHolder.unitePic);

        viewHolder.creattime.setText(TimeUtil.getTimeStr(BasicWeibo.stringDate(data.getTime(),
                                                                               "yyyy-MM-dd HH:mm:ss")));
        viewHolder.conmentCount.setText(String.valueOf(data.getReplyCount()));// 评论数
        viewHolder.forwardCount.setText(String.valueOf(data.getRetweetCount()));// 转发数

        String preview = data.getPreview();

        String version = data.getSource();

        // 子内容显示
        BasicWeibo.subContentShow(context, viewHolder.newsTitle, type, title, preview, null,
                                  viewHolder.content, data.getContent());

        // 财园判断发自哪个版本以及投注跳转、新闻跳转控制
        BasicWeibo.weiboFrom(context, type, version, title, viewHolder.weiboFrom, viewHolder.newsTitle,
                             data.getAttachid(), preview);

        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!checkLogin()) {
                    lottery("动态正文", data);
                }
                else {
                    Intent intent = new Intent();
                    intent.setClass(context, WeiboZhengWen.class);
                    Bundle b = new Bundle();
                    b.putString("weiboId", String.valueOf(data.getId()));
                    intent.putExtras(b);
                    // startActivityForResult(intent, 0);
                    context.startActivity(intent);
                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                        (new AnimationModel((Activity) context)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                           R.anim.push_to_right_out);
                    }
                    Jump();
                }
            }
        });

//        viewHolder.ly.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                if (!checkLogin()) {
//                    lottery("动态正文", data);
//                }
//                else {
//                    Intent intent = new Intent();
//                    intent.setClass(context, WeiboZhengWen.class);
//                    Bundle b = new Bundle();
//                    b.putString("weiboId", String.valueOf(data.getId()));
//                    intent.putExtras(b);
//                    // startActivityForResult(intent, 0);
//                    context.startActivity(intent);
//                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
//                        (new AnimationModel((Activity) context)).overridePendingTransition(R.anim.push_to_right_in,
//                                                                                           R.anim.push_to_right_out);
//                    }
//                    Jump();
//                }
//            }
//        });

        viewHolder.avatar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LotteryApp appState = ((LotteryApp) context.getApplicationContext());
                if (!checkLogin()) {
                    lottery("用户资料", data);
                }
                else if (appState.getUserid() == data.getUserid() ||
                    appState.getUserid().equals(data.getUserid())) {
                    Intent intent = new Intent();
                    intent.setClass(context, MyProfileActivity.class);
                    context.startActivity(intent);
                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                        (new AnimationModel((Activity) context)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                           R.anim.push_to_right_out);
                    }
                    Jump();
                }
                else {
                    Intent intent = new Intent();
                    intent.setClass(context, UserProfileActivity.class);
                    Bundle b = new Bundle();
                    // 把动态对象传到指定的地方
                    b.putInt("userId", Integer.valueOf(data.getUserid()));
                    intent.putExtras(b);
                    context.startActivity(intent);
                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                        (new AnimationModel((Activity) context)).overridePendingTransition(R.anim.push_to_right_in,
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

    // 随机获取头像
    private int getRandom() {
        int random = 0;
        int[] ints = {0x7f020108, 0x7f020136, 0x7f02006e, 0x7f020109, 0x7f020161};
        int index = (int) (Math.random() * ints.length);
        random = ints[index];
        return random;
    }

    private void lottery(String flag, WeiboData data) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("forwardFlag", flag);
        bundle.putBoolean("ifStartSelf", false);
        bundle.putInt("userId", Integer.valueOf(data.getUserid()));
        bundle.putString("weiboId", String.valueOf(data.getId()));
        bundle.putSerializable("WbData", data);
        intent.putExtras(bundle);
        intent.setClass(context, Login.class);
// intent.setClass(context, StartUp.class);
        context.startActivity(intent);
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
            (new AnimationModel((Activity) context)).overridePendingTransition(R.anim.push_to_right_in,
                                                                               R.anim.push_to_right_out);
        }
    }

    // 判断是否登录
    private boolean checkLogin() {
        LotteryApp app = ((LotteryApp) context.getApplicationContext());
        String userid = app.getSessionid();
        if (userid == null) {
            return false;
        }
        else {
            return true;
        }
    }

    protected void Jump() {
// ((Activity) context).overridePendingTransition(R.anim.push_to_right_in, R.anim.push_to_right_out);
    }
}
