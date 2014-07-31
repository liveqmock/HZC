package com.haozan.caipiao.util;

import java.io.File;

import org.apache.http.HttpEntity;

import android.content.Context;
import android.text.TextUtils;

import com.haozan.caipiao.netbasic.AndroidHttpClient;

public class UpdateUtil {

    public static double SDCardLeftSize() {
        // 取得SDCard当前的状态
        String sDcString = android.os.Environment.getExternalStorageState();
        if (sDcString.equals(android.os.Environment.MEDIA_MOUNTED)) {
            // 取得sdcard文件路径
            File pathFile = android.os.Environment.getExternalStorageDirectory();
            android.os.StatFs statfs = new android.os.StatFs(pathFile.getPath());
            // 获取SDCard上每个block的SIZE
            long nBlocSize = statfs.getBlockSize();
            // 获取可供程序使用的Block的数量
            double nAvailaBlock = statfs.getAvailableBlocks();
            // 计算系统可以安装的容量大小MB
            double nSDTotalSize = nAvailaBlock * nBlocSize / 1024 / 1024;
            return nSDTotalSize;
        }
        else
            return 0;
    }

    public static Boolean SDCardExist(Context context) {
        String sDcString = android.os.Environment.getExternalStorageState();
        if (sDcString.equals(android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        }
        else {
            String inf = "请插入SD卡";
            ViewUtil.showTipsToast(context, inf);
            return false;
        }
    }

    public static long getUpdateSize(Context context, String url) {
        if (TextUtils.isEmpty(url)) {
            return -1;
        }

        AndroidHttpClient httpClient = new AndroidHttpClient(context);
        HttpEntity client = httpClient.getHttpEntity(url);
        if (client != null) {
            long length = client.getContentLength();
            return length;
        }
        else
            return -1;
    }
}
