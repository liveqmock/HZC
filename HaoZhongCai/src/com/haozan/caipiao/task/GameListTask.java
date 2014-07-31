package com.haozan.caipiao.task;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Handler;

import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.netbasic.AsyncConnectionBasic;
import com.haozan.caipiao.netbasic.ConnectionBasic;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.requestInf.PluginInfRequestInf;
import com.haozan.caipiao.taskbasic.Task;
import com.haozan.caipiao.util.ActionUtil;

/**
 * 获取插件信息
 * 
 * @author peter_wang
 * @create-time 2013-10-15 下午9:28:37
 */
public class GameListTask
    extends Task {
    private Context mContext;
    private Handler mHandler;

    public GameListTask(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
    }

    @Override
    public void runTask() {

        PluginInfRequestInf requestInf = new PluginInfRequestInf();
        ConnectionBasic connect = new ConnectionBasic(mContext);
        String[] json = connect.requestGet(requestInf.getGameUrl(mContext));

        if (json != null && json.length == 2) {
            if (json[0].equals(AsyncConnectionBasic.GET_SUCCEED_STATUS)) {

                JsonAnalyse analyse = new JsonAnalyse();
                String response_data = analyse.getData(json[1], "response_data");

                Editor databaseData = ActionUtil.getEditor(mContext);
                databaseData.putString("game_list", response_data);
                databaseData.commit();

                mHandler.sendEmptyMessage(ControlMessage.GAME_INF);
            }
        }
    }
}