package com.haozan.caipiao.util.anamation;

import android.app.Activity;

public class AnimationModel {
    private Activity context;

    public AnimationModel(Activity context) {
        this.context = context;
    }

    /**
     * call overridePendingTransition() on the supplied Activity.
     * 
     * @param a
     * @param b
     */
    public void overridePendingTransition(int a, int b) {
//        context.overridePendingTransition(a, b);
    }

}