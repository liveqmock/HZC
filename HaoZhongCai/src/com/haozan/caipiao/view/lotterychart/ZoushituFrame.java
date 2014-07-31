package com.haozan.caipiao.view.lotterychart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.widget.Toast;

public class ZoushituFrame
    extends ViewGroup {

    private GestureDetector mDetector;

    private Paint paintColorLb;
    private Paint paintColorLy;
    private Paint paintRect;
    private Paint paintTextRed;
    private Paint paintTextBlue;
    private Paint firstRowPaint;
    private Paint cubePaint;
    private Paint circlePaintRed;
    private Paint circlePaintBlue;
    private Paint paintText;

    // 表格的行数和列数
    private int row, col;
    // 表格定位的左上角X和右上角Y
    private final static int STARTX = 0;
    private final static int STARTY = 0;
    // 表格的宽度
    private float gridWidth;
    private float gridHeight;
    private float gridBarWidth;
    private float gridBarHeight;
    private int tabcol = 49;
    private int tabRow = 21;

    private int tabBarcol = 1;
    private int tabBarRow = 21;

    private TableView mZoushiTu;
    private TermBar mTermBar;

    private Scroller mScroller;

    public static final int FLING_VELOCITY_DOWNSCALE = 4;
    private String[][] zoushiTuArray;
    private Context context;

    public ZoushituFrame(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public ZoushituFrame(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        this.context = context;
    }

    public void setTableData(String[][] zoushiTuArray) {
        this.zoushiTuArray = zoushiTuArray;
    }

    public void setTableRowAndCol(int taRow, int tabcol) {
        this.tabRow = taRow;
        this.tabcol = tabcol;
        tabBarRow = tabRow;
    }

    public void setGridWidthAndGridHeight(float gridWidth, float gridHeight) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
    }

    public void setTableViewProperty() {
        mZoushiTu = new TableView(getContext());
        addView(mZoushiTu);

        mTermBar = new TermBar(getContext());
        addView(mTermBar);

        mZoushiTu.setDetecTor(mDetector);
        mZoushiTu.setGridWidthAndHeight(gridWidth, gridHeight);
        mZoushiTu.setTableData(zoushiTuArray);
        mZoushiTu.initRowAndCol(tabRow, tabcol);
    }

    private void init() {

        firstRowPaint = new Paint();
        firstRowPaint.setStyle(Style.FILL);
        firstRowPaint.setColor(Color.rgb(253, 226, 181));

        cubePaint = new Paint();
        cubePaint.setStyle(Style.FILL);
        cubePaint.setColor(Color.rgb(247, 247, 247));

        paintColorLb = new Paint();
        paintColorLb.setStyle(Style.FILL);
        paintColorLb.setColor(Color.rgb(235, 241, 221));

        paintColorLy = new Paint();
        paintColorLy.setStyle(Style.FILL);
        paintColorLy.setColor(Color.rgb(219, 238, 243));

        paintRect = new Paint();
        paintRect.setColor(Color.rgb(255, 255, 255));
        paintRect.setStrokeWidth(2);
        paintRect.setStyle(Style.STROKE);

        paintTextRed = new Paint();
        paintTextRed.setColor(Color.RED);
        paintTextRed.setStyle(Style.STROKE);
        paintTextRed.setTextAlign(Align.CENTER);

        paintTextBlue = new Paint();
        paintTextBlue.setColor(Color.BLUE);
        paintTextBlue.setStyle(Style.STROKE);
        paintTextBlue.setTextAlign(Align.CENTER);

        circlePaintRed = new Paint();
        circlePaintRed.setColor(Color.RED);
        circlePaintRed.setStyle(Style.FILL);
        circlePaintRed.setAntiAlias(true);

        circlePaintBlue = new Paint();
        circlePaintBlue.setColor(Color.BLUE);
        circlePaintBlue.setStyle(Style.FILL);
        circlePaintBlue.setAntiAlias(true);

        paintText = new Paint();
        paintText.setColor(Color.WHITE);
        paintText.setStyle(Style.STROKE);
        paintText.setTextAlign(Align.CENTER);
        paintText.setTextSize(15);

        mDetector = new GestureDetector(ZoushituFrame.this.getContext(), new GestureListener());
        mDetector.setIsLongpressEnabled(false);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension((int) (gridWidth * tabcol) + 120, h);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mTermBar.layout(0, 0, 120, h);
        mZoushiTu.layout(120, 0, (int) (gridWidth * tabcol) + 120, h);
        super.onSizeChanged(mTermBar.getWidth() + mZoushiTu.getWidth(), h, oldw, oldh);
    }

    private class TermBar
        extends View {

        public TermBar(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            // 填充表格颜色
            canvas.drawRect(STARTX, STARTY, STARTX + gridBarWidth * tabBarcol, STARTY + gridBarHeight *
                tabBarRow, cubePaint);// 画一个大方框
            for (int i = 0; i < tabBarRow; i++) {// 画行
                if ((i + 1) % 2 == 1) {
                    Paint paint;
                    if (i == 0)
                        paint = firstRowPaint;
                    else
                        paint = cubePaint;
                    canvas.drawRect(STARTX, i * gridBarHeight + STARTY, STARTX + tabBarcol * gridBarWidth,
                                    STARTY + (i + 1) * gridBarHeight, paint);
                }
            }

            // 画表格最外层边框
            canvas.drawRect(STARTX, STARTY, STARTX + gridBarWidth * tabBarcol, STARTY + gridBarHeight *
                tabBarRow, paintRect);
            // 画表格的行和列,先画行后画列
            paintRect.setStrokeWidth(1);
            for (int i = 0; i < tabBarRow - 1; i++) {
                canvas.drawLine(STARTX, STARTY + (i + 1) * gridBarHeight, STARTX + tabBarcol * gridBarWidth,
                                STARTY + (i + 1) * gridBarHeight, paintRect);
            }
            for (int j = 0; j < tabBarcol - 1; j++) {
                canvas.drawLine(STARTX + (j + 1) * gridBarWidth, STARTY, STARTX + (j + 1) * gridBarWidth,
                                STARTY + tabBarRow * gridBarHeight, paintRect);
            }

            paintTextRed.setTextSize(15);
// paintTextBlue.setTextSize(10);

            FontMetrics fontMetrics = paintTextRed.getFontMetrics();
            float fontHeight = fontMetrics.bottom - fontMetrics.top;

            drawTableTerm(fontHeight, canvas);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            gridBarWidth = 120;
            gridBarHeight = gridHeight;
            super.onSizeChanged(120, h, oldw, oldh);
        }

        private void drawTableTerm(float fontHeight, Canvas canvas) {
            float textBaseY = (int) (gridBarHeight + fontHeight) >> 1;
            canvas.drawText("期号" + "", (int) (gridBarWidth) >> 1, textBaseY, paintTextRed);
            for (int i = 1; i < tabBarRow; i++) {
                for (int j = 0; j < tabBarcol; j++) {
                    float mLeft = j * gridBarWidth + STARTX;
                    float mTop = i * gridBarHeight + STARTY;
                    float mRight = mLeft + gridBarWidth;
                    canvas.drawText("2013051" + "", (int) (mLeft + mRight) >> 1, textBaseY + mTop,
                                    paintTextRed);
                }
            }
        }
    }

    private class GestureListener
        extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // Set up the Scroller for a fling
            mScroller.fling((int) e1.getX(), 0, (int) velocityX, (int) velocityY, Integer.MIN_VALUE,
                            Integer.MAX_VALUE, 0, 0);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            int left = 0, top = 0, right = 0, bottom = 0;

            /** 在这里要进行判断处理，防止在drag时候越界 **/

            /** 获取相应的l，t,r ,b **/
            left = current_x - start_x;
            right = current_x - start_x + mZoushiTu.getWidth();
            top = current_y - start_y;
            bottom = current_y - start_y + getHeight();
            System.out.println("touch_move:" + e2.getX() + "|" + e2.getRawX());
            /** 水平进行判断 **/
            if (left >= 120) {
                left = 120;
                right = mZoushiTu.getWidth() + 120;
            }
            if (right <= 480) {
                left = 480 - mZoushiTu.getWidth();
                right = 480;
            }

            mZoushiTu.layout(left, 0, right, mZoushiTu.getBottom());
            current_x = (int) e2.getRawX() + 120;
            current_y = (int) e2.getRawY();
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            current_x = (int) e.getRawX() + 120;
            current_y = (int) e.getRawY();

            start_x = (int) e.getX() + 120;
            start_y = current_y - getTop();

            testTouchColorPanel(e.getX(), e.getY());
            return true;
        }
    }

    private int start_x, start_y, current_x, current_y;// 触摸位置

    public boolean testTouchColorPanel(float x, float y) {
        initValue();
// if (y < gridBarHeight && y > 0) {
        if (x > firstX && y > firstY && x < firstX + firstSidesX + secondSideX * widthNum &&
            y < firstY + sideY * heightNum) {

            int ty = (int) ((y - firstY) / sideY);
            int tx;

            if (x - firstX - firstSidesX > 0) {
                tx = (int) ((x - firstX - firstSidesX) / secondSideX + 1);
            }
            else {
                tx = 0;
            }
            System.out.println("x_y:" + ty + "|" + tx);
// int index = ty * widthNum + tx;
            if (!zoushiTuArray[ty - 1][tx].equals(""))
                Toast.makeText(context.getApplicationContext(), String.valueOf(zoushiTuArray[ty - 1][tx]),Toast.LENGTH_SHORT).show();
// myFormListener.showNum(""+index);
            return true;
        }
// }
        return false;
    }

    private void initValue() {
        firstX = 0; // 起始点x
        firstY = 0; // 起始点y
        secondX = (int) (gridWidth); // 第二点x
        widthNum = tabcol; // 列
        heightNum = tabRow; // 行
        secondSideX = (int) gridWidth; // 第二列的宽
        sideY = (int) gridHeight; // 行高
        firstSidesX = (int) gridWidth; // 第一列的宽
    }

    private int firstX = 15; // 起始点x
    private int firstY = 65; // 起始点y
    private int secondX = 95; // 第二点x
// private int secondY = 115; // 第二点y
    private int widthNum = 8; // 列
    private int heightNum = 8; // 行
    private int secondSideX = 150; // 第二列的宽
    private int sideY = 50; // 行高
    private int firstSidesX = 80; // 第一列的宽
}
