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

public class UserTable
    extends SimpleTable {

    public static final String TABLE_NAME = "user";
    private static final String CREATE_TABLE = "create table if not exists " + TABLE_NAME + "(" +
        UserDbBean.ACCOUNT + " varchar(20) primary key not null, " + UserDbBean.PASSWORD +
        " varchar(40) not null," + UserDbBean.PASSWORD_STATUS + " int," + UserDbBean.LOGIN_STATUS + " int," +
        UserDbBean.LAST_LOGIN_TIME + " varchar(30)," + UserDbBean.SESSIONID + " varchar(30)," +
        UserDbBean.INTEGRITY + " int," + UserDbBean.REGISTER_TYPE + " char(20)," + UserDbBean.RESERVED_PHONE +
        " varchar(20)," + UserDbBean.NICKNAME + " varchar(24)," + UserDbBean.EMAIL + " varchar(56)," +
        UserDbBean.USER_ID + " varchar(24)," + UserDbBean.BALANCE + " double," + UserDbBean.WIN_BALANCE +
        " double," + UserDbBean.SCORE + " int," + UserDbBean.IS_BAND_BANKCARD + " int," +
        UserDbBean.SUBSCRIBE_SERVICE + " varchar(64))";
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
