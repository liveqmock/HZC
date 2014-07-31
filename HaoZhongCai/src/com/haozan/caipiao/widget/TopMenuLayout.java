package com.haozan.caipiao.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;

/**
 * 选项卡控件，适合作为一个页面tab
 * 
 * @author peter_feng
 * @create-time 2013-5-25 下午10:50:51
 */
public class TopMenuLayout
    extends RelativeLayout
    implements OnClickListener {
    private String[] topMenuItemContent;
    private Context context;

    private LinearLayout menuRadioGroup;
    private ImageView shineImg;
    private int animationStartShine = 0;

    private int itemLength = 0;

    private int lastIndex = -1;

    private OnTabSelectedItemListener tabSelectedListener;

    public TopMenuLayout(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public TopMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private TopMenuLayout(Context context, String betCode, String lottery) {
        super(context);
        this.context = context;
        init();
    }

    private void init() {
        shineImg = new ImageView(context);
        shineImg.setImageResource(R.drawable.tab_shine);
        setBackgroundResource(R.drawable.top_tab_bg);
    }

    /**
     * 初始化选项卡内容。必须调用的方法，否则无数据显示
     * 
     * @param topMenuItemContent 传递进来的显示内容
     */
    public void setTopMenuItemContent(String[] topMenuItemContent) {
        this.topMenuItemContent = topMenuItemContent;
        initViews();
    }

    private void initShineImgPosition() {
        RelativeLayout.LayoutParams imgParams =
            new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT);
        imgParams.leftMargin = (int) (screenWidth / (topMenuItemContent.length * 2) - 17 / 2);
        shineImg.setLayoutParams(imgParams);
    }

    private DisplayMetrics dm;
    private int screenWidth;

    /**
     * 根据构造函数获得当前手机的屏幕系数
     */
    public void densityUtil(Context context) {
        // 获取当前屏幕
        dm = new DisplayMetrics();
        dm = context.getApplicationContext().getResources().getDisplayMetrics();
        // 密度因子
        screenWidth = dm.widthPixels;
    }

    private void initViews() {
        densityUtil(context);
        createTopMenu();
        createSelectedFlag();
        check(0);
    }

    private void createTopMenu() {
        itemLength = topMenuItemContent.length;
        RelativeLayout.LayoutParams params =
            new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        menuRadioGroup = new LinearLayout(context);
        menuRadioGroup.setOrientation(LinearLayout.HORIZONTAL);
        for (int i = 0; i < itemLength; i++) {
            TextView menuItem = new TextView(context);
            menuItem.setId(i);
            RadioGroup.LayoutParams itemParams =
                new RadioGroup.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
            menuItem.setPadding(0, 0, 0, 0);
            menuItem.setGravity(Gravity.CENTER);
            menuItem.setTextColor(getResources().getColor(R.color.light_purple));
            menuItem.setText(topMenuItemContent[i]);
            menuItem.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            menuItem.setOnClickListener(this);
            menuRadioGroup.addView(menuItem, itemParams);
        }
        addView(menuRadioGroup, params);
    }

    private void createSelectedFlag() {
        RelativeLayout.LayoutParams itemParams =
            new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(shineImg, itemParams);
        initShineImgPosition();
    }

    /**
     * 选中某一项
     * 
     * @param position 从0开始，0代表选中第一项
     */
    public void check(int position) {
        if (position == lastIndex) {
            return;
        }
        animateArrow(position);
        if (lastIndex >= 0) {
            ((TextView) menuRadioGroup.getChildAt(lastIndex)).setTextColor(getResources().getColor(R.color.light_purple));
        }
        ((TextView) menuRadioGroup.getChildAt(position)).setTextColor(getResources().getColor(R.color.red_text));
        lastIndex = position;
        if (tabSelectedListener != null) {
            tabSelectedListener.onTabSelectedAction(position);
        }
    }

    private void animateArrow(int position) {
        int animationEndShine = screenWidth / itemLength * position;
        moveFrontBg(shineImg, animationStartShine, animationEndShine, 0, 0);
        animationStartShine = animationEndShine;
    }

    private void moveFrontBg(View v, int startX, int toX, int startY, int toY) {
        TranslateAnimation anim = new TranslateAnimation(startX, toX, startY, toY);
        anim.setDuration(50);
        anim.setFillAfter(true);
        v.startAnimation(anim);
    }

    public void setTabSelectedListener(OnTabSelectedItemListener tabSelectedListener) {
        this.tabSelectedListener = tabSelectedListener;
    }

    public interface OnTabSelectedItemListener {
        /**
         * 选择了哪个tab
         * 
         * @param selection 从0开始，0代表第一个
         */
        public void onTabSelectedAction(int selection);
    }

    @Override
    public void onClick(View v) {
        int position = v.getId();
        check(position);
    }
}
