package com.haozan.caipiao.util.error;

public class G {
    // This must be set by the application - it used to automatically
    // transmit exceptions to the trace server
    public static String FILES_PATH = null;
    public static String APP_VERSION = "unknown";
    public static String APP_PACKAGE = "unknown";
    public static String PHONE_MODEL = "unknown";
    public static String ANDROID_VERSION = "unknown";
    public static String CLASS_NAME = "unknown";
    // Where are the stack traces posted?
    public static String URL = "http://trace.nullwire.com/collect/";
    public static String TraceVersion = "0.3.0";

    // 错误报告放在sdcard的目录名
    public static String PATH_NAME = "zzc_bug_notes";
}
