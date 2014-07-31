package com.haozan.caipiao.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class ViewPagerAdapter
    extends PagerAdapter {
    private List<View> mListViews;

    private String[] tabContent;

    public ViewPagerAdapter(List<View> mListViews) {
        this.mListViews = mListViews;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mListViews.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mListViews.get(position), 0);
        return mListViews.get(position);
    }

    @Override
    public int getCount() {
        return mListViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setTabContent(String[] tabContent) {
        if (tabContent.length != mListViews.size()) {
            return;
        }
        this.tabContent = tabContent;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (tabContent != null) {
            return tabContent[position];
        }
        return super.getPageTitle(position);
    }
}
