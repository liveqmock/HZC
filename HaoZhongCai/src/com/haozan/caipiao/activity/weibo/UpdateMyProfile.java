package com.haozan.caipiao.activity.weibo;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haozan.caipiao.LotteryApp;
import com.haozan.caipiao.R;
import com.haozan.caipiao.activity.BasicActivity;
import com.haozan.caipiao.connect.GetMyProfileService;
import com.haozan.caipiao.connect.GetServerTime;
import com.haozan.caipiao.jgravatar.Gravatar;
import com.haozan.caipiao.netbasic.AndroidHttpClient;
import com.haozan.caipiao.netbasic.ConnectService;
import com.haozan.caipiao.netbasic.JsonAnalyse;
import com.haozan.caipiao.types.MyProfileData;
import com.haozan.caipiao.util.HttpConnectUtil;
import com.haozan.caipiao.util.LotteryUtils;
import com.haozan.caipiao.util.OperateInfUtils;
import com.haozan.caipiao.util.ViewUtil;
import com.haozan.caipiao.util.security.EncryptUtil;
import com.umeng.analytics.MobclickAgent;

public class UpdateMyProfile
    extends BasicActivity
    implements OnClickListener {

    private String newName = "img.jpg";
    // private String actionUrl =
    // "http://192.168.0.3/BuKeServ/servlet/VerificationImage";

    File picture;
    Bitmap photo;

    public static final int NONE = 0;
    public static final int PHOTOHRAPH = 1;// 拍照
    public static final int PHOTOZOOM = 2; // 缩放
    public static final int PHOTORESOULT = 3;// 结果
    public static final String IMAGE_UNSPECIFIED = "image/*";

    private static String FEEDBACKFAIL = "修改资料失败";
    private String Name = null;
    private String Email = null;
    private String City = null;
    private String QianMing = null;
    private TextView titleTextView;
    private ImageView avatar;
    private Button upTouxiang;
    private RadioButton man;
    private RadioButton women;

    private EditText niceName;
    private EditText city;
    private EditText content;
    private EditText email;
    private Bitmap bitmap;
    private Button save;

    private ProgressBar firstLoadProgress;
    MyProfileData profileArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.update_my_profile);
        setupViews();
        init();

    }

    private void setupViews() {
        firstLoadProgress = (ProgressBar) this.findViewById(R.id.progressBar);
        titleTextView = (TextView) findViewById(R.id.initName);
        man = (RadioButton) findViewById(R.id.men);
        women = (RadioButton) findViewById(R.id.women);

        titleTextView.setText("修改网络资料");
        avatar = (ImageView) findViewById(R.id.potoView);
        upTouxiang = (Button) findViewById(R.id.upPoto);
        upTouxiang.setOnClickListener(this);
        niceName = (EditText) findViewById(R.id.niceNameET);
        city = (EditText) findViewById(R.id.cityET);
        content = (EditText) findViewById(R.id.conmentET);
        email = (EditText) findViewById(R.id.emailET);
        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(this);
    }

    private void init() {

        if (HttpConnectUtil.isNetworkAvailable(UpdateMyProfile.this)) {
            MyProfileTask task = new MyProfileTask();
            task.execute();
        }
        else {
            firstLoadProgress.setVisibility(View.GONE);
            ViewUtil.showTipsToast(this, noNetTips);
        }

    }

    // -------------------------------------------------------------------------------------------
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.save) {
            if (checkInput() == true) {
                sendin();
                final InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive())
                    imm.hideSoftInputFromWindow(save.getWindowToken(), 0);
// uploadFile();
            }
        }
        else if (v.getId() == R.id.upPoto) {
            final String[] items = {"相机拍摄", "手机相册"};
// final String[] items = {"相机拍摄"};
            AlertDialog.Builder builder = new Builder(UpdateMyProfile.this);
            builder.setTitle("选择头像");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (items[which].equals("相机拍摄")) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                        Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                                                              "temp.jpg")));
                        startActivityForResult(intent, PHOTOHRAPH);
                    }
                    else if (items[which].equals("手机相册")) {
                        Intent intent = new Intent(Intent.ACTION_PICK, null);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
                        startActivityForResult(intent, PHOTOZOOM);
                    }
                }

            });
            builder.show();
        }
    }

    /*
     * 照片获取部分
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == NONE)
            return;
        // 拍照
        if (requestCode == PHOTOHRAPH) {
            // 设置文件保存路径这里放在跟目录下
            picture = new File(Environment.getExternalStorageDirectory() + "/temp.jpg");
            startPhotoZoom(Uri.fromFile(picture));
        }
        if (data == null)
            return;
        // 读取相册缩放图片
        if (requestCode == PHOTOZOOM) {
            startPhotoZoom(data.getData());
        }
        // 处理结果
        if (requestCode == PHOTORESOULT) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                photo = extras.getParcelable("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
// photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);// (0 -
// // 100)压缩文件
// ThumbnailUtils.extractThumbnail(photo, 1, 1);
                avatar.setImageBitmap(photo);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);

        // //////////////////////
// if (requestCode == PHOTOHRAPH) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(picture + "", options); // 此时返回bm为空(拍照)
        options.inJustDecodeBounds = false;
        // 缩放比
        int be = (int) (options.outHeight / (float) 100);
        if (be <= 0)
            be = 1;
        options.inSampleSize = be;
        // 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(picture + "", options);
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        System.out.println(w + "   " + h);
        ImageView iv = new ImageView(this);
        iv.setImageBitmap(bitmap);

        File file = new File(picture + "");
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 80, out)) {
                out.flush();
                out.close();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // /////////////////////////

// }

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTORESOULT);

    }

    // /*照片上传部分
    // * */
    private void uploadFile() {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        // //////////////
        String jsessionid = appState.getSessionid();
        String time = appState.getTime();
        if (time == null) {
            GetServerTime getServerTime = new GetServerTime(UpdateMyProfile.this);
            time = getServerTime.getFormatTime();
            OperateInfUtils.refreshTime(UpdateMyProfile.this, time);
        }
        String code = EncryptUtil.MD5Encrypt(time + LotteryUtils.getKey(UpdateMyProfile.this));
        // ////////////////
        String actionUrl =
            "http://skylight.westhost.cn:8080/BuKeServ/servlet/VerificationImage;jsessionid=" + jsessionid +
                "?service=2004100&pid=" + LotteryUtils.getPid(this) + "&phone=" + appState.getUsername() +
                "&user_id=" + appState.getUserid() + "&timestamp=" + time + "&sign=" + code;
        try {
            URL url = new URL(actionUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setRequestMethod("POST");
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            /* 设置DataOutputStream */
            DataOutputStream ds = new DataOutputStream(con.getOutputStream());
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data; " + "name=\"file\";filename=\"" + newName + "\"" +
                end);
            ds.writeBytes(end);

            /* 取得文件的FileInputStream */
            FileInputStream fStream = new FileInputStream(picture);
            /* 设置每次写入1024bytes */
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int length = -1;
            /* 从文件读取数据至缓冲区 */
            while ((length = fStream.read(buffer)) != -1) {
                /* 将资料写入DataOutputStream中 */
                ds.write(buffer, 0, length);
            }
            ds.writeBytes(end);
            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);

            fStream.close();
            ds.flush();

            /* 取得Response内容 */
            InputStream is = con.getInputStream();
            int ch;
            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }

            showDialog("上传成功" + b.toString().trim());
            ds.close();
        }
        catch (Exception e) {
            showDialog("上传失败" + e);
        }
    }

    /* 显示Dialog的method */
    private void showDialog(String mess) {
        new AlertDialog.Builder(UpdateMyProfile.this).setTitle("Message").setMessage(mess).setNegativeButton("确定",
                                                                                                             new DialogInterface.OnClickListener() {
                                                                                                                 public void onClick(DialogInterface dialog,
                                                                                                                                     int which) {
                                                                                                                 }
                                                                                                             }).show();
    }

    // -------------------------------------------------------------------------

    private Boolean checkInput() {
        String warning = null;
        Name = niceName.getText().toString();
        City = city.getText().toString();
        Email = email.getText().toString();
        QianMing = content.getText().toString();
        if (Name == null || Name.equals("")) {
            warning = "请输入昵称";
            niceName.requestFocus();
        }
        else if (Name.equals("null") || Name.equals(null)) {
            warning = "此姓名太受欢迎，已有人抢了";
            niceName.requestFocus();
        }
        else if (!Pattern.matches("^[\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w]{2,10}$", Name)) {
            warning = "昵称只能输入2~10位汉字、英文或者数字";
            niceName.requestFocus();
        }
        else if (!Pattern.matches("^[         \\u4E00-\\u9FA5\\uF900-\\uFA2D\\w]{2,10}$", Name)) {
            warning = "昵称只能输入2~10汉字、英文或者数字";
            niceName.requestFocus();
        }
        // 判断签名输入框中是否包含%
// else if (QianMing.indexOf(String.valueOf("%")) != -1) {
// warning = "不能输入百分号";
// content.requestFocus();
// }
        else if (City.length() > 12) {
            warning = "城市的输入大于12个字符";
            city.requestFocus();
        }
// else if (Email.length() != 0) {
// if (!Pattern.matches("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$",
// Email)) {
// warning = "您输入的邮箱不合法";
// email.requestFocus();
// }
// }
        else if (QianMing.length() > 140) {
            warning = "个性签名输入大于140个字符";
            content.requestFocus();
        }
        if (warning != null) {
            ViewUtil.showTipsToast(this, warning);
            return false;
        }
        return true;
    }

    protected void sendin() {
        if (HttpConnectUtil.isNetworkAvailable(UpdateMyProfile.this)) {
            UpdateMyProfileTask task = new UpdateMyProfileTask();
            task.execute();
            // PhotoUploadTask task1 = new PhotoUploadTask();
            // task1.execute();
        }
        else {
            ViewUtil.showTipsToast(this, noNetTips);
        }
    }

    class UpdateMyProfileTask
        extends AsyncTask<Void, Object, String> {

        @Override
        protected void onPostExecute(String json) {
            restoreSubmit();
            String inf = null;
            if (json == null) {
                inf = FEEDBACKFAIL;
                ViewUtil.showTipsToast(UpdateMyProfile.this, inf);
            }
            else {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                String errordesc = analyse.getData(json, "error_desc");
                if (status.equals("200")) {
                    inf = "修改资料成功";
                    ViewUtil.showTipsToast(UpdateMyProfile.this, inf);
                    LotteryApp appState = ((LotteryApp) getApplicationContext());
                    appState.setNickname(niceName.getText().toString());
                    UpdateMyProfile.this.finish();
                }

                else if (status.equals("300")) {
                    ViewUtil.showTipsToast(UpdateMyProfile.this, errordesc);
                }
                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(UpdateMyProfile.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(UpdateMyProfile.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
                else {
                    inf = FEEDBACKFAIL;
                    ViewUtil.showTipsToast(UpdateMyProfile.this, inf);
                }
            }
            firstLoadProgress.setVisibility(View.GONE);
        }

        @Override
        protected void onPreExecute() {
            save.setText("正在保存...");
            save.setEnabled(false);
            firstLoadProgress.setVisibility(View.VISIBLE);
        }

        private HashMap<String, String> initHashMap()
            throws Exception {
            HashMap<String, String> parameter = new HashMap<String, String>();
            parameter.put("service", "2004020");
            parameter.put("pid", LotteryUtils.getPid(UpdateMyProfile.this));
            parameter.put("phone", HttpConnectUtil.encodeParameter(appState.getUsername()));
            parameter.put("user_id", appState.getUserid());
            parameter.put("nickname", HttpConnectUtil.encodeParameter(niceName.getText().toString()));
            parameter.put("city", HttpConnectUtil.encodeParameter(city.getText().toString()));
            parameter.put("signature", HttpConnectUtil.encodeParameter(content.getText().toString()));
            parameter.put("email", HttpConnectUtil.encodeParameter(email.getText().toString()));
            if (man.isChecked() == true) {
                parameter.put("gender", "1");
            }
            else {
                parameter.put("gender", "0");
            }
            return parameter;
        }

        @Override
        protected String doInBackground(Void... arg0) {
            ConnectService connectNet = new ConnectService(UpdateMyProfile.this);
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

    private void restoreSubmit() {
        save.setText("保存资料");
        save.setEnabled(true);
    }

    class MyProfileTask
        extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            GetMyProfileService getMyProfile = new GetMyProfileService(UpdateMyProfile.this);
            String json = getMyProfile.sending();
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            if (json != null) {
                JsonAnalyse analyse = new JsonAnalyse();
                String status = analyse.getStatus(json);
                if (status.equals("200")) {
                    String data = analyse.getData(json, "response_data");
                    firstLoadProgress.setVisibility(View.GONE);
                    if (data.equals("[]") == false) {
                        getMyProfileArray(profileArray, data);
                    }
                }

                else if (status.equals("302")) {
                    OperateInfUtils.clearSessionId(UpdateMyProfile.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_timeout));
                }
                else if (status.equals("304")) {
                    OperateInfUtils.clearSessionId(UpdateMyProfile.this);
                    showLoginAgainDialog(getResources().getString(R.string.login_again));
                }
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        public void getMyProfileArray(MyProfileData profileArray, String json) {
            if (json != null) {
                JSONArray hallArray;
                try {
                    hallArray = new JSONArray(json);
                    JSONObject jo = hallArray.getJSONObject(0);
                    niceName.setText(jo.getString("nickname"));
                    niceName.requestFocus();
                    niceName.setSelection(niceName.getText().toString().length());

// GetPicTask task = new GetPicTask();
// task.execute("http://skylight.westhost.cn:8080/BuKeServ/servlet/VerificationImage?" +
// "&phone=" + appState.getUsername() + "&user_id=" + jo.getString("user_id") +
// "&service=2004100");

                    String gender1 = jo.getString("gender");
                    if (gender1.equals("1")) {
                        man.setChecked(true);
                    }
                    else {
                        women.setChecked(true);
                    }

                    String chengshi = jo.getString("city");
                    if (chengshi == null || chengshi.equals("null") || chengshi.equals("")) {
                        city.setText("");
                    }
                    else {
                        city.setText(jo.getString("city"));
                    }
                    city.setSelection(city.getText().toString().length());

                    String qm = jo.getString("signature");
                    if (qm == null || qm.equals("null") || qm.equals("")) {
                        content.setText("");
                    }
                    else {
                        content.setText(jo.getString("signature"));
                    }
                    content.setSelection(content.getText().toString().length());

                    String em = jo.getString("email");
                    if (em == null || em.equals("null") || em.equals("")) {
                        email.setText("");
                    }
                    else {
                        email.setText(jo.getString("email"));
                    }
                    email.setSelection(email.getText().toString().length());

                }

                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 获取图片
    public InputStream getImage()
        throws IOException {
        String url = "";
        String parameter = "";
        InputStream inputStream = null;
        AndroidHttpClient client = new AndroidHttpClient(UpdateMyProfile.this);
        HttpClient httpClient = client.getDefaultHttpClient();
        HttpPost httpPost = client.getHttpPost(url, parameter);
        try {
            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    inputStream = entity.getContent();
                    entity.consumeContent();
                }
            }
        }
        catch (IOException ex) {
            httpPost.abort();
        }
        if (inputStream != null) {
            return inputStream;
        }
        else
            return null;
    }

    // 获取图片线程
    class GetPicTask
        extends AsyncTask<String, Long, byte[]> {

        @Override
        protected void onPostExecute(byte[] data) {
            if (data != null) {
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap
                if (bitmap != null) {
                    avatar.setImageBitmap(bitmap);// display image
                }
                else {
                    avatar.setImageResource(R.drawable.lucky_cat);
                }
            }
            else {
                ViewUtil.showTipsToast(UpdateMyProfile.this, "头像获取失败");
            }

        }

        // 任务被执行之后
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected byte[] doInBackground(String... params) {
            HttpClient c = new DefaultHttpClient();
            Gravatar g = new Gravatar();
            String url = null;
            try {

                url = params[0];
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
            HttpGet get = new HttpGet(url);
            HttpResponse response = null;
            // try {
            try {
                response = c.execute(get);
                HttpEntity client = response.getEntity();
                if (client != null) {
                    InputStream is = null;
                    ByteArrayOutputStream outStream = null;
                    try {
                        is = client.getContent();
                        if (is != null) {
                            outStream = new ByteArrayOutputStream();
                            byte[] buffer = new byte[1024];
                            int len = 0;
                            while ((len = is.read(buffer)) != -1) {
                                outStream.write(buffer, 0, len);
                            }
                            return outStream.toByteArray();
                        }
                        else
                            return null;
                    }
                    catch (IllegalStateException e) {
                        e.printStackTrace();
                        return null;
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                    finally {
                        if (is != null)
                            is.close();
                        if (outStream != null)
                            outStream.close();
                    }
                }
                else {
                    return null;
                }
            }
            catch (ClientProtocolException e1) {
                e1.printStackTrace();
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> map = new HashMap<String, String>();
        map.put("inf", "username [" + appState.getUsername() + "]: open garden update personal profile");
        String eventName = "v2 open garden update personal profile";
        FlurryAgent.onEvent(eventName, map);
        besttoneEventCommint(eventName);
    }

    @Override
    protected void submitData() {
        String eventName = "open_garden_modify_user_profile";
        MobclickAgent.onEvent(this, eventName);
        besttoneEventCommint(eventName);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            UpdateMyProfile.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
