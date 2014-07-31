package com.haozan.caipiao.view.lotterychart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

class TableView
    extends View {

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
    private float gridWidth = 32;
    private float gridHeight = 32;
    private int tabcol = 49;
    private int tabRow = 21;

    private Scroller mScroller;

    public static final int FLING_VELOCITY_DOWNSCALE = 4;

    private String[][] zoushiTuArray = new String[20][49];

    private Context context;

    private Matrix mTransform = new Matrix();

    public TableView(Context context) {
        super(context);
        init();
    }

    public void setGridWidthAndHeight(float gridWidth, float gridHeight) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridWidth;
    }

    public void setTableData(String[][] zoushiTuArray) {
        this.zoushiTuArray = zoushiTuArray;
    }

    public void setDetecTor(GestureDetector mDetector) {
        this.mDetector = mDetector;
    }

    public void initRowAndCol(int tabRow, int tabcol) {
        this.tabcol = tabcol;
        this.tabRow = tabRow;
    }

    private void init() {
        firstRowPaint = new Paint();
        firstRowPaint.setStyle(Style.FILL);
        firstRowPaint.setColor(Color.rgb(253, 226, 181));

        cubePaint = new Paint();
        cubePaint.setStyle(Style.FILL);
        cubePaint.setColor(Color.rgb(251, 239, 246));

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
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 填充表格颜色
        canvas.drawRect(STARTX, STARTY, STARTX + gridWidth * tabcol, STARTY + gridHeight * tabRow, cubePaint);// 画一个大方框
        for (int i = 0; i < tabRow; i++) {// 画行
            if ((i + 1) % 2 == 1) {
                Paint paint;
                if (i == 0)
                    paint = firstRowPaint;
                else
                    paint = cubePaint;
                canvas.drawRect(STARTX, i * gridHeight + STARTY, STARTX + tabcol * gridWidth, STARTY +
                    (i + 1) * gridHeight, paint);
            }
        }

        // 画表格最外层边框
        canvas.drawRect(STARTX, STARTY, STARTX + gridWidth * tabcol, STARTY + gridHeight * tabRow, paintRect);
        // 画表格的行和列,先画行后画列
        paintRect.setStrokeWidth(1);
        for (int i = 0; i < tabRow - 1; i++) {
            canvas.drawLine(STARTX, STARTY + (i + 1) * gridHeight, STARTX + tabcol * gridWidth, STARTY +
                (i + 1) * gridHeight, paintRect);
        }
        for (int j = 0; j < tabcol - 1; j++) {
            if (j == 32)
                paintRect.setStrokeWidth(5);
            else
                paintRect.setStrokeWidth(1);
            canvas.drawLine(STARTX + (j + 1) * gridWidth, STARTY, STARTX + (j + 1) * gridWidth, STARTY +
                tabRow * gridHeight, paintRect);
        }

        // 在单元格填充数字—如果行数大于60并且列数大于30，就不显示数字；大于10，就改变字大小
        if (tabRow <= 50 && tabcol <= 50) {
            /*
             * if (tabRow > 40 || tabcol > 25) { paintText.setTextSize(7); } else if (tabRow > 30 || tabcol >
             * 20) { paintText.setTextSize(8); } else if (tabRow > 20 || tabcol > 15) {
             * paintText.setTextSize(9); } else if (tabRow > 10 || tabcol > 10) { paintText.setTextSize(10); }
             */

            paintTextRed.setTextSize(15);
            paintTextBlue.setTextSize(15);

            FontMetrics fontMetrics = paintTextRed.getFontMetrics();
            float fontHeight = fontMetrics.bottom - fontMetrics.top;
            int text = 0;
            drawTableDefaultNum(text, fontHeight, canvas);
            fillTableNum(text, fontHeight, canvas);
        }

// if (!mScroller.isFinished()) {
// postInvalidate();
// }
    }

    private void drawTableDefaultNum(int text, float fontHeight, Canvas canvas) {
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < tabcol - 16; j++) {
                float mLeft = j * gridWidth + STARTX;
                float mTop = i * gridHeight + STARTY;
                float mRight = mLeft + gridWidth;
                text++;
                float textBaseY = (int) (gridHeight + fontHeight) >> 1;
                canvas.drawText(text + "", (int) (mLeft + mRight) >> 1, textBaseY + mTop, paintTextRed);
            }

            text = 0;
            for (int j = tabcol - 16; j < tabcol; j++) {
                float mLeft = j * gridWidth + STARTX;
                float mTop = i * gridHeight + STARTY;
                float mRight = mLeft + gridWidth;
                text++;
                float textBaseY = (int) (gridHeight + fontHeight) >> 1;
                canvas.drawText(text + "", (int) (mLeft + mRight) >> 1, textBaseY + mTop, paintTextBlue);
            }
        }
    }

    private void fillTableNum(int text, float fontHeight, Canvas canvas) {
        int radius=(int) (gridWidth/2-2);
        for (int i = 1; i < tabRow; i++) {
            for (int j = 0; j < tabcol - 16; j++) {
                float mLeft = j * gridWidth + STARTX;
                float mTop = i * gridHeight + STARTY;
                float mRight = mLeft + gridWidth;
                float textBaseY = (int) (gridHeight + fontHeight) >> 1;
                if (!zoushiTuArray[i - 1][j].equals(""))
                    canvas.drawCircle((int) (mLeft + mRight) >> 1, textBaseY + mTop -7, radius, circlePaintRed);
                canvas.drawText(zoushiTuArray[i - 1][j] + "", (int) (mLeft + mRight) >> 1, textBaseY + mTop,
                                paintText);
            }

            for (int j = tabcol - 16; j < tabcol; j++) {
                float mLeft = j * gridWidth + STARTX;
                float mTop = i * gridHeight + STARTY;
                float mRight = mLeft + gridWidth;
                float textBaseY = (int) (gridHeight + fontHeight) >> 1;
                if (!zoushiTuArray[i - 1][j].equals(""))
                    canvas.drawCircle((int) (mLeft + mRight) >> 1, textBaseY + mTop - 5, radius, circlePaintBlue);
                canvas.drawText(zoushiTuArray[i - 1][j] + "", (int) (mLeft + mRight) >> 1, textBaseY + mTop,
                                paintText);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
// gridHeight = screenHeight / tabBarRow;
// gridWidth = gridHeight;
        super.onSizeChanged((int) (gridWidth * tabcol), h, oldw, oldh);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Let the GestureDetector interpret this event
        boolean result = true;
        mDetector.onTouchEvent(event);
        return result;
    }
}