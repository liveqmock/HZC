package com.haozan.caipiao.adapter.topup;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.topup.BankInfo;

/**
 * 银行列表
 * 
 * @author peter_wang
 * @create-time 2013-9-18 上午11:18:48
 */
public class BankListAdapter
    extends BaseAdapter {
    private Context mContext;
    private ArrayList<BankInfo> mBankList;
    private BankSelectedListener mBankSelectedListener;

    public BankListAdapter(Context context, ArrayList<BankInfo> bankList) {
        super();
        this.mContext = context;
        this.mBankList = bankList;
    }

    public final class ViewHolder {
        public TextView tvFirstCharHint;
        private LinearLayout layoutBank;
        public TextView tvBank;
        public ImageView ivIcon;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.list_item_bank, null);
            holder = new ViewHolder();
            holder.tvFirstCharHint = (TextView) convertView.findViewById(R.id.text_first_char_hint);
            holder.layoutBank = (LinearLayout) convertView.findViewById(R.id.bank_layout);
            holder.tvBank = (TextView) convertView.findViewById(R.id.bank_name);
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.bank_icon);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        BankInfo bankInfo = mBankList.get(position);
        int idx = position - 1;
        String previewChar = idx >= 0 ? mBankList.get(idx).getFirstchar() : "";
        String currentChar = bankInfo.getFirstchar();
        if (currentChar.equals(previewChar) == false) {
            holder.tvFirstCharHint.setVisibility(View.VISIBLE);
            holder.tvFirstCharHint.setText(currentChar);
        }
        else {
            holder.tvFirstCharHint.setVisibility(View.GONE);
        }

        if (bankInfo.getIconResource() != -1) {
            holder.ivIcon.setImageResource(bankInfo.getIconResource());
        }
        else {
            holder.ivIcon.setImageResource(R.drawable.bank_default_icon);
        }

        holder.tvBank.setText(bankInfo.getChinesename());

        holder.layoutBank.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mBankSelectedListener.onBankSelected(mBankList.get(position));
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return mBankList.size();
    }

    @Override
    public Object getItem(int position) {
        return mBankList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setmBankSelectedListener(BankSelectedListener mBankSelectedListener) {
        this.mBankSelectedListener = mBankSelectedListener;
    }

    public interface BankSelectedListener {
        public void onBankSelected(BankInfo bank);
    }

}
