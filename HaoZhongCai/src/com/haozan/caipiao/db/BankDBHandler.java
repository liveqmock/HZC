package com.haozan.caipiao.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.haozan.caipiao.types.BankInfo;

public class BankDBHandler {
    private Context mContext;
    private ArrayList<BankInfo> dataStatus;

    public BankDBHandler(Context context) {
        mContext = context;
    }

    /**
     * 根据银行名称取出整条银行信息
     * 
     * @return BankInfo
     */
    public BankInfo getInfo(String bankName) {
        BankDBHelper dbHelper = new BankDBHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String selection = "cn=?";// 查询条件
        String[] selectionArgs = new String[] {bankName};// 查询条件参数
        Cursor cursor =
            database.query(BankDBHelper.BANKS_TABLE, null, selection, selectionArgs, null, null, null);

        BankInfo info = new BankInfo();
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow(BanksDbBean.ID));
            String firstChar = cursor.getString(cursor.getColumnIndexOrThrow(BanksDbBean.FIRSTCHAR));
            String chineseName = cursor.getString(cursor.getColumnIndexOrThrow(BanksDbBean.CHINESENAME));
            String key = cursor.getString(cursor.getColumnIndexOrThrow(BanksDbBean.KEY));
            String deposit = cursor.getString(cursor.getColumnIndexOrThrow(BanksDbBean.DEPOSIT));
            String credit = cursor.getString(cursor.getColumnIndexOrThrow(BanksDbBean.CREDIT));
            String lastUse = cursor.getString(cursor.getColumnIndexOrThrow(BanksDbBean.LASTUSE));
            String rechargeType = cursor.getString(cursor.getColumnIndexOrThrow(BanksDbBean.RECHARGETYPE));
            info.setId(Integer.valueOf(id));
            info.setFirstChar(firstChar);
            info.setChineseName(chineseName);
            info.setKey(key);
            info.setDepositCard(deposit);
            info.setCreditCard(credit);
            info.setLastUse(lastUse);
            info.setRechargeType(rechargeType);
        }

        cursor.close();
        database.close();
        dbHelper.close();
        return info;
    }

    /**
     * 将数据库中保存的帐号信息以链表的形式取出
     * 
     * @param ACCOUNT
     * @return
     */
    public ArrayList<BankInfo> getStatus(String ACCOUNT) {
        dataStatus = new ArrayList<BankInfo>();
        BankDBHelper dbHelper = new BankDBHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String selection = "cn=?";// 查询条件
        String[] selectionArgs = new String[] {ACCOUNT};// 查询条件参数
// Cursor cursor =
// database.query(BankDBHelper.USERS_TABLE, null, selection, selectionArgs, null, null, null);
        Cursor cursor = database.query(BankDBHelper.BANKS_TABLE, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            BankInfo info = new BankInfo();
            String id = cursor.getString(cursor.getColumnIndexOrThrow(BanksDbBean.ID));
            String firstChar = cursor.getString(cursor.getColumnIndexOrThrow(BanksDbBean.FIRSTCHAR));
            String chineseName = cursor.getString(cursor.getColumnIndexOrThrow(BanksDbBean.CHINESENAME));
            String key = cursor.getString(cursor.getColumnIndexOrThrow(BanksDbBean.KEY));
            String deposit = cursor.getString(cursor.getColumnIndexOrThrow(BanksDbBean.DEPOSIT));
            String credit = cursor.getString(cursor.getColumnIndexOrThrow(BanksDbBean.CREDIT));
            String lastUse = cursor.getString(cursor.getColumnIndexOrThrow(BanksDbBean.LASTUSE));
            String rechargeType = cursor.getString(cursor.getColumnIndexOrThrow(BanksDbBean.RECHARGETYPE));

            info.setId(Integer.valueOf(id));
            info.setFirstChar(firstChar);
            info.setChineseName(chineseName);
            info.setKey(key);
            info.setDepositCard(deposit);
            info.setCreditCard(credit);
            info.setLastUse(lastUse);
            info.setRechargeType(rechargeType);

            // 添加到Status
            dataStatus.add(info);
        }
// cursor.close();
        database.close();
        dbHelper.close();
        return this.dataStatus;
    }

    /**
     * 保存银行信息
     * 
     * @param accountData
     */
    public void save(BankInfo info) {
// int id = accountData.getId();
        String firstChar = info.getFirstChar();
        String chineseName = info.getChineseName();
        String key = info.getKey();
        String deposit = info.getDepositCard();
        String credit = info.getCreditCard();
        String lastUse = info.getLastUse();
        String rechargeType = info.getRechargeType();
        // ////////////////
        save(firstChar, chineseName, key, deposit, credit, lastUse, rechargeType);
    }

    /**
     * 保存银行信息
     * 
     * @param accountData
     */
    public void save(ArrayList<BankInfo> array) {
// int id = accountData.getId();
        for (int i = 0; i < array.size(); i++) {
            BankInfo info = array.get(i);
            String firstChar = info.getFirstChar();
            String chineseName = info.getChineseName();
            String key = info.getKey();
            String deposit = info.getDepositCard();
            String credit = info.getCreditCard();
            String lastUse = info.getLastUse();
            String rechargeType = info.getRechargeType();
            // ////////////////
            save(firstChar, chineseName, key, deposit, credit, lastUse, rechargeType);
        }

    }

    /**
     * 具体实现保存信息
     * 
     * @param id
     * @param firstChar
     * @param chineseName
     * @param key
     * @param rechargeType
     * @param lastUse
     */
    private void save(String firstChar, String chineseName, String key, String deposit, String credit,
                      String lastUse, String rechargeType) {
        ContentValues values = new ContentValues();
        BankDBHelper dbHelper = new BankDBHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // 当要保存的帐号数据库中已有时，更新支持的充值方式
        if (checkIfHave(chineseName)) {
            values.put(BanksDbBean.DEPOSIT, deposit);
            values.put(BanksDbBean.CREDIT, credit);
            values.put(BanksDbBean.LASTUSE, lastUse);
            values.put(BanksDbBean.RECHARGETYPE, rechargeType);
            String[] selectionArgs = new String[] {chineseName};// 查询条件参数
            database.update(BankDBHelper.BANKS_TABLE, values, BanksDbBean.CHINESENAME + "=?", selectionArgs);
            database.close();
            dbHelper.close();
        }
        // 当要保存的帐号数据库中没有时，执行插入操作
        else {
            // values.put(BanksDB.ID, id);
            values.put(BanksDbBean.FIRSTCHAR, firstChar);
            values.put(BanksDbBean.CHINESENAME, chineseName);
            values.put(BanksDbBean.KEY, key);
            values.put(BanksDbBean.DEPOSIT, deposit);
            values.put(BanksDbBean.CREDIT, credit);
            values.put(BanksDbBean.LASTUSE, lastUse);
            values.put(BanksDbBean.RECHARGETYPE, rechargeType);

            database.insertOrThrow(BankDBHelper.BANKS_TABLE, null, values);
            database.close();
            dbHelper.close();
        }

    }

    private boolean checkIfHave(String chineseName) {
        BankDBHelper dbHelper = new BankDBHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String selection = "cn=?";// 查询条件
        String[] selectionArgs = new String[] {chineseName};// 查询条件参数
        Cursor c = database.query(BankDBHelper.BANKS_TABLE, null, selection, selectionArgs, null, null, null);
        c.moveToNext();
        int a = c.getCount();
        database.close();
        dbHelper.close();
        if (a == 0) {
            return false;
        }
        else {
            return true;
        }

    }

    /**
     * 清除指定表内所有数据
     * 
     * @param tableName
     */
    public void clearTable(String tableName) {
        BankDBHelper sqlHelper = new BankDBHelper(mContext);
        SQLiteDatabase database = sqlHelper.getWritableDatabase();
        database.delete(tableName, null, null);
        database.close();
    }

    public void deleteFromTable(String tableName, String account) {
        BankDBHelper sqlHelper = new BankDBHelper(mContext);
        SQLiteDatabase database = sqlHelper.getWritableDatabase();
        String[] type = {account};
        database.delete(tableName, "cn=?", type);
        database.close();
    }

    /**
     * 将所有银行置为非上次使用
     * 
     * @param tableName
     */
    public void refreshTable(String tableName) {
        BankDBHelper sqlHelper = new BankDBHelper(mContext);
        SQLiteDatabase database = sqlHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.clear();
        values.put(BanksDbBean.LASTUSE, 0);
        values.put(BanksDbBean.RECHARGETYPE, "");
        database.update(tableName, values, null, null);
        database.close();
    }

    /**
     * 更新银行上次使用的充值方式
     * 
     * @param bankName
     * @param rechargeType
     */

    public void updateOneData(String bankName, String rechargeType) {
        BankInfo info = getInfo(bankName);
        info.setRechargeType(rechargeType);
        save(info);
    }

    void log(String msg) {
    }

}
