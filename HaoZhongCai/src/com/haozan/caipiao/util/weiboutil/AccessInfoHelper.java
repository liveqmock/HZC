package com.haozan.caipiao.util.weiboutil;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.haozan.caipiao.LotteryApp;

public class AccessInfoHelper {
    private DBHelper dbHelper;
    private SQLiteDatabase newsDB;
    private Context context;

    public AccessInfoHelper(Context context) {
        this.context = context;
    }

// 初始化数据库连接

    public AccessInfoHelper open() {
        dbHelper = new DBHelper(this.context);

        newsDB = dbHelper.getWritableDatabase();
        return this;
    }

// 关闭连接

    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

// 创建一条记录

    public long create(LotteryApp accessInfo) {
        ContentValues values = new ContentValues();

        values.put(AccessInfoColumn.USERID, accessInfo.getWeiboUserID());
        values.put(AccessInfoColumn.ACCESS_TOKEN, accessInfo.getAccessToken());
        values.put(AccessInfoColumn.ACCESS_SECRET, accessInfo.getAccessSecret());

        return newsDB.insert(DBHelper.ACCESSLIB_TABLE, null, values);
    }

// 更新

    public boolean update(LotteryApp accessInfo) {
        ContentValues values = new ContentValues();

        values.put(AccessInfoColumn.USERID, accessInfo.getWeiboUserID());
        values.put(AccessInfoColumn.ACCESS_TOKEN, accessInfo.getAccessToken());
        values.put(AccessInfoColumn.ACCESS_SECRET, accessInfo.getAccessSecret());

        String whereClause = AccessInfoColumn.USERID + "=" + accessInfo.getWeiboUserID();

        return newsDB.update(DBHelper.ACCESSLIB_TABLE, values, whereClause, null) > 0;
    }

// 获取全部AccessInfo信息

    public ArrayList<LotteryApp> getAccessInfos() {
        ArrayList<LotteryApp> list = new ArrayList<LotteryApp>();

        LotteryApp accessInfo = null;
        Cursor cursor =
            newsDB.query(DBHelper.ACCESSLIB_TABLE, AccessInfoColumn.PROJECTION, null, null, null, null, null);

        if (cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                accessInfo = new LotteryApp();

                accessInfo.setWeiboUserID(cursor.getString(AccessInfoColumn.USERID_COLUMN));
                accessInfo.setAccessToken(cursor.getString(AccessInfoColumn.ACCESS_TOKEN_COLUMN));
                accessInfo.setAccessSecret(cursor.getString(AccessInfoColumn.ACCESS_SECRET_COLUMN));
                list.add(accessInfo);
            }
        }

        cursor.close();
        cursor = null;

        return list;
    }

// 删除
    public boolean delete() {
        int ret = newsDB.delete(DBHelper.ACCESSLIB_TABLE, null, null);
        return ret > 0 ? true : false;
    }
}