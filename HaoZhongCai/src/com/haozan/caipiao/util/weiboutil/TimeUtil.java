package com.haozan.caipiao.util.weiboutil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
    private static final String hhmmFormat = "HH:mm";
    private static final String MMddFormat = "MM月dd日 HH:mm";
    private static final String yyyyMMddFormat = "yyyy年MM月dd日";

    public static String getTimeStr(Date string) {
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(string);
        Date agoTime = cal.getTime();
        long mtime = now.getTime() - agoTime.getTime();
        String str = "";
        long stime = mtime / 1000;
        long minute = 60;
        long hour = 60 * 60;
        long day = 24 * 60 * 60;
// long weeks = 7 * 24 * 60 * 60;
        long weeks = 3 * 24 * 60 * 60;
        long months = 100 * 24 * 60 * 60;
        if (stime < minute) {
            long time_value = stime;
            if (time_value <= 0) {
                time_value = 1;
            }
            // str=time_value+"秒前";
            str = "刚刚";
// str = "1分钟前";
        }
        else if (stime >= minute && stime < hour) {
            long time_value = stime / minute;
            if (time_value <= 0) {
                time_value = 1;
            }
            str = time_value + "分钟前";
        }
        else if (stime >= hour && stime < day) {
            long time_value = stime / hour;
            if (time_value <= 0) {
                time_value = 1;
            }
            str = time_value + "小时前";
        }
        else if (stime >= day && stime < weeks) {
            long time_value = stime / day;
            if (time_value <= 0) {
                time_value = 1;
            }
            DateFormat df = new SimpleDateFormat(TimeUtil.hhmmFormat);
// str = time_value + "天前" + " " + df.format(date);
            if (time_value == 1) {
                str = "昨天" + " " + df.format(string);
            }
            else if (time_value == 2) {
                str = "前天" + " " + df.format(string);
            }

        }
        else if (stime >= weeks && stime < months) {
            DateFormat df = new SimpleDateFormat(TimeUtil.MMddFormat);
            str = df.format(string);
        }
        else {
            DateFormat df = new SimpleDateFormat(TimeUtil.yyyyMMddFormat);
            str = df.format(string);
        }
        return str;
    }

    void log(String msg) {
    }
}
