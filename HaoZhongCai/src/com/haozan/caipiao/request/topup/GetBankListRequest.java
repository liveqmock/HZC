package com.haozan.caipiao.request.topup;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;

import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.netbasic.ConnectionBasic;
import com.haozan.caipiao.taskbasic.Task;
import com.haozan.caipiao.types.topup.BankInfo;
import com.haozan.caipiao.util.ActionUtil;

/**
 * 获取银行列表接口 ，如：http://m.haozan88.com//Html//banklist_10.html
 * 
 * @author peter_wang
 * @create-time 2013-11-9 下午4:21:12
 */
public class GetBankListRequest
    extends Task {
    private Context mContext;
    private Handler mHandler;

    private float mVersion;
    private String mUrl;

    private ArrayList<BankInfo> mBankList;

    public GetBankListRequest(Context context, Handler handler, float version, String url,
                              ArrayList<BankInfo> bankList) {
        this.mContext = context;
        this.mHandler = handler;

        this.mVersion = version;
        this.mUrl = url;
        this.mBankList = bankList;
    }

    public void responseData(String result) {
        if (null != result) {
            try {
                mBankList.clear();

                JSONArray jsonArray = new JSONArray(result);
                int length = jsonArray.length();
                for (int i = 0; i < length; i++) {
                    JSONObject jo = jsonArray.getJSONObject(i);
                    addBanks(jo);
                }

                setBankListVersionFlag();

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 成功获取到银行数据后保存当前版本号
    private void setBankListVersionFlag() {
        Editor editor = ActionUtil.getEditor(mContext);
        editor.putFloat("bank_list_version", mVersion);
        editor.commit();
    }

    public void addBanks(JSONObject jo) {
        BankInfo info = new BankInfo();
        try {
            info.setChinesename(jo.getString("z"));
            info.setCredit(jo.getString("c"));
            info.setDeposit(jo.getString("d"));
            info.setKey(jo.getString("k"));
            info.setShorthand(jo.getString("f"));
            info.setFirstchar(info.getShorthand().substring(0, 1));
            mBankList.add(info);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void runTask() {
        ConnectionBasic connect = new ConnectionBasic(mContext);
        String[] inf = connect.requestGet(mUrl);
        if (inf != null && inf.length == 2) {
            responseData(inf[1]);
            Message message = Message.obtain(mHandler, ControlMessage.GET_BANK_LIST_SUCCESS, mBankList);
            message.sendToTarget();
        }
        else {
            Message message = Message.obtain(mHandler, ControlMessage.GET_BANK_LIST_FAIL, "银行列表获取失败");
            message.sendToTarget();
        }
    }
}