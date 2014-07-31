package com.haozan.caipiao.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.FeedBackMessageData;

public class FeedBackAdapter
    extends BaseExpandableListAdapter {
    private ArrayList<FeedBackMessageData> title;
    private ArrayList<String> content;
    private Context context;
    private LayoutInflater inflater;

    public FeedBackAdapter(Context context, ArrayList<FeedBackMessageData> title) {
        super();
        this.context = context;
        this.title = title;
        inflater = LayoutInflater.from(this.context);
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        FeedBackMessageData messageData = title.get(groupPosition);
        convertView = inflater.inflate(R.layout.list_item_feedback_answer, null);
        TextView niceName = (TextView) convertView.findViewById(R.id.tvItemName);
        TextView content = (TextView) convertView.findViewById(R.id.tvItemContent);
        TextView feektime = (TextView) convertView.findViewById(R.id.tvItemDate);

        String name = messageData.getName();
// String phone ="手机用户"+ messageData.getPhone().substring(0, 3)+"****"+messageData.getPhone().substring(7);
        if (name == null) {
            niceName.setText("匿名彩友");
        }
        else {
            if (messageData.getName().equals("null") || messageData.getName().equals("")) {
                niceName.setText("匿名彩友");
            }
            else {
                niceName.setText(messageData.getName());
            }
        }

        content.setText(messageData.getContent());

// Date d=new Date(messageData.getTime());
// SimpleDateFormat sf=new SimpleDateFormat("yyyy年MM月dd日 ");
        feektime.setText(messageData.getTime().substring(2, 10));

        ImageView image = (ImageView) convertView.findViewById(R.id.tubiao);
        if (isExpanded) {
            image.setBackgroundResource(R.drawable.arrow_down);
        }
        else {
            image.setBackgroundResource(R.drawable.arrow_right);
        }
        return convertView;
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public Object getGroup(int groupPosition) {
        return title.get(groupPosition);
    }

    public int getGroupCount() {
        return title.size();
    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {
        convertView = inflater.inflate(R.layout.answer, null);
        FeedBackMessageData c = title.get(groupPosition);
        final TextView hufu = (TextView) convertView.findViewById(R.id.tvItemContent);
        if (c.getHuifu() == null) {
            hufu.setText("暂未回复");
        }
        else {
            hufu.setText(c.getHuifu());
        }

        return convertView;
    }

    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    public boolean hasStableIds() {
        return false;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return content.get(groupPosition);
    }

}
