package com.haozan.caipiao.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.view.DragListView;
import com.haozan.caipiao.view.DragListView.OnDragListViewListener;
import com.umeng.analytics.MobclickAgent;

public class PlusMinusLottery
    extends BasicActivity
    implements OnDragListViewListener {
    private String lotterySelected;
    private String lotteryNotSelected;
    private ArrayList<String> existList;
    private ArrayList<String> notExistList;
    private ArrayList<ImageView> iconList;

    private Editor databaseData;
    private SharedPreferences preferences;
    private static int existNum = 0;
    private DragListView dragListView;
    private Button comlete;
    private Button reset;
    private boolean isClick = false;
    private boolean isMove = false;

    private static List<String> list = null;
    private DragListAdapter adapter = null;

    public static List<String> groupKey = new ArrayList<String>();
    public static List<String> navList = new ArrayList<String>();
    public static List<String> moreList = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plus_minus_lottery);
        setupViews();
        init();
        setupItems();
        initData();
    }

    private void resetDataBaseData() {
        databaseData.remove("lottery_selected");
        databaseData.remove("lottery_not_selected");
        databaseData.remove("exist_lottery_num");
        databaseData.remove("lottery_item_move");
    }

    private void setupViews() {
        dragListView = (DragListView) this.findViewById(R.id.drag_list);
        comlete = (Button) this.findViewById(R.id.complete_action);
        comlete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (isClick) {
                    resetDataBaseData();

                    databaseData.putString("lottery_selected", lotteryArray01.toString());
                    databaseData.commit();

                    if (lotteryArray02.length() != 0) {
                        databaseData.putString("lottery_not_selected", lotteryArray02.toString());
                        databaseData.commit();
                    }

                    databaseData.putInt("exist_lottery_num", listNum);
                    databaseData.commit();
                    existList.clear();
                    notExistList.clear();
                    databaseData.putBoolean("lottery_item_move", true);
                    databaseData.commit();
                    setupItems();

                    StringBuilder existCode = null;
                    if (existList.size() > 0) {
                        existCode = new StringBuilder();
                        existCode.append(existList.get(0));
                        for (int i = 0; i < existList.size() - 1; i++) {
                            existCode.append("|" + existList.get(i + 1));
                        }
                    }
                    StringBuilder notExistCode = null;
                    if (notExistList.size() > 0) {
                        notExistCode = new StringBuilder();
                        notExistCode.append(notExistList.get(0));
                        for (int i = 0; i < notExistList.size() - 1; i++) {
                            notExistCode.append("|" + notExistList.get(i + 1));
                        }
                        databaseData.putString("lottery_not_selected", notExistCode.toString());
                    }
                    else {
                        databaseData.putString("lottery_not_selected", null);
                    }
                    if (existCode != null) {
                        if (!existCode.toString().equals(lotterySelected)) {
                            databaseData.putString("lottery_selected", existCode.toString());
                            databaseData.commit();
                            setResult(RESULT_OK);
                        }
                    }
                    if (notExistCode != null) {
                        if (!notExistCode.toString().equals(lotteryNotSelected)) {
                            databaseData.putString("lottery_not_selected", notExistCode.toString());
                            databaseData.commit();
                        }
                    }
                }
                PlusMinusLottery.this.finish();
            }
        });
        reset = (Button) this.findViewById(R.id.reset_action);
        reset.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                PlusMinusLottery.this.finish();
            }
        });
    }

    private void setupItems() {
        isMove = preferences.getBoolean("lottery_item_move", false);
        if (isMove) {
            lotterySelected = preferences.getString("lottery_selected", LotteryUtils.lotteryArray);
            lotteryNotSelected = preferences.getString("lottery_not_selected", null);
        }
        else {
            lotterySelected = preferences.getString("lottery_selected", LotteryUtils.lotteryArrayAdd);
            lotteryNotSelected =
                preferences.getString("lottery_not_selected", LotteryUtils.lotteryArrayMinus);
        }
        String[] lotteryExistKind = lotterySelected.split("\\|");
        for (int i = 0; i < lotteryExistKind.length; i++) {
            existList.add(lotteryExistKind[i]);
        }

        if (lotteryNotSelected != null) {
            String[] lotteryNotExistKind = lotteryNotSelected.split("\\|");
            for (int i = 0; i < lotteryNotExistKind.length; i++) {
                notExistList.add(lotteryNotExistKind[i]);
            }
        }
        existNum = preferences.getInt("exist_lottery_num", LotteryUtils.lotteryArray.split("\\|").length);
    }

    protected void updateIconObserver() {
        if (iconList.size() <= 2) {
            for (int i = 0; i < iconList.size(); i++)
                iconList.get(i).setVisibility(View.INVISIBLE);
        }
        else {
            for (int i = 0; i < iconList.size(); i++)
                iconList.get(i).setVisibility(View.VISIBLE);
        }
    }

    private void init() {
        existList = new ArrayList<String>();
        notExistList = new ArrayList<String>();
        iconList = new ArrayList<ImageView>();
        databaseData = getSharedPreferences("user", 0).edit();
        preferences = getSharedPreferences("user", 0);
        isMove = preferences.getBoolean("lottery_item_move", false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            StringBuilder existCode = null;
            if (existList.size() > 0) {
                existCode = new StringBuilder();
                existCode.append(existList.get(0));
                for (int i = 0; i < existList.size() - 1; i++) {
                    existCode.append("|" + existList.get(i + 1));
                }
            }
            StringBuilder notExistCode = null;
            if (notExistList.size() > 0) {
                notExistCode = new StringBuilder();
                notExistCode.append(notExistList.get(0));
                for (int i = 0; i < notExistList.size() - 1; i++) {
                    notExistCode.append("|" + notExistList.get(i + 1));
                }
                databaseData.putString("lottery_not_selected", notExistCode.toString());
            }
            else
                databaseData.putString("lottery_not_selected", null);
            if (existCode != null) {
                if (!existCode.toString().equals(lotterySelected)) {
                    databaseData.putString("lottery_selected", existCode.toString());
                    databaseData.commit();
                    setResult(RESULT_OK);
                }
            }
            if (notExistCode != null) {
                if (!notExistCode.toString().equals(lotteryNotSelected)) {
                    databaseData.putString("lottery_not_selected", notExistCode.toString());
                    databaseData.commit();
                }
            }
            PlusMinusLottery.this.finish();
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open plus minus lottery");
        String eventName = "v2 open plus minus lottery";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_edit_lotterykind_show";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    private void initData() {
        // 数据结果
        list = new ArrayList<String>();
        list.clear();
        groupKey.clear();
        navList.clear();
        moreList.clear();

        // groupKey存放的是分组标签
        groupKey.add("大厅显示的彩种");
        groupKey.add("未显示的彩种");
        groupKey.add("");

        for (int i = 0; i < existList.size(); i++) {
            navList.add(LotteryUtils.getLotteryName(existList.get(i)));
        }
        list.add("大厅显示的彩种");
        list.addAll(navList);

        for (int i = 0; i < notExistList.size(); i++) {
            moreList.add(LotteryUtils.getLotteryName(notExistList.get(i)));
        }
        list.add("未显示的彩种");
        list.addAll(moreList);

        dragListView.setExstTemNum(existNum);
        adapter = new DragListAdapter(this, list);
        dragListView.setAdapter(adapter);
        dragListView.setOnDragListViewListener(this);
    }

    public static class DragListAdapter
        extends ArrayAdapter<String> {

        public DragListAdapter(Context context, List<String> objects) {
            super(context, 0, objects);
        }

        public List<String> getList() {
            return list;
        }

        @Override
        public boolean isEnabled(int position) {
            if (groupKey.contains(getItem(position))) { // 如果是分组标签，返回false，不能选中，不能点击
                return false;
            }
            return super.isEnabled(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = convertView;
            if (groupKey.contains(getItem(position))) { // 如果是分组标签，就加载分组标签的布局文件，两个布局文件显示效果不同
                view = LayoutInflater.from(getContext()).inflate(R.layout.drag_list_item_tag, null);
                ImageView tagImageBottom = (ImageView) view.findViewById(R.id.tag_bottom_bg);// tag top
                ImageView tagBottom = (ImageView) view.findViewById(R.id.tag_bottom);// tag bottom
                if (position == 0)
                    tagImageBottom.setVisibility(View.GONE);
                if (list.get(list.size() - 1).equals("未显示的彩种")) {
                    if (position != 0)
                        tagBottom.setVisibility(View.VISIBLE);
                }
                else
                    tagBottom.setVisibility(View.GONE);
            }
            else {
                // 如果是正常数据项标签，就加在正常数据项的布局文件
                view = LayoutInflater.from(getContext()).inflate(R.layout.drag_list_item, null);
// if (position == list.size() - 1)
// view.setBackgroundResource(R.drawable.plus_minus_bottom);
// else
// view.setBackgroundResource(R.drawable.plus_minus_middle);
            }

            TextView textView = (TextView) view.findViewById(R.id.drag_list_item_text);
            textView.setText(getItem(position));
            if (groupKey.contains(getItem(position)) == false) {
                ImageView controlIcon = (ImageView) view.findViewById(R.id.dele_list_view_item);
                int index = list.indexOf("未显示的彩种");
                if (position <= index - 1 && position > 0) {
                    controlIcon.setImageResource(R.drawable.icon_delete);
// controlIcon.setBackgroundResource(R.drawable.minus_item);
                    if (index == 3) {
                        if (position == 1||position == 2){
                            controlIcon.setVisibility(View.INVISIBLE);
                        }
                    }
                }
                else if (position > index) {
                    controlIcon.setImageResource(R.drawable.icon_add);
// controlIcon.setBackgroundResource(R.drawable.plus_item);
                }
            }
            return view;
        }
    }

    StringBuilder lotteryArray01 = new StringBuilder();
    StringBuilder lotteryArray02 = new StringBuilder();
    int listNum;

    @Override
    public void onDragListViewItemClick(String list, int listNum) {
        this.listNum = listNum;
        lotteryArray01.delete(0, lotteryArray01.length());
        lotteryArray02.delete(0, lotteryArray02.length());
        String[] listArray = list.split("\\未显示的彩种");
        String[] listItemArray01 = listArray[0].split("\\,");
        String[] listItemArray02;
        if (listArray[1].equals("]"))
            listItemArray02 = listArray[1].split("\\,");
        else
            listItemArray02 = listArray[1].split("\\]")[0].split("\\,");

        for (int i = 1; i < listItemArray01.length; i++) {
            lotteryArray01.append(getLotteryNameInverse(listItemArray01[i]));
            lotteryArray01.append("|");
        }

        for (int j = 1; j < listItemArray02.length; j++) {
            lotteryArray02.append(getLotteryNameInverse(listItemArray02[j]));
            lotteryArray02.append("|");
        }
        lotteryArray01.delete(lotteryArray01.length() - 1, lotteryArray01.length());
        if (lotteryArray02.length() != 0)
            lotteryArray02.delete(lotteryArray02.length() - 1, lotteryArray02.length());
        isClick = true;
    }

    private String getLotteryNameInverse(String name) {
        for (int j = 0; j < LotteryUtils.LOTTERY_NAMES.length; j++)
            if (name.trim().equals(LotteryUtils.LOTTERY_NAMES[j]))
                return LotteryUtils.LOTTERY_ID[j];
        return "";
    }
}