package com.haozan.caipiao.activity.weibo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.ContainTipsPageBasicActivity;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.ConmentListData;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.util.weiboutil.TextUtil;
import com.haozan.caipiao.util.weiboutil.TimeUtil;
import com.haozan.caipiao.widget.pulltorefresh.PullToRefreshBase;
import com.haozan.caipiao.widget.pulltorefresh.PullToRefreshListView;
import com.haozan.caipiao.widget.pulltorefresh.PullToRefreshBase.OnHeaderRefreshListener;
import com.haozan.caipiao.widget.pulltorefresh.PullToRefreshBase.OnLastItemVisibleListener;
import com.umeng.analytics.MobclickAgent;

public class ConmentListActivity
    extends ContainTipsPageBasicActivity
    implements OnClickListener, OnHeaderRefreshListener, OnLastItemVisibleListener {
    private final static String FAIL = "查询评论信息失败或信息已删除";
    private final static int SIZE = 10;
    private int page = 1;

    private ConmentsAdapter adapter;
    private PullToRefreshListView pulllistView;
    // 下拉刷新
    private RelativeLayout titleLy;
    private TextView titleTextView;
    private Button conmentButton;
    private ImageButton toTopButton;
    private ArrayList<ConmentListData> conmentArray;

    private String weiboId;
    private String conweiboId;
    private String userId;
    private String nickName;
    private boolean ring = false;
    public boolean ifClearArray = false;
    ListView actualListView;
    View footView;
    boolean ifGetMoreData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weibo_conments_list);
        setupViews();
        init();
    }

    public void setupViews() {
        titleLy = (RelativeLayout) findViewById(R.id.title);
        titleLy.setOnClickListener(this);
        // 接收传过来的微博Id
        weiboId = this.getIntent().getExtras().getString("weiboId");

        titleTextView = (TextView) findViewById(R.id.newCmtextView);
        conmentButton = (Button) findViewById(R.id.title_btinit_right);
        conmentButton.setOnClickListener(this);
        titleTextView.setText("动态评论");
        conmentButton.setText("  写评论   ");
        toTopButton = (ImageButton) findViewById(R.id.list_quick_to_top);
        toTopButton.setOnClickListener(this);

        pulllistView = (PullToRefreshListView) findViewById(R.id.weiboConmentList);
        pulllistView.setOnHeaderRefreshListener(this);
        pulllistView.setOnLastItemVisibleListener(this);
        actualListView = pulllistView.getRefreshableView();
        footView = View.inflate(ConmentListActivity.this, R.layout.list_item_load_more_view, null);
        // TODO 注册上下文菜单
        registerForContextMenu(this.pulllistView);
    }

    public void init() {
        // 返回顶部按钮显示
// BasicWeibo.backTopShow(conmentlist, toTopButton);
        conmentArray = new ArrayList<ConmentListData>();
        adapter = new ConmentsAdapter(ConmentListActivity.this, conmentArray);
        actualListView.setAdapter(adapter);
        loadData();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.title) {
            BasicWeibo.backTop(actualListView);
            toTopButton.setVisibility(View.GONE);
        }
        else if (v.getId() == R.id.list_quick_to_top) {
            BasicWeibo.backTop(actualListView);
            toTopButton.setVisibility(View.GONE);
        }
        else if (v.getId() == R.id.title_btinit_right) {
            Intent intent = new Intent();
            intent.setClass(ConmentListActivity.this, NewConmentsActivity.class);
            Bundle b = new Bundle();
            b.putString("weiboId", weiboId);
            intent.putExtras(b);
            ConmentListActivity.this.startActivityForResult(intent, 0);
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(ConmentListActivity.this)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                         R.anim.push_to_right_out);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { // resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                ring = false;
                conmentArray = new ArrayList<ConmentListData>();
                adapter = new ConmentsAdapter(ConmentListActivity.this, conmentArray);
                actualListView.setAdapter(adapter);
                conmentArray.clear();
                page = 1;
                ConmentsTask task = new ConmentsTask();
                task.execute();
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class ConmentsTask
        extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            GetConmentsService getWeibo = new GetConmentsService(ConmentListActivity.this, page, SIZE);
            String json = getWeibo.sending();
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            actualListView.removeFooterView(footView);
            dismissProgress();
            ifGetMoreData = true;
            pulllistView.onHeaderRefreshComplete();
            if (json == null) {
                showFail(failTips);
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    String data = analyse.getData(json, "response_data");

                    if (ifClearArray == true) {
                        conmentArray.clear();
                        ifClearArray = false;
                    }
                    if (data.equals("[]") == false) {
                        int lastSize = conmentArray.size();
                        getConmentArray(conmentArray, data);
                        int allSize = conmentArray.size();
                        if (allSize - lastSize < 10) {
                            ifGetMoreData = false;
                        }
                    }
                    else {
                        showNoData();
                    }
                }
                else if (status.equals("202")) {
                    showNoData();
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(ConmentListActivity.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(ConmentListActivity.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else {
                    showFail(failTips);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
            dismissTipsPage();
        }
    }

    private void showFail(String failInf) {
        if (conmentArray.size() == 0) {
            showTipsPage(failInf);
            adapter.notifyDataSetChanged();
        }
        else {
            ViewUtil.showTipsToast(this, failInf);
        }
    }

    public void showNoData() {
        if (conmentArray.size() == 0) {
            showTipsPage(FAIL);
            pulllistView.setVisibility(View.GONE);
        }
        else {
            ViewUtil.showTipsToast(this, noMoreData);
        }
    }

    class GetConmentsService {
        private Context context;
        private int page;
        private int size;

        public GetConmentsService(Context context, int page, int size) {
            this.context = context;
            this.page = page;
            this.size = size;
        }

        private HashMap<String, String> initHashMap()
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2004040");
            parameter.put("pid", LotteryUtils.getPid(ConmentListActivity.this));
            parameter.put("phone", HttpConnectUtil.encodeParameter(appState.getUsername()));
            parameter.put("type", "1");
            parameter.put("reply_id", weiboId);
            parameter.put("page_no", "" + page);
            parameter.put("page_size", "" + size);
            return parameter;
        }

        public String sending() {
            ConnectService connectNet = new ConnectService(ConmentListActivity.this);
            String json = null;
            try {
                json = connectNet.getJsonGet(4, true, initHashMap());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }
    }

    public void getConmentArray(ArrayList<ConmentListData> myweiboArray, String json) {
        if (json != null) {
            JSONArray hallArray;
            try {
                hallArray = new JSONArray(json);
                for (int i = 0; i < hallArray.length(); i++) {
                    ConmentListData conmentdata = new ConmentListData();
                    JSONObject jo = hallArray.getJSONObject(i);
                    conmentdata.setUserid(jo.getString("user_id"));
                    conmentdata.setWeiboid(jo.getString("weibo_id"));
                    conmentdata.setName(jo.getString("nickname"));
                    conmentdata.setTime(jo.getString("issue_date"));
                    conmentdata.setContent(jo.getString("content"));
                    conmentdata.setRetweetCount(jo.getString("retweet_count"));
                    conmentdata.setReplyCount(jo.getString("reply_count"));
                    // weibodata.setId(jo.getInt("weibo_id"));
                    myweiboArray.add(conmentdata);

                }
                adapter.notifyDataSetChanged();
                page++;
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class ConmentsAdapter
        extends BaseAdapter {
        private ArrayList<ConmentListData> conments;
        private Context context;
        private LayoutInflater inflater;

        public ConmentsAdapter(Context context, ArrayList<ConmentListData> conments) {
            super();
            this.context = context;
            this.conments = conments;
            inflater = LayoutInflater.from(this.context);
        }

        private final class ViewHolder {
            private TextView niceName;
            private TextView content;
            private TextView creattime;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            ConmentListData con = conments.get(position);
            if (convertView != null) {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            else {
                convertView = inflater.inflate(R.layout.weibo_conments_item, null);
                viewHolder = new ViewHolder();
                viewHolder.niceName = (TextView) convertView.findViewById(R.id.niceName);
                viewHolder.content = (TextView) convertView.findViewById(R.id.tvItemContent);
                viewHolder.creattime = (TextView) convertView.findViewById(R.id.tvItemDate);
                convertView.setTag(viewHolder);
            }
            String name = con.getName();
            final String userId = con.getUserid();
            final String conweiboId = con.getWeiboid();
            final String nickName = con.getName();
            if (name == null) {
                viewHolder.niceName.setText("匿名彩友");
            }
            else {
                if (con.getName().equals("null") || con.getName().equals("")) {
                    viewHolder.niceName.setText("匿名彩友");
                }
                else {
// if (appState.getUserid() == userId || appState.getUserid().equals(userId)) {
// viewHolder.niceName.setText("我");
// }
// else {
                    viewHolder.niceName.setText(con.getName());
// }
                }
            }
            viewHolder.content.setText(TextUtil.formatContent(con.getContent(), context));
            viewHolder.creattime.setText(TimeUtil.getTimeStr(BasicWeibo.stringDate(con.getTime(),
                                                                                   "yyyy-MM-dd HH:mm:ss")));

            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    openContextMenu(v, conweiboId, userId, nickName);
                }
            });
            return convertView;
        }

        @Override
        public int getCount() {
            return conments.size();
        }

        @Override
        public Object getItem(int position) {
            return conments.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    protected void openContextMenu(View v, String weiboId1, String userId1, String nickName2) {
        conweiboId = weiboId1;
        userId = userId1;
        nickName = nickName2;
        openContextMenu(v);
    }

    public boolean onContextItemSelected(MenuItem item) {
// AdapterContextMenuInfo lm = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case 1://
                Intent intent = new Intent();
                intent.setClass(ConmentListActivity.this, NewConmentsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("weiboId", weiboId);
                bundle.putString("nickName", nickName);
                intent.putExtras(bundle);
                ConmentListActivity.this.startActivityForResult(intent, 0);
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                    (new AnimationModel(ConmentListActivity.this)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                             R.anim.push_to_right_out);
                }
                break;
            case 2://
                if (appState.getUserid() == userId || appState.getUserid().equals(userId)) {
                    Intent intent1 = new Intent();
                    intent1.setClass(ConmentListActivity.this, MyProfileActivity.class);
                    ConmentListActivity.this.startActivity(intent1);
                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                        (new AnimationModel(ConmentListActivity.this)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                                 R.anim.push_to_right_out);
                    }
                }
                else {
                    Intent intent1 = new Intent();
                    intent1.setClass(ConmentListActivity.this, UserProfileActivity.class);
                    Bundle b = new Bundle();
                    b.putInt("userId", Integer.valueOf(userId));
                    intent1.putExtras(b);
                    ConmentListActivity.this.startActivity(intent1);
                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                        (new AnimationModel(ConmentListActivity.this)).overridePendingTransition(R.anim.push_to_right_in,
                                                                                                 R.anim.push_to_right_out);
                    }
                }
                break;
            case 3://
                if (HttpConnectUtil.isNetworkAvailable(ConmentListActivity.this)) {
                    DeleteConmentTask task = new DeleteConmentTask();
                    task.execute();
                }
                else {
                    ViewUtil.showTipsToast(this, noNetTips);
                }
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("功能");
        menu.add(1, 1, 1, "回复评论");
        menu.add(1, 2, 2, "查看用户资料");
        if (appState.getUserid() == userId || appState.getUserid().equals(userId)) {
            menu.add(1, 3, 3, "删除评论");
        }
    }

    class DeleteConmentTask
        extends AsyncTask<Void, Void, String> {

        private HashMap<String, String> initHashMap()
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2004080");
            parameter.put("pid", LotteryUtils.getPid(ConmentListActivity.this));
            parameter.put("phone", HttpConnectUtil.encodeParameter(appState.getUsername()));
            parameter.put("type", "1");
            parameter.put("content_id", conweiboId);
            return parameter;
        }

        @Override
        protected String doInBackground(Void... params) {
            ConnectService connectNet = new ConnectService(ConmentListActivity.this);
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
            String json = (String) result;
            if (json == null) {
                ViewUtil.showTipsToast(ConmentListActivity.this, "删除失败");
            }
            else {
                if (json != null) {
                    JsonAnalyse analyse = new JsonAnalyse();
                    String status = analyse.getStatus(json);
                    if (status.equals("200")) {
                        ring = false;
                        ViewUtil.showTipsToast(ConmentListActivity.this, "删除成功");
// 刷新
                        conmentArray.clear();
                        page = 1;
                        ConmentsTask getconmentsTask = new ConmentsTask();
                        getconmentsTask.execute();
                    }
                    else if (status.equals("302")) {
                        OperateInfUtils.clearSessionId(ConmentListActivity.this);
                        showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                    }
                    else if (status.equals("304")) {
                        OperateInfUtils.clearSessionId(ConmentListActivity.this);
                        showLoginAgainDialog(getResources().getString(R.string.login_again));
                    }
                    else {
                        ViewUtil.showTipsToast(ConmentListActivity.this, "删除失败");
                    }
                }
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open garden comment list");
        String eventName = "v2 open garden comment list";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_garden_comment_list";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ConmentListActivity.this.finish();
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimationModel(ConmentListActivity.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                         R.anim.push_to_left_out);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void loadData() {
        if (HttpConnectUtil.isNetworkAvailable(ConmentListActivity.this)) {
            ConmentsTask task = new ConmentsTask();
            task.execute();
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
        }
    }

    @Override
    public void onHeaderRefresh(PullToRefreshBase refreshView) {
        page = 1;
        ifClearArray = true;
        loadData();
    }

    @Override
    public void onLastItemVisible() {
        if (ifGetMoreData) {
            actualListView.addFooterView(footView);
            loadData();
        }
        else {
            actualListView.removeFooterView(footView);
        }
    }
}
