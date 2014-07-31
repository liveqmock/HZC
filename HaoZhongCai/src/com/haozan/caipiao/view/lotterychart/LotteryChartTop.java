package com.haozan.caipiao.view.lotterychart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.view.View;

public class LotteryChartTop
    extends View {

    // 表格定位的左上角X和右上角Y
    private final static int STARTX = 0;
    private final static int STARTY = 0;
    // 表格的宽度
    private float gridWidth = 32;
    private float gridHeight = 32;

    private float tabBarRow;
    private float tabBarcol;

    private Paint firstRowPaint;
    private Paint cubePaint;
    private Paint paintRect;
    private Paint paintTextRed;

    public LotteryChartTop(Context context) {
        super(context);
        init();
    }

    private void init() {
        firstRowPaint = new Paint();
        firstRowPaint.setStyle(Style.FILL);
        firstRowPaint.setColor(Color.rgb(253, 226, 181));

        cubePaint = new Paint();
        cubePaint.setStyle(Style.FILL);
        cubePaint.setColor(Color.rgb(247, 247, 247));

        paintRect = new Paint();
        paintRect.setColor(Color.rgb(255, 255, 255));
        paintRect.setStrokeWidth(2);
        paintRect.setStyle(Style.STROKE);

        paintTextRed = new Paint();
        paintTextRed.setColor(Color.RED);
        paintTextRed.setStyle(Style.STROKE);
        paintTextRed.setTextAlign(Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 填充表格颜色
        canvas.drawRect(STARTX, STARTY, STARTX + gridWidth * tabBarcol, STARTY + gridHeight * tabBarRow,
                        cubePaint);// 画一个大方框
        for (int i = 0; i < tabBarRow; i++) {// 画行
            if ((i + 1) % 2 == 1) {
                Paint paint;
                if (i == 0)
                    paint = firstRowPaint;
                else
                    paint = cubePaint;
                canvas.drawRect(STARTX, i * gridHeight + STARTY, STARTX + tabBarcol * gridWidth, STARTY +
                    (i + 1) * gridHeight, paint);
            }
        }

        // 画表格最外层边框
        canvas.drawRect(STARTX, STARTY, STARTX + gridWidth * tabBarcol, STARTY + gridHeight * tabBarRow,
                        paintRect);
        // 画表格的行和列,先画行后画列
        paintRect.setStrokeWidth(1);
        for (int i = 0; i < tabBarRow - 1; i++) {
            canvas.drawLine(STARTX, STARTY + (i + 1) * gridHeight, STARTX + tabBarcol * gridWidth, STARTY +
                (i + 1) * gridHeight, paintRect);
        }
        for (int j = 0; j < tabBarcol - 1; j++) {
            canvas.drawLine(STARTX + (j + 1) * gridWidth, STARTY, STARTX + (j + 1) * gridWidth, STARTY +
                tabBarRow * gridHeight, paintRect);
        }

        paintTextRed.setTextSize(15);
// paintTextBlue.setTextSize(10);

        FontMetrics fontMetrics = paintTextRed.getFontMetrics();
        float fontHeight = fontMetrics.bottom - fontMetrics.top;

        drawTableTerm(fontHeight, canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged((int) (gridWidth * tabBarcol), h, oldw, oldh);
    }

    private void drawTableTerm(float fontHeight, Canvas canvas) {
        float textBaseY = (int) (gridHeight + fontHeight) >> 1;
        canvas.drawText("期号" + "", (int) (gridWidth) >> 1, textBaseY, paintTextRed);
        for (int i = 1; i < tabBarRow; i++) {
            for (int j = 0; j < tabBarcol; j++) {
                float mLeft = j * gridWidth + STARTX;
                float mTop = i * gridHeight + STARTY;
                float mRight = mLeft + gridWidth;
                canvas.drawText("2013051" + "", (int) (mLeft + mRight) >> 1, textBaseY + mTop, paintTextRed);
            }
        }
    }

}
