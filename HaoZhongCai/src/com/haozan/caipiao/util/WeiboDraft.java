package com.haozan.caipiao.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;

public class WeiboDraft {
    private Context mContext;
    public static final String DRAFT = "draft";

    public WeiboDraft(Context context) {
        mContext = context;
    }

// 保存草稿
    public void save(String text) {
        try {
            FileOutputStream outputStream = mContext.openFileOutput(DRAFT, Context.MODE_PRIVATE);
            outputStream.write(text.getBytes());
            outputStream.flush();
            outputStream.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

// 取出草稿,若草稿不存在，返回null

    public String get() {
        try {
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            FileInputStream inputStream = mContext.openFileInput(DRAFT);
            int len = 0;
            byte[] b = new byte[128];
            while ((len = inputStream.read(b)) != -1) {
                arrayOutputStream.write(b, 0, len);
            }
            return arrayOutputStream.toString();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

// 删除草稿

    public void delete() {
        try {
            FileOutputStream outputStream = mContext.openFileOutput(DRAFT, Context.MODE_PRIVATE);
            outputStream.write("".getBytes());
            outputStream.flush();
            outputStream.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
