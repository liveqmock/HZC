/*******************************************************************************
 * Copyright 2013 Zhang Zhuo(william@TinyGameX.com). Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
package com.haozan.caipiao.db.api;

import java.util.HashSet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public abstract class BaseTable {

    public static final int ITEMS = 0;
    public static final int ITEM = 1;

    /**
     * 匹配路径，唯一。可设置多个。建意使用表名
     */
    protected String[] paths;

    protected HashSet<String> mReadableColumnsSet = new HashSet<String>();

    protected String table_name;
    /**
     * 不用手动设置，代码会自动设置
     */
    int mPathIndex;

    public BaseTable() {
        table_name = configTableName();
        paths = configPath();
        configReadableColumns(mReadableColumnsSet);
    }

    protected abstract String configTableName();

    protected abstract String[] configPath();

    protected void configReadableColumns(HashSet<String> set) {
    };

    public boolean alterTable(SQLiteDatabase db, Context context) {
        // formatter:off
        return dropTable(db, context) && createTable(db, context) && addIndex(db, context) &&
            addTrigger(db, context) && addData(db, context);
        // formatter:on
    }

    public boolean dropTable(SQLiteDatabase db, Context context) {
        if (table_name != null)
            try {
                db.execSQL("DROP TABLE IF EXISTS " + table_name);
            }
            catch (SQLException ex) {
                // #debug error
                throw ex;
            }
        return true;
    }

    public abstract boolean createTable(SQLiteDatabase db, Context context);

    public abstract boolean addIndex(SQLiteDatabase db, Context context);

    public abstract boolean addTrigger(SQLiteDatabase db, Context context);

    public boolean addData(SQLiteDatabase db, Context context) {
        return true;
    }

    public abstract int delete(SQLiteDatabase db, Context context, int pathIndex, Uri uri, String selection,
                               String[] selectionArgs);

    public abstract Uri insert(SQLiteDatabase db, Context context, int pathIndex, Uri uri,
                               ContentValues values);

    public abstract Cursor query(SQLiteDatabase db, Context context, int pathIndex, Uri uri,
                                 String[] projection, String selection, String[] selectionArgs,
                                 String sortOrder);

    public abstract int update(SQLiteDatabase db, Context context, int pathIndex, Uri uri,
                               ContentValues values, String selection, String[] selectionArgs);

    public abstract String getType(int pathIndex);
}
