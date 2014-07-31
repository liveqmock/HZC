package com.haozan.caipiao.util.weiboutil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.haozan.caipiao.activity.BasicActivity;

/**
 * 类说明： 实现一些基础方法
 * 
 * @author @Cundong
 * @weibo http://weibo.com/liucundong
 * @blog http://www.liucundong.com
 * @date Apr 29, 2011 2:50:48 PM
 * @version 1.0
 */
public class WeiboBaseActivity
    extends BasicActivity {
    protected Activity instance;
    protected Context mContext;

    public final String SDCARD_MNT = "/mnt/sdcard";
    public final String SDCARD = "/sdcard";

    /**
     * 通过uri获取文件的绝对路径
     * 
     * @param uri
     * @return
     */
    public String getAbsoluteImagePath(Uri uri) {
        String imagePath = "";
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, proj, // Which columns to return
                                     null, // WHERE clause; which rows to return (all rows)
                                     null, // WHERE clause selection arguments (none)
                                     null); // Order-by clause (ascending by name)

        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                imagePath = cursor.getString(column_index);
            }
        }

        return imagePath;
    }

    /**
     * 获取图片缩略图 只有Android2.1以上版本支持
     * 
     * @param imgName
     * @param kind MediaStore.Images.Thumbnails.MICRO_KIND
     * @return
     */
    public Bitmap loadImgThumbnail(String imgName, int kind) {
        Bitmap bitmap = null;

        String[] proj = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME};

        Cursor cursor =
            managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj,
                         MediaStore.Images.Media.DISPLAY_NAME + "='" + imgName + "'", null, null);

        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            ContentResolver crThumb = getContentResolver();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            //getThumbnail要2.1以上才支持
//            bitmap = MediaStore.Images.Thumbnails.getThumbnail(crThumb, cursor.getInt(0), kind, options);
        }
        return bitmap;
    }

    /**
     * 获取SD卡中最新图片路径
     * 
     * @return
     */
    public String getLatestImage() {
        String latestImage = null;
        String[] items = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
        Cursor cursor =
            managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, items, null, null,
                         MediaStore.Images.Media._ID + " desc");

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                latestImage = cursor.getString(1);
                break;
            }
        }

        return latestImage;
    }

    /**
     * @param bitmap 要保存的图片
     * @param filePath 保存到的路径
     * @return
     */
    public boolean saveLocalImg(Bitmap bitmap, String filePath) {
        FileOutputStream fos = null;
        try {
            File file = new File(filePath);
            fos = new FileOutputStream(file);
            Bitmap b = bitmap;
            b.compress(CompressFormat.JPEG, 80, fos);
            fos.flush();
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        finally {
            if (fos != null) {
                try {
                    fos.close();
                }
                catch (IOException e) {
                    return false;
                }
            }
        }
    }

// 获取相册最新照片
    public static String getRealPathFromURI(Uri uri, ContentResolver resolver) {

        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = resolver.query(uri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String str = cursor.getString(column_index);
        cursor.close();

        return str;

    }
}