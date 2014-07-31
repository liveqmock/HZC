package com.haozan.caipiao.control.topup;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Handler;
import android.widget.TextView;

import com.haozan.caipiao.R;
import com.haozan.caipiao.control.ControlMessage;
import com.haozan.caipiao.db.BankDbBean;
import com.haozan.caipiao.db.BankTable;
import com.haozan.caipiao.netbasic.AsyncConnectionBasic;
import com.haozan.caipiao.request.topup.GetBankListRequest;
import com.haozan.caipiao.request.topup.GetBankListVersionRequest;
import com.haozan.caipiao.task.SaveBankListTask;
import com.haozan.caipiao.taskbasic.TaskPoolService;
import com.haozan.caipiao.types.topup.BankInfo;
import com.haozan.caipiao.util.ActionUtil;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.StringUtil;
import com.haozan.caipiao.util.UEDataAnalyse;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.error.LocalExceptionHandler;

/**
 * 银行列表选择控制器
 * 
 * @author peter_wang
 * @create-time 2013-11-9 下午3:58:08
 */
public abstract class SelectBankControlBasic {

    protected Context mContext;
    protected Handler mHandler;

    protected SharedPreferences mSharedPreferences;
    protected Editor mEditor;

    public SelectBankControlBasic(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;

        this.mSharedPreferences = ActionUtil.getSharedPreferences(context);
        this.mEditor = ActionUtil.getEditor(context);
    }

    /**
     * 从数据库中初始化银联列表,先获取所有银行，再提取出热门银行，排成银行数组
     * 
     * @param bankList
     * @param mDbBankList
     * @param topBankIcons
     * @param topBankNamesFull
     */
    public void initBankListInDb(ArrayList<BankInfo> hotBankList, ArrayList<BankInfo> mDbBankList,
                                 String[] topBankNamesFull, int[] topBankIcons) {
        insertBankFromDb(mDbBankList);

        if (mDbBankList.size() != 0) {
            insertHotBank(mDbBankList, hotBankList, topBankNamesFull, topBankIcons);
        }
    }

    public void insertHotBank(ArrayList<BankInfo> bankList, ArrayList<BankInfo> hotBankList,
                              String[] hotBankNames, int[] hotBankIcons) {
        for (int i = 0; i < hotBankNames.length; i++) {

            for (int j = 0; j < bankList.size(); j++) {
                BankInfo bank = (BankInfo) bankList.get(j).clone();

                if (bank.getChinesename().equals(hotBankNames[i])) {
                    bank.setIconResource(hotBankIcons[i]);
                    bank.setFirstchar("热门银行");
                    hotBankList.add(bank);
                }
            }
        }
    }

    private String generalQueryHotBank(String hotBankName) {
        return BankDbBean.CHINESENAME + "='" + hotBankName + "'";
    }

    public abstract void insertBankFromDb(ArrayList<BankInfo> bankList);

    public abstract void selectedBank(BankInfo bank);

    public abstract void setTitle(TextView tvTitle);

    private BankInfo readBankFromDb(String selction) {
        Cursor cursor =
            mContext.getContentResolver().query(BankTable.CONTENT_URI, null, selction, null,
                                                BankDbBean.SHORTHAND + " ASC");

        if (cursor != null && cursor.moveToNext()) {
            BankInfo bank = new BankInfo();
            bank.setChinesename(cursor.getString(cursor.getColumnIndexOrThrow(BankDbBean.CHINESENAME)));
            bank.setCredit(cursor.getString(cursor.getColumnIndexOrThrow(BankDbBean.CREDIT)));
            bank.setDeposit(cursor.getString(cursor.getColumnIndexOrThrow(BankDbBean.DEPOSIT)));
            bank.setFirstchar(cursor.getString(cursor.getColumnIndexOrThrow(BankDbBean.FIRSTCHAR)));
            bank.setKey(cursor.getString(cursor.getColumnIndexOrThrow(BankDbBean.KEY)));
            bank.setShorthand(cursor.getString(cursor.getColumnIndexOrThrow(BankDbBean.SHORTHAND)));
            cursor.close();
            return bank;
        }
        else {
            cursor.close();
            return null;
        }
    }

    public void getBankListVersion() {
        if (HttpConnectUtil.isNetworkAvailable(mContext)) {
            TaskPoolService.getInstance().requestService(new AsyncConnectionBasic(
                                                                                  new GetBankListVersionRequest(
                                                                                                                mContext,
                                                                                                                mHandler)));

        }
        else {
            ViewUtil.showTipsToast(mContext, mContext.getString(R.string.network_not_avaliable));
        }
    }

    public void analyseBankListVersion(String version, String url, ArrayList<BankInfo> bankList) {
        try {
            float bankListVersion = mSharedPreferences.getFloat("bank_list_local_version", -1);
            float serverVersion = Float.valueOf(version);
            if (serverVersion > bankListVersion || bankList.size() == 0) {
                getBankList(serverVersion, url, bankList);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            LocalExceptionHandler.exportExceptionInf(e);
        }
    }

    public void getBankList(float serverVersion, String url, ArrayList<BankInfo> bankList) {
        if (HttpConnectUtil.isNetworkAvailable(mContext)) {
            mHandler.sendEmptyMessage(ControlMessage.SHOW_PROGRESS);

            mEditor.putFloat("bank_list_local_version", serverVersion);

            TaskPoolService.getInstance().requestService(new GetBankListRequest(mContext, mHandler,
                                                                                serverVersion, url, bankList));

        }
        else {
            ViewUtil.showTipsToast(mContext, mContext.getString(R.string.network_not_avaliable));
        }
    }

    public void saveBankList(ArrayList<BankInfo> bankList) {
        mHandler.sendEmptyMessage(ControlMessage.SHOW_PROGRESS);

        TaskPoolService.getInstance().requestService(new SaveBankListTask(mContext, mHandler, bankList));
    }

    /**
     * 根据搜索内容显示相应的银行列表
     * 
     * @param searchContent
     * @param bankList 显示的银行列表
     * @param dbBankList 数据库中所有的银行列表
     */
    public void showSearchBank(String searchContent, ArrayList<BankInfo> bankList,
                               ArrayList<BankInfo> dbBankList) {
        bankList.clear();

        searchContent = searchContent.trim();
        if (StringUtil.containChinese(searchContent)) {
            for (int i = 0; i < dbBankList.size(); i++) {
                BankInfo bank = dbBankList.get(i);
                if (bank.getChinesename().contains(searchContent)) {
                    bankList.add(bank);
                }
            }
        }
        else {
            searchContent = searchContent.toLowerCase();// 转成小写
            // 根据输入字母，查找银行关键字和拼音简写中包含对应字母的银行
            for (int i = 0; i < dbBankList.size(); i++) {
                BankInfo bank = dbBankList.get(i);
                if (bank.getKey().contains(searchContent) || bank.getShorthand().contains(searchContent)) {
                    bankList.add(bank);
                }
            }
        }
    }

    public void submitDataStatisticsOpen() {
        UEDataAnalyse.onEvent(mContext, "open_select_bank");
    }
}
