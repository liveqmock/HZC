package com.haozan.caipiao.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.types.Lottery;
import com.haozan.caipiao.types.TrendChartTopBarData;

public class TrendChartViewGroup
    extends LinearLayout {
    private static final int COLUMN = 20;

    private int redballColor;
    private int blueballColor;

    private PredicateLayout layoutBalls;
    private LinearLayout layoutTrendChart;
    private TrendChartView trendChartView;
    private TextView tvTips;

    private TrendChartTopBarData[] trendChartTopBarData;
    private Lottery[] lotteryInf;
    private boolean[] selectedColumn;
    private int[] eachGroupLength;

    private int selectedSize = 0;

    private TrendChartSelecteBallsListener selecteBallListener;

    public TrendChartViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TrendChartViewGroup(Context context) {
        super(context);
        init();
    }

    private void init() {
        redballColor = getResources().getColor(R.color.red_ball);
        blueballColor = getResources().getColor(R.color.blue_ball);

        View view = View.inflate(getContext(), R.layout.layout_trend_chart_top, null);
        layoutTrendChart = (LinearLayout) view.findViewById(R.id.trendchart_layout);

        tvTips = (TextView) view.findViewById(R.id.tips2);
        layoutBalls = (PredicateLayout) view.findViewById(R.id.balls);
        Button toBet = (Button) view.findViewById(R.id.finish_selected);
        toBet.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
            	try{
            		if (selectedSize != 0) {
                        StringBuilder ballsSb = new StringBuilder();
                        int length = trendChartTopBarData.length;
                        int lastSumColumn = 0;

                        for (int i = 0; i < length; i++) {
                            String[] childText = trendChartTopBarData[i].getTrendTopText();
                            int childLength = childText.length;
                            for (int j = 0; j < childLength; j++) {
                                if (selectedColumn[lastSumColumn + j]) {
                                    ballsSb.append(childText[j] + ",");
                                }
                            }
                            ballsSb.delete(ballsSb.length() - 1, ballsSb.length());

                            if (i != length - 1) {
                                ballsSb.append("|");
                            }

                            lastSumColumn += childLength;
                        }
                        selecteBallListener.selectedBalls(ballsSb.toString());
                    }
                    else {
                        selecteBallListener.selectedBalls(null);
                    }
            	}catch(Exception e){
            		selecteBallListener.selectedBalls(null);
            	}
            }
        });

        trendChartView = new TrendChartView(getContext());
        layoutTrendChart.addView(trendChartView);
        addView(view);
    }

    public void setTrendChartTopBarData(TrendChartTopBarData[] trendChartTopBarData) {
        this.trendChartTopBarData = trendChartTopBarData;
        trendChartView.initDisplay();
    }

    public void setLotteryInf(Lottery[] lotteryInf) {
        this.lotteryInf = lotteryInf;
        trendChartView.initDisplay();
    }

    public void invalidateBalls() {
        layoutBalls.removeAllViews();

        for (int i = 0; i < selectedColumn.length; i++) {
            if (selectedColumn[i]) {
                selectedSize++;

                final int index = i;

                // 算出显示什么文字，group代表属于哪个组，order代表组中哪个序号
                int group = 0;
                int order = i;
                for (int j2 = 0; j2 < eachGroupLength.length; j2++) {
                    if (order < eachGroupLength[j2]) {
                        group = j2;
                        break;
                    }
                    else {
                        order -= eachGroupLength[j2];
                    }
                }

                TextView ball = new TextView(getContext());
                if (trendChartTopBarData[group].getColor() == redballColor) {
                    ball.setBackgroundResource(R.drawable.smallredball);
                }
                else {
                    ball.setBackgroundResource(R.drawable.smallblueball);
                }
                ball.setGravity(Gravity.CENTER);
                ball.setText(trendChartTopBarData[group].getTrendTopText()[order]);
                ball.setTextSize(16);
                Paint paint = ball.getPaint();
                paint.setFakeBoldText(true);
                ball.setTextColor(Color.WHITE);
                ball.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        selectedColumn[index] = false;
                        invalidateBalls();
                        trendChartView.invalidate();
                    }
                });
                layoutBalls.addView(ball);
            }
        }

        if (selectedSize == 0) {
            tvTips.setVisibility(View.VISIBLE);
        }
        else {
            tvTips.setVisibility(View.GONE);
        }
    }

    class TrendChartView
        extends View {
        private int colorTopText;
        private int colorTopBg;
        private int colorTermFirst;
        private int colorTermSecond;
        private int colorNumFirst;
        private int colorNumSecond;
        private int colorContent;
        private int colorBalls;
        private int colorDeepLine;
        private int colorLightLine;

        private boolean[][] lotteryNums;

        private Paint paint;

        private int textTopBarHeightPostion;
        private int textContentHeightPostion;

        private int topBarWidth = 40;
        private int topBarHeight = 60;

        private int termItemWidth = 140;

        private int ballRadius = 18;

        private int contentItemWidth = 40;
        private int contentItemHeight = 40;

        private int dataLine = COLUMN;
        private int dataColumn;

        private Context context;

        public TrendChartView(Context context) {
            super(context);
            this.context = context;
            init();
        }

        public TrendChartView(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.context = context;
            init();
        }

        private void init() {
            setBackgroundColor(Color.rgb(242, 240, 234));
// setBackgroundColor(Color.rgb(255, 255, 255));

            paint = new Paint();
            paint.setStyle(Style.FILL);

            paint.setAntiAlias(true);
// topBarTvPaint.setStyle(Style.FILL);
// topBarTvPaint.setTextAlign(Align.CENTER);
            paint.setTextSize(24);

            Rect boundsTopBar = new Rect();
            paint.getTextBounds("期次", 0, 1, boundsTopBar);
            textTopBarHeightPostion = (topBarHeight + boundsTopBar.height()) >> 1;

            Rect boundsContent = new Rect();
            paint.getTextBounds("期次", 0, 1, boundsContent);
            textContentHeightPostion = (contentItemHeight + boundsContent.height()) >> 1;

            Resources rs = getContext().getResources();
            colorTopText = rs.getColor(R.color.yellow_text);
            colorTopBg = rs.getColor(R.color.trend_chart_top);
            colorTermFirst = rs.getColor(R.color.trend_chart_term1);
            colorTermSecond = rs.getColor(R.color.trend_chart_term2);
            colorNumFirst = rs.getColor(R.color.trend_chart_content);
            colorNumSecond = rs.getColor(R.color.trend_chart_selected_column);
            colorContent = rs.getColor(R.color.light_purple);
            colorBalls = rs.getColor(R.color.white);
            colorDeepLine = rs.getColor(R.color.trend_chart_line);
            colorLightLine = rs.getColor(R.color.trend_chart_line);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            drawTopBarView(canvas);

            drawContentView(canvas);

            drawRectAndLine(canvas);
        }

        private void drawTopBarView(Canvas canvas) {
            paint.setFakeBoldText(false);
            paint.setColor(colorTopBg);
            canvas.drawRect(0, 0, termItemWidth, topBarHeight, paint);
            paint.setColor(colorTopText);
            canvas.drawText("期次", (termItemWidth - (int) paint.measureText("期次")) >> 1,
                            textTopBarHeightPostion, paint);

            int length = trendChartTopBarData.length;
            int presentColumn = 0;
            for (int i = 0; i < length; i++) {
                String[] childText = trendChartTopBarData[i].getTrendTopText();
                int childLength = childText.length;
                presentColumn += childLength;

                for (int j = 0; j < childLength; j++) {
                    paint.setColor(colorTopBg);
                    canvas.drawRect(termItemWidth + (presentColumn - childLength + j) * topBarWidth, 0,
                                    termItemWidth + (presentColumn - childLength + j + 1) * topBarWidth,
                                    topBarHeight, paint);

                    paint.setColor(colorTopText);
                    canvas.drawText(childText[j], termItemWidth + (presentColumn - childLength + j) *
                                        topBarWidth +
                                        ((topBarWidth - (int) paint.measureText(childText[j])) >> 1),
                                    textTopBarHeightPostion, paint);
                }
            }

            paint.setColor(colorTopBg);
        }

        private void drawContentView(Canvas canvas) {
            drawTerm(canvas);
            drawSelectedColumn(canvas);
            drawNum(canvas);
        }

        private void drawTerm(Canvas canvas) {
            int length = dataLine;
            if (lotteryInf == null) {
                for (int i = 0; i < length; i++) {
                    if (i % 2 == 0) {
                        paint.setColor(colorTermFirst);
                    }
                    else {
                        paint.setColor(colorTermSecond);
                    }
                    canvas.drawRect(0, topBarHeight + i * contentItemHeight, termItemWidth, topBarHeight +
                        (i + 1) * contentItemHeight, paint);
                }
            }
            else {
                for (int i = 0; i < length; i++) {
                    if (i % 2 == 0) {
                        paint.setColor(colorTermFirst);
                    }
                    else {
                        paint.setColor(colorTermSecond);
                    }
                    canvas.drawRect(0, topBarHeight + i * contentItemHeight, termItemWidth, topBarHeight +
                        (i + 1) * contentItemHeight, paint);

                    paint.setColor(colorContent);
                    canvas.drawText(lotteryInf[i].getTerm(),
                                    (termItemWidth - (int) paint.measureText(lotteryInf[i].getTerm())) >> 1,
                                    topBarHeight + textContentHeightPostion + i * contentItemHeight, paint);
                }
            }
        }

        private void drawNum(Canvas canvas) {
            paint.setFakeBoldText(true);
            // i代表整个列表的行，j代表整个列表的列
            for (int i = 0; i < lotteryNums.length; i++) {
                for (int j = 0; j < lotteryNums[i].length; j++) {

                    if (lotteryNums[i][j]) {
                        // 算出显示什么文字，group代表属于哪个组，order代表组中哪个序号
                        int group = 0;
                        int order = j;
                        for (int j2 = 0; j2 < eachGroupLength.length; j2++) {
                            if (order < eachGroupLength[j2]) {
                                group = j2;
                                paint.setColor(trendChartTopBarData[j2].getColor());
                                break;
                            }
                            else {
                                order -= eachGroupLength[j2];
                            }
                        }

                        canvas.drawCircle(termItemWidth + (j + 0.5f) * contentItemWidth, topBarHeight +
                            (i + 0.5f) * contentItemHeight, ballRadius, paint);

                        paint.setColor(colorBalls);
                        canvas.drawText(trendChartTopBarData[group].getTrendTopText()[order],
                                        termItemWidth +
                                            j *
                                            contentItemWidth +
                                            ((contentItemWidth - (int) paint.measureText(trendChartTopBarData[group].getTrendTopText()[order])) >> 1),
                                        topBarHeight + i * contentItemHeight + textContentHeightPostion,
                                        paint);
                    }
                }
            }
        }

        private void drawSelectedColumn(Canvas canvas) {
            paint.setColor(colorNumSecond);
            for (int i = 0; i < selectedColumn.length; i++) {
                if (selectedColumn[i]) {
                    canvas.drawRect(termItemWidth + i * contentItemWidth, topBarHeight, termItemWidth +
                        (i + 1) * contentItemWidth, topBarHeight + dataLine * contentItemHeight, paint);
                }
            }
        }

        private void drawRectAndLine(Canvas canvas) {
            paint.setStrokeWidth(3.0f);
            paint.setColor(colorDeepLine);
            canvas.drawLine(0, 0, termItemWidth + dataColumn * contentItemWidth, 0, paint);
            canvas.drawLine(0, topBarHeight, termItemWidth + dataColumn * contentItemWidth, topBarHeight,
                            paint);
            canvas.drawLine(0, 0, 0, topBarHeight + dataLine * contentItemHeight, paint);
            canvas.drawLine(termItemWidth, 0, termItemWidth, topBarHeight + dataLine * contentItemHeight,
                            paint);
            canvas.drawLine(0, topBarHeight + dataLine * contentItemHeight, termItemWidth + dataColumn *
                contentItemWidth, topBarHeight + dataLine * contentItemHeight, paint);

            paint.setStrokeWidth(1.0f);
            paint.setColor(colorLightLine);
            for (int i = 0; i < dataLine; i++) {
                canvas.drawLine(0, topBarHeight + i * contentItemHeight, termItemWidth + dataColumn *
                    contentItemWidth, topBarHeight + i * contentItemHeight, paint);
            }
            for (int i = 0; i < dataColumn; i++) {
                canvas.drawLine(termItemWidth + i * contentItemWidth, 0,
                                termItemWidth + i * contentItemWidth, topBarHeight + dataColumn *
                                    contentItemHeight, paint);
            }
        }

        void initDisplay() {
            countLineAndColumns();
            analyseNums();
        }

        private void countLineAndColumns() {
            if (lotteryInf != null) {
                dataLine = lotteryInf.length;
            }

            dataColumn = 0;
            int length = trendChartTopBarData.length;
            eachGroupLength = new int[length];
            for (int i = 0; i < length; i++) {
                String[] childText = trendChartTopBarData[i].getTrendTopText();
                eachGroupLength[i] = childText.length;
                dataColumn += childText.length;
            }

            selectedColumn = new boolean[dataColumn];
        }

        private void analyseNums() {
            lotteryNums = new boolean[dataLine][dataColumn];

            if (lotteryInf == null)
                return;

            for (int i = 0; i < dataLine; i++) {
                String balls = lotteryInf[i].getBalls();
                String[] nums = balls.split("\\|");
                for (int j = 0; j < nums.length; j++) {
                    String[] eachGroup = nums[j].split(",");
                    int orgLenght = 0;
                    for (int k = 0; k < j; k++) {
                        String[] childText = trendChartTopBarData[k].getTrendTopText();
                        orgLenght += childText.length;
                    }
                    for (int j2 = 0; j2 < eachGroup.length; j2++) {
                        lotteryNums[i][orgLenght + Integer.valueOf(eachGroup[j2]) - 1] = true;
                    }
                }
            }
        }

        private float lastX;
        private float lastY;

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = x;
                    lastY = y;
                    break;
                case MotionEvent.ACTION_UP:
                    if (Math.abs(x - lastX) + Math.abs(y - lastY) < 100) {
                        float xPosition = getScrollX() + x;
                        if (xPosition >= termItemWidth) {
                            int index = (int) ((x - termItemWidth + 1) / contentItemWidth);
                            if (selectedColumn[index]) {
                                selectedColumn[index] = false;
                            }
                            else {
                                selectedColumn[index] = true;
                            }
                            invalidate();
                            invalidateBalls();
                        }
                    }
                    break;
                default:
                    break;
            }
            return true;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(termItemWidth + dataColumn * contentItemWidth, topBarHeight + dataLine *
                contentItemHeight);
        }

    }

    public void setSelecteBallListener(TrendChartSelecteBallsListener selecteBallListener) {
        this.selecteBallListener = selecteBallListener;
    }

    public interface TrendChartSelecteBallsListener {
        public void selectedBalls(String ballsCode);
    }

}
