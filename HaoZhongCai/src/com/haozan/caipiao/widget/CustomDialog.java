package com.haozan.caipiao.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;

/**
 * 自定义对话框
 * 
 * @author peter_feng
 * @create-time 2013-6-25 下午12:10:38
 */
public class CustomDialog
    extends Dialog {

    public CustomDialog(Context context, int theme) {
        super(context, theme);
    }

    public CustomDialog(Context context) {
        super(context);
    }

    /**
     * Helper class for creating a custom dialog
     */
    public static class Builder {

        private Context context;
        private Boolean isWarning = false;
        private String title = "提示";
        private String message;
        private String positiveButtonText;
        private boolean isEmphasis = false;
        private String negativeButtonText;
        private View contentView;

        private DialogInterface.OnClickListener positiveButtonClickListener, negativeButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * Set the Dialog message from String
         * 
         * @param title
         * @return
         */
        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * Set the Dialog message from resource
         * 
         * @param title
         * @return
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * 设置对话框为警告类型
         * 
         * @return
         */
        public Builder setWarning() {
            this.isWarning = true;
            return this;
        }

        /**
         * Set the Dialog title from resource
         * 
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         * 
         * @param title
         * @return
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * Set a custom content view for the Dialog. If a message is set, the contentView is not added to the
         * Dialog...
         * 
         * @param v
         * @return
         */
        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         * 
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText, DialogInterface.OnClickListener listener) {
            this.positiveButtonText = (String) context.getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * 设置左键是否重点提示，设置成红色
         * 
         * @return
         */
        public Builder beEmphasis() {
            this.isEmphasis = true;
            return this;
        }

        /**
         * Set the positive button text and it's listener
         * 
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(String positiveButtonText, DialogInterface.OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * Set the negative button resource and it's listener
         * 
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNegativeButton(int negativeButtonText, DialogInterface.OnClickListener listener) {
            this.negativeButtonText = (String) context.getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * Set the negative button text and it's listener
         * 
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNegativeButton(String negativeButtonText, DialogInterface.OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * Create the custom dialog
         */
        public CustomDialog create() {
            LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final CustomDialog dialog = new CustomDialog(context, R.style.dialog);
            View layout = inflater.inflate(R.layout.custom_dialog, null);
            dialog.addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT,
                                                           LayoutParams.WRAP_CONTENT));
            // set the dialog title
            ((TextView) layout.findViewById(R.id.title)).setText(title);
            // set the confirm button
            if (positiveButtonText != null) {
                ((Button) layout.findViewById(R.id.positive_button)).setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.positive_button)).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                        }
                    });

                    if (isEmphasis) {
                        ((Button) layout.findViewById(R.id.positive_button)).setBackgroundResource(R.drawable.custom_button_highlight);
                        ((Button) layout.findViewById(R.id.positive_button)).setTextColor(context.getResources().getColor(R.color.white));
                    }
                }
            }
            else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.positive_button).setVisibility(View.GONE);
            }
            // set the cancel button
            if (negativeButtonText != null) {
                ((Button) layout.findViewById(R.id.negative_button)).setText(negativeButtonText);
                if (negativeButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.negative_button)).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            negativeButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                        }
                    });
                }
            }
            else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.negative_button).setVisibility(View.GONE);
            }
            // set the content message
            if (message != null) {
                ((TextView) layout.findViewById(R.id.message)).setText(Html.fromHtml(message));
            }
            else if (contentView != null) {
                // if no message set
                // add the contentView to the dialog body
                ((LinearLayout) layout.findViewById(R.id.content)).removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.content)).addView(contentView,
                                                                           new LayoutParams(
                                                                                            LayoutParams.WRAP_CONTENT,
                                                                                            LayoutParams.WRAP_CONTENT));
            }
            if (!isWarning) {
                layout.findViewById(R.id.title_icon).setVisibility(View.GONE);
                ((TextView) layout.findViewById(R.id.title)).setTextColor(context.getResources().getColor(R.color.black));
            }
            dialog.setContentView(layout);
            setWindowSize(context, dialog);
            return dialog;
        }

        private void setWindowSize(Context context, CustomDialog dialog) {
            WindowManager m = ((Activity) context).getWindowManager();
            Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高

            LayoutParams p = dialog.getWindow().getAttributes(); // 获取对话框当前的参数值
            // p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
            p.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.9
            p.gravity = Gravity.CENTER;
            dialog.getWindow().setAttributes(p);
        }
    }
}