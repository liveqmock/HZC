/*******************************************************************************
 * Copyright 2013 Zhang Zhuo(william@TinyGameX.com). Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
package com.haozan.caipiao.db;

import com.haozan.caipiao.db.api.BaseProvider;
import com.haozan.caipiao.db.api.BaseProviderConfig;

/**
 * 数据库创建表等信息类
 * 
 * @author peter_wang
 * @create-time 2013-10-24 上午11:04:05
 */
public class WholeProvider
    extends BaseProvider {

    @Override
    protected void customsTable(BaseProviderConfig config) {
        try {
            config.addNewTable(UserTable.class);
            config.addNewTable(BankTable.class);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getAuthority() {
        return BaseProviderConfig.DB_AUTHORITY;
    }

}
