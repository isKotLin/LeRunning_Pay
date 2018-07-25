package com.vigorchip.lerunning;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chinamobile.iot.onenet.edp.CmdMsg;
import com.chinamobile.iot.onenet.edp.Common;
import com.chinamobile.iot.onenet.edp.ConnectRespMsg;
import com.chinamobile.iot.onenet.edp.EdpMsg;
import com.chinamobile.iot.onenet.edp.PingRespMsg;
import com.chinamobile.iot.onenet.edp.PushDataMsg;
import com.chinamobile.iot.onenet.edp.toolbox.EdpClient;
import com.chinamobile.iot.onenet.edp.toolbox.Listener;
import com.goole.zxing.client.android.EncodingUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by wr-app1 on 2018/1/18.
 */

public class mMain extends AppCompatActivity {
    private ViewPager viewPager;
    private ImageButton btn_Key;
    private CustomerKeyboard mCustomerKeyboard;
    private PasswordEditText mPasswordEt;
    private Button btn_back, btn_enter;
    private ImageView Code;
    private String mOnenetCallback_Key;
    private String mOnenetCallback_ID;
    private boolean isStart=false;
    private MyThread myThread;
    private int currentItem = 0;
    private final Long deviceID = 102000001L;
    //计算密码的异或固定值
    private static final int[][] ARR = {
            {378, 242}, {562, 901}, {555, 761}, {044, 289}, {221, 034}, {561, 765}, {324, 8}, {017, 201}, {387, 969},
            {763, 245}, {811, 93}, {286, 379}, {656, 241}, {368, 067}, {590, 287}, {353, 190}, {242, 795}, {587, 249}, {379, 486},
            {827, 045}, {699, 579}, {842, 131}, {358, 339}, {459, 139}, {336, 851}, {241, 967}, {887, 063}, {147, 171}, {189, 662}
    };

    private List<String> mCount = new ArrayList<>();
    private List<String> List_advImgUrl = new ArrayList<String>();
    private List<String> List_advVideoUrl = new ArrayList<String>();
    private List<String> suffixName = new ArrayList<String>();
    private List<ImageView> List_advImage = new ArrayList<ImageView>();

    private File adv_path = new File("/mnt/sdcard/advertising");
    private File qrCode_path = new File("/mnt/sdcard/qrCode");

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Bitmap qrcode = (Bitmap) msg.obj;
                    LocalUtils.saveQRCode(qrcode);
                    Code.setImageBitmap(BitmapFactory.decodeFile("/mnt/sdcard/qrCode/MyPayQRCode.jpg"));
                    break;
                case 2:
                    for (int i = 0; i < List_advImgUrl.size(); i++) {
                        ImageView img = new ImageView(mMain.this);
                        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        Glide.with(mMain.this)
                                .load(List_advImgUrl.get(i))
                                .into(img);
                        List_advImage.add(img);
                    }
                    if (List_advImgUrl.size() != 0) {
                        if (List_advImgUrl.size() == List_advImage.size()) {
                            viewPager.setAdapter(new MyAdapter(mMain.this, List_advImage));
                            isStart = true;
                            myThread = new MyThread();
                            myThread.start();
                        }
                    }
                    break;
                case 3:
                    if (List_advImgUrl.size() == List_advImage.size()) {
                        int page = (Integer) msg.obj;
                        viewPager.setCurrentItem(page);
                    }
                    break;
                }
            }
        };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        btn_Key = (ImageButton) findViewById(R.id.btn_Key);
        Code = (ImageView) findViewById(R.id.Code);
        math();
        keyboard();
        ViewpagerAction();
        getQRCode();
        AdvertiseHttp();
        Create_OneNetDevice();
    }

    private void keyboard(){
        btn_Key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(mMain.this, R.style.DefaultDialog);
                dialog.setContentView(R.layout.dialog_password);
                Window window = dialog.getWindow();
                window.setGravity(Gravity.CENTER_HORIZONTAL);
                window.getDecorView().setPadding(0, 0, 0, 0);
                window.setWindowAnimations(R.style.dialog_style);
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(lp);
                dialog.show();
                mCustomerKeyboard = (CustomerKeyboard) dialog.findViewById(R.id.custom_key_board);
                mPasswordEt = (PasswordEditText) dialog.findViewById(R.id.password_et);
                btn_enter = (Button) dialog.findViewById(R.id.btn_enter);
                btn_back = (Button) dialog.findViewById(R.id.btn_back);

                mCustomerKeyboard.setOnCustomerKeyboardClickListener(new CustomerKeyboard.CustomerKeyboardClickListener() {
                    @Override
                    public void click(String number) {
//                        Toast.makeText(MainActivity.this,number,Toast.LENGTH_SHORT).show();
                        mPasswordEt.addPassword(number);
                    }

                    @Override
                    public void delete() {
//                        Toast.makeText(MainActivity.this,"删除",Toast.LENGTH_SHORT).show();
                        mPasswordEt.deleteLastPassword();
                    }
                });

                btn_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                btn_enter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String password = mPasswordEt.getText().toString().trim();
                        if (password == "" || mPasswordEt.length() != 6) {
                            mToast.makeText(mMain.this, "          请输入6位数密码\nPlease Enter a Six Password",
                                    Toast.LENGTH_SHORT).show();
                        }else if (!mCount.contains(password) && mPasswordEt.length() != 6){
                            mToast.makeText(mMain.this, " 您输入的密码有误\nPassword Is Wrong", Toast.LENGTH_SHORT).show();
                        }
                        if (mPasswordEt != null && mPasswordEt.length() == 6 && mCount.contains(password)) {
                            Intent intent = new Intent(mMain.this, Hello.class);
                            startActivity(intent);
                            mMain.this.overridePendingTransition(R.anim.activity_open,0);
                            dialog.dismiss();
                            finish();
                        }
                        if (!mCount.contains(password) && mPasswordEt.length() == 6){
                            mPasswordEt.deleteAllPassword();
                            mToast.makeText(mMain.this, " 您输入的密码有误\nPassword Is Wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void math(){
        Long m1H = (deviceID % 1000L * 123) % 1000L ^ (deviceID % 1000000L / 1000L);
        Log.d("计算高三位异或密码", String.valueOf(m1H));
        Long m1L = (m1H ^ (deviceID % 1000000000L / 1000000L) ^ (deviceID % 1000000000000L / 1000000000L));
        Log.d("计算低三位异或密码", String.valueOf(m1L));
        Long[] mhs = {m1H, m1L};
        //将
        mCount.add(String.format("%03d", (m1H % 1000)) + String.format("%03d", (m1L % 1000)));
        for (int[] anArr : ARR) {
            StringBuilder mh = new StringBuilder();
            for (int x = 0; x < anArr.length; x++) {
                mh.append(String.format("%03d", (mhs[x] ^ anArr[x]) % 1000));
            }
            mCount.add(mh.toString());
            Log.d("打印查看密码", String.valueOf(mCount));
        }
    }

    private void ViewpagerAction(){
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentItem = position;
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    private void Create_OneNetDevice() {
        final SharedPreferences sharedPreferences_save = mMain.this.getSharedPreferences("values",Context.MODE_PRIVATE);
        final SharedPreferences sharedPreferences_load = getSharedPreferences("values",MODE_PRIVATE);
        String DeviceID = deviceID.toString();
        int length = DeviceID.length();
        String strdeviceID = DeviceID.substring(length-5, length);

        if (sharedPreferences_load == null) {
            OkHttpClient OneNetHttpClient = new OkHttpClient();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("sn", DeviceID);
                jsonObject.put("title", strdeviceID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody OneNetDevice_body = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
            final Request OneNetDevice_request = new Request.Builder()
                    .url("http://api.heclouds.com/register_de?register_code=2kgIUjON5tSjKTcx")
                    .post(OneNetDevice_body)
                    .build();

            Call OneNetDevice_Callback = OneNetHttpClient.newCall(OneNetDevice_request);
            OneNetDevice_Callback.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i("e.getMessage()", e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    InputStream is = response.body().byteStream();
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    int len = 0;
                    byte buffer[] = new byte[1024];
                    while ((len = is.read(buffer)) != -1) {
                        os.write(buffer, 0, len);
                    }
                    is.close();
                    os.close();
                    String responseStr = os.toString();

                    try {
                        JSONObject OnenetJson_response = new JSONObject(responseStr);
                        JSONObject mData = OnenetJson_response.getJSONObject("data");
                        JSONObject OnenetJson_mdata = new JSONObject(String.valueOf(mData));
                        String Onenet_device_id = OnenetJson_mdata.getString("device_id");
                        String Onenet_key = OnenetJson_mdata.getString("key");
                        SharedPreferences.Editor mEditor = sharedPreferences_save.edit();
                        mEditor.putString("OnenetDeviceID", Onenet_device_id);
                        mEditor.putString("Onenetkey", Onenet_key);
                        mEditor.commit();
                        Log.i("网络获取的OnenetID是 ", mOnenetCallback_ID + "  获取的Key是：" + mOnenetCallback_Key);
                        EdpClient.initialize(mMain.this, 1, mOnenetCallback_ID, mOnenetCallback_Key);
                        EdpClient.getInstance().setListener(mEdpListener);
                        EdpClient.getInstance().setPingInterval(3 * 60 * 1000);
                        EdpClient.getInstance().connect();
                        EdpClient.getInstance().sendConnectReq();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            mOnenetCallback_ID = sharedPreferences_load.getString("OnenetDeviceID", "");
            mOnenetCallback_Key = sharedPreferences_load.getString("Onenetkey", "");
            Log.i("本地获取的OnenetID是 ", mOnenetCallback_ID + "  获取的Key是：" + mOnenetCallback_Key);
            EdpClient.initialize(mMain.this, 1, mOnenetCallback_ID, mOnenetCallback_Key);
            EdpClient.getInstance().setListener(mEdpListener);
            EdpClient.getInstance().setPingInterval(3 * 60 * 1000);
            EdpClient.getInstance().connect();
            EdpClient.getInstance().sendConnectReq();
        }
    }

    private Listener mEdpListener = new Listener() {

        @Override
        public void onReceive(List<EdpMsg> msgList) {
            if (null == msgList) {
                return;
            }
            for (EdpMsg msg : msgList) {
                if (null == msg) {
                    continue;
                }
                switch (msg.getMsgType()) {
                    // 连接响应
                    case Common.MsgType.CONNRESP:
                        ConnectRespMsg connectRespMsg = (ConnectRespMsg) msg;
                        Log.i("连接响应码: " , String.valueOf(connectRespMsg.getResCode()));
                        if (connectRespMsg.getResCode() == Common.ConnResp.ACCEPTED) {
                            Log.v("连接成功", String.valueOf(connectRespMsg.getResCode() == Common.ConnResp.ACCEPTED));
                        }
                        break;
                    // 心跳响应
                    case Common.MsgType.PINGRESP:
                        PingRespMsg pingRespMsg = (PingRespMsg) msg;
                        Log.i("心跳响应", pingRespMsg.toString());
                        break;

                    // 存储确认
                    case Common.MsgType.SAVERESP:
                        break;

                    // 转发（透传）
                    case Common.MsgType.PUSHDATA:
                        PushDataMsg pushDataMsg = (PushDataMsg) msg;
                        Log.i("透传数据: ",  new String(pushDataMsg.getData()));
                        break;

                    // 存储（转发）
                    case Common.MsgType.SAVEDATA:
                        break;

                    // 命令请求
                    case Common.MsgType.CMDREQ:
                        CmdMsg cmdMsg = (CmdMsg) msg;
                        String cmdData = new String(cmdMsg.getData());
                        Log.i("cmdid: ", cmdMsg.getCmdId() + "\n命令请求内容: " + cmdData);
                        if (cmdData.equals("1")){
                            Log.i("付款成功","");
                            Intent intent = new Intent(mMain.this,Hello.class);
                            startActivity(intent);
                        }else if (cmdData.equals("0")){
                            Log.i("付款失败","");
                        }
                        break;
                    // 加密响应
                    case Common.MsgType.ENCRYPTRESP:
                        break;
                }
            }
        }

        @Override
        public void onFailed(Exception e) {
            e.printStackTrace();
        }

        @Override
        public void onDisconnect() {

        }
    };

    private void getQRCode() {
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"),
        "{\n" +"\"deviceId\": \""+deviceID+"\",\n" +
                "  \"time\": 123\n" +
                "}");

        Request request = new Request.Builder()
                .url("http://www.hzleshare.net.cn/run/get-pay-qrcode")
                .post(body)
                .build();

        Call call = okHttpClient.newCall(request);
        //异步调用并设置回调函数,這裏Callback開始獲取返回的參數
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("result111", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseStr = response.body().string();
                try {
                    JSONObject jsonObj = new JSONObject(responseStr);
                    Boolean getsuccess = jsonObj.optBoolean("success");
                    String imgUrl = jsonObj.optString("data");
                    String getmsg = jsonObj.optString("msg");
                    JSONObject jasonCode = new JSONObject(imgUrl);
                    String QRcodeURL = jasonCode.optString("data");

                    if (getsuccess == true) {
                        Bitmap myQRcode = EncodingUtils.Create2DCode(QRcodeURL, 120, 120, null);
                        Message msg = new Message();
                        if (!qrCode_path.exists()) {
                            LocalUtils.createFiles("/mnt/sdcard/qrCode");
                        }
                        msg.what = 1;
                        msg.obj = myQRcode;
                        handler.sendMessage(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void AdvertiseHttp(){
        OkHttpClient AdvertiseOk = new OkHttpClient();
        RequestBody adv_body = RequestBody.create(MediaType.parse("application/json"),
                "{\n" + "\"deviceId\",\n" + "}");
        final Request adv_request = new Request.Builder()
                .url("http://www.hzleshare.net.cn/run/get-index-ads")
                .post(adv_body)
                .build();
        Call advCall = AdvertiseOk.newCall(adv_request);
        advCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("adv_response", String.valueOf(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String adv_response = response.body().string();
                Log.i("adv_response",adv_response);
                try {
                    JSONObject adv_jsonObj = new JSONObject(adv_response);
                    JSONObject json_data = adv_jsonObj.getJSONObject("data");
                    JSONArray Array_getUrls = json_data.getJSONArray("urls");
                    Boolean json_success = adv_jsonObj.optBoolean("success");
                    String json_msg = adv_jsonObj.optString("msg");

                    if (json_success == true) {
                        boolean isDownable=false;
                        if (Array_getUrls != null && Array_getUrls.length() != 0) {
                            for (int i = 0; i < Array_getUrls.length(); i++) {
                                String adv_url = Array_getUrls.getString(i);
                                List_advImgUrl.add(adv_url);
                                String getsuffixName = adv_url.substring(adv_url.lastIndexOf("/") + 1);
                                File suffixNamePath = new File("/mnt/sdcard/advertising/" + getsuffixName);
                                suffixName.add(getsuffixName);
                                if (adv_path.exists() && adv_path.isDirectory()) {
                                    if (!suffixNamePath.exists()) {
                                        if (!isDownable){
                                            LocalUtils.deleteDir("/mnt/sdcard/advertising");//删除文件夹
                                            if (!adv_path.exists()) {
                                                LocalUtils.createFiles(String.valueOf(adv_path));//创建广告文件夹
                                            }
                                        }
                                        isDownable=true;
                                    }
                                } else {
                                    LocalUtils.createFiles(String.valueOf(adv_path));//创建广告文件夹
                                }
                            }
                            if (isDownable) {
                                for (int j = 0; j < List_advImgUrl.size(); j++) {
                                    SecondSendURL(List_advImgUrl.get(j) , suffixName.get(j));
                                }
                            }
                            if (List_advImgUrl.size() == Array_getUrls.length()) {
                                Message adv_msg = new Message();
                                adv_msg.obj = List_advImgUrl;
                                handler.obtainMessage(2).sendToTarget();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void SecondSendURL(String advUrl , final String suffixName){
        OkHttpClient secondOk = new OkHttpClient();
        final Request secondRequest = new Request.Builder()
                .url(advUrl)
                .build();
        secondOk.newCall(secondRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream advInput = response.body().byteStream();
                Bitmap bitmap = BitmapFactory.decodeStream(advInput);
                LocalUtils.saveDir("advertising",bitmap,suffixName);
            }
        });
    }

    class MyThread extends Thread{
        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            while(isStart){
                Message message = new Message();
                message.obj = currentItem;
                message.what=3;
                handler.sendMessage(message);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                currentItem++;
            }
        }
    }
}
