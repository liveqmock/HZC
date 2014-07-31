package com.haozan.caipiao.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.haozan.caipiao.R;

/**
 * 包含提示语图标和文字的页面
 * 
 * @author peter_feng
 * @create-time 2013-3-16 下午3:06:14
 */
public class ContainTipsPageBasicActivity
    extends BasicActivity {
    private View showFailPage;
    private TextView message;

    private void setupTipsView() {
        message = (TextView) this.findViewById(R.id.message);
        showFailPage = this.findViewById(R.id.show_fail_page);
    }

    /**
     * 显示提示语页面
     * 
     * @param tips 提示语内容
     */
    protected void showTipsPage(String tips) {
        if (message == null && showFailPage == null) {
            setupTipsView();
        }
        message.setText(tips);
        showFailPage.setVisibility(View.VISIBLE);
        showFailPage.setEnabled(false);
    }

    protected void dismissTipsPage() {
        if (message == null && showFailPage == null) {
            setupTipsView();
        }
        showFailPage.setVisibility(View.GONE);
    }

    protected void showNetErrorPage() {
        if (message == null && showFailPage == null) {
            setupTipsView();
        }
        message.setText("未连接网络，点击查看网络");
        showFailPage.setVisibility(View.VISIBLE);
        showFailPage.setEnabled(true);
        showFailPage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intentToNetwork = new Intent("/");
                ComponentName componentName =
                    new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
                intentToNetwork.setComponent(componentName);
                intentToNetwork.setAction("android.intent.action.VIEW");
                startActivity(intentToNetwork);
            }
        });
    }

    protected void showFailPage() {
        if (message == null && showFailPage == null) {
            setupTipsView();
        }
        message.setText("查询失败，点击屏幕尝试刷新");
        showFailPage.setVisibility(View.VISIBLE);
        showFailPage.setEnabled(true);
        showFailPage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getInfAgain();
            }
        });
    }

    /**
     * 再次获取数据
     */
    protected void getInfAgain() {

    }
}
