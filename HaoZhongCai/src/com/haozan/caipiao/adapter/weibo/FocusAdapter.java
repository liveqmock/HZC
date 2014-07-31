package com.haozan.caipiao.adapter.weibo;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.weibo.UserProfileActivity;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.FocusListData;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;

public class FocusAdapter
    extends BaseAdapter {
    private ArrayList<FocusListData> focus;
    private Context context;
    private LayoutInflater inflater;

// private ProgressBar progressBar;

    public FocusAdapter(Context context, ArrayList<FocusListData> focus) {
        super();
        this.context = context;
        this.focus = focus;
// this.progressBar = progressBar;
        inflater = LayoutInflater.from(this.context);
    }

    private class ViewHolder {
        private TextView niceName;
        private TextView qianming;
        private ImageView avatar;
        private ImageView genderPic;
        private Button deleteFocus;
        private Button addFocus;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final ViewHolder viewHolder;
        final FocusListData fs = focus.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.focus_item, null);
            viewHolder = new ViewHolder();
            viewHolder.niceName = (TextView) convertView.findViewById(R.id.niceName);
            viewHolder.qianming = (TextView) convertView.findViewById(R.id.tvItemContent);
            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.ivItemPortrait);
            viewHolder.genderPic = (ImageView) convertView.findViewById(R.id.genderPic);
            viewHolder.deleteFocus = (Button) convertView.findViewById(R.id.deleteFocus);
            viewHolder.addFocus = (Button) convertView.findViewById(R.id.addFocus);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (fs.getBitmap() != null) {

            viewHolder.avatar.setImageBitmap(fs.getBitmap());
        }
        else {
            viewHolder.avatar.setImageResource(R.drawable.lucky_cat);
        }
        String name = fs.getName();
        if (name == null) {
            viewHolder.niceName.setText("匿名彩友");
        }
        else {
            if (fs.getName().equals("null") || fs.getName().equals("")) {
                viewHolder.niceName.setText("匿名彩友");
            }
            else {
                viewHolder.niceName.setText(fs.getName());
            }
        }

        String qm = fs.getSignature();
        if (qm == null || qm.equals("null")) {
            viewHolder.qianming.setText("个性签名:" + "");
        }
        else {
            viewHolder.qianming.setText("个性签名：" + fs.getSignature());
        }

        String gender = fs.getGender();
        if (gender.equals("1")) {
            viewHolder.genderPic.setImageResource(R.drawable.icon_male);
        }
        else {
            viewHolder.genderPic.setImageResource(R.drawable.icon_female);
        }
        // 判断是否已经关注了该用户，“true”表示已关注
        Boolean isfollow = fs.isIsfollows();
        if (isfollow == true) {
            viewHolder.deleteFocus.setVisibility(View.VISIBLE);
            viewHolder.addFocus.setVisibility(View.GONE);
        }
        else {
            viewHolder.deleteFocus.setVisibility(View.GONE);
            viewHolder.addFocus.setVisibility(View.VISIBLE);
        }

        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClass(context, UserProfileActivity.class);
                Bundle b = new Bundle();
                b.putInt("data_type", 4);
                b.putInt("userId", Integer.valueOf(fs.getId()));
                intent.putExtras(b);
                context.startActivity(intent);
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                    (new AnimationModel(((Activity) context))).overridePendingTransition(R.anim.push_to_right_in,
                                                                                         R.anim.push_to_right_out);
                }
            }
        });

        viewHolder.addFocus.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (HttpConnectUtil.isNetworkAvailable(context)) {
// progressBar.setVisibility(View.VISIBLE);
                    AddFocusTask delete = new AddFocusTask(fs, viewHolder.deleteFocus, viewHolder.addFocus);
                    delete.execute();
                }
                else {
                    Toast.makeText(context, R.string.network_not_avaliable, 2000).show();
                }
            }
        });

        viewHolder.deleteFocus.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (HttpConnectUtil.isNetworkAvailable(context)) {
// progressBar.setVisibility(View.VISIBLE);
                    DeleteFocusTask delete =
                        new DeleteFocusTask(fs, viewHolder.deleteFocus, viewHolder.addFocus);
                    delete.execute();
                }
                else {
                    Toast.makeText(context, R.string.network_not_avaliable, 2000).show();
                }
            }
        });
        return convertView;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return focus.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return focus.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    class AddFocusTask
        extends AsyncTask<Void, Void, String> {
        private FocusListData fs;
        private Button deleteFocus;
        private Button addFocus;

        public AddFocusTask(FocusListData fs, Button deleteFocus, Button addFocus) {
            this.fs = fs;
            this.deleteFocus = deleteFocus;
            this.addFocus = addFocus;
        }

        private HashMap<String, String> initHashMap()
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2004050");
            parameter.put("pid", LotteryUtils.getPid(context));
            parameter.put("phone",
                          HttpConnectUtil.encodeParameter(((LotteryApp) context.getApplicationContext()).getUsername()));
            parameter.put("type", "1");
            parameter.put("user_id", fs.getId());
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(context);
            String json = null;
            try {
                json = connectNet.getJsonGet(4, true, initHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            String json = (String) result;
            String detail = null;
            if (json != null) {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    detail = "关注成功";
                    fs.setIsfollows(true);
// progressBar.setVisibility(View.GONE);
                    addFocus.setVisibility(View.GONE);
                    deleteFocus.setVisibility(View.VISIBLE);
                }
                else if (status.equals("300")) {
// progressBar.setVisibility(View.GONE);
                    detail = "已经关注该用户";
                }
                else {
// progressBar.setVisibility(View.GONE);
                    detail = "关注失败";
                }
            }
            else {
                detail = "关注失败";
            }

            if (detail != null) {
                ViewUtil.showTipsToast(context, detail);
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }
    }

    private class DeleteFocusTask
        extends AsyncTask<Void, Void, String> {
        private FocusListData fs;
        private Button deleteFocus;
        private Button addFocus;

        public DeleteFocusTask(FocusListData fs, Button deleteFocus, Button addFocus) {
            // TODO Auto-generated constructor stub
            this.fs = fs;
            this.deleteFocus = deleteFocus;
            this.addFocus = addFocus;
        }

        private HashMap<String, String> initHashMap()
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2004050");
            parameter.put("pid", LotteryUtils.getPid(context));
            parameter.put("phone",
                          HttpConnectUtil.encodeParameter(((LotteryApp) context.getApplicationContext()).getUsername()));
            parameter.put("type", "2");
            parameter.put("user_id", fs.getId());
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(context);
            String json = null;
            try {
                json = connectNet.getJsonGet(4, true, initHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            String json = (String) result;
            String detail = null;
            if (json != null) {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
// ring = false;
                    detail = "取消关注成功";
                    fs.setIsfollows(false);
// progressBar.setVisibility(View.GONE);
                    addFocus.setVisibility(View.VISIBLE);
                    deleteFocus.setVisibility(View.GONE);
                }
                else if (status.equals("300")) {
// progressBar.setVisibility(View.GONE);
                    detail = "该用户已取消关注";
                }
                else {
// progressBar.setVisibility(View.GONE);
                    detail = "取消关注失败";
                }
            }
            else {
// progressBar.setVisibility(View.GONE);
                detail = "取消关注失败";
            }

            if (detail != null) {
                ViewUtil.showTipsToast(context, detail);
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }
    }
}
