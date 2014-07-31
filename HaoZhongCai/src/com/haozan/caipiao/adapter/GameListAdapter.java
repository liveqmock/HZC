package com.haozan.caipiao.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.Login;
import com.haozan.caipiao.types.GameDownloadInf;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.PluginUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 游戏列表适配器
 * 
 * @author peter_feng
 * @create-time 2012-8-14 上午11:31:03
 */
public class GameListAdapter
    extends BaseAdapter {
    // 彩种类别及投注方式
    private String lotteryKind;
    private String way;

    protected Context context;
    protected ArrayList<GameDownloadInf> gameList;
    private LotteryApp appState;

    public GameListAdapter(Context context, ArrayList<GameDownloadInf> gameList) {
        this.context = context;
        this.gameList = gameList;
        appState = (LotteryApp) ((Activity) context).getApplication();
    }

    @Override
    public int getCount() {
        return gameList.size();
    }

    @Override
    public Object getItem(int index) {
        return gameList.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public final class ViewHolder {
        private TextView gameItem;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        final int currentPostion = position;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(this.context);
            convertView = inflater.inflate(R.layout.game_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.gameItem = (TextView) convertView.findViewById(R.id.game_name);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.gameItem.setText(gameList.get(position).getGameName());
        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (HttpConnectUtil.isNetworkAvailable(context)) {
                    goGame(currentPostion);
                }
                else {
                    String inf = context.getResources().getString(R.string.network_not_avaliable);
                    ViewUtil.showTipsToast(context, inf);
                }
            }
        });
        return convertView;
    }

    /**
     * 跳转到游戏
     * 
     * @param currentPostion 指定游戏序号
     */
    protected void goGame(int currentPostion) {
        if (PluginUtils.checkGameExist(context, gameList.get(currentPostion).getGamePackageName())) {
            if (checkUserInf(currentPostion)) {
                Bundle bundle = new Bundle();
                bundle.putString("pid", LotteryUtils.getPid(context));
                bundle.putString("key", LotteryUtils.getKey(context));
                bundle.putString("phone", appState.getUsername());
                bundle.putString("time", appState.getTime());
                bundle.putString("integrity", String.valueOf(appState.getPerfectInf()));
                bundle.putString("sessionid", appState.getSessionid());
                if (lotteryKind != null && way != null) {
                    bundle.putString("lottery", lotteryKind);
                    bundle.putString("way", way);
                }
                sumbitGameListClickAnalyseData(gameList.get(currentPostion).getGameIndex());
                PluginUtils.goPlugin(context, bundle, gameList.get(currentPostion).getGamePackageName(),
                                     gameList.get(currentPostion).getGameActivityName());
            }
        }
        else {
            PluginUtils.showPluginDownloadDialog(context, gameList.get(currentPostion).getGameName(), "确定下载" +
                                                     gameList.get(currentPostion).getGameName() + "?",
                                                 gameList.get(currentPostion).getGameDownloadUrl(), false);
        }
    }

    // 统计游戏点击事件
    private void sumbitGameListClickAnalyseData(String gameName) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: game list click");
        map.put("more_inf", gameName);
        String eventName = "game list click";
        FlurryAgent.onEvent(eventName, map);
        String eventNameUmeng = "game_list_click";
        MobclickAgent.onEvent(context, eventNameUmeng, gameName);
    }

    // 判断是否登录，无登陆进入登陆页面
    private boolean checkUserInf(int currentPostion) {
        if (appState.getUsername() == null) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("forwardFlag", "games");
            bundle.putString("position", "" + currentPostion);
            bundle.putBoolean("ifStartSelf", false);
            intent.putExtras(bundle);
            intent.setClass(context, Login.class);
            context.startActivity(intent);
            return false;
        }
        else
            return true;
    }

    public void setLotteryBet(String lotteryKind, String way) {
        this.lotteryKind = lotteryKind;
        this.way = way;
    }
}
