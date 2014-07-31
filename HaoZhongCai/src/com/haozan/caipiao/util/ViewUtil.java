package com.haozan.caipiao.util;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.view.Gravity;
import android.widget.Toast;

import com.haozan.caipiao.activity.Feedback;
import com.haozan.caipiao.widget.CustomDialog;

public class ViewUtil {
    private static Toast mToast;

    public static void showTipsToast(Context context, String inf) {
        if (mToast == null) {
            Context application = context.getApplicationContext();
            mToast = Toast.makeText(application, null, Toast.LENGTH_SHORT);
        }

        mToast.setGravity(Gravity.BOTTOM, 0, 80);
        mToast.setText(inf);
        mToast.show();
    }

    public static void showTipsToast(Context context, String inf, int flag) {
        if (mToast == null) {
            Context application = context.getApplicationContext();
            mToast = Toast.makeText(application, null, Toast.LENGTH_SHORT);
        }

        mToast.setGravity(Gravity.CENTER, 0, 80);
        mToast.setText(inf);
        mToast.show();
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void showFailDialog(final Context context, String error) {
        CustomDialog dlgFail = null;
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
        customBuilder.setWarning().setMessage(error).setPositiveButton("联系客服",
                                                                       new DialogInterface.OnClickListener() {
                                                                           public void onClick(DialogInterface dialog,
                                                                                               int which) {
                                                                               dialog.dismiss();
                                                                               Intent intent = new Intent();
                                                                               intent.setClass(context,
                                                                                               Feedback.class);
                                                                               context.startActivity(intent);
                                                                               ((Activity) context).finish();
                                                                           }
                                                                       }).setNegativeButton("取  消",
                                                                                            new DialogInterface.OnClickListener() {
                                                                                                public void onClick(DialogInterface dialog,
                                                                                                                    int which) {
                                                                                                    dialog.dismiss();
                                                                                                }
                                                                                            });
        dlgFail = customBuilder.create();
        dlgFail.show();
    }
}