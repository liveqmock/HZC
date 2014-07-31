package com.haozan.caipiao.util.cache;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class FileCache {

    public static String getSdcardPath() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return android.os.Environment.getExternalStorageDirectory() + "/" + "CaipiaoCache";

        return null;
    }

    public static File getPicFile(String url) {
        // 将url的hashCode作为缓存的文件名
        String filename = String.valueOf(url.hashCode());
        // Another possible solution
        // String filename = URLEncoder.encode(url);

        String path = getSdcardPath();
        if (path != null) {
            File cacheDir = new File(getSdcardPath());
            if (!cacheDir.exists())
                cacheDir.mkdirs();

            File f = new File(cacheDir, filename);
            return f;
        }
        return null;
    }

    public static String getPicFilePath(String url) {
        // 将url的hashCode作为缓存的文件名
        String filename = String.valueOf(url.hashCode());
        // Another possible solution
        // String filename = URLEncoder.encode(url);

        String path = getSdcardPath();
        if (path != null) {
            File cacheDir = new File(path);
            if (!cacheDir.exists())
                cacheDir.mkdirs();

            return path + "/" + filename;
        }
        else {
            return "";
        }
    }

    // 使用BitmapFactory.Options的inSampleSize参数来缩放
    public static Bitmap resizeImage(String path, int width, int height) {
        File file = new File(path);
        if (file.exists() == false) {
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;// 不加载bitmap到内存中
        BitmapFactory.decodeFile(path, options);
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inSampleSize = 1;

        if (outWidth != 0 && outHeight != 0 && width != 0 && height != 0) {
            int sampleSize = (outWidth / width + outHeight / height) / 2;
            options.inSampleSize = sampleSize;
        }

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

}
