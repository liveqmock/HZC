package com.haozan.caipiao.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.umeng.analytics.MobclickAgent;

/**
 * 号码统计设置功能
 * 
 * @author peter_feng
 * @create-time 2012-11-1 下午8:50:22
 */
public class LotteryStatisticsSetting
    extends BasicActivity
    implements OnClickListener {
    private static final int[] IDS = {R.id.first_item, R.id.second_item, R.id.third_item, R.id.forth_item};
    private static final int[] TERMS = {20, 30, 50, 100};

    private LinearLayout hotNumWholeLayout;
    private LinearLayout omitWholeLayout;
    private Button searchTerm20;
    private Button searchTerm30;
    private Button searchTerm50;
    private Button searchTerm100;
    private RelativeLayout hotNumLayout;
    private ImageView selectHotNum;
    private RelativeLayout omitLayout;
    private ImageView selectOmit;
    private Button makesure;
    private Button cancle;

    private ArrayList<Button> buttons;
    private String type;
    // 选中那个期次的，从0开始，0代表第一个期次20期，以此类推
    private int present = 0;
    // 设置是否显示冷热门号码
    private boolean showHotNum = false;
    // 设置是否显示遗漏号码
    private boolean showOmit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lottery_statistics_setting);
        setUpViews();
        init();
    }

    protected void setUpViews() {
        hotNumWholeLayout = (LinearLayout) this.findViewById(R.id.layout_hot);
        omitWholeLayout = (LinearLayout) this.findViewById(R.id.layout_omit);
        searchTerm20 = (Button) findViewById(R.id.first_item);
        searchTerm20.setOnClickListener(this);
        searchTerm30 = (Button) findViewById(R.id.second_item);
        searchTerm30.setOnClickListener(this);
        searchTerm50 = (Button) findViewById(R.id.third_item);
        searchTerm50.setOnClickListener(this);
        searchTerm100 = (Button) findViewById(R.id.forth_item);
        searchTerm100.setOnClickListener(this);
        hotNumLayout = (RelativeLayout) this.findViewById(R.id.layout_show_hot);
        hotNumLayout.setOnClickListener(this);
        selectHotNum = (ImageView) this.findViewById(R.id.select_show_hot);
        omitLayout = (RelativeLayout) this.findViewById(R.id.layout_show_omit);
        omitLayout.setOnClickListener(this);
        selectOmit = (ImageView) this.findViewById(R.id.select_omit);

        makesure = (Button) this.findViewById(R.id.make_sure);
        makesure.setOnClickListener(this);
        cancle = (Button) this.findViewById(R.id.cancle);
        cancle.setOnClickListener(this);
    }

    private void init() {
        buttons = new ArrayList<Button>();
        buttons.add(searchTerm20);
        buttons.add(searchTerm30);
        buttons.add(searchTerm50);
        buttons.add(searchTerm100);
        getDataFromIntent();
    }

    // 获取intent传递过来的数据并初始化
    private void getDataFromIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getString("type");
            if (type.equals("hot")) {
                omitWholeLayout.setVisibility(View.GONE);
                present = bundle.getInt("index");
                showHotNum = bundle.getBoolean("show_hot");
                selectTerm(present);
                if (showHotNum) {
                    selectHotNum.setBackgroundResource(R.drawable.choosing_select);
                }
                else {
                    selectHotNum.setBackgroundResource(R.drawable.choosing_not_select);
                }
            }
            else {
                hotNumWholeLayout.setVisibility(View.GONE);
                showOmit = bundle.getBoolean("show_omit");
                if (showOmit) {
                    selectOmit.setBackgroundResource(R.drawable.choosing_select);
                }
                else {
                    selectOmit.setBackgroundResource(R.drawable.choosing_not_select);
                }
            }
        }
    }

    // 选中冷热号显示期数的某一个选项
    private void selectTerm(int index) {
        int length = buttons.size();
        for (int i = 0; i < length; i++) {
            if (i != index) {
                buttons.get(i).setEnabled(true);
                buttons.get(i).setTextColor(getResources().getColor(R.color.black));
            }
            else {
                buttons.get(i).setEnabled(false);
                buttons.get(i).setTextColor(getResources().getColor(R.color.white));
                present = i;
            }
        }
    }

    // 上传用户选择号码统计设置情况事件
    private void submitStatisticBetNumStatistics() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: bet statistics setting");
        if (type.equals("hot")) {
            if (showOmit) {
                map.put("more_inf", "show hot data");
            }
            else {
                map.put("more_inf", "not show hot data");
            }
            map.put("extra_inf", "hot num term " + TERMS[present]);
        }
        else {
            if (showOmit) {
                map.put("more_inf", "show omit data");
            }
            else {
                map.put("more_inf", "not show omit data");
            }
        }
        String eventName = "bet statistics setting";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
        HashMap<String, String> mapUmeng = new HashMap<String, String>();
        map.put("user", appState.getUsername());
        if (type.equals("hot")) {
            map.put("show_hot", "" + showHotNum);
        }
        else {
            map.put("show_omit", "" + showOmit);
            map.put("hot_num_term", "" + TERMS[present]);
        }
        String eventNameUmeng = "bet_statistics_setting";
        MobclickAgent.onEvent(this, eventNameUmeng, mapUmeng);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.make_sure) {
            if (appState.getUsername() == null && (present != 0 || showHotNum || showOmit)) {
                ActionUtil.toLogin(this, "统计号码设置");
            }
            else {
                submitStatisticBetNumStatistics();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("type", type);
                bundle.putInt("index", present);
                bundle.putBoolean("show_hot", showHotNum);
                bundle.putBoolean("show_omit", showOmit);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
        else if (v.getId() == R.id.cancle) {
            finish();
        }
        else if (v.getId() == R.id.layout_show_hot) {
            if (showHotNum) {
                if (present != 0) {
                    checkLogin();
                }
                else {
                    makesure.setText("  确 定  ");
                }
                showHotNum = false;
                selectHotNum.setBackgroundResource(R.drawable.choosing_not_select);
            }
            else {
                checkLogin();
                showHotNum = true;
                selectHotNum.setBackgroundResource(R.drawable.choosing_select);
            }
        }
        else if (v.getId() == R.id.layout_show_omit) {
            if (showOmit) {
                makesure.setText("  确 定  ");
                showOmit = false;
                selectOmit.setBackgroundResource(R.drawable.choosing_not_select);
            }
            else {
                checkLogin();
                showOmit = true;
                selectOmit.setBackgroundResource(R.drawable.choosing_select);
            }
        }
        else if (v.getId() == R.id.first_item) {
            if (showOmit || showHotNum) {
                checkLogin();
            }
            selectTerm(0);
            makesure.setText("  确 定  ");
        }
        else if (v.getId() == R.id.second_item) {
            selectTerm(1);
            checkLogin();
        }
        else if (v.getId() == R.id.third_item) {
            selectTerm(2);
            checkLogin();
        }
        else if (v.getId() == R.id.forth_item) {
            selectTerm(3);
            checkLogin();
        }
    }

    private void checkLogin() {
        if (appState.getUsername() == null) {
            makesure.setText("  登 录  ");
        }
        else {
            makesure.setText("  确 定  ");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                makesure.setText("  确 定  ");
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open bet statistics setting");
        String eventName = "open bet statistics setting";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_bet_statistics_setting";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            LotteryStatisticsSetting.this.finish();
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(LotteryStatisticsSetting.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                              R.anim.push_to_left_out);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
