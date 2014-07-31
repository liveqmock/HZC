package com.haozan.caipiao.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class PredicateLayout
    extends ViewGroup {
    private int line_height;
    private boolean isQuickBet = false;
    private boolean haveLotteryType = false;
    private int limit = 0;
    private int paddingRight = 0;
    private int[] childViewWidth;

    public static class LayoutParams
        extends ViewGroup.LayoutParams {
        public final int horizontal_spacing;
        public final int vertical_spacing;

        /**
         * @param horizontal_spacing Pixels between items, horizontally
         * @param vertical_spacing Pixels between items, vertically
         */
        public LayoutParams(int horizontal_spacing, int vertical_spacing) {
            super(0, 0);
            this.horizontal_spacing = horizontal_spacing;
            this.vertical_spacing = vertical_spacing;
        }
    }

    public PredicateLayout(Context context) {
        super(context);
    }

    public PredicateLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setHaveLotteryType(boolean haveLotteryType) {
        this.haveLotteryType = haveLotteryType;
    }

    public void setLotteryLimitNum(int limit) {
        this.limit = limit;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        assert (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED);
// LayoutParams lnp=(LayoutParams) this.getLayoutParams();
        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        final int count = getChildCount();
        int line_height = 0;
        int cw = 0;

        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();
        childViewWidth = new int[count];
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                paddingRight = lp.horizontal_spacing;
                child.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),
                              MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED));

                final int childw = child.getMeasuredWidth();
                cw = childw;
                childViewWidth[i] = cw;
                line_height = Math.max(line_height, child.getMeasuredHeight() + lp.vertical_spacing);

                if (xpos + childw > width) {
                    xpos = getPaddingLeft();
                    ypos += line_height;
                }

                xpos += childw + lp.horizontal_spacing;
            }
        }
        this.line_height = line_height;

        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            height = ypos + line_height;
        }
        else if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            if (ypos + line_height < height) {
                height = ypos + line_height;
            }
        }
        if (isQuickBet) {
            int newWidth = calculateWidget() + paddingRight * (count - 1) + 7;
//            if (newWidth < (width / 6 * 5))
//                setMeasuredDimension(newWidth, height);
//            else
                setMeasuredDimension(width , height);
        }
        else
            setMeasuredDimension(width, height);
    }

    private int calculateWidget() {
        int width = 0;
        for (int i = 0; i < childViewWidth.length; i++) {
            width += childViewWidth[i];
        }
        return width;
    }

    public void setQuick(boolean isQuickBet) {
        
        this.isQuickBet = isQuickBet;
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(1, 1); // default of 1px spacing
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        if (p instanceof LayoutParams)
            return true;
        return false;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        final int width = r - l;
        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final int childw = child.getMeasuredWidth();
                final int childh = child.getMeasuredHeight();
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (xpos + childw > width) {
                    xpos = getPaddingLeft();
                    ypos += line_height;
                }
                child.layout(xpos, ypos, xpos + childw, ypos + childh);
                xpos += childw + lp.horizontal_spacing;
            }
        }
    }
}
