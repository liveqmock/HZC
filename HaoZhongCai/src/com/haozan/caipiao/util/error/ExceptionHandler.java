package com.haozan.caipiao.util.error;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;

public class ExceptionHandler {

    public static String TAG = "ExceptionHandler";

    private static String[] stackTraceFileList = null;

    public static void backupInf(final Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi;
            // Version
            pi = pm.getPackageInfo(context.getPackageName(), 0);
            G.APP_VERSION = pi.versionName;
            // Package name
            G.APP_PACKAGE = pi.packageName;
            File sdCard = Environment.getExternalStorageDirectory();
            String rootPath = sdCard.getPath() + "/" + G.PATH_NAME + "/";
            File file = new File(rootPath);
            if (!file.exists()) {
                file.mkdir();
            }
            // Files dir for storing the stack traces
            G.FILES_PATH = rootPath;
            // Device model
            G.PHONE_MODEL = android.os.Build.MODEL;
            // Android version
            G.ANDROID_VERSION = android.os.Build.VERSION.RELEASE;
            G.CLASS_NAME = context.getClass().getName();
        }
        catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Register handler for unhandled exceptions.
     * 
     * @param context
     */
    public static boolean register(final Context context) {
        backupInf(context);

        boolean stackTracesFound = false;
        // We'll return true if any stack traces were found
// if ( searchForStackTraces().length > 0 ) {
// stackTracesFound = true;
// }

        new Thread() {
            @Override
            public void run() {
                // First of all transmit any stack traces that may be lying around
// submitStackTraces();
                UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();
                if (currentHandler != null) {
                }
                // don't register again if already registered
                if (!(currentHandler instanceof DefaultExceptionHandler)) {
                    // Register default exceptions handler
                    Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(context,
                                                                                          currentHandler));
                }
            }
        }.start();

        return stackTracesFound;
    }

    /**
     * Register handler for unhandled exceptions.
     * 
     * @param context
     * @param Url
     */
    public static void register(Context context, String url) {
        // Use custom URL
        G.URL = url;
        // Call the default register method
        register(context);
    }

    /**
     * Search for stack trace files.
     * 
     * @return
     */
    private static String[] searchForStackTraces() {
        if (stackTraceFileList != null) {
            return stackTraceFileList;
        }
        File dir = new File(G.FILES_PATH + "/");
        // Try to create the files folder if it doesn't exist
        dir.mkdir();
        // Filter for ".stacktrace" files
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".stacktrace");
            }
        };
        return (stackTraceFileList = dir.list(filter));
    }

    /**
     * Look into the files folder to see if there are any "*.stacktrace" files. If any are present, submit
     * them to the trace server.
     */
    public static void submitStackTraces() {
        try {
            String[] list = searchForStackTraces();
            if (list != null && list.length > 0) {
                for (int i = 0; i < list.length; i++) {
                    String filePath = G.FILES_PATH + "/" + list[i];
                    // Extract the version from the filename: "packagename-version-...."
                    String version = list[i].split("-")[0];
                    // Read contents of stacktrace
                    StringBuilder contents = new StringBuilder();
                    BufferedReader input = new BufferedReader(new FileReader(filePath));
                    String line = null;
                    String androidVersion = null;
                    String phoneModel = null;
                    while ((line = input.readLine()) != null) {
                        if (androidVersion == null) {
                            androidVersion = line;
                            continue;
                        }
                        else if (phoneModel == null) {
                            phoneModel = line;
                            continue;
                        }
                        contents.append(line);
                        contents.append(System.getProperty("line.separator"));
                    }
                    input.close();
                    String stacktrace;
                    stacktrace = contents.toString();
                    // Transmit stack trace with POST request
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(G.URL);
                    List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                    nvps.add(new BasicNameValuePair("package_name", G.APP_PACKAGE));
                    nvps.add(new BasicNameValuePair("package_version", version));
                    nvps.add(new BasicNameValuePair("phone_model", phoneModel));
                    nvps.add(new BasicNameValuePair("android_version", androidVersion));
                    nvps.add(new BasicNameValuePair("stacktrace", stacktrace));
                    httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
                    // We don't care about the response, so we just hope it went well and on with it
                    httpClient.execute(httpPost);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                String[] list = searchForStackTraces();
                for (int i = 0; i < list.length; i++) {
                    File file = new File(G.FILES_PATH + "/" + list[i]);
                    file.delete();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
