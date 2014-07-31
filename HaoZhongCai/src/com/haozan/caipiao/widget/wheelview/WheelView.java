package com.haozan.caipiao.widget.wheelview;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import com.haozan.caipiao.R;
import com.haozan.caipiao.widget.wheelview.adapter.WheelAdapterBase;

public class WheelView
    extends View {

    private static final int[] SHADOWS_COLORS = new int[] {0xFF111111, 0x00AAAAAA, 0x00AAAAAA};

    private static final int ITEM_OFFSET_PERCENT = 10;

    private static final int PADDING = 10;

    private static final int DEF_VISIBLE_ITEMS = 5;

    private int currentItem = 0;

    private int visibleItems = DEF_VISIBLE_ITEMS;

    private int itemHeight = 0;

    private String normalTx = "";

    private Drawable centerDrawable;

    private GradientDrawable topShadow;
    private GradientDrawable bottomShadow;

    private WheelScroller scroller;
    private boolean isScrollingPerformed;
    private int scrollingOffset;

    boolean isCyclic = false;

    private LinearLayout itemsLayout;

    private int firstItem;

    private WheelAdapterBase viewAdapter;

    private WheelRecycle recycle = new WheelRecycle(this);

    private List<OnWheelChangedListener> changingListeners = new LinkedList<OnWheelChangedListener>();
    private List<OnWheelScrollListener> scrollingListeners = new LinkedList<OnWheelScrollListener>();
    private List<OnWheelClickedListener> clickingListeners = new LinkedList<OnWheelClickedListener>();

    private boolean isClicked = false;

    public WheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initData(context);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(context);
    }

    public WheelView(Context context) {
        super(context);
        initData(context);
    }

    private void initData(Context context) {
        scroller = new WheelScroller(getContext(), scrollingListener);
    }

    public String getNormalTx() {
        return normalTx;
    }

    public void setNormalTx(String normalTx) {
        this.normalTx = normalTx;
    }

    public void setWheelClicked(boolean isClicked) {
        this.isClicked = isClicked;
    }

    WheelScroller.ScrollingListener scrollingListener = new WheelScroller.ScrollingListener() {
        public void onStarted() {
            isScrollingPerformed = true;
            notifyScrollingListenersAboutStart();
        }

        public void onScroll(int distance) {
            doScroll(distance);

            int height = getHeight();
            if (scrollingOffset > height) {
                scrollingOffset = height;
                scroller.stopScrolling();
            }
            else if (scrollingOffset < -height) {
                scrollingOffset = -height;
                scroller.stopScrolling();
            }
        }

        public void onFinished() {
            if (isScrollingPerformed) {
                notifyScrollingListenersAboutEnd();
                isScrollingPerformed = false;
            }

            scrollingOffset = 0;
            invalidate();
        }

        public void onJustify() {
            if (Math.abs(scrollingOffset) > WheelScroller.MIN_DELTA_FOR_SCROLLING) {
                scroller.scroll(scrollingOffset, 0);
            }
        }
    };

    public void setInterpolator(Interpolator interpolator) {
        scroller.setInterpolator(interpolator);
    }

    public int getVisibleItems() {
        return visibleItems;
    }

    public void setVisibleItems(int count) {
        visibleItems = count;
    }

    public WheelAdapterBase getViewAdapter() {
        return viewAdapter;
    }

    // Adapter listener
    private DataSetObserver dataObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            invalidateWheel(false);
        }

        @Override
        public void onInvalidated() {
            invalidateWheel(true);
        }
    };

    public void setViewAdapter(WheelAdapterBase viewAdapter) {
        if (this.viewAdapter != null) {
            this.viewAdapter.unregisterDataSetObserver(dataObserver);
        }
        this.viewAdapter = viewAdapter;
        if (this.viewAdapter != null) {
            this.viewAdapter.registerDataSetObserver(dataObserver);
        }

        invalidateWheel(true);
    }

    public void addChangingListener(OnWheelChangedListener listener) {
        changingListeners.add(listener);
    }

    public void removeChangingListener(OnWheelChangedListener listener) {
        changingListeners.remove(listener);
    }

    protected void notifyChangingListeners(int oldValue, int newValue) {
        for (OnWheelChangedListener listener : changingListeners) {
            listener.onChanged(this, oldValue, newValue);
        }
    }

    public void addScrollingListener(OnWheelScrollListener listener) {
        scrollingListeners.add(listener);
    }

    public void removeScrollingListener(OnWheelScrollListener listener) {
        scrollingListeners.remove(listener);
    }

    protected void notifyScrollingListenersAboutStart() {
        for (OnWheelScrollListener listener : scrollingListeners) {
            listener.onScrollingStarted(this);
        }
    }

    protected void notifyScrollingListenersAboutEnd() {
        for (OnWheelScrollListener listener : scrollingListeners) {
            listener.onScrollingFinished(this);
        }
    }

    public void addClickingListener(OnWheelClickedListener listener) {
        clickingListeners.add(listener);
    }

    public void removeClickingListener(OnWheelClickedListener listener) {
        clickingListeners.remove(listener);
    }

    protected void notifyClickListenersAboutClick(int item) {
        for (OnWheelClickedListener listener : clickingListeners) {
            listener.onItemClicked(this, item);
        }
    }

    public int getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(int index, boolean animated) {
        if (viewAdapter == null || viewAdapter.getItemsCount() == 0) {
            return; // throw?
        }

        int itemCount = viewAdapter.getItemsCount();
        if (index < 0 || index >= itemCount) {
            if (isCyclic) {
                while (index < 0) {
                    index += itemCount;
                }
                index %= itemCount;
            }
            else {
                return;
            }
        }
        if (index != currentItem) {
            if (animated) {
                int itemsToScroll = index - currentItem;
                if (isCyclic) {
                    int scroll = itemCount + Math.min(index, currentItem) - Math.max(index, currentItem);
                    if (scroll < Math.abs(itemsToScroll)) {
                        itemsToScroll = itemsToScroll < 0 ? scroll : -scroll;
                    }
                }
                scroll(itemsToScroll, 0);
            }
            else {
                scrollingOffset = 0;

                int old = currentItem;
                currentItem = index;

                notifyChangingListeners(old, currentItem);

                invalidate();
            }
        }
    }

    public void setCurrentItem(int index) {
        setCurrentItem(index, false);
    }

    public boolean isCyclic() {
        return isCyclic;
    }

    public void setCyclic(boolean isCyclic) {
        this.isCyclic = isCyclic;
        invalidateWheel(false);
    }

    public void invalidateWheel(boolean clearCaches) {
        if (clearCaches) {
            recycle.clearAll();
            if (itemsLayout != null) {
                itemsLayout.removeAllViews();
            }
            scrollingOffset = 0;
        }
        else if (itemsLayout != null) {
            recycle.recycleItems(itemsLayout, firstItem, new ItemsRange());
        }

        invalidate();
    }

    private void initResourcesIfNecessary() {
        if (centerDrawable == null) {
            centerDrawable = getContext().getResources().getDrawable(R.drawable.wheelviewline);
        }

        if (topShadow == null) {
            topShadow = new GradientDrawable(Orientation.TOP_BOTTOM, SHADOWS_COLORS);
        }

        if (bottomShadow == null) {
            bottomShadow = new GradientDrawable(Orientation.BOTTOM_TOP, SHADOWS_COLORS);
        }

        setBackgroundResource(R.drawable.wheelviewbg);
    }

    private int getDesiredHeight(LinearLayout layout) {
        if (layout != null && layout.getChildAt(0) != null) {
            itemHeight = layout.getChildAt(0).getMeasuredHeight();
        }

        int desired = itemHeight * visibleItems - itemHeight * ITEM_OFFSET_PERCENT / 50;

        return Math.max(desired, getSuggestedMinimumHeight());
    }

    private int getItemHeight() {
        if (itemHeight != 0) {
            return itemHeight;
        }

        if (itemsLayout != null && itemsLayout.getChildAt(0) != null) {
            itemHeight = itemsLayout.getChildAt(0).getHeight();
            return itemHeight;
        }

        return getHeight() / visibleItems;
    }

    private int calculateLayoutWidth(int widthSize, int mode) {
        initResourcesIfNecessary();

        itemsLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        itemsLayout.measure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.UNSPECIFIED),
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        int width = itemsLayout.getMeasuredWidth();

        if (mode == MeasureSpec.EXACTLY) {
            width = widthSize;
        }
        else {
            width += 2 * PADDING;

            width = Math.max(width, getSuggestedMinimumWidth());

            if (mode == MeasureSpec.AT_MOST && widthSize < width) {
                width = widthSize;
            }
        }

        itemsLayout.measure(MeasureSpec.makeMeasureSpec(width - 2 * PADDING, MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

        return width;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        buildViewForMeasuring();

        int width = calculateLayoutWidth(widthSize, widthMode);

        int height;
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        }
        else {
            height = getDesiredHeight(itemsLayout);

            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize);
            }
        }

        setMeasuredDimension(width, height);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layout(r - l, b - t);
    }

    private void layout(int width, int height) {
        int itemsWidth = width - 2 * PADDING;

        itemsLayout.layout(0, 0, itemsWidth, height);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (viewAdapter != null && viewAdapter.getItemsCount() > 0) {
            updateView();

            drawItems(canvas);
            drawCenterRect(canvas);
        }

        drawShadows(canvas);
    }

    private void drawShadows(Canvas canvas) {
        int height = (int) (1.5 * getItemHeight());
        topShadow.setBounds(0, 0, getWidth(), height);
        topShadow.draw(canvas);

        bottomShadow.setBounds(0, getHeight() - height, getWidth(), getHeight());
        bottomShadow.draw(canvas);
    }

    private void drawItems(Canvas canvas) {
        canvas.save();

        int top = (currentItem - firstItem) * getItemHeight() + (getItemHeight() - getHeight()) / 2;
        canvas.translate(PADDING, -top + scrollingOffset);

        itemsLayout.draw(canvas);

        canvas.restore();
    }

    private void drawCenterRect(Canvas canvas) {
        int center = getHeight() / 2;
        int offset = (int) (getItemHeight() / 2 * 1.2);
        centerDrawable.setBounds(0, center - offset, getWidth(), center + offset);
        centerDrawable.draw(canvas);
        Paint p = new Paint();
// String familyName = "黑体";
        Typeface font = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
        p.setFakeBoldText(true);
        p.setFlags(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.BLACK);
        p.setTypeface(font);
        // The gesture threshold expressed in dip
        float GESTURE_THRESHOLD_DIP = 16.0f;

        // Convert the dips to pixels
        final float scale = getContext().getResources().getDisplayMetrics().density;
        int textSize = (int) (GESTURE_THRESHOLD_DIP * scale + 0.5f);
        p.setTextSize(textSize);
        canvas.drawText(getNormalTx(), getWidth() / 4 * 3, getHeight() / 2 + (float) (getItemHeight() / 4), p);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled() || getViewAdapter() == null) {
            return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;

            case MotionEvent.ACTION_UP:
                if (!isScrollingPerformed) {
                    int distance = (int) event.getY() - getHeight() / 2;
                    if (distance > 0) {
                        distance += getItemHeight() / 2;
                    }
                    else {
                        distance -= getItemHeight() / 2;
                    }
                    int items = distance / getItemHeight();
                    if (isClicked) {
                        if (isValidItemIndex(currentItem + items)) {
                            notifyClickListenersAboutClick(currentItem + items);
                        }
                    }
                    else {
                        if (items != 0 && isValidItemIndex(currentItem + items)) {
                            notifyClickListenersAboutClick(currentItem + items);
                        }
                    }
                }
                break;
        }

        return scroller.onTouchEvent(event);
    }

    private void doScroll(int delta) {
        scrollingOffset += delta;

        int itemHeight = getItemHeight();
        int count = scrollingOffset / itemHeight;

        int pos = currentItem - count;
        int itemCount = viewAdapter.getItemsCount();

        int fixPos = scrollingOffset % itemHeight;
        if (Math.abs(fixPos) <= itemHeight / 2) {
            fixPos = 0;
        }
        if (isCyclic && itemCount > 0) {
            if (fixPos > 0) {
                pos--;
                count++;
            }
            else if (fixPos < 0) {
                pos++;
                count--;
            }
            while (pos < 0) {
                pos += itemCount;
            }
            pos %= itemCount;
        }
        else {
            if (pos < 0) {
                count = currentItem;
                pos = 0;
            }
            else if (pos >= itemCount) {
                count = currentItem - itemCount + 1;
                pos = itemCount - 1;
            }
            else if (pos > 0 && fixPos > 0) {
                pos--;
                count++;
            }
            else if (pos < itemCount - 1 && fixPos < 0) {
                pos++;
                count--;
            }
        }

        int offset = scrollingOffset;
        if (pos != currentItem) {
            setCurrentItem(pos, false);
        }
        else {
            invalidate();
        }

        scrollingOffset = offset - count * itemHeight;
        if (scrollingOffset > getHeight()) {
            scrollingOffset = scrollingOffset % getHeight() + getHeight();
        }
    }

    public void scroll(int itemsToScroll, int time) {
        int distance = itemsToScroll * getItemHeight() - scrollingOffset;
        scroller.scroll(distance, time);
    }

    private ItemsRange getItemsRange() {
        if (getItemHeight() == 0) {
            return null;
        }

        int first = currentItem;
        int count = 1;

        while (count * getItemHeight() < getHeight()) {
            first--;
            count += 2;
        }

        if (scrollingOffset != 0) {
            if (scrollingOffset > 0) {
                first--;
            }
            count++;

            int emptyItems = scrollingOffset / getItemHeight();
            first -= emptyItems;
            count += Math.asin(emptyItems);
        }
        return new ItemsRange(first, count);
    }

    private boolean rebuildItems() {
        boolean updated = false;
        ItemsRange range = getItemsRange();
        if (itemsLayout != null) {
            int first = recycle.recycleItems(itemsLayout, firstItem, range);
            updated = firstItem != first;
            firstItem = first;
        }
        else {
            createItemsLayout();
            updated = true;
        }

        if (!updated) {
            updated = firstItem != range.getFirst() || itemsLayout.getChildCount() != range.getCount();
        }

        if (firstItem > range.getFirst() && firstItem <= range.getLast()) {
            for (int i = firstItem - 1; i >= range.getFirst(); i--) {
                if (!addViewItem(i, true)) {
                    break;
                }
                firstItem = i;
            }
        }
        else {
            firstItem = range.getFirst();
        }

        int first = firstItem;
        for (int i = itemsLayout.getChildCount(); i < range.getCount(); i++) {
            if (!addViewItem(firstItem + i, false) && itemsLayout.getChildCount() == 0) {
                first++;
            }
        }
        firstItem = first;

        return updated;
    }

    private void updateView() {
        if (rebuildItems()) {
            calculateLayoutWidth(getWidth(), MeasureSpec.EXACTLY);
            layout(getWidth(), getHeight());
        }
    }

    private void createItemsLayout() {
        if (itemsLayout == null) {
            itemsLayout = new LinearLayout(getContext());
            itemsLayout.setOrientation(LinearLayout.VERTICAL);
        }
    }

    private void buildViewForMeasuring() {
        if (itemsLayout != null) {
            recycle.recycleItems(itemsLayout, firstItem, new ItemsRange());
        }
        else {
            createItemsLayout();
        }

        int addItems = visibleItems / 2;
        for (int i = currentItem + addItems; i >= currentItem - addItems; i--) {
            if (addViewItem(i, true)) {
                firstItem = i;
            }
        }
    }

    private boolean addViewItem(int index, boolean first) {
        View view = getItemView(index);
        if (view != null) {
            if (first) {
                itemsLayout.addView(view, 0);
            }
            else {
                itemsLayout.addView(view);
            }

            return true;
        }

        return false;
    }

    private boolean isValidItemIndex(int index) {
        return viewAdapter != null && viewAdapter.getItemsCount() > 0 &&
            (isCyclic || index >= 0 && index < viewAdapter.getItemsCount());
    }

    private View getItemView(int index) {
        if (viewAdapter == null || viewAdapter.getItemsCount() == 0) {
            return null;
        }
        int count = viewAdapter.getItemsCount();
        if (!isValidItemIndex(index)) {
            return viewAdapter.getEmptyItem(recycle.getEmptyItem(), itemsLayout);
        }
        else {
            while (index < 0) {
                index = count + index;
            }
        }

        index %= count;
        return viewAdapter.getItem(index, recycle.getItem(), itemsLayout);
    }

    public void stopScrolling() {
        scroller.stopScrolling();
    }
}
