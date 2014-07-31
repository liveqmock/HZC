package com.haozan.caipiao.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class WeiboSQLHelper
    extends SQLiteOpenHelper {
    private static final int VERSION = 1;

    private String create_home_table = "create table " + WeiboHallDB.HOME_TABLE + "(" + WeiboHallDB.WEIBOID +
        " TEXT," + WeiboHallDB.CONTENT + " TEXT," + WeiboHallDB.PIC + " TEXT," + WeiboHallDB.TIME + " TEXT," +
        WeiboHallDB.TITLE + " TEXT," + WeiboHallDB.SOURCE + " TEXT," + WeiboHallDB.REPLYCOUNT + " TEXT," +
        WeiboHallDB.RETWEETCOUNT + " TEXT," + WeiboHallDB.USERID + " TEXT," + WeiboHallDB.NAME + " TEXT," +
        WeiboHallDB.AVATAR + " TEXT," + WeiboHallDB.TYPE + " TEXT," + WeiboHallDB.PREVIEW + " TEXT," +
        WeiboHallDB.ATTACHID + " TEXT," + WeiboHallDB.WEIBOTYPE + " TEXT)";

    public WeiboSQLHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public WeiboSQLHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    public WeiboSQLHelper(Context context, String name) {
        super(context, name, null, VERSION);
    }

    public WeiboSQLHelper(Context context) {
        super(context, WeiboHallDB.SINAWEIBO, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(create_home_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
