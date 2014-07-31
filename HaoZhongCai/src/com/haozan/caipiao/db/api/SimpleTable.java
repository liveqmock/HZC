package com.haozan.caipiao.db.api;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public abstract class SimpleTable
    extends BaseTable {
    public static final String SQL_INSERT_OR_REPLACE = "__sql_insert_or_replace__";

    @Override
    protected String[] configPath() {
        return new String[] {table_name, table_name + "/#"};
    }

    @Override
    public Uri insert(SQLiteDatabase db, Context context, int pathIndex, Uri uri, ContentValues values) {
        // 判断是否更新时没有则插入
        boolean replace = false;
        if (values.containsKey(SimpleTable.SQL_INSERT_OR_REPLACE)) {
            replace = values.getAsBoolean(SQL_INSERT_OR_REPLACE);

            // Clone the values object, so we don't modify the original.
            // This is not strictly necessary, but depends on your needs
            values = new ContentValues(values);

            // Remove the key, so we don't pass that on to db.insert() or db.replace()
            values.remove(SQL_INSERT_OR_REPLACE);
        }

        long rowId;
        if (replace) {
            rowId = db.replace(table_name, null, values);
        }
        else {
            rowId = db.insert(table_name, null, values);
        }

        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(uri, rowId);
            context.getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        return null;
    }

    @Override
    public Cursor query(SQLiteDatabase db, Context context, int pathIndex, Uri uri, String[] projection,
                        String selection, String[] selectionArgs, String sortOrder) {
        return db.query(table_name, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public int update(SQLiteDatabase db, Context context, int pathIndex, Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {
        int rowId = db.update(table_name, values, selection, selectionArgs);
        if (rowId > 0) {
            context.getContentResolver().notifyChange(uri, null);
        }
        return rowId;
    }

    @Override
    public int delete(SQLiteDatabase db, Context context, int pathIndex, Uri uri, String selection,
                      String[] selectionArgs) {
        int rowId = db.delete(table_name, selection, selectionArgs);
        if (rowId > 0) {
            context.getContentResolver().notifyChange(uri, null);
        }

        return rowId;
    }

    @Override
    public String getType(int pathIndex) {
        switch (pathIndex) {
            case 0:
                return "vnd.android.cursor.dir/" + table_name;
            case 1:
                return "vnd.android.cursor.item/" + table_name;
            default:
                return null;
        }
    }

    @Override
    public boolean dropTable(SQLiteDatabase db, Context context) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + table_name);
            return true;
        }
        catch (SQLException ex) {
            throw ex;
        }
    }

    @Override
    public boolean addIndex(SQLiteDatabase db, Context context) {
        return true;
    }

    @Override
    public boolean addTrigger(SQLiteDatabase db, Context context) {
        return true;
    }

}
