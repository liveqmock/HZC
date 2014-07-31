package com.haozan.caipiao.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Paint;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.NoticeContent;
import com.haozan.caipiao.util.ActionUtil;

public class NoticeListAdapter
    extends BaseAdapter {
    private ArrayList<NoticeContent> list;
    private Context context;
    private LayoutInflater inflater;

    public NoticeListAdapter(Context context, ArrayList<NoticeContent> list) {
        this.context = context;
        inflater = LayoutInflater.from(this.context);
        this.list = list;
    }

    public final class ViewHolder {
        private TextView noticeView;
        private TextView timeView;
        private TextView titleView;
        private RelativeLayout noticeContentDetail;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NoticeContent notice = list.get(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.notice_item_view, null);
            viewHolder.noticeView = (TextView) convertView.findViewById(R.id.notice_content);
            viewHolder.noticeView.setMovementMethod(LinkMovementMethod.getInstance());
            viewHolder.timeView = (TextView) convertView.findViewById(R.id.notice_time);
            viewHolder.titleView = (TextView) convertView.findViewById(R.id.notice_title);
            viewHolder.noticeContentDetail =
                (RelativeLayout) convertView.findViewById(R.id.notice_content_detail);
            convertView.setTag(viewHolder);
            ((TextView) convertView.findViewById(R.id.more_inf)).getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.noticeView.setText(notice.getDigest());
        viewHolder.timeView.setText(notice.getTime());
        viewHolder.titleView.setText(notice.getTitle());

        if (notice.getContent() == null) {
            viewHolder.noticeContentDetail.setVisibility(View.GONE);
        }
        else {
            if (notice.getContent().indexOf("http") == -1) {
                viewHolder.noticeContentDetail.setVisibility(View.GONE);
            }
            else {
                viewHolder.noticeContentDetail.setVisibility(View.VISIBLE);
            }
        }
        final int index = position;
        viewHolder.noticeContentDetail.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ActionUtil.toWebBrowser(context, "公告详情", list.get(index).getContent());
            }
        });
        return convertView;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
