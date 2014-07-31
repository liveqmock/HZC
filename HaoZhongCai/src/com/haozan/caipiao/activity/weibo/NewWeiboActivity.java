package com.haozan.caipiao.activity.weibo;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.R;
import com.haozan.caipiao.adapter.FaceAdapter;
import com.haozan.caipiao.connect.NewWeiboService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.WeiboDraft;
import com.haozan.caipiao.util.anamation.AnimationModel;
import com.haozan.caipiao.util.weiboutil.Face;
import com.haozan.caipiao.util.weiboutil.FileUtils;
import com.haozan.caipiao.util.weiboutil.InfoHelper;
import com.haozan.caipiao.util.weiboutil.MediaUtils;
import com.haozan.caipiao.util.weiboutil.StringUtils;
import com.haozan.caipiao.util.weiboutil.TextUtil;
import com.haozan.caipiao.util.weiboutil.WeiboBaseActivity;
import com.haozan.caipiao.widget.CustomDialog;
import com.umeng.analytics.MobclickAgent;

public class NewWeiboActivity
    extends WeiboBaseActivity
    implements OnClickListener {
    private static final int REQUEST_CODE_GETIMAGE_BYSDCARD = 0;
    private static final int REQUEST_CODE_GETIMAGE_BYCAMERA = 1;
    private String thisLarge = null, theSmall = null;
    private ImageView imgView = null;
    private int what = -1;
    private ImageView imageView;
    private Bitmap bm;
    private String draft;
    private static String FEEDBACKFAIL = "发表动态失败";
    private ProgressBar progress;

    private String shareText;
    private TextView titleTextView;
    private Button clearButton;
    private Button sendButton;
    private Button listentButton;
    private EditText content;
    private String WeiboContent = null;
    private Button face;
    private Button keyboard;
    private Button camera;
    private GridView faceGrid;

    private int MAX_LENGTH = 140;
    int Rest_Length = MAX_LENGTH;
    private ImageView imageVerticalLine;
    private CustomDialog loginAgainDialog;

    private String accessToken, accessSecret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.new_weibo);
        setupViews();
        init();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (faceGrid.isShown()) {
                faceGrid.setVisibility(View.GONE);
                keyboard.setVisibility(View.GONE);
                face.setVisibility(View.VISIBLE);
            }
            else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setupViews() {
        accessToken = preferences.getString("accessToken", null);
        accessSecret = preferences.getString("accessSecret", null);
// Bundle bundle = getIntent().getExtras();
// if (bundle != null) {
// accessToken = bundle.containsKey("accessToken") ? bundle.getString("accessToken") : "";
// accessSecret = bundle.containsKey("accessSecret") ? bundle.getString("accessSecret") : "";
// }
// ///////////
        imageView = new ImageView(this);
        titleTextView = (TextView) findViewById(R.id.newCmtextView);
        clearButton = (Button) findViewById(R.id.title_btinit_left);
        imageVerticalLine = (ImageView) findViewById(R.id.weibo_right_button_line);
        clearButton.setVisibility(View.VISIBLE);
        sendButton = (Button) findViewById(R.id.title_btinit_right);
        listentButton = (Button) findViewById(R.id.clear_button);
        content = (EditText) findViewById(R.id.newComentText);
        content.setOnClickListener(this);
        progress = (ProgressBar) this.findViewById(R.id.progressBar);
        face = (Button) findViewById(R.id.face_button);
        face.setOnClickListener(this);
        keyboard = (Button) findViewById(R.id.keyboard_button);
        keyboard.setOnClickListener(this);
        camera = (Button) findViewById(R.id.camera_button);
        camera.setOnClickListener(this);
        imgView = (ImageView) findViewById(R.id.share_image);
        faceGrid = (GridView) findViewById(R.id.updater_faceGrid);
        faceGrid.setOnItemClickListener(itemClickListener);
        titleTextView.setText("写动态");
        clearButton.setText("清 空");
        sendButton.setText("发 表");
        clearButton.setOnClickListener(this);
        sendButton.setOnClickListener(this);
// listentButton.setOnClickListener(new ClearListener());
        content.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Rest_Length = MAX_LENGTH - content.getText().length();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (Rest_Length > 0) {
                    listentButton.setText("   " + Rest_Length + "   ");

                }
                else {
                    listentButton.setText(Html.fromHtml("<font color='red'>" + "   " + Rest_Length + "   " +
                        "</font>"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Rest_Length > 0) {

                    listentButton.setText("   " + Rest_Length + "   ");
                }
                else {
                    listentButton.setText(Html.fromHtml("<font color='red'>" + "   " + Rest_Length + "   " +
                        "</font>"));
                }
            }
        });
        listentButton.setText("   " + (140 - content.getText().length()) + "   ");
    }

    public void init() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            shareText = this.getIntent().getExtras().getString("shareText");
        }
        content.setText(shareText);
        draft = getDraft();
        // 将光标移动到EditText文本的右边
        Editable b = content.getText();
        content.setSelection(b.length());
    }

    private Boolean checkInput() {
        String warning = null;
        WeiboContent = content.getText().toString();
        if (WeiboContent == null || WeiboContent.equals("")) {
            warning = "写点什么吧";
        }
        else if (WeiboContent.length() > 140) {
            warning = "输入字数超出上限";
        }
        if (warning != null) {
            ViewUtil.showTipsToast(this, warning);
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.title_btinit_right) {
            if (checkInput() == true) {
                sending();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
        else if (v.getId() == R.id.title_btinit_left) {
            content.setText("");
            MAX_LENGTH = 140;
            listentButton.setText("   " + MAX_LENGTH + "   ");
        }
        else if (v.getId() == R.id.face_button) {
            // 隐藏软键盘
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            showFace();
        }
        else if (v.getId() == R.id.keyboard_button) {
            showKeyboard();
        }
        else if (v.getId() == R.id.newComentText) {
            keyboard.setVisibility(View.GONE);
            face.setVisibility(View.VISIBLE);
            faceGrid.setVisibility(View.GONE);
        }
        else if (v.getId() == R.id.camera_button) {
            CharSequence[] items = {"手机相册", "手机拍照", "清除照片"};
            imageChooseItem(items);
        }
    }

    private OnItemClickListener itemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            if (arg2 < Face.faceNames.length) {
                // android EditText插入字符串到光标所在位置
                int index = content.getSelectionStart();
                Editable edit = content.getEditableText();
                edit.insert(index,
                            TextUtil.formatImage("[" + Face.faceNames[arg2] + "]", NewWeiboActivity.this));
            }
        }
    };

    protected void sending() {
        if (HttpConnectUtil.isNetworkAvailable(NewWeiboActivity.this)) {
            /**
             * 暂时取消动态发布到新浪
             */
// if (isBindSnda()) {
// UpdateStatusTask task1 = new UpdateStatusTask();
// task1.execute();
// }
// else {
// loginAgainDialog = new CustomDialog(NewWeiboActivity.this, 5);
// loginAgainDialog.setTitle("注意");
// loginAgainDialog.setTextContent("还未绑定新浪微博，是否马上绑定？");
// loginAgainDialog.setFirstString("确定");
// loginAgainDialog.setSecondString("取消");
// loginAgainDialog.setActionListener(NewWeiboActivity.this);
// loginAgainDialog.show();
// }
            WeiboTask task = new WeiboTask();
            task.execute();
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
        }
    }

    class WeiboTask
        extends AsyncTask<Void, Object, String> {

        @Override
        protected void onPostExecute(String json) {
            restoreSubmit();
            String inf = null;
            if (json == null) {
                inf = FEEDBACKFAIL;
                ViewUtil.showTipsToast(NewWeiboActivity.this, inf);
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                String errordesc = analyse.getData(json, "error_desc");
                if (status.equals("200")) {
                    inf = "发表动态成功";
                    ViewUtil.showTipsToast(NewWeiboActivity.this, inf);
                    deleteDraft();// 删除草稿

                    Intent intent = new Intent();
                    intent.setClass(NewWeiboActivity.this, WeiboUserHallActivity.class);
                    setResult(RESULT_OK, intent); // 这理有2个参数(int resultCode,
                    // Intent intent)
                    // finish();
                    NewWeiboActivity.this.finish();
                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                        (new AnimationModel(NewWeiboActivity.this)).overridePendingTransition(R.anim.push_to_left_in,
                                                                                              R.anim.push_to_left_out);
                    }
                }
                else if (status.equals("306")) {
                    ViewUtil.showTipsToast(NewWeiboActivity.this, errordesc);
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(NewWeiboActivity.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(NewWeiboActivity.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else {
                    inf = FEEDBACKFAIL;
                    ViewUtil.showTipsToast(NewWeiboActivity.this, inf);
                }
            }
            progress.setVisibility(View.GONE);
        }

        @Override
        protected void onPreExecute() {
            sendButton.setText("   提交中   ");
            sendButton.setEnabled(false);
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... arg0) {
            // 将输入的火车符换成空格，否则会造成显示篇幅过长问题
            String newText =
                content.getText().toString().replaceAll(System.getProperty("line.separator"), " ");
            String c = HttpConnectUtil.encodeParameter(newText);
            NewWeiboService newweibo = new NewWeiboService(NewWeiboActivity.this, c);
            String status = newweibo.sending();
            return status;
        }
    }

    private void restoreSubmit() {
        sendButton.setText("   发表   ");
        sendButton.setEnabled(true);
    }

    DialogInterface.OnClickListener saveListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            saveDraft(content.getText().toString());
            finish();
        }
    };
    DialogInterface.OnClickListener dontSaveListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            finish();
        }
    };

    // 保存要发布的动态信息
    private void saveDraft(String text) {
        new WeiboDraft(this).save(text);
    }

    // 获取保存的动态信息
    private String getDraft() {
        return new WeiboDraft(this).get();
    }

    // 删除保存的动态信息
    private void deleteDraft() {
        new WeiboDraft(this).delete();
    }

    // 发表微博到新浪微博线程
    class UpdateStatusTask
        extends AsyncTask<Void, Object, String> {
        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
// weibo4android.Status status = null;
// weibo.setToken(accessToken, accessSecret);
// try {
// String msg = content.getText().toString();
// // if (msg.getBytes().length != msg.length()) {
// // msg = URLEncoder.encode(msg, "UTF-8");
// status = weibo.updateStatus(msg);
// System.out.println(status);
// // }
// if (status != null) {
// what = 1;
// }
// }
// catch (Exception e) {
// e.printStackTrace();
// }
            return null;
        }

        @Override
        protected void onPostExecute(String json) {
            progress.setVisibility(View.GONE);
            if (what > 0) {
                Toast.makeText(mContext, "分享成功", Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.GONE);
            }
            else {
                Intent intent = new Intent();
                intent.setClass(NewWeiboActivity.this, WeiboOauthActvity.class);
                Toast.makeText(mContext, "分享失败", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            progress.setVisibility(View.VISIBLE);
        }

    }

    public static byte[] readFileImage(String filename)
        throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(filename));
        int len = bufferedInputStream.available();
        byte[] bytes = new byte[len];
        int r = bufferedInputStream.read(bytes);
        if (len != r) {
            bytes = null;
            throw new IOException("读取文件不正确");
        }
        bufferedInputStream.close();
        return bytes;
    }

    // 把Bitmap转换成二进制
    public byte[] getBitmapByte(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        try {
            out.flush();
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    /**
     * 显示表情
     */
    private void showFace() {
        faceGrid.setVisibility(View.VISIBLE);
        face.setVisibility(View.GONE);
        keyboard.setVisibility(View.VISIBLE);
        if (faceGrid.getAdapter() == null) {
            faceGrid.setAdapter(new FaceAdapter(this, Face.faceNames));
        }
    }

    /**
     * 显示键盘
     */
    private void showKeyboard() {
        // 打开（自动控制的再次点击按钮就会消失的）
        InputMethodManager imm =
            (InputMethodManager) NewWeiboActivity.this.getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        faceGrid.setVisibility(View.GONE);
        keyboard.setVisibility(View.GONE);
        face.setVisibility(View.VISIBLE);
    }

    /**
     * 相机操作选择
     * 
     * @param items
     */
    public void imageChooseItem(CharSequence[] items) {
        AlertDialog imageDialog =
            new AlertDialog.Builder(NewWeiboActivity.this).setTitle("增加图片").setItems(items,
                                                                                     new DialogInterface.OnClickListener() {
                                                                                         public void onClick(DialogInterface dialog,
                                                                                                             int item) {
                                                                                             // 手机选图
                                                                                             if (item == 0) {
                                                                                                 Intent intent =
                                                                                                     new Intent(
                                                                                                                Intent.ACTION_GET_CONTENT);
                                                                                                 intent.setType("image/*");
                                                                                                 startActivityForResult(intent,
                                                                                                                        REQUEST_CODE_GETIMAGE_BYSDCARD);
                                                                                             }
                                                                                             // 拍照
                                                                                             else if (item == 1) {
                                                                                                 Intent intent =
                                                                                                     new Intent(
                                                                                                                "android.media.action.IMAGE_CAPTURE");
                                                                                                 String camerName =
                                                                                                     InfoHelper.getFileName();// 使用当前时间戳拼接一个唯一的文件名
                                                                                                 String fileName =
                                                                                                     "Share" +
                                                                                                         camerName +
                                                                                                         ".tmp";

                                                                                                 File camerFile =
                                                                                                     new File(
                                                                                                              InfoHelper.getCamerPath(),
                                                                                                              fileName);
                                                                                                 File folder =
                                                                                                     new File(
                                                                                                              InfoHelper.getCamerPath());
                                                                                                 if (!folder.exists()) {
                                                                                                     folder.mkdirs();
                                                                                                 }
                                                                                                 if (!camerFile.exists()) {
                                                                                                     try {
                                                                                                         camerFile.createNewFile();
                                                                                                     }
                                                                                                     catch (IOException e) {
                                                                                                         // TODO
// Auto-generated catch block
                                                                                                         e.printStackTrace();
                                                                                                     }
                                                                                                 }
                                                                                                 theSmall =
                                                                                                     InfoHelper.getCamerPath() +
                                                                                                         fileName;
                                                                                                 thisLarge =
                                                                                                     getLatestImage();

                                                                                                 Uri originalUri =
                                                                                                     Uri.fromFile(camerFile);
                                                                                                 intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                                                                                                 originalUri);
                                                                                                 startActivityForResult(intent,
                                                                                                                        REQUEST_CODE_GETIMAGE_BYCAMERA);
                                                                                             }
                                                                                             else if (item == 2) {
                                                                                                 thisLarge =
                                                                                                     null;
                                                                                                 imgView.setBackgroundDrawable(null);
                                                                                             }
                                                                                         }
                                                                                     }).create();

        imageDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GETIMAGE_BYSDCARD) {
            if (resultCode != RESULT_OK) {
                return;
            }

            if (data == null)
                return;

            Uri thisUri = data.getData();
            String thePath = InfoHelper.getAbsolutePathFromNoStandardUri(thisUri);

            // 如果是标准Uri
            if (StringUtils.isBlank(thePath)) {
                thisLarge = getAbsoluteImagePath(thisUri);
            }
            else {
                thisLarge = thePath;
            }

            String attFormat = FileUtils.getFileFormat(thisLarge);
            if (!"photo".equals(MediaUtils.getContentType(attFormat))) {
                Toast.makeText(mContext, "请选择图片文件！", Toast.LENGTH_SHORT).show();
                return;
            }
            String imgName = FileUtils.getFileName(thisLarge);

            Bitmap bitmap = loadImgThumbnail(imgName, MediaStore.Images.Thumbnails.MICRO_KIND);
            if (bitmap != null) {
                imgView.setBackgroundDrawable(new BitmapDrawable(bitmap));
            }
        }
        // 拍摄图片
        else if (requestCode == REQUEST_CODE_GETIMAGE_BYCAMERA) {
            if (resultCode != RESULT_OK) {
                return;
            }

            super.onActivityResult(requestCode, resultCode, data);

            Bitmap bitmap = InfoHelper.getScaleBitmap(mContext, theSmall);
            if (bitmap != null) {
                imgView.setBackgroundDrawable(new BitmapDrawable(bitmap));
            }
        }

        imgView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(thisLarge)), "image/*");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open garden personal create event");
        String eventName = "v2 open garden personal create event";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_garden_submit_event";
        MobclickAgent.onEvent(this, eventName, "new");
        besttoneEventCommint(eventName);
    }
}
