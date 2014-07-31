package com.haozan.caipiao.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

import com.haozan.caipiao.task.GetLabelDataTask;

public class MySpanableSet {

    private String string = "#双色球# //@大乐透: @3D:@七乐彩: @东方6+1:@竞彩足球:@快乐十分:lau@qq.com "
        + "http://www.bukeinfo.com" + "转发// //@竞彩足球:@竞彩篮球:@胜负彩: Email:service@bukeinfo.com";
    private static final String STR_PATTERN_THEME = "#[^\\#|.]+#";
    private static final Pattern mPatternTheme = Pattern.compile(STR_PATTERN_THEME);

    private SpannableStringBuilder ssb;
    private Matcher matcher = null;
    private Context context;
    private TextView textView;
    private SharedPreferences packagetClassSave;
    private Editor packagetClassSaveEditor;
    private String packName = null;
    private String clsName = null;
    private String dlUrl = null;

    public MySpanableSet(Context context) {
        this.context = context;
        packagetClassSave = context.getSharedPreferences("name_p", 0);
        packagetClassSaveEditor = context.getSharedPreferences("name_p", 0).edit();
    }

    public SpannableStringBuilder getText(String string) {
        ssb = new SpannableStringBuilder();
        if (string != null)
            this.string = string;
        ssb.append(string);
        findTheme(this.string, 0);
        return ssb;
    }

    public void setTextLinkMovementNull() {
        textView.setMovementMethod(null);
    }

    // 从位置pos开始查找##关键字
    private void findTheme(String str, int pos) {
        if (str == null || str.length() == 0) {
            return;
        }
        matcher = mPatternTheme.matcher(str);
        if (matcher.find()) {
            final String find = matcher.group();
            final int start = str.indexOf(find) + pos;
            final int end = start + find.length();
            MyClickableSpan clickableSpan = new MyClickableSpan(string.substring(start, end));
            clickableSpan.setKind(2);
            ssb.setSpan(clickableSpan, start + 1, end - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            pos = end;
            findTheme(string.substring(end), pos);
        }
    }

    class URLSpanNotUnderLine
        extends URLSpan {
        public URLSpanNotUnderLine(String url) {
            super(url);
        }

        @Override
        public void onClick(View widget) {
            super.onClick(widget);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false); //
        }
    }

    class MyClickableSpan
        extends ClickableSpan {

        String str = null;
        int kind = 0;

        public MyClickableSpan(String str) {
            this.str = str;
        }

        public void setKind(int kind) {
            this.kind = kind;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(true);
        }

        @Override
        public void onClick(View widget) {
            if (!packagetClassSave.contains(str)) {
                GetLabelDataTask getLabelDataTask =
                    new GetLabelDataTask(context, str, packagetClassSaveEditor);
                getLabelDataTask.execute();
            }

            String[] nameArrayStrings = packagetClassSave.getString(str, "0|0|0").split("\\|");
            packName = nameArrayStrings[0];
            clsName = nameArrayStrings[1];
            if (nameArrayStrings.length == 3)
                dlUrl = nameArrayStrings[2];

            Intent intent = new Intent();
            ComponentName componetName = null;
            if (!packName.equals("0") && !clsName.equals("0")) {
                if (TextUtils.isEmpty(dlUrl)) {
                    Class<?> c = null;
                    try {
                        c = Class.forName(context.getPackageName() + "." + clsName);
                    }
                    catch (ClassNotFoundException e) {
                        ViewUtil.showTipsToast(context, "当前版本不支持，请升级到最新版");
                        e.printStackTrace();
                    }
                    intent.setClass(context, c);
                    context.startActivity(intent);
                }
                else {
                    if (checkGameIfExist(packName)) {
                        componetName = new ComponentName(packName, packName + "." + clsName);
                        intent.setComponent(componetName);
                        context.startActivity(intent);
                    }
                    else {
                        PluginUtils.showPluginDownloadDialog(context, "游戏", "确定下载该游戏吗?", dlUrl, false);
                    }
                }

            }
        }
    }

    protected boolean checkGameIfExist(String packageName) {
        boolean isExist = true;
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
        }
        catch (NameNotFoundException e) {
            isExist = false;
            e.printStackTrace();
        }
        return isExist;
    }

    private boolean isSDExist() {
        boolean sdCardExist =
            Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        return sdCardExist;
    }

}
