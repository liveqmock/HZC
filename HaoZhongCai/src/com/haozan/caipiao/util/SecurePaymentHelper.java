package com.haozan.caipiao.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

import com.shengpay.smk.ICheckoutCallback;
import com.shengpay.smk.ISFTCheckout;

public class SecurePaymentHelper {
    static String TAG = "MobileSecurePayer";

    Integer lock = 0;
    ISFTCheckout sftCheckout = null;
    boolean mbPaying = false;

    Activity mActivity = null;

    private ServiceConnection mSDPayConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            synchronized (lock) {
                sftCheckout = ISFTCheckout.Stub.asInterface(service);
                lock.notify();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            sftCheckout = null;
        }
    };

    public boolean pay(final String strOrderInfo, final Handler callback, final int myWhat,
                       final Activity activity) {
        if (mbPaying)
            return false;
        mbPaying = true;

        mActivity = activity;

        // bind the service.
        if (sftCheckout == null) {
            mActivity.getApplicationContext().bindService(new Intent(ISFTCheckout.class.getName()),
                                                          mSDPayConnection, Context.BIND_AUTO_CREATE);
        }

        new Thread(new Runnable() {
            public void run() {
                Message msg = new Message();
                try {
                    synchronized (lock) {
                        if (sftCheckout == null)
                            lock.wait();
                    }

                    // register a Callback for the service.
                    sftCheckout.registerCallback(mCallback);

                    // call the MobileSecurePay service.
                    String strRet = sftCheckout.payOrder(strOrderInfo);

                    // unregister the Callback for the service.
                    sftCheckout.unregisterCallback(mCallback);

                    mbPaying = false;

                    msg.what = myWhat;
                    msg.obj = strRet;
                    callback.sendMessage(msg);
                }
                catch (Exception e) {
                    e.printStackTrace();

                    msg.what = myWhat;
                    msg.obj = e.toString();
                    callback.sendMessage(msg);
                }
                finally {
                    mActivity.getApplicationContext().unbindService(mSDPayConnection);
                }
            }
        }).start();

        return true;
    }

    private ICheckoutCallback mCallback = new ICheckoutCallback.Stub() {

        @Override
        public void startActivity(String packageName, String className, int pid, Bundle bundle)
            throws RemoteException {
            Intent intent = new Intent(Intent.ACTION_MAIN, null);

            if (bundle == null)
                bundle = new Bundle();

            try {
                bundle.putInt("CallingPid", pid);
                intent.putExtras(bundle);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            intent.setClassName(packageName, className);
            mActivity.startActivity(intent);
        }
    };
}