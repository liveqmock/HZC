package com.haozan.caipiao.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

public class TextUtil {
    public static Spanned highLight(String str) {
        Pattern pattern = Pattern.compile("@[^\\s:：]+[:：\\s]");
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            String m = matcher.group();
            str = str.replace(m, "<font color=Navy>" + m + "</font>");
        }
        return Html.fromHtml(str);
    }

    public static SpannableString light(CharSequence text) {
        SpannableString spannableString = new SpannableString(text);
        Pattern pattern = Pattern.compile("@[^\\s:：]+[:：\\s]");
        Matcher matcher = pattern.matcher(spannableString);
        while (matcher.find()) {
            spannableString.setSpan(new ForegroundColorSpan(Color.CYAN), matcher.start(), matcher.end(),
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    public static String formatName(String url) {
        if (url == null || "".equals(url)) {
            return url;
        }
        int start = url.lastIndexOf("/");
        int end = url.lastIndexOf(".");
        if (start == -1 || end == -1) {
            return url;
        }
        return url.substring(start + 1, end);
    }

    public static String formatSource(String name) {
        if (name == null || "".equals(name)) {
            return name;
        }
        int start = name.indexOf(">");
        int end = name.lastIndexOf("<");
        if (start == -1 || end == -1) {
            return name;
        }
        return name.substring(start + 1, end);
    }

    public static SpannableString formatImage(CharSequence text, Context context) {
        SpannableString spannableString = new SpannableString(text);
        Pattern pattern = Pattern.compile("\\[[^0-9]{1,4}\\]");
        Matcher matcher = pattern.matcher(spannableString);
        while (matcher.find()) {
            String faceName = matcher.group();
            String key = faceName.substring(1, faceName.length() - 1);
// if (Face.getfaces(context).containsKey(key)) {
// spannableString.setSpan(new ImageSpan(context, Face.getfaces(context).get(key)),
// matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
// }
        }
        return spannableString;
    }

    public static SpannableString formatContent(CharSequence text, Context context) {
        SpannableString spannableString = new SpannableString(text);
        Pattern pattern = Pattern.compile("@[^\\s:：]+[:：\\s]|\\[[^0-9]{1,4}\\]");
        Matcher matcher = pattern.matcher(spannableString);
        while (matcher.find()) {
            String match = matcher.group();
            if (match.startsWith("@")) {
                spannableString.setSpan(new ForegroundColorSpan(0xff0077ff), matcher.start(), matcher.end(),
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            else if (match.startsWith("[")) {
                String key = match.substring(1, match.length() - 1);
// if (Face.getfaces(context).containsKey(key)) {
// spannableString.setSpan(new ImageSpan(context, Face.getfaces(context).get(key)),
// matcher.start(), matcher.end(),
// Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
// }
            }
        }
        return spannableString;
    }

    static void Log(String msg) {
    }

    /**
     * 设置文字粗体显示
     * 
     * @param tv
     */
    public static void setTextBold(TextView tv) {
        TextPaint tp = tv.getPaint();
        tp.setFakeBoldText(true);
    }

}
