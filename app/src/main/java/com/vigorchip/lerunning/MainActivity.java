package com.vigorchip.lerunning;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.VideoView;

import com.goole.zxing.client.android.EncodingUtils;
import com.zyao89.view.zloading.ZLoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

public class MainActivity extends AppCompatActivity {


    private ImageButton btn_Key;
    private CustomerKeyboard mCustomerKeyboard;
    private PasswordEditText mPasswordEt;
    private Button btn_back, btn_enter;
    private ImageView Code;
    private VideoView video_advertising;
    private ViewPager vp_advertising;

    private mToast mtoast;

    //需要用到的变量
    String Suffix = null;
    String advURL;
    String Sub;

    //广告文件地址
    File adv_path = new File("/mnt/sdcard/advertising");
    File qrCode_path = new File("/mnt/sdcard/qrCode");
    //广告地址类型集合
    List<String> ListURL = new ArrayList<String>();
    //广告图片后缀名集合
    List<String> ListSuffix = new ArrayList<String>();
    //广告bitmap转imageview，放进此集合
    ArrayList<ImageView> ListImage = new ArrayList<ImageView>();
    ArrayList<ImageView> ListImage2 = new ArrayList<ImageView>();
    //文件夹遍历需要用到的集合
    List<File> FolderList_Foreach = new ArrayList<File>();
    //文件集合
    List<File> documentsList;

    //控制图片是否开始轮播的开关,默认关的
    private boolean isStart = false;
    //开始图片轮播的线程
    private MyThread t;

    //统计下载了几张图片
    int n = 0;
    //统计当前viewpager轮播到第几页
    int p = 0;

    int a = 0;

    //获取设备号
    final Long deviceID = 500102000001L;
    //计算密码的异或固定值
    private static final int[][] ARR = {{378, 242}, {562, 901}, {555, 761}, {044, 289}, {221, 034}, {561, 765}, {324, 8}, {017, 201},
            {387, 969}, {763, 245}, {811, 93}, {286, 379}, {656, 241}, {368, 067}, {590, 287}, {353, 190}, {242, 795}, {587, 249}, {379, 486},
            {827, 045}, {699, 579}, {842, 131}, {358, 339}, {459, 139}, {336, 851}, {241, 967}, {887, 063}, {147, 171}, {189, 662}
    };
    //创建密码集合
    List<String> mCount = new ArrayList<>();
    private ArrayList<Bitmap> bitmap_list = new ArrayList<Bitmap>();
    //网络加载广告时的加载动画
    ZLoadingDialog zdialog = new ZLoadingDialog(MainActivity.this);

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //Handler獲取msg的消息，判斷區別碼是否相同
            switch (msg.what) {
                case 1:
                    //將msg的obj裏面的圖片轉成Bitmap對象
                    Bitmap qrcode = (Bitmap) msg.obj;
                    /**
                     * 获取网络二维码 存到本地图片库
                     * */
                    LocalUtils.saveQRCode(qrcode);
                    Code.setImageBitmap(BitmapFactory.decodeFile("/mnt/sdcard/qrCode/MyPayQRCode.jpg"));//setImageBitmap方法獲取bitmap顯示出來
                    Log.d("二维码是否空？", String.valueOf(qrcode));
                    break;
                case 2:
                    n++;
                    Log.d("下载了几张图片", String.valueOf(n));
                    //將msg的obj裏面的圖片轉成Bitmap對象
                    Bitmap advertisingBitmap = (Bitmap) msg.obj;
                    Log.d("advertisingBitmap是否为空？", String.valueOf(a));
                    if (advertisingBitmap != null) {
                        saveAdvertising(advertisingBitmap, a);//创建图片保存到advertising文件夹
                    }
//                        if (mFileList != null) {//遍历mFileList
////                            Log.d("数组1", String.valueOf(fileList));
//                            FileInputStream myfis = null;
//                            Bitmap LocalAdv = null;
//                            for (int i = 0; i < fileList.size(); i++) {
//                                File file = fileList.get(i);
//                                Log.i("--广告文件名字--", file.getName());
//                                Log.i("--广告文件路径--", String.valueOf(mFileList.get(i)));
//                                try {
//                                    myfis = new FileInputStream(mFileList.get(i));//文件流
//                                    LocalAdv = BitmapFactory.decodeStream(myfis);//BitmapFactory.decodeStream将fis流转成Bitmap图片
//                                } catch (FileNotFoundException e) {
//                                    e.printStackTrace();
//                                } finally {
//                                    try {
//                                        myfis.close();
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                                Log.d("--查看广告图是否为空--", String.valueOf(LocalAdv));
//                            }
//                        }
                    ImageView iv = new ImageView(MainActivity.this);//创建一个Imageview对象
                    iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    iv.setImageBitmap(advertisingBitmap);//将每次发送请求 获取的图片装进Imageview对象，使iv拥有图片
                    ListImage.add(iv);//将每次发送请求的图片装进Imageview类型集合
                    Log.d("ListBitmap", String.valueOf(ListImage));
//                  Log.d("ListURL长度是", String.valueOf(ListURL.size()));
//                  zdialog.dismiss();
//                  Log.d("zdialog关闭", String.valueOf(zdialog));
                    if (n == ListImage.size()) {
                        vp_advertising.setAdapter(new MyAdapter(MainActivity.this, ListImage));
//                        mAdapter.notifyDataSetChanged();
//                        if (!isStart) {
                            isStart = true;
                            t = new MyThread();
                            t.start();
//                        }
                    }
                    a++;
                    break;
                case 666:
                    //接受到的线程发过来的p数字
                    int page = (Integer) msg.obj;
                    vp_advertising.setCurrentItem(page);
                    break;
            }
        }
    };


    //判断设备有没有开启网络
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }

    AdvertiseView mA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        setContentView(R.layout.activity_a);
//        mA = (AdvertiseView) findViewById(R.id.mAdvertiseView);
//        ArrayList<Bitmap> bitmap_list=new ArrayList<Bitmap>();
//        bitmap_list.add(BitmapFactory.decodeResource(getResources(),R.mipmap.img_code));
//        bitmap_list.add(BitmapFactory.decodeResource(getResources(),R.mipmap.img_key));
//        bitmap_list.add(BitmapFactory.decodeResource(getResources(),R.mipmap.img_pleasecode));
//        ArrayList<String> title=new ArrayList<String>();
//        title.add("1");
//        title.add("2");
//        title.add("3");
//        //传数据接口
//        mA.setImagesAndTitles(bitmap_list,title);
//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                while(true){
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mA.autoscroll();
//                        }
//                    });
//                    SystemClock.sleep(2000);
//                }
//            }
//        }.start();
        btn_Key = (ImageButton) findViewById(R.id.btn_Key);
        Code = (ImageView) findViewById(R.id.Code);
        video_advertising = (VideoView) findViewById(R.id.viewpager);

        getQRCode();
        init();
        math();
        getAdvertising();

        File pathD = new File("/mnt/sdcard/qrCode");
        File pathF = new File("/mnt/sdcard/qrCode/MyPayQRCode.jpg");

        if (!pathD.exists()){
            LocalUtils.createFiles("/mnt/sdcard/qrCode");//创建广告文件夹
        } else if (!pathF.exists()) {
            Code.setImageBitmap(BitmapFactory.decodeFile("/mnt/sdcard/qrCode/MyPayQRCode.jpg"));
        }

        //如果本地有目录
        if (adv_path.exists()) {
            //图片文件集合
            documentsList = getFile(adv_path);
        }else {
            LocalUtils.createFiles(String.valueOf(adv_path));//创建广告文件夹
        }

        Log.d("fileList", String.valueOf(documentsList));

        File[] listFiles = adv_path.listFiles();
        if (listFiles.length > 0) {
            for (int path = 0; path < documentsList.size(); path++) {
                Bitmap mb = BitmapFactory.decodeFile(String.valueOf(documentsList.get(path)));
                ImageView iv = new ImageView(MainActivity.this);//创建一个Imageview对象
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                iv.setImageBitmap(mb);//将每次发送请求 获取的图片装进Imageview对象，使iv拥有图片
                ListImage.add(iv);//将每次发送请求的图片装进Imageview类型集合
                Log.d("ListBitmap", String.valueOf(ListImage));
            }

//          Log.d("ListURL长度是", String.valueOf(ListURL.size()));
//                zdialog.dismiss();
//          Log.d("zdialog关闭", String.valueOf(zdialog));
            vp_advertising.setAdapter(new MyAdapter(MainActivity.this, ListImage));
                isStart = true;
                t = new MyThread();
                t.start();
        }

        if (!isNetworkAvailable(MainActivity.this)) {
            if (!pathF.exists()) {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream("/mnt/sdcard/qrCode/MyPayQRCode.jpg");//文件流
                    Bitmap LocalQRCode = BitmapFactory.decodeStream(fis);//BitmapFactory.decodeStream将fis流转成Bitmap图片
                    Log.d("无网络时显示的图片", String.valueOf(LocalQRCode));
                    Code.setImageBitmap(LocalQRCode);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
//                finally {
//                    try {
//                        fis.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
            }
            mtoast.makeText(getApplicationContext(), "当前无可用网络！", Toast.LENGTH_LONG).show();
        }



        //无网络时调用此方法获取二维码

//            File f = new File("/mnt/sdcard/advertising");
//            fileList = getFile(f);//获取文件夹下的所有文件，赋给fileList集合
//            FileInputStream myfis = null;
//            Bitmap LocalAdv = null;
//            for (int i = 0; i < fileList.size(); i++) {
//                File file = fileList.get(i);
//                Log.i("--广告文件名字--", file.getName());
//                Log.i("--广告文件路径--", String.valueOf(mFileList.get(i)));
//                try {
//                    myfis = new FileInputStream(mFileList.get(i));//文件流
//                    LocalAdv = BitmapFactory.decodeStream(myfis);//BitmapFactory.decodeStream将fis流转成Bitmap图片
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } finally {
//                    try {
//                        myfis.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                Log.d("--查看广告图是否为空--", String.valueOf(LocalAdv));
//
//            ImageView iv = new ImageView(MainActivity.this);//创建一个Imageview对象
//            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            iv.setImageBitmap(LocalAdv);//将每次发送请求 获取的图片装进Imageview对象，使iv拥有图片
//            ListImage.add(iv);//将每次发送请求的图片装进Imageview类型集合
//            if (n == ListURL.size()) {
////                        Log.d("ListURL长度是", String.valueOf(ListURL.size()));
////                        zdialog.dismiss();
////                        Log.d("zdialog关闭", String.valueOf(zdialog));
//                vp_advertising.setAdapter(new MyAdapter(ListImage, MainActivity.this));
//                isStart = true;
//                t = new MyThread();
//                t.start();
//            }
//        }

        btn_Key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(MainActivity.this, R.style.DefaultDialog);
                dialog.setContentView(R.layout.dialog_password);
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
                            mtoast.makeText(MainActivity.this, "          请输入6位数密码\nPlease Enter a Six Password",
                                    Toast.LENGTH_SHORT).show();
                        }else if (!mCount.contains(password) && mPasswordEt.length() != 6){
                            mtoast.makeText(MainActivity.this, " 您输入的密码有误\nPassword Is Wrong", Toast.LENGTH_SHORT).show();
                        }
                        if (mPasswordEt != null && mPasswordEt.length() == 6 && mCount.contains(password)) {
//                            mtoast.makeText(MainActivity.this, "密码输入完毕，校验密码" + password, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, Hello.class);
                            startActivity(intent);
                            MainActivity.this.overridePendingTransition(R.anim.activity_open,0);
                            dialog.dismiss();
                            finish();
                        }
                        if (!mCount.contains(password) && mPasswordEt.length() == 6){
                            mPasswordEt.deleteAllPassword();
                            mtoast.makeText(MainActivity.this, " 您输入的密码有误\nPassword Is Wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

//                mPasswordEt.setOnPasswordFulListener(new PasswordEditText.PasswordFulListener() {
//                    @Override
//                    public void passwordFull(String password) {
//                        //显示进度条去后台校验密码
//                        Toast.makeText(MainActivity.this, "密码输入完毕，校验密码" + password, Toast.LENGTH_SHORT).show();
//                    }
//                });


                Window window = dialog.getWindow();
                window.setGravity(Gravity.CENTER_HORIZONTAL); //可设置dialog的位置
                window.getDecorView().setPadding(0, 0, 0, 0); //消除边距
                window.setWindowAnimations(R.style.dialog_style);

                WindowManager.LayoutParams lp = window.getAttributes();
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;   //设置宽度充满屏幕
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(lp);

                dialog.show();
            }
        });

    }



    //计算密码公式
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

    //获取文件夹下的文件
    private List<File> getFile(File file) {
        File[] fileArray = file.listFiles();
//        Log.d("1", String.valueOf(fileArray));
        FolderList_Foreach.clear();
        for (File f : fileArray) {
            if (f.isFile()) {
                FolderList_Foreach.add(f);
//                Log.d("2",f.getName());
            } else {
                getFile(f);
//                Log.d("getFile","mFileList");
            }
        }
//        Log.d("mFileList", String.valueOf(mFileList));
        return FolderList_Foreach;
    }


    //将图片保存在指定目录
    private void saveAdvertising(Bitmap bmp,int a) {
        File appDir = new File(Environment.getExternalStorageDirectory(), "advertising");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
//        String fileName = System.currentTimeMillis() + ".jpg";
        String fileName = ListSuffix.get(a);
        Log.d("广告图片名字的打印！！！！！！！", fileName);
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        // TODO Auto-generated method stub
        /**
         * Viewpager监听事件
         * */
        vp_advertising = (ViewPager) findViewById(R.id.viewpager);
        vp_advertising.setEnabled(false);
        vp_advertising.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int ar0, float arg1, int arg2) {

            }

            @Override
            public void onPageSelected(int position) {
                //把当前的页数赋值给P
                p = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                if (state==ViewPager.SCROLL_STATE_DRAGGING){
//                    isStart=false;
//                }else if (state==ViewPager.SCROLL_STATE_IDLE){
//                    isStart=true;
//                }
            }
        });

    }

    private void getQRCode() {
        OkHttpClient okHttpClient = new OkHttpClient();


        //post方法获取
        RequestBody body = RequestBody.create(MediaType.parse("application/json"),
                "{\n" +
                "  \"deviceId\": \"102000001\",\n" +
                "  \"time\": 123\n" +
                "}");

//        "{\n" +"\"deviceId\": \""+deviceId+"\",\n" +
//                "  \"time\": 123\n" +
//                "}");

//        "{\n"
//                + "  \"deviceId\": \""
//                +deviceId+
//                "\",\n"
//                + "  \"time\": 123\n" + "}"
        //get方法获取
        //构造Request对象
        //采用建造者模式，链式调用指明进行Get请求,传入Get的请求地址
        Request request = new Request.Builder()
                .url("http://www.hzleshare.net.cn/run/get-pay-qrcode")
                .post(body)
                .build();

        Call call = okHttpClient.newCall(request);
        //异步调用并设置回调函数,這裏Callback開始獲取返回的參數
        call.enqueue(new Callback() {
            //如果失敗調用此方法
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("result111", e.getMessage());
            }

            //如果成功調用此方法
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                //將請求返回的轉換成字符串
                final String responseStr = response.body().string();
                Log.e("二维码请求返回的字符串对象responseStr", responseStr);

                try {
//                            JSONArray jsonArray = new JSONArray(responseStr);
//                            for(int i = 0; i < jsonArray.length(); i++){
//                                JSONObject jsonObj = jsonArray.getJSONObject(i);
//                                String imgUrl = jsonObj.getString("swiperImg");
//                                Log.e("result", imgUrl);
//                            }


                    //字符串轉json對象，並放入我的請求字符串
                    JSONObject jsonObj = new JSONObject(responseStr);
                    Log.e("二维码的Json对象jsonObj", String.valueOf(jsonObj));
                    Boolean getsuccess = jsonObj.optBoolean("success");
                    String imgUrl = jsonObj.optString("data");
                    String getmsg = jsonObj.optString("msg");

                    //第一级的data里面还有data
                    JSONObject jasonCode = new JSONObject(imgUrl);
                    //解析data里面的data
                    String QRcodeURL = jasonCode.optString("data");

                    Log.d("二维码返回状态码status", String.valueOf(getsuccess));
                    Log.d("二维码返回的地址data", QRcodeURL);
                    Log.d("二维码返回的消息msg", getmsg);

                    if (getsuccess == true) {
                        Bitmap myQRcode = EncodingUtils.Create2DCode(QRcodeURL, 120, 120, null);
                        Message msg = new Message();//創建消息對象
                        // 创建文件夹 将图片重新加进创建的文件夹
                        File pathF = new File("/mnt/sdcard/qrCode");
                        if (!pathF.exists()) {
                            LocalUtils.createFiles("/mnt/sdcard/qrCode");//创建qrCode文件夹
                        }
                        msg.what = 1;//設置消息唯一的區別碼
                        msg.obj = myQRcode;//將圖片賦值給msg的obj
                        handler.sendMessage(msg);//將msg發送消息通知Handler更新
                    }

//                    }

//                    URL url=new URL(img);
//                    InputStream is= url.openStream();
                    //从InputStream流中解析出图片

//                    Log.d("bitmap", String.valueOf(bitmap));
                    //  imageview.setImageBitmap(bitmap);
                    //发送消息，通知UI组件显示图片
//                    handler.sendEmptyMessage(0x9527);

//                    } else{
//                        mtoast.makeText(MainActivity.this,"获取二维码失败",Toast.LENGTH_LONG).show();
//                    }
//                        //轉換成功之後，開始二次請求
//                        final Request request2 = new Request.Builder()
//                                .url(imgUrl)//請求轉換成字符串的imgUrl，imgUrl是圖片地址
//                                .build();
//
//                        OkHttpClient okHttpClient2 = new OkHttpClient();
//
//                        okHttpClient2.newCall(request2).enqueue(new Callback() {
//                            public void onFailure(Call call, IOException e) {
//
//                            }
//
//                            public void onResponse(Call call, Response response) throws IOException {
//
//                                InputStream inputStream = response.body().byteStream();//得到图片的流
//                                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);//將流轉換成圖片
//
//                                Message msg = new Message();//創建消息對象
//                                msg.what = 10086;//設置消息唯一的區別碼
//                                msg.obj = bitmap;//將圖片賦值給msg的obj
//                                handler.sendMessage(msg);//將msg發送消息通知Handler更新
//                            }
//                        });

//                    URL url=new URL(img);
//                    InputStream is= url.openStream();
                    //从InputStream流中解析出图片

//                    Log.d("bitmap", String.valueOf(bitmap));
                    //  imageview.setImageBitmap(bitmap);
                    //发送消息，通知UI组件显示图片
//                    handler.sendEmptyMessage(0x9527);

//                    } else{
//                        mtoast.makeText(MainActivity.this,"获取二维码失败",Toast.LENGTH_LONG).show();
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
////                        Toast.makeText(MainActivity.this,"result"+responseStr,Toast.LENGTH_LONG).show();
//
//
//                    }
//                });
            }
        });
    }

    private void getAdvertising() {

        OkHttpClient okHttpClient = new OkHttpClient();
        //post方法获取
        RequestBody body = RequestBody.create(MediaType.parse("application/json"),
                "{\n" + "\"deviceId\",\n" + "}");

        //get方法获取
        //构造Request对象
        //采用建造者模式，链式调用指明进行Get请求,传入Get的请求地址
        final Request request = new Request.Builder()
                .url("http://www.hzleshare.net.cn/run/get-index-ads")
                .post(body)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                //將請求返回的轉換成字符串
                final String responseStr2 = response.body().string();

                try {
                    JSONObject jsonObj = new JSONObject(responseStr2);//字符串轉json對象，並放入我的請求字符串
                    Log.e("广告页面Json对象jsonObj", String.valueOf(jsonObj));
                    JSONObject getJsonData = jsonObj.getJSONObject("data");//jsobj转换成字符串对象
                    JSONArray getElement = getJsonData.getJSONArray("urls");//字符串对象转换成JsonAarry数组对象
                    Boolean getSuccess = jsonObj.optBoolean("success");
                    Log.e("广告页获取返回是否成功的参数", String.valueOf(getSuccess));
                    String getMsg = jsonObj.optString("msg");


                    if (getSuccess == true) {
                        boolean isDownable=false;
                        for (int i = 0; i < getElement.length(); i++) {
                            Sub = getElement.getString(i);
                            ListURL.add(Sub);
                            Log.e("遍历ListURL数组的图片地址", String.valueOf(ListURL));
                            advURL = ListURL.get(i);
                            Suffix = advURL.substring(advURL.lastIndexOf("/") + 1);//截取广告图后缀名
                            ListSuffix.add(Suffix);
                            Log.d("查看截取的字符串", Suffix);
                            Log.d("查看mFileList", String.valueOf(FolderList_Foreach));

                            File fileP = new File("/mnt/sdcard/advertising/" + Suffix);
                            Log.d("查看file", String.valueOf(fileP));
                            if (adv_path.exists() && adv_path.isDirectory()) {
                                if (!fileP.exists()) {
                                    Log.d("文件不存在", String.valueOf(i));
                                    if (!isDownable){
                                        LocalUtils.deleteDir("/mnt/sdcard/advertising");//删除文件夹
                                        if (adv_path.exists()) {
                                            LocalUtils.createFiles(String.valueOf(adv_path));//创建广告文件夹
                                        }
                                        isDownable=true;
                                    }
                                } else {
                                    Log.d("文件存在", String.valueOf(i));
                                }
                            }
                        }

                        if (isDownable) {
                            for (int j = 0;j<ListURL.size();j++) {
                                myURL(ListURL.get(j));
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        zdialog.setLoadingBuilder(Z_TYPE.DOUBLE_CIRCLE)//设置类型
//                                                .setLoadingColor(Color.BLACK)//颜色
//                                                .setHintText("加载广告中...")
//                                                .setHintTextSize(8)
//                                                .setCanceledOnTouchOutside(false)
//                                                .show();
//                                        Log.d("zdialog开启", String.valueOf(zdialog));
//                                    }
//                                });
                            }
                        }

                        Log.d("广告后缀名", String.valueOf(ListSuffix));
//                            myURL("http://imgsrc.baidu.com/imgad/pic/item/562c11dfa9ec8a1377806ee9fd03918fa0ecc024.jpg");
//                            myURL("http://img2.niutuku.com/desk/1207/1053/ntk121546.jpg");
//                            myURL("http://imgsrc.baidu.com/imgad/pic/item/f636afc379310a5592c42400bd4543a983261065.jpg");
//                            myURL("http://imgsrc.baidu.com/imgad/pic/item/f2deb48f8c5494eeec90573326f5e0fe99257e6d.jpg");


//                        String getElement1 = getElement.getString(0);
//                        String getElement2 = getElement.getString(1);
//                        String getEl ement3 = getElement.getString(2);

//                        //打印JsonArray数组下标
//                        Log.e("getElement[1]", getElement1);
//                        Log.e("getElement[2]", getElement2);
//                        Log.e("getElement[3]", getElement3);

//                        byte bitmapArray[] = getElement1.getBytes();
//                        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
//                        Log.e("ssssssssssss", String.valueOf(bitmap));
//                        Message msg = new Message();//創建消息對象
//                        msg.what = 2;//設置消息唯一的區別碼
//                        msg.obj = bitmap;//將圖片賦值給msg的obj
//                        handler.sendMessage(msg);//將msg發送消息通知Handler更新
                    } else {
                        Log.d("没走Success,图片获取失败了o(╥﹏╥)o", "(灬0.0灬)");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void myURL(String advertisingURL) {
        //轉換成功之後，開始二次請求
        final Request request2 = new Request.Builder()
                .url(advertisingURL)//請求轉換成字符串的imgUrl，imgUrl是圖片地址
                .build();

        OkHttpClient okHttpClient2 = new OkHttpClient();

        okHttpClient2.newCall(request2).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {

            }

            public void onResponse(Call call, Response response) throws IOException {

                InputStream inputStream = response.body().byteStream();//得到图片的流
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);//將图片流轉換成圖片
                inputStream.close();
//                bitmap_list.add(bitmap);
                Message msg = new Message();//創建消息對象
                msg.what = 2;//設置消息唯一的區別碼
//                msg.obj = bitmap_list;//將圖片賦值給msg的obj
                msg.obj = bitmap;
                handler.sendMessage(msg);//將msg發送消息通知Handler更新

                //这里是下载全部数据
//                handler.obtainMessage(2).sendToTarget();
            }
        });
    }

    //控制图片轮播
    class MyThread extends Thread{
        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            while(isStart){
                Message message=new Message();
                message.what=666;
                message.obj=p;
                handler.sendMessage(message);
                try {
                    //睡眠3秒,在isStart为真的情况下，一直每隔三秒循环
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                p++;
            }
        }
    }

}
//    public void getHttpQRcode(){
//        OkHttpClient okHttpClient = new OkHttpClient();
////创建一个Request
//        final Request request = new Request.Builder()
//                .url("http://api.k780.com:88/?app=qr.get&data=test&level=L&size=6")
//                .build();
////new call
//        Call call = okHttpClient.newCall(request);
//        //请求加入调度
//        call.enqueue(new Callback()
//        {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                Log.d("response" , String.valueOf(response));
//
//
//
//            }
//        });
//
//
//    }

//    private String getHttpCode(){
//        String getUrl = "https://run.hzleshare.com/api/get-pay-qrcode?deviceId=12312313&time=1110";
//        try {
//            URL url = new URL(getUrl);
//            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
//            httpURLConnection.setConnectTimeout(8000);
//            httpURLConnection.setRequestMethod("GET");
//            httpURLConnection.setRequestProperty("accept", "*/*");
//            httpURLConnection.setRequestProperty("", "");
//        } catch (ProtocolException e) {
//            e.printStackTrace();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


//    private Bitmap getHttpBitmap(String url) {
//        URL myFileURL;
//        Bitmap bitmap=null;
//        try{
//            myFileURL = new URL(url);
//            //获得连接
//            HttpURLConnection conn=(HttpURLConnection)myFileURL.openConnection();
//            //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
//            conn.setConnectTimeout(6000);
//            //连接设置获得数据流
//            conn.setDoInput(true);
//            //不使用缓存
//            conn.setUseCaches(false);
//            //这句可有可无，没有影响
//            //conn.connect();
//            //得到数据流
//            InputStream is = conn.getInputStream();
//            //解析得到图片
//            bitmap = BitmapFactory.decodeStream(is);
//            //关闭数据流
//            is.close();
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        return bitmap;
//    }
//    }

//    private void sendRequestHttp(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                HttpURLConnection httpURLConnection = null;
//                BufferedReader bufferedReader = null;
//                try {
//                    URL url = new URL("https://run.hzleshare.com/api/get-pay-qrcode?deviceId  =&time=  ");
//                    httpURLConnection = (HttpURLConnection)url.openConnection();
//                    httpURLConnection.setRequestMethod("GET");
//                    httpURLConnection.setConnectTimeout(8000);
//                    httpURLConnection.setReadTimeout(8000);
//                    InputStream in = httpURLConnection.getInputStream();
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//                    StringBuilder respons = new StringBuilder();
//                    Toast.makeText(MainActivity.this,"respons"+respons,Toast.LENGTH_SHORT).show();
//                    String line;
//                    while ((line=reader.readLine())!=null){
//                        respons.append(line);
//                    }
//                    showResponse(respons.toString());
//                }catch (Exception e){
//                    e.printStackTrace();
//                }finally {
//                    if (bufferedReader!=null){
//                        try{
//                            bufferedReader.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    if (httpURLConnection!=null){
//                        httpURLConnection.disconnect();
//                    }
//                }
//            }
//        }).start();
//    }
//
//    private void showResponse(final Bitmap respons){
//        img_advertising.setImageBitmap(respons);
//
//    }

//    public class HttpUtils {
//        public final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//        public String code(String url, String json) throws IOException {
//            //把请求的内容字符串转换为json
//            RequestBody body = RequestBody.create(JSON, json);
//            //RequestBody formBody = new FormEncodingBuilder()
//
//            Request request = new Request.Builder()
//                    .url("https://run.hzleshare.com/api/get-pay-qrcode?deviceId  =&time=  ")
//                    .post(body)
//                    .build();
//
//            Response response = client.newCall(request).execute();
//            String result = response.body().string();
//
//            return result;
//        }
//        public String bolwingJson(Bitmap bitmap) {
//            return "{'username':" + username + "," + "'password':" + password + "}";
//            //     "{'username':" + username + ","+"'password':"+password+"}";
//        }
//    }
