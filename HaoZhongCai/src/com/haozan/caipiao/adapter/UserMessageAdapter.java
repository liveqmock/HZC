package com.haozan.caipiao.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.UserHistoryRecord;
import com.haozan.caipiao.types.UserMessageData;
import com.haozan.caipiao.util.MySpanableSet;

public class UserMessageAdapter
    extends BaseExpandableListAdapter {

    private ArrayList<UserMessageData> title;
    private ArrayList<SpannableStringBuilder> content;
    private Context context;
    private LayoutInflater inflater;
    private MySpanableSet mySpanableSet;

    protected UserHistoryRecord record;

    public UserMessageAdapter(Context context, ArrayList<UserMessageData> title,
                              ArrayList<SpannableStringBuilder> content) {
        this.context = context;
        this.title = title;
        this.content = content;
        inflater = LayoutInflater.from(this.context);
        mySpanableSet = new MySpanableSet(context);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return content.get(groupPosition);
    }

    public final class ViewHolderChild {
        private TextView tvContent;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {
        ViewHolderChild viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolderChild();
            viewHolder.tvContent = getChildView();
            convertView = viewHolder.tvContent;
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolderChild) convertView.getTag();
        }
        viewHolder.tvContent.setText(content.get(groupPosition));
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return title.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return title.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public final class ViewHolderGroup {
        private ImageView termView;
        private TextView titleView;
        private TextView timeView;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolderGroup viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolderGroup();
            convertView = inflater.inflate(R.layout.message_title, null);
            viewHolder.termView = (ImageView) convertView.findViewById(R.id.message_icon);
            viewHolder.titleView = (TextView) convertView.findViewById(R.id.message_title);
            viewHolder.timeView = (TextView) convertView.findViewById(R.id.message_time);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolderGroup) convertView.getTag();
        }
        UserMessageData c = title.get(groupPosition);
        if (c.getIsRead())
            viewHolder.termView.setBackgroundResource(R.drawable.msg_open);
        else
            viewHolder.termView.setBackgroundResource(R.drawable.msg_new);
        viewHolder.titleView.setText(c.getSubject());
        viewHolder.timeView.setText(c.getTime());
        return convertView;
    }

    private TextView getChildView() {
        TextView tv = new TextView(context);
        tv.setTextSize(14);
        tv.setTextColor(context.getResources().getColor(R.color.dark_purple));
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 110);
        tv.setLayoutParams(layoutParams);
        tv.setGravity(Gravity.CENTER_VERTICAL);
        tv.setPadding(0, 0, 0, 0);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        tv.setBackgroundResource(R.drawable.jczq_list_item_sub_bg);
        return tv;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

}
