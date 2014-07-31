/*******************************************************************************
 * Copyright 2013 Zhang Zhuo(william@TinyGameX.com). Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
package com.haozan.caipiao.db;

/**
 * 用户表
 * 
 * @author peter_wang
 * @create-time 2013-10-24 上午11:04:29
 */

public class UserDbBean {
    public static final String ACCOUNT = "account";
    public static final String PASSWORD = "password";
    public static final String PASSWORD_STATUS = "password_status";// 是否记住密码，0代表不记住，1代表记住并自动登录
    public static final String LOGIN_STATUS = "login_status";// 默认为0,1代表上次登录的账号
    public static final String LAST_LOGIN_TIME = "last_login";
    public static final String SESSIONID = "sessionid";
    public static final String INTEGRITY = "integrity";
    public static final String REGISTER_TYPE = "type";
    public static final String RESERVED_PHONE = "reserved_phone";
    public static final String NICKNAME = "nickname";
    public static final String EMAIL = "email";
    public static final String USER_ID = "user_id";
    public static final String BALANCE = "balance";
    public static final String WIN_BALANCE = "win_balance";
    public static final String WITHDRAW_BALANCE = "withdraw_balance";
    public static final String SCORE = "score";
    public static final String IS_BAND_BANKCARD = "is_band";
    public static final String SUBSCRIBE_SERVICE = "service";
}
