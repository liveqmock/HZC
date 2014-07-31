package com.haozan.caipiao.task;

import android.content.Context;
import android.content.SharedPreferences.Editor;

import com.haozan.caipiao.netbasic.AsyncConnectionBasic;
import com.haozan.caipiao.netbasic.ConnectionBasic;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.requestInf.ClientCommunicationRequestInf;
import com.haozan.caipiao.taskbasic.Task;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.Logger;
import com.haozan.caipiao.util.LotteryUtils;

/**
 * 获取版本对应的qq和联系方式
 * 
 * @author peter_wang
 * @create-time 2013-10-15 下午9:27:35
 */
public class ClientCommunicationTask
    extends Task {

    private Context mContext;

    public ClientCommunicationTask(Context context) {
        this.mContext = context;
    }

    @Override
    public void runTask() {
        ClientCommunicationRequestInf requestInf = new ClientCommunicationRequestInf();
        ConnectionBasic connect = new ConnectionBasic(mContext);
        String[] json = connect.requestGet(requestInf.getUrl(mContext));

        if (json != null && json.length == 2) {
            if (json[0].equals(AsyncConnectionBasic.GET_SUCCEED_STATUS)||json[0].equals("")) {
                JsonAnalyse ja = new JsonAnalyse();
                String telephone = ja.getData(json[1], "telephone");
                String qq = ja.getData(json[1], "qq");
                if(null==telephone)telephone = "4006938038";
                if(null==qq)qq="4006938038";
                Editor databaseData = ActionUtil.getEditor(mContext);
                Logger.inf(json[1]);
                if (telephone != null && !LotteryUtils.TELEPHONE.equals(telephone)) {
                    databaseData.putString("last_use_phone", telephone);
                    databaseData.commit();
                }
                if (qq != null && !LotteryUtils.QQ.equals(qq)) {
                    databaseData.putString("last_use_qq", qq);
                    databaseData.commit();
                }
            }
        }
    }
}