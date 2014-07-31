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
import com.haozan.caipiao.types.TempData;

public class PhoneGridAdapter
    extends BaseAdapter {
    private Context context;
    private ArrayList<TempData> tempList;
    private OnSelectedPhoneNumClickListener clickListener;
    private LayoutInflater inflater;

    public PhoneGridAdapter(Context context, ArrayList<TempData> tempList) {
        this.context = context;
        this.tempList = tempList;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return tempList.size();
    }

    @Override
    public Object getItem(int position) {
        return tempList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        TempData td = tempList.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.phone_grid_item, null);
            viewHolder = new ViewHolder();
            viewHolder.phone = (TextView) convertView.findViewById(R.id.phone);
            viewHolder.clear = (ImageView) convertView.findViewById(R.id.clear);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        convertView.setBackgroundResource(R.drawable.jingcai_history_button_bg_normal);
        viewHolder.phone.setText(td.gettName());
        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!clickListener.checkSelectedPhonNumClick(position))
                    return;
            }
        });


        return convertView;
    }

    public final class ViewHolder {
        private TextView phone;
        private ImageView clear;
    }

    public interface OnSelectedPhoneNumClickListener {
        public boolean checkSelectedPhonNumClick(int position);
    }

    public void setSelectedPhoneNumClickListener(OnSelectedPhoneNumClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
