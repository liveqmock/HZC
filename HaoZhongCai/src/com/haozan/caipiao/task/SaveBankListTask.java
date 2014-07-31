package com.haozan.caipiao.task;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;

import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.db.BankDbBean;
import com.haozan.caipiao.db.BankTable;
import com.haozan.caipiao.db.api.SimpleTable;
import com.haozan.caipiao.taskbasic.Task;
import com.haozan.caipiao.types.topup.BankInfo;

/**
 * 存储银行列表到数据库
 * 
 * @author peter_wang
 * @create-time 2013-11-9 下午9:59:20
 */
public class SaveBankListTask
    extends Task {
    private ArrayList<BankInfo> mBankList;

    private Context mContext;
    private Handler mHandler;

    public SaveBankListTask(Context context, Handler handler, ArrayList<BankInfo> bankList) {
        this.mContext = context;

        this.mBankList = bankList;
        this.mHandler = handler;
    }

    @Override
    public void runTask() {
        // 保存之前先清除数据
        mContext.getContentResolver().delete(BankTable.CONTENT_URI, null, null);

        int length = mBankList.size();
        for (int i = 0; i < length; i++) {
            saveBank(mBankList.get(i));
        }

        mHandler.sendEmptyMessage(ControlMessage.SAVE_BANK_LIST);
    }

    private void saveBank(BankInfo bankInfo) {
        ContentValues values = new ContentValues();
        values.put(SimpleTable.SQL_INSERT_OR_REPLACE, true);

        values.put(BankDbBean.CHINESENAME, bankInfo.getChinesename());
        values.put(BankDbBean.CREDIT, bankInfo.getCredit());
        values.put(BankDbBean.DEPOSIT, bankInfo.getDeposit());
        values.put(BankDbBean.FIRSTCHAR, bankInfo.getFirstchar());
        values.put(BankDbBean.KEY, bankInfo.getKey());
        values.put(BankDbBean.SHORTHAND, bankInfo.getShorthand());
        mContext.getContentResolver().insert(BankTable.CONTENT_URI, values);
    }
}