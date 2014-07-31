package com.haozan.caipiao.activity.serviceweibo;

import android.os.Bundle;

public interface QQDialogListener {

    /**
     * Called when a dialog completes.
     * 
     * Executed by the thread that initiated the dialog.
     * 
     * @param values
     *            Key-value string pairs extracted from the response.
     */
    public void onComplete(Bundle values);
    public void onComplete(String values);

    /**
     * Called when a Weibo responds to a dialog with an error.
     * 
     * Executed by the thread that initiated the dialog.
     * 
     */
    //TODO
//    public void onWeiboException(WeiboDialogListener e);

    /**
     * Called when a dialog has an error.
     * 
     * Executed by the thread that initiated the dialog.
     * 
     */
//    public void onError(DialogError e);

    /**
     * Called when a dialog is canceled by the user.
     * 
     * Executed by the thread that initiated the dialog.
     * 
     */
    public void onCancel();

}
