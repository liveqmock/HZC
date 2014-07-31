package com.haozan.caipiao.widget;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.haozan.caipiao.R;

public class UPdateProgressBarDialog
    extends Dialog {
    private ProgressBar mProgress;
    private TextView mProgressNumber;
    private TextView mProgressPercent;

    public Button cancle;

    public static final int M = 1024 * 1024;
    public static final int K = 1024;

    private double dMax;
    private double dProgress;

    private int middle = K;

    private int prev = 0;

    private Handler mViewUpdateHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            double precent = dProgress / dMax;
            if (prev != (int) (precent * 100)) {
                mProgress.setProgress((int) (precent * 100));
                mProgressNumber.setText(df.format(dProgress) + "/" + df.format(dMax) +
                    (middle == K ? "K" : "M"));
                mProgressPercent.setText(nf.format(precent));
                prev = (int) (precent * 100);
            }

        }
    };
    private Context context;
    private OnDialogActionListener actionListener;
    private static final NumberFormat nf = NumberFormat.getPercentInstance();
    private static final DecimalFormat df = new DecimalFormat("###.##");

    public UPdateProgressBarDialog(Context context) {
        super(context, R.style.dialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_dialog_progress);
        setupViews();
        init();
    }

    private void setupViews() {
        mProgress = (ProgressBar) findViewById(R.id.progress);
        mProgress.setMax(100);
        mProgressNumber = (TextView) findViewById(R.id.progress_number);
        mProgressPercent = (TextView) findViewById(R.id.progress_percent);
        cancle = (Button) findViewById(R.id.cancle);
        cancle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                actionListener.onActionClick();
                dismiss();
            }
        });
    }

    private void init() {
        onProgressChanged();
        WindowManager m = ((Activity) context).getWindowManager();
        Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高

        LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
// p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.95
        p.gravity = Gravity.CENTER;
        getWindow().setAttributes(p); // 设置生效
    }

    private void onProgressChanged() {
        mViewUpdateHandler.sendEmptyMessage(0);
    }

    public double getDMax() {
        return dMax;
    }

    public void setDMax(double max) {
        if (max > M) {
            middle = M;
        }
        else {
            middle = K;
        }

        dMax = max / middle;
    }

    public double getDProgress() {
        return dProgress;
    }

    public void setDProgress(double progress) {
        dProgress = progress / middle;
        onProgressChanged();
    }

    public void setActionListener(OnDialogActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public interface OnDialogActionListener {
        public void onActionClick();
    }
}
