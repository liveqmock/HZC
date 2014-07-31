package com.haozan.caipiao.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.WeiboSelectedItem;

public class WeiboSelectFansAdapter
    extends BaseAdapter {
    private Context context;
    private ArrayList<WeiboSelectedItem> nameList;
    private LayoutInflater inflater;

// private OnBallClickListener clickListener;

    public WeiboSelectFansAdapter(Context context, ArrayList<WeiboSelectedItem> listItem) {
        this.context = context;
        this.nameList = listItem;
        inflater = LayoutInflater.from(this.context);
    }

    public final class ViewHolder {
        private TextView name;
        private ImageView img;
    }

    @Override
    public int getCount() {
        return nameList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return nameList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int index, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        WeiboSelectedItem item = nameList.get(index);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.weibo_selectfans_item, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.img);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (index % 2 == 0) {
            convertView.setBackgroundResource(R.color.gray_light_for_list);
        }
        else {
            convertView.setBackgroundResource(R.color.gray_dark_for_list);
        }
        viewHolder.name.setText(item.getName());
        if (nameList.get(index).isSelected() == true) {
            viewHolder.img.setImageResource(R.drawable.choosing_select);
        }
        else {
            viewHolder.img.setImageResource(R.drawable.choosing_not_select);
        }
        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (nameList.get(index).isSelected() == true) {
// if (!clickListener.checkClick(index, j, false))
// return;
                    nameList.get(index).setSelected(false);
                    viewHolder.img.setImageResource(R.drawable.choosing_not_select);

                }
                else {
// if (!clickListener.checkClick(index, j, true))
// return;
                    nameList.get(index).setSelected(true);
                    viewHolder.img.setImageResource(R.drawable.choosing_select);

                }
            }
        });
        return convertView;
    }

// public interface OnBallClickListener {
// public boolean checkClick(int groupPosition, int childPosition, boolean ifChecked);
// }
//
// public void setClickListener(OnBallClickListener clickListener) {
// this.clickListener = clickListener;
// }
}