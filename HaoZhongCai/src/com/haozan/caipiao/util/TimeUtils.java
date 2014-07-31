package com.haozan.caipiao.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/**
 * 时间处理类
 *
 * @author peter_wang
 * @create-time 2013-10-13 下午8:30:25
 */
public class TimeUtils {
    public static String calendarFormat(Calendar calendar) {
        if (calendar == null)
            return null;
        Date date = calendar.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }

    public static int getDate(String date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(df.parse(date));
        }
        catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
        return c.get(Calendar.DAY_OF_WEEK);
    }

    public static long getDateMillisecond(String date) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long millis = 0;
        try {
            Date date1 = format1.parse(date);
            millis = date1.getTime();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return millis;
    }

    public static String showSportsEndTime(long millis) {
        SimpleDateFormat format1 = new SimpleDateFormat("MM-dd HH:mm");
        return format1.format(millis);
    }

    /**
     * 获取正常的倒计时时间显示
     * 
     * @param millis 时间总和
     * @return 显示的倒计时格式
     */
    public static String getNormalCountDownTime(long millis) {
        String betLastTime;
        int seconds = (int) millis / 1000;
        int minutes = seconds / 60;
        int hours = minutes / 60;
        int day = hours / 24;
        seconds %= 60;
        minutes %= 60;
        hours %= 24;
        day %= 24;
        betLastTime = day + "天" + hours + "时";
        if (minutes < 10)
            betLastTime += "0";
        betLastTime += minutes + "分";
        if (seconds < 10)
            betLastTime += "0";
        betLastTime += seconds + "秒";
        return betLastTime;
    }

    /**
     * 获取简单的倒计时时间显示
     * 
     * @param millis 时间总和
     * @return 显示的倒计时格式
     */
    public static String getSimpleCountDownTime(long millis) {
        String betLastTime = "";
        int seconds = (int) millis / 1000;
        int minutes = seconds / 60;
        int hours = minutes / 60;
        int day = hours / 24;
        seconds %= 60;
        minutes %= 60;
        hours %= 24;
        day %= 24;
        if (day != 0) {
            betLastTime = day + "天" + hours + "时";
            if (minutes < 10)
                betLastTime += "0";
            betLastTime += minutes + "分";
            if (seconds < 10)
                betLastTime += "0";
            betLastTime += seconds + "秒";
        }
        else {
            if (hours != 0) {
                betLastTime = hours + "时";
                if (minutes < 10)
                    betLastTime += "0";
                betLastTime += minutes + "分";
                if (seconds < 10)
                    betLastTime += "0";
                betLastTime += seconds + "秒";
            }
            else {
                if (minutes != 0) {
                    if (minutes < 10)
                        betLastTime += "0";
                    betLastTime += minutes + "分";
                    if (seconds < 10)
                        betLastTime += "0";
                    betLastTime += seconds + "秒";
                }
                else {
                    if (seconds < 10)
                        betLastTime = "0";
                    betLastTime += seconds + "秒";
                }
            }
        }
        return betLastTime;
    }

    /**
     * 获取人性化的倒计时时间显示
     * 
     * @param millis 时间总和
     * @return 显示的倒计时格式
     */
    private static StringBuilder betLastTime;

    public static String getCountDownTime(long millis) {
        if (betLastTime == null) {
            betLastTime = new StringBuilder();
        }
        betLastTime.delete(0, betLastTime.length());
        int seconds = (int) millis / 1000;
        int minutes = seconds / 60;
        int hours = minutes / 60;
        int day = hours / 24;
        seconds %= 60;
        minutes %= 60;
        hours %= 24;
        day %= 24;
        if (day != 0) {
            betLastTime.append(day + "天" + hours + "小时");
        }
        else {
            if (hours != 0) {
                betLastTime.append(hours + "小时");
                if (minutes < 10)
                    betLastTime.append("0");
                betLastTime.append(minutes + "分");
            }
            else {
                if (minutes < 10)
                    betLastTime.append("0");
                betLastTime.append(minutes + "分");
                if (seconds < 10)
                    betLastTime.append("0");
                betLastTime.append(seconds + "秒");
            }
        }
        return betLastTime.toString();
    }

    /**
     * 显示正规的时间格式
     * 
     * @param adateStrteStr
     * @param format 原始的格式，如"yyyy-MM-dd HH:mm:ss"
     * @return 返回对应的时间格式字符串
     */
    public static String stringDate(String adateStrteStr, String format) {
        Date date = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            date = simpleDateFormat.parse(adateStrteStr);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm");
            return dateFormat.format(date);
        }
        catch (Exception ex) {

        }
        return null;
    }

    /**
     * 转换时间格式
     * 
     * @param adateStrteStr 原始时间字符串
     * @param oldFormat 原始时间格式
     * @param format 转换成的时间格式
     * @return
     */
    public static String convertDate(String adateStrteStr, String oldFormat, String format) {
        Date date = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(oldFormat);
            date = simpleDateFormat.parse(adateStrteStr);
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            return dateFormat.format(date);
        }
        catch (Exception ex) {

        }
        return null;
    }

    /**
     * string格式的日期转换为Data类型
     * 
     * @param date
     * @return
     */
    public static Date stringConvertToDate(String date) {
        Date toDate = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            toDate = dateFormat.parse(date);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return toDate;
    }

    /**
     * 返回相应日期是周几
     * 
     * @param date
     * @return
     */
    public static String dayOfWeek(Date date) {
        String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }
}
