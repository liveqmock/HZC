package com.haozan.caipiao.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

public class PopUpEditText
    extends EditText {
    private Context context;

// private View waySwitchLayout;
// private PopupWindow popMessage;
// private TextView popMessageTv;
// private boolean isChecked = false;
// private boolean needShow = true;
// private int width;
// private int height;
// private int yPosition;
// public GestureDetector gestureDetector;
// private PopUpEditText popEdit;

    public PopUpEditText(Context context) {
        super(context);
    }

    public PopUpEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
// gestureDetector = new GestureDetector(this);
// popMessage = new PopupWindow(context);
// setPopWindowProperty();
        this.addTextChangedListener(tw);
// this.setOnTouchListener(this);
    }

// public void setYpoistion(int yPosition) {
// this.yPosition = yPosition;
// }
//
// public void setWidget(PopUpEditText popEdit) {
// this.popEdit = popEdit;
// }
//
// public void setNeed(boolean needShow) {
// this.needShow = needShow;
// }

// private void setPopWindowProperty() {
// LayoutInflater mLayoutInflater = LayoutInflater.from(context);
// waySwitchLayout = mLayoutInflater.inflate(R.layout.edittextpopupinf, null);
// popMessageTv = (TextView) waySwitchLayout.findViewById(R.id.popup_content);
// popMessageTv.setTextColor(context.getResources().getColor(R.color.black));
// popMessage.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
// popMessage.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
// popMessage.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
// popMessage.setOutsideTouchable(true);
// popMessage.setContentView(waySwitchLayout);
// }

// public void setPopContent(String popContent) {
// if (popContent == null) {
// if (popMessage.isShowing())
// dismPopWindow();
// }
// else {
// popMessageTv.setText(popContent);
// if (!popMessage.isShowing()) {
// if (needShow) {
// if (yPosition > 80)
// showPopWindow();
// else
// dismPopWindow();
// }
// }
// }
// }

// private void showPopWindow() {
// int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
// int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
// waySwitchLayout.measure(w, h);
// width = waySwitchLayout.getMeasuredWidth();
// height = waySwitchLayout.getMeasuredHeight();
// int[] location = new int[2];
// getLocationInWindow(location); // 获取在当前窗口内的绝对坐标
// waySwitchLayout.setBackgroundResource(R.drawable.popup_inline_error);
// popMessage.showAsDropDown(this, getWidth() - width, -(getHeight() + height));
// }

// private void dismPopWindow() {
// popMessage.dismiss();
// }

    TextWatcher tw = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 0) {
// if (isChecked) {
// if (!popMessage.isShowing())
// if (needShow)
// if (yPosition > 85)
// showPopWindow();
// else
// dismPopWindow();
// }
//
// if (editTextTextWatcher != null)
// editTextTextWatcher.setEditTextTextWatcher(PopUpEditText.this,true);
            }
            else {
// if (popMessage.isShowing()) {
// dismPopWindow();
// isChecked = true;
// }

            }
            if (s.length() != 0 && editTextTextWatcher != null)
                editTextTextWatcher.setEditTextTextWatcher(PopUpEditText.this, false);
            else if (s.length() == 0 && editTextTextWatcher != null)
                editTextTextWatcher.setEditTextTextWatcher(PopUpEditText.this, true);
        }
    };

// public void setPopDirection(int w, int h) {
// if (w == 240 && h == 320)
// needAbove = true;
// else
// needAbove = false;
// }

// boolean isShow = true;

// private void outPutWidgetPosition(PopUpEditText widget) {
// int[] location = new int[2];
// widget.getLocationInWindow(location);
// widget.setYpoistion(location[1]);
// System.out.println("TextView _money_top" + location[1]);
// }

// @Override
// public boolean onDown(MotionEvent e) {
// needShow = true;
// return false;
// }

// @Override
// public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
// dismPopWindow();
// if (popEdit != null)
// outPutWidgetPosition(popEdit);
// needShow = false;
// return false;
// }

// @Override
// public void onLongPress(MotionEvent e) {
//
// }

// @Override
// public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
// dismPopWindow();
// needShow = false;
// if (popEdit != null)
// outPutWidgetPosition(popEdit);
// return false;
// }

// @Override
// public void onShowPress(MotionEvent e) {
//
// }

// @Override
// public boolean onSingleTapUp(MotionEvent e) {
// return false;
// }

// @Override
// public boolean onTouchEvent(MotionEvent event) {
// switch (event.getAction()) {
// case MotionEvent.ACTION_MOVE:
// needShow = false;
// dismPopWindow();
// break;
//
// default:
// needShow = true;
// break;
// }
// return super.onTouchEvent(event);
// }

    EditTextTextWatcher editTextTextWatcher;

    public void setTextWatcher(EditTextTextWatcher editTextTextWatcher) {
        this.editTextTextWatcher = editTextTextWatcher;
    }

    public interface EditTextTextWatcher {
        public void setEditTextTextWatcher(View view, boolean isNull);
    }
}
