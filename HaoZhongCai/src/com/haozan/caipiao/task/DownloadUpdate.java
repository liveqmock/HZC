package com.haozan.caipiao.task;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class DownloadUpdate {

    public void install(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File("/sdcard/zhangzhongcai.apk")),
                              "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
