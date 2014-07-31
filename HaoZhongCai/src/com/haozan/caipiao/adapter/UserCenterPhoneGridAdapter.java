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

public class UserCenterPhoneGridAdapter
    extends BaseAdapter {
    private Context context;
    private ArrayList<String> tempList;
    private OnSelectedPhoneNumClickListener clickListener;
    private LayoutInflater inflater;

    public UserCenterPhoneGridAdapter(Context context, ArrayList<String> tempList) {
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
        final String td = tempList.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.usercenter_phone_grid_item, null);
            viewHolder = new ViewHolder();
            viewHolder.phone = (TextView) convertView.findViewById(R.id.phone);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.clear);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.phone.setText(td);
        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!clickListener.checkSelectedPhonNumClick(position, td))
                    return;
            }
        });

        return convertView;
    }

    public final class ViewHolder {
        private TextView phone;
        private ImageView img;
    }

    public interface OnSelectedPhoneNumClickListener {
        public boolean checkSelectedPhonNumClick(int position, String phone);
    }

    public void setSelectedPhoneNumClickListener(OnSelectedPhoneNumClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
