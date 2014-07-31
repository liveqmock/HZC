package com.haozan.caipiao.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.NewsListItem;

public class NewsExpanableAdapter
    extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<ArrayList<NewsListItem>> newsList;
    private LayoutInflater inflater;
    private String[] newsTypeArray = {"新闻", "预测", "技巧"};

// private String[] tagArray={"双色球","3D","大乐透","竞彩",""};

    public final class ViewHolder {
        // ground
        private TextView newsType;
        private ImageView imageView;
        // child
        private TextView newsTitle;
        private TextView newsDate;
        private LinearLayout tagViewHolder;
        private TextView tagName;
    }

    public NewsExpanableAdapter(Context context, ArrayList<ArrayList<NewsListItem>> newsList) {
        this.context = context;
        this.newsList = newsList;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public Object getChild(int arg0, int arg1) {
        return newsList.get(arg0).get(arg1);
    }

    @Override
    public long getChildId(int arg0, int arg1) {
        return arg1;
    }

    @Override
    public View getChildView(int arg0, int arg1, boolean arg2, View arg3, ViewGroup arg4) {
        ViewHolder viewHolder;
        if (arg3 == null) {
            viewHolder = new ViewHolder();
            arg3 = inflater.inflate(R.layout.news_adapter_child_layout, null);
            viewHolder.newsTitle = (TextView) arg3.findViewById(R.id.news_title);
            viewHolder.newsDate = (TextView) arg3.findViewById(R.id.news_date);
            viewHolder.tagViewHolder = (LinearLayout) arg3.findViewById(R.id.news_tag_container);
            viewHolder.tagName = (TextView) arg3.findViewById(R.id.tag_name);
            arg3.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) arg3.getTag();
        }

        try {
            viewHolder.tagName.setText(newsTypeArray[arg0]);
            viewHolder.newsTitle.setText(newsList.get(arg0).get(arg1).getTitle());
            viewHolder.newsDate.setText(newsList.get(arg0).get(arg1).getDate());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
// addNewsTag(viewHolder.tagViewHolder, arg0);
        return arg3;
    }

// private void addNewsTag(LinearLayout newsTagContainer, int postion) {
// TextView textTag = new TextView(context);
// if (postion == 0)
// textTag.setBackgroundResource(R.drawable.ic_feed_tag);
// else
// textTag.setText(newsTypeArray[postion]);
// textTag.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
// textTag.setTextColor(context.getResources().getColor(R.color.dark_purple));
// newsTagContainer.addView(textTag);
// }

    @Override
    public int getChildrenCount(int arg0) {
        return newsList.get(arg0).size();
    }

    @Override
    public Object getGroup(int arg0) {
        return newsList.get(arg0);
    }

    @Override
    public int getGroupCount() {
        return newsList.size();
    }

    @Override
    public long getGroupId(int arg0) {
        return arg0;
    }

    @Override
    public View getGroupView(int arg0, boolean arg1, View arg2, ViewGroup arg3) {
        ViewHolder viewHolder;
        if (arg2 == null) {
            viewHolder = new ViewHolder();
            arg2 = inflater.inflate(R.layout.news_adapter_group_layout, null);
            viewHolder.newsType = (TextView) arg2.findViewById(R.id.news_type);
            arg2.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) arg2.getTag();
        }
        viewHolder.newsType.setText(newsTypeArray[arg0]);
// setTextBold(viewHolder.newsType);
        return arg2;
    }

    private void setTextBold(TextView bt) {
        TextPaint tp = bt.getPaint();
        tp.setFakeBoldText(true);
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

}
