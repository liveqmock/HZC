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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.weibo.BasicWeibo;
import com.haozan.caipiao.activity.weibo.MyProfileActivity;
import com.haozan.caipiao.activity.weibo.UserProfileActivity;
import com.haozan.caipiao.types.SearchPeopleDate;
import com.haozan.caipiao.util.anamation.AnimationModel;

public class SearchPeopleAdapter
    extends BaseAdapter {
    private ArrayList<SearchPeopleDate> PeopleDate;
    private Context context;
    private EditText searchET;

    public SearchPeopleAdapter(Context context, ArrayList<SearchPeopleDate> peopleDate, EditText searchET) {
        super();
        this.context = context;
        this.PeopleDate = peopleDate;
        this.searchET = searchET;
    }

    private class ViewHolder {
        private TextView niceName;
        private TextView qianming;
        private ImageView avatar;
        private ImageView genderPic;
        private TextView city;
        private Button bt;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final ViewHolder viewHolder;
        final SearchPeopleDate Data = PeopleDate.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.focus_item, null);
            viewHolder = new ViewHolder();
            viewHolder.niceName = (TextView) convertView.findViewById(R.id.niceName);
            viewHolder.qianming = (TextView) convertView.findViewById(R.id.tvItemContent);
            viewHolder.city = (TextView) convertView.findViewById(R.id.tvItemDate);
            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.ivItemPortrait);
            viewHolder.genderPic = (ImageView) convertView.findViewById(R.id.genderPic);
            viewHolder.bt = (Button) convertView.findViewById(R.id.deleteFocus);
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
        viewHolder.bt.setVisibility(View.GONE);
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
        String gender1 = Data.getGender();
        if (gender1.equals("1")) {
            viewHolder.genderPic.setImageResource(R.drawable.icon_male);
        }
        else {
            viewHolder.genderPic.setImageResource(R.drawable.icon_female);
        }

        String chengshi = Data.getCity();
        if (chengshi == null || chengshi.equals("null") || chengshi.equals("")) {
            viewHolder.city.setText("城市：" + "未知");
        }
        else {
            viewHolder.city.setText("城市：" + Data.getCity());
        }

        String qm = Data.getQianming();
        if (qm == null || qm.equals("null")) {
            viewHolder.qianming.setText("个性签名：" + " ");
        }
        else {
            viewHolder.qianming.setText("个性签名：" + Data.getQianming());
        }

        convertView.setOnClickListener(new OnClickListener() {
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
                    b.putInt("data_type", 2);
                    b.putInt("userId", Integer.valueOf(Data.getUserid()));
                    intent1.putExtras(b);
                    context.startActivity(intent1);
                }
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                    (new AnimationModel((Activity)context)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                     R.anim.push_to_right_out);
                }
            }
        });
        BasicWeibo.hightKeyWords(context, viewHolder.niceName, Data.getName(), searchET.getText().toString());
        return convertView;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return PeopleDate.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return PeopleDate.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
}
