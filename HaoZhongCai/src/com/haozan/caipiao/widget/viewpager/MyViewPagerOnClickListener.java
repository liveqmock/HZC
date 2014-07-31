package com.haozan.caipiao.widget.viewpager;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;

public class MyViewPagerOnClickListener
    implements OnClickListener {
    public int index = 0;
    public ViewPager myViewPager;

    public MyViewPagerOnClickListener(int i, ViewPager viewPager) {
        index = i;
        myViewPager = viewPager;
    }

    public void onClick(View v) {
        myViewPager.setCurrentItem(index);
    }

}
