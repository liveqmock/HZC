package com.haozan.caipiao.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.NewsListItem;

public class LotteryNewsListAdapter
    extends BaseAdapter {
    private Context context;
    private ArrayList<NewsListItem> ListItemRecord;
    private LayoutInflater inflater;
 
    private int backGroundId=0;

    public LotteryNewsListAdapter(Context context, ArrayList<NewsListItem> ListItemRecord) {
        this.context = context;
        this.ListItemRecord = ListItemRecord;
        this.inflater = LayoutInflater.from(this.context);
    }

    public final class ViewHolder{
        private TextView newsTitle;
        private TextView issueDate;
        private LinearLayout newsItemContainer;
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
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        ViewHolder viewHolder;
        if(arg1==null){
            arg1 = inflater.inflate(R.layout.news_list_item, null);
            viewHolder=new ViewHolder();
            viewHolder.newsTitle = (TextView) arg1.findViewById(R.id.news_title);
            viewHolder.issueDate = (TextView) arg1.findViewById(R.id.news_date);
            viewHolder.newsItemContainer=(LinearLayout) arg1.findViewById(R.id.news_item_container);
            arg1.setTag(viewHolder);
        }else{
           viewHolder=(ViewHolder) arg1.getTag();
        }
        if (ListItemRecord != null) {
            final NewsListItem newsListItem = ListItemRecord.get(arg0);
            if (newsListItem != null) {
                viewHolder.newsTitle.setText(newsListItem.getTitle());
                viewHolder.issueDate.setText(newsListItem.getDate());
            }
        }
//        if (arg0 == 0)
//            backGroundId = R.drawable.history_item_first;
//        else
            backGroundId = R.drawable.list_bg;
        viewHolder.newsItemContainer.setBackgroundResource(backGroundId);
        return arg1;
    }

}
