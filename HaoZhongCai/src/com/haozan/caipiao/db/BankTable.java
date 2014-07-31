/*******************************************************************************
 * Copyright 2013 Zhang Zhuo(william@TinyGameX.com). Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
package com.haozan.caipiao.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.haozan.caipiao.db.api.BaseProviderConfig;
import com.haozan.caipiao.db.api.SimpleTable;

public class BankTable
    extends SimpleTable {

    public static final String TABLE_NAME = "banks_table";
    private static final String CREATE_TABLE = "create table if not exists " + TABLE_NAME + "(" +
        BankDbBean.CHINESENAME + " char(40) primary key, " + BankDbBean.FIRSTCHAR + " char(10)," +
        BankDbBean.KEY + " char(10)," + BankDbBean.DEPOSIT + " char(20)," + BankDbBean.CREDIT + " char(20)," +
        BankDbBean.SHORTHAND + " char(20))";
    public static final Uri CONTENT_URI = Uri.parse(BaseProviderConfig.CONTENT_URI + "/" + TABLE_NAME);

    @Override
    protected String configTableName() {
        return TABLE_NAME;
    }

    @Override
    public boolean createTable(SQLiteDatabase db, Context context) {
        db.execSQL(CREATE_TABLE);
        return true;
    }
}
