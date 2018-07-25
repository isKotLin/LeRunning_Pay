//package com;
//
//import android.app.Activity;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.vigorchip.lerunning.AdvertiseView;
//import com.vigorchip.lerunning.LocalUtils;
//import com.vigorchip.lerunning.R;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.List;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//
///**
// * Created by wr-app1 on 2018/1/8.
// */
//
//public class TextActivity extends Activity {
//    String Sub;
//    //需要用到的变量
//    String Suffix = null;
//    String advURL;
//    //广告地址类型集合
//    List<String> ListURL = new ArrayList<String>();
//    //广告图片后缀名集合
//    List<String> ListLocation = new ArrayList<String>();
//    AdvertiseView mA;
//    //这是图片
//    ArrayList<Bitmap> bitmap_list=new ArrayList<Bitmap>();
//    //这是图片描述
//    ArrayList<String> bitmap_title=new ArrayList<String>();
//    Handler mHandler=new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            Toast.makeText(TextActivity.this,"呵呵",Toast.LENGTH_SHORT).show();
//            switch(msg.what){
//                case 0:
//                    if(bitmap_list.size()>0&&bitmap_title.size()>0){
//                        mA.setImagesAndTitles(bitmap_list,bitmap_title);
//                    }
//                    break;
//            }
//        }
//    };
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.text);
//
//        mA = (AdvertiseView) findViewById(R.id.ad);
//        getAdvertising();
//    }
//    private void getAdvertising() {
//
//        OkHttpClient okHttpClient = new OkHttpClient();
//        //post方法获取
//        RequestBody body = RequestBody.create(MediaType.parse("application/json"),
//                "{\n" + "\"deviceId\",\n" + "}");
//
//        //get方法获取
//        //构造Request对象
//        //采用建造者模式，链式调用指明进行Get请求,传入Get的请求地址
//        final Request request = new Request.Builder()
//                .url("http://www.hzleshare.net.cn/run/get-index-ads")
//                .post(body)
//                .build();
//
//        Call call = okHttpClient.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//
//                //將請求返回的轉換成字符串
//                final String responseStr2 = response.body().string();
//
//                try {
//                    JSONObject jsonObj = new JSONObject(responseStr2);//字符串轉json對象，並放入我的請求字符串
//                    Log.e("广告页面Json对象jsonObj", String.valueOf(jsonObj));
//                    LocalUtils.deleteDir("/mnt/sdcard/advertising");//删除文件夹
//                    JSONObject getJsonData = jsonObj.getJSONObject("data");//jsobj转换成字符串对象
//                    JSONArray getElement = getJsonData.getJSONArray("urls");//字符串对象转换成JsonAarry数组对象
//                    Boolean getSuccess = jsonObj.optBoolean("success");
//                    Log.e("广告页获取返回是否成功的参数", String.valueOf(getSuccess));
//                    String getMsg = jsonObj.optString("msg");
//
//                    //打印返回参数
//                    Log.e("广告返回是否成功？", String.valueOf(getSuccess));
//                    Log.e("广告返回消息msg为？", getMsg);
//
//                    if (getSuccess == true) {
//                        //下载数据前，先清除数据
//                        bitmap_list.clear();
//                        bitmap_title.clear();
//                        for (int i = 0; i < getElement.length(); i++) {
//                            Sub = getElement.getString(i);
//                            ListURL.add(Sub);
//                            Log.e("遍历ListURL数组的图片地址", String.valueOf(ListURL));
////                            String strMp4 = Sub.substring(Sub.lastIndexOf(".p")+1);
////                            Log.e("截取字符串.p后面的字符", strMp4);
//
////                        for (int i = 0;i<ListURL.size();i++){
////                            ListURL.get(i);
////                            Log.e("遍历List数组的图片地址", String.valueOf(ListURL));
////                                   }
//                            advURL = ListURL.get(i);
//                            Suffix = advURL.substring(advURL.lastIndexOf("/") + 1);//截取广告图后缀名
//                            ListLocation.add(Suffix);
//                            Log.d("查看截取的字符串", Suffix);
//                            myURL(advURL);
//                        }
//                        //这里是下载全部数据
//                        mHandler.obtainMessage(0).sendToTarget();
//                        Log.d("广告后缀名", String.valueOf(ListLocation));
////                            myURL("http://imgsrc.baidu.com/imgad/pic/item/562c11dfa9ec8a1377806ee9fd03918fa0ecc024.jpg");
////                            myURL("http://img2.niutuku.com/desk/1207/1053/ntk121546.jpg");
////                            myURL("http://imgsrc.baidu.com/imgad/pic/item/f636afc379310a5592c42400bd4543a983261065.jpg");
////                            myURL("http://imgsrc.baidu.com/imgad/pic/item/f2deb48f8c5494eeec90573326f5e0fe99257e6d.jpg");
//
//
////                        String getElement1 = getElement.getString(0);
////                        String getElement2 = getElement.getString(1);
////                        String getEl ement3 = getElement.getString(2);
//
////                        //打印JsonArray数组下标
////                        Log.e("getElement[1]", getElement1);
////                        Log.e("getElement[2]", getElement2);
////                        Log.e("getElement[3]", getElement3);
//
////                        byte bitmapArray[] = getElement1.getBytes();
////                        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
////                        Log.e("ssssssssssss", String.valueOf(bitmap));
////                        Message msg = new Message();//創建消息對象
////                        msg.what = 2;//設置消息唯一的區別碼
////                        msg.obj = bitmap;//將圖片賦值給msg的obj
////                        handler.sendMessage(msg);//將msg發送消息通知Handler更新
//                    } else {
//                        Log.d("没走Success,图片获取失败了o(╥﹏╥)o", "(灬ꈍ ꈍ灬)");
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//    public void myURL(final String advertisingURL) {
//        //轉換成功之後，開始二次請求
//        final Request request2 = new Request.Builder()
//                .url(advertisingURL)//請求轉換成字符串的imgUrl，imgUrl是圖片地址
//                .build();
//
//        OkHttpClient okHttpClient2 = new OkHttpClient();
//
//        okHttpClient2.newCall(request2).enqueue(new Callback() {
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            public void onResponse(Call call, Response response) throws IOException {
//
//                InputStream inputStream = response.body().byteStream();//得到图片的流
//                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);//將图片流轉換成圖片
//                inputStream.close();
//                bitmap_list.add(bitmap);
//                //这样就能把一条字符串按/分割成数组，末尾就是图片文件名
//                String[] name=advertisingURL.split("/");
//                bitmap_title.add(name[name.length-1]);
//                System.out.println("name:"+name[name.length-1]);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(TextActivity.this,"下载图片",Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
//    }
//
//
//}
