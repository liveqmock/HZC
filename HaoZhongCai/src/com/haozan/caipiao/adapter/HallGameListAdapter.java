package com.haozan.caipiao.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.task.PluginDownloadTask;
import com.haozan.caipiao.types.GameDownloadInf;
import com.haozan.caipiao.util.PluginUtils;
import com.haozan.caipiao.widget.CircleProgress;
import com.haozan.caipiao.widget.imageloader.ImageLoader;
import com.haozan.caipiao.widget.imageloader.ImageLoader.onFinishGetImgListener;

public class HallGameListAdapter
    extends GameListAdapter implements onFinishGetImgListener {
    ImageLoader imageLoader;

    public HallGameListAdapter(Context context, ArrayList<GameDownloadInf> gameList) {
        super(context, gameList);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (gameList != null) {
            final ViewHolder viewHolder;
            final int currentPostion = position;
            final GameDownloadInf game = gameList.get(position);
            imageLoader = new ImageLoader(context);
            imageLoader.setonFinishGetImgListener(this);
            if (game != null) {
                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(this.context);
                    convertView = inflater.inflate(R.layout.hall_game_item_view, null);
                    viewHolder = new ViewHolder();
                    viewHolder.nameView = (TextView) convertView.findViewById(R.id.game_name);
                    viewHolder.infView = (TextView) convertView.findViewById(R.id.game_description);
                    viewHolder.gameIcon = (ImageView) convertView.findViewById(R.id.game_icon);
                    viewHolder.ll_download = (LinearLayout) convertView.findViewById(R.id.ll_download);
                    viewHolder.ll_right = (LinearLayout) convertView.findViewById(R.id.ll_right);
                    viewHolder.tv_schedule = (TextView) convertView.findViewById(R.id.tv_schedule);
                    viewHolder.mCircleProgressBar1 =
                        (CircleProgress) convertView.findViewById(R.id.roundBar1);
                    convertView.setTag(viewHolder);
                }
                else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                // 加载游戏logo
                if (null != game.getGameIconUrl() && !"".equals(game.getGameIconUrl())) {
                    imageLoader.DisplayImage(game.getGameIconUrl(), viewHolder.gameIcon);
                }
                viewHolder.nameView.setText(game.getGameName());
                viewHolder.infView.setText(game.getGameDescription());
                viewHolder.ll_download.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // 无网络时无法启动游戏
// if (HttpConnectUtil.isNetworkAvailable(context)) {
                        goGame(currentPostion);
// }
// else {
// String inf = context.getResources().getString(R.string.network_not_avaliable);
// Toast toast = ToastUtils.getToastInstance(context);
// toast.setText(inf);
// toast.show();
// }
                    }
                });
                // action图标设置
                if (PluginUtils.checkGameExist(context, gameList.get(currentPostion).getGamePackageName())) {
                    if (PluginUtils.checkPluginVersionNodialog(gameList.get(currentPostion), context)) {
                        gameList.get(currentPostion).setStatus("3");// 升级
                        viewHolder.ll_right.setVisibility(View.VISIBLE);
                        // viewHolder.action.setVisibility(View.VISIBLE);
                        // viewHolder.action.setBackgroundResource(R.drawable.icon_up);
                        viewHolder.mCircleProgressBar1.setBackgroundResource(R.drawable.game_list_up);
                    }
                    else {
                        gameList.get(currentPostion).setStatus("2");// 启动
                        // viewHolder.action.setVisibility(View.INVISIBLE);
                        viewHolder.ll_right.setVisibility(View.GONE);
                    }
                }
                else {
                    gameList.get(currentPostion).setStatus("1");// 下载
                    viewHolder.ll_right.setVisibility(View.VISIBLE);
                    // viewHolder.action.setVisibility(View.VISIBLE);
                    // viewHolder.action.setBackgroundResource(R.drawable.icon_down);
                    viewHolder.mCircleProgressBar1.setBackgroundResource(R.drawable.game_list_down);
                }
                // action点击监听
                viewHolder.ll_right.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        if (gameList.get(currentPostion).getStatus().equals("1") ||
                            gameList.get(currentPostion).getStatus().equals("3")) {
                            PluginDownloadTask downloadTask =
                                new PluginDownloadTask(context, gameList.get(currentPostion).getGameName(),
                                                       viewHolder.mCircleProgressBar1,
                                                       viewHolder.tv_schedule, viewHolder.ll_right);
                            downloadTask.execute(gameList.get(currentPostion).getGameDownloadUrl());
                        }
                    }
                });

            }
        }
        return convertView;
    }

    public class ViewHolder {
        TextView nameView;
        TextView infView;
        ImageView gameIcon;
        LinearLayout ll_right;
        LinearLayout ll_download;
        CircleProgress mCircleProgressBar1;
        TextView tv_schedule;
    }

    @Override
    public void onFinishGetImg() {
        // TODO Auto-generated method stub
        
    }
}
