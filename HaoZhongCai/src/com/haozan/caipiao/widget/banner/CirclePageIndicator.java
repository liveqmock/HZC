package com.haozan.caipiao.widget.banner;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.widget.LinearLayout.HORIZONTAL;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.haozan.caipiao.R;

/**
 * Draws circles (one for each view). The current view position is filled and others are only stroked.
 */
public class CirclePageIndicator
    extends View
    implements PageIndicator {
    private float mRadius;
    private final Paint mPaintPageFill = new Paint(ANTI_ALIAS_FLAG);
    private final Paint mPaintStroke = new Paint(ANTI_ALIAS_FLAG);
    private final Paint mPaintFill = new Paint(ANTI_ALIAS_FLAG);
    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mListener;
    private int mCurrentPage;
    private int mSnapPage;
    private float mPageOffset;
    private int mScrollState;
    private int mOrientation;
    private boolean mCentered;
    private boolean mSnap;

    public CirclePageIndicator(Context context) {
        this(context, null);
    }

    public CirclePageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.vpiCirclePageIndicatorStyle);
    }

    public CirclePageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (isInEditMode())
            return;

        // Load defaults from resources
        final Resources res = getResources();
        final int defaultPageColor = res.getColor(R.color.light_purple); // 圆点里面的颜色
        final int defaultFillColor = res.getColor(R.color.white); // 圆点填充色
        final int defaultOrientation = 0; //
        final int defaultStrokeColor = res.getColor(R.color.light_purple); // 默认圆点线的颜色
        final float defaultStrokeWidth = 1;// 圆点线的宽度
        final float defaultRadius = 4;
        final boolean defaultCentered = true;
        final boolean defaultSnap = false; // 圆点的出现方法
        final int[] CirclePageIndicator =
            {0x010100c4, 0x010100d4, 0x7f010006, 0x7f010008, 0x7f01000a, 0x7f01000b, 0x7f01000c, 0x7f01000d,
                    0x7f01000e};
        // Retrieve styles attributes
        TypedArray a = context.obtainStyledAttributes(attrs, CirclePageIndicator, defStyle, 0);

        mPaintPageFill.setStyle(Style.FILL);
        mPaintPageFill.setColor(a.getColor(5, defaultPageColor));
        mPaintStroke.setStyle(Style.STROKE);
        mPaintStroke.setColor(a.getColor(8, defaultStrokeColor));
        mPaintStroke.setStrokeWidth(a.getDimension(3, defaultStrokeWidth));
        mPaintFill.setStyle(Style.FILL);
        mPaintFill.setColor(a.getColor(4, defaultFillColor));
        mRadius = a.getDimension(6, defaultRadius);
        mSnap = a.getBoolean(7, defaultSnap);
// Drawable background = a.getDrawable(R.styleable.CirclePageIndicator_android_background);
// if (background != null) {
// setBackgroundDrawable(background);
// }

        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mViewPager == null) {
            return;
        }
        final int count = mViewPager.getAdapter().getCount();
        if (count == 0) {
            return;
        }

        if (mCurrentPage >= count) {
            setCurrentItem(count - 1);
            return;
        }

        int longSize;
        int longPaddingBefore;
        int longPaddingAfter;
        int shortPaddingBefore;
        if (mOrientation == HORIZONTAL) {
            longSize = getWidth();
            longPaddingBefore = getPaddingLeft();
            longPaddingAfter = getPaddingRight();
            shortPaddingBefore = getPaddingTop();
        }
        else {
            longSize = getHeight();
            longPaddingBefore = getPaddingTop();
            longPaddingAfter = getPaddingBottom();
            shortPaddingBefore = getPaddingLeft();
        }

        final float threeRadius = mRadius * 5;
        final float shortOffset = shortPaddingBefore + mRadius;
        float longOffset = longPaddingBefore + mRadius;
        if (mCentered) {
            longOffset +=
                ((longSize - longPaddingBefore - longPaddingAfter) / 2.0f) - ((count * threeRadius) / 2.0f);
        }

        float dX;
        float dY;

        float pageFillRadius = mRadius;
        if (mPaintStroke.getStrokeWidth() > 0) {
            pageFillRadius -= mPaintStroke.getStrokeWidth() / 2.0f;
        }

        // Draw stroked circles
        for (int iLoop = 0; iLoop < count; iLoop++) {
            float drawLong = longOffset + (iLoop * threeRadius);
            if (mOrientation == HORIZONTAL) {
                dX = drawLong;
                dY = shortOffset;
            }
            else {
                dX = shortOffset;
                dY = drawLong;
            }
            // Only paint fill if not completely transparent
            if (mPaintPageFill.getAlpha() > 0) {
                canvas.drawCircle(dX, dY, pageFillRadius, mPaintPageFill);
            }

            // Only paint stroke if a stroke width was non-zero
            if (pageFillRadius != mRadius) {
                canvas.drawCircle(dX, dY, mRadius, mPaintStroke);
            }
        }

        // Draw the filled circle according to the current scroll
        float cx = (mSnap ? mSnapPage : mCurrentPage) * threeRadius;
        if (!mSnap) {
            cx += mPageOffset * threeRadius;
        }
        if (mOrientation == HORIZONTAL) {
            dX = longOffset + cx;
            dY = shortOffset;
        }
        else {
            dX = shortOffset;
            dY = longOffset + cx;
        }
        canvas.drawCircle(dX, dY, mRadius, mPaintFill);
    }

    @Override
    public void setViewPager(ViewPager view) {
        if (mViewPager == view) {
            return;
        }
        if (mViewPager != null) {
            mViewPager.setOnPageChangeListener(null);
        }
        if (view.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        mViewPager = view;
        mViewPager.setOnPageChangeListener(this);
        invalidate();
    }

    @Override
    public void setViewPager(ViewPager view, int initialPosition) {
        setViewPager(view);
        setCurrentItem(initialPosition);
    }

    @Override
    public void setCurrentItem(int item) {
        if (mViewPager == null) {
            throw new IllegalStateException("ViewPager has not been bound.");
        }
        mViewPager.setCurrentItem(item);
        mCurrentPage = item;
        invalidate();
    }

    @Override
    public void notifyDataSetChanged() {
        invalidate();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        mScrollState = state;

        if (mListener != null) {
            mListener.onPageScrollStateChanged(state);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mCurrentPage = position;
        mPageOffset = positionOffset;
        invalidate();

        if (mListener != null) {
            mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (mSnap || mScrollState == ViewPager.SCROLL_STATE_IDLE) {
            mCurrentPage = position;
            mSnapPage = position;
            invalidate();
        }

        if (mListener != null) {
            mListener.onPageSelected(position);
        }
    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mListener = listener;
    }

    /*
     * (non-Javadoc)
     * @see android.view.View#onMeasure(int, int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mOrientation == HORIZONTAL) {
            setMeasuredDimension(measureLong(widthMeasureSpec), measureShort(heightMeasureSpec));
        }
        else {
            setMeasuredDimension(measureShort(widthMeasureSpec), measureLong(heightMeasureSpec));
        }
    }

    /**
     * Determines the width of this view
     * 
     * @param measureSpec A measureSpec packed into an int
     * @return The width of the view, honoring constraints from measureSpec
     */
    private int measureLong(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if ((specMode == MeasureSpec.EXACTLY) || (mViewPager == null)) {
            // We were told how big to be
            result = specSize;
        }
        else {
            // Calculate the width according the views count
            final int count = mViewPager.getAdapter().getCount();
            result =
                (int) (getPaddingLeft() + getPaddingRight() + (count * 2 * mRadius) + (count - 1) *3* mRadius + 1);
            // Respect AT_MOST value if that was what is called for by measureSpec
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    /**
     * Determines the height of this view
     * 
     * @param measureSpec A measureSpec packed into an int
     * @return The height of the view, honoring constraints from measureSpec
     */
    private int measureShort(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        }
        else {
            // Measure the height
            result = (int) (2 * mRadius + getPaddingTop() + getPaddingBottom() + 1);
            // Respect AT_MOST value if that was what is called for by measureSpec
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }
}
