package com.haozan.caipiao.control.topup;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.haozan.caipiao.activity.topup.TopupRecommend;
import com.haozan.caipiao.db.BankDbBean;
import com.haozan.caipiao.db.BankTable;
import com.haozan.caipiao.types.topup.BankInfo;

/**
 * 信用卡银行列表选择控制器
 * 
 * @author peter_wang
 * @create-time 2013-11-9 下午3:58:08
 */
public class UnionPayCreditSelectBankControl
    extends SelectBankControlBasic {

    public UnionPayCreditSelectBankControl(Context context, Handler handler) {
        super(context, handler);
    }

    public void insertBankFromDb(ArrayList<BankInfo> bankList) {
        Cursor cursor =
            mContext.getContentResolver().query(BankTable.CONTENT_URI, null, BankDbBean.CREDIT + "!=''",
                                                null, BankDbBean.SHORTHAND + " ASC");

        while (cursor != null && cursor.moveToNext()) {
            BankInfo bank = new BankInfo();
            bank.setChinesename(cursor.getString(cursor.getColumnIndexOrThrow(BankDbBean.CHINESENAME)));
            bank.setCredit(cursor.getString(cursor.getColumnIndexOrThrow(BankDbBean.CREDIT)));
            bank.setDeposit(cursor.getString(cursor.getColumnIndexOrThrow(BankDbBean.DEPOSIT)));
            bank.setFirstchar(cursor.getString(cursor.getColumnIndexOrThrow(BankDbBean.FIRSTCHAR)));
            bank.setKey(cursor.getString(cursor.getColumnIndexOrThrow(BankDbBean.KEY)));
            bank.setShorthand(cursor.getString(cursor.getColumnIndexOrThrow(BankDbBean.SHORTHAND)));
            bankList.add(bank);
        }

        cursor.close();
    }

    @Override
    public void selectedBank(BankInfo bank) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt("type", TopupRecommend.UNIONPAY_CREDIT);
        bundle.putString("way", bank.getCredit());
        bundle.putString("bank_name", bank.getChinesename());
        intent.putExtras(bundle);
        intent.setClass(mContext, TopupRecommend.class);
        mContext.startActivity(intent);
    }

    @Override
    public void setTitle(TextView tvTitle) {
        tvTitle.setText("选择银行-信用卡");
    }
}
