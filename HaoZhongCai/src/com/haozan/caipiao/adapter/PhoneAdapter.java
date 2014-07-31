package com.haozan.caipiao.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.PhoneItemData;

public class PhoneAdapter
    extends BaseAdapter {
    private Context context;
    private ArrayList<PhoneItemData> phoneList;
    private LayoutInflater inflater;
    private OnBallClickListener clickListener;

    public PhoneAdapter(Context context, ArrayList<PhoneItemData> listItem) {
        this.context = context;
        this.phoneList = listItem;
        inflater = LayoutInflater.from(this.context);
    }

    public final class ViewHolder {
        private TextView name;
        private LinearLayout phoneLin;
    }

    @Override
    public int getCount() {
        return phoneList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return phoneList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int index, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.phone_item, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.phoneLin = (LinearLayout) convertView.findViewById(R.id.ll_phone_num);

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
// initListView(viewHolder, index);
        initListViews(index, viewHolder);
        return convertView;
    }

    public void initListViews(final int index, ViewHolder viewHolder) {
        viewHolder.phoneLin.removeAllViews();
        viewHolder.name.setText(phoneList.get(index).getName());
        RelativeLayout.LayoutParams rp =
            new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < phoneList.get(index).getPhonenums().length; i++) {
            final int j = i;
            RelativeLayout ll = new RelativeLayout(context);
            ll.setLayoutParams(rp);
            TextView tv = new TextView(context);
            if (null == phoneList.get(index).getPhonenums(i) ||
                ("").equals(phoneList.get(index).getPhonenums(i))) {
                break;
            }
            tv.setText(phoneList.get(index).getPhonenums(i));
            tv.setTextColor(context.getResources().getColor(R.color.dark_purple));
            tv.setTextSize(18);
            RelativeLayout.LayoutParams rp1 =
                new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            rp1.addRule(RelativeLayout.CENTER_HORIZONTAL);
            ll.addView(tv, rp1);
            final ImageView img = new ImageView(context);
            if (phoneList.get(index).getIfChecked(j) == true) {
                img.setImageResource(R.drawable.choosing_select);
            }
            else {
                img.setImageResource(R.drawable.choosing_not_select);
            }

            ll.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (phoneList.get(index).getIfChecked(j) == true) {
                        if (!clickListener.checkClick(index, j, false))
                            return;
//                        phoneList.get(index).setIfChecked(j, false);
                        img.setImageResource(R.drawable.choosing_not_select);
                        
                    }
                    else {
                        if (!clickListener.checkClick(index, j, true))
                            return;
//                        phoneList.get(index).setIfChecked(j, true);
                        img.setImageResource(R.drawable.choosing_select);
                        
                    }
                }
            });

            RelativeLayout.LayoutParams rp2 =
                new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            rp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rp2.setMargins(0, 0, 10, 0);
            ll.addView(img, rp2);
            viewHolder.phoneLin.addView(ll);
        }
    }

    public interface OnBallClickListener {
        public boolean checkClick(int groupPosition, int childPosition, boolean ifChecked);
    }

    public void setClickListener(OnBallClickListener clickListener) {
        this.clickListener = clickListener;
    }
}