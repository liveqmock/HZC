package com.haozan.caipiao.util.error;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Random;

/**
 * 异常处理
 * 
 * @author peter_wang
 * @create-time 2013-11-1 下午3:37:00
 */
public class LocalExceptionHandler {

    public static final void exportExceptionInf(Throwable e) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        try {
            // Random number to avoid duplicate files
            Random generator = new Random();
            int random = generator.nextInt(99999);
            // Embed version in stacktrace filename
            String filename = G.APP_VERSION + "-" + Integer.toString(random);
            // Write the stacktrace to disk
            BufferedWriter bos =
                new BufferedWriter(new FileWriter(G.FILES_PATH + "/" + filename + ".stacktrace"));
            bos.write(G.ANDROID_VERSION + "\n");
            bos.write(G.PHONE_MODEL + "\n");
            bos.write(G.CLASS_NAME + "\n");
            bos.write(result.toString());
            bos.flush();
            // Close up everything
            bos.close();
        }
        catch (Exception ebos) {
            // Nothing much we can do about this - the game is over
            ebos.printStackTrace();
        }
    }
}
