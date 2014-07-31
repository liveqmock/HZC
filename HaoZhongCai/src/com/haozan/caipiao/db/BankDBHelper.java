package com.haozan.caipiao.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BankDBHelper
    extends SQLiteOpenHelper {
    public static final String BANKS_TABLE = "banks_table";
    public static final String DB_NAME = "banks.db";
    private static final int VERSION = 1;

    private String CREATE_BANKS_TABLE = "create table if not exists " + BANKS_TABLE + "(" + BanksDbBean.ID +
        " integer primary key, " + BanksDbBean.FIRSTCHAR + " TEXT," + BanksDbBean.CHINESENAME + " TEXT," +
        BanksDbBean.KEY + " TEXT," + BanksDbBean.DEPOSIT + " TEXT," + BanksDbBean.CREDIT + " TEXT," + BanksDbBean.LASTUSE +
        " TEXT," + BanksDbBean.RECHARGETYPE + " TEXT)";

    public BankDBHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, DB_NAME, factory, version);
    }

    public BankDBHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    public BankDBHelper(Context context, String name) {
        super(context, name, null, VERSION);
    }

    public BankDBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(CREATE_BANKS_TABLE);
// db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

}
