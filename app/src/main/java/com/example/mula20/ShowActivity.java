package com.example.mula20;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.annotation.RequiresApi;

import com.example.mula20.HttpUnit.HttpUnitFactory;
import com.example.mula20.Modules.DeviceData;
import com.example.mula20.Modules.LogHelper;
import com.example.mula20.Modules.Paras;
import com.example.mula20.Modules.SPUnit;
import com.example.mula20.Utils.Base64FileUtil;
import com.example.mula20.Utils.DateUtil;
import com.example.mula20.models.MyWebView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ShowActivity extends BaseActivity {

    private MyWebView webView1;
    private MyWebView webView2;
    private Button btn;
    private boolean waitDouble = true;
    private Date endTime=new Date();
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show);
        btn=  findViewById(R.id.back);
        btn.getBackground().setAlpha(0);
        Paras.appContext=this;

        View decorView = getWindow().getDecorView();
        // 隐藏状态栏
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        //隐藏状态栏时也可以把ActionBar也隐藏掉
        ActionBar actionBar = getActionBar();
        //actionBar.hide();

        SPUnit spUnit = new SPUnit(ShowActivity.this);
        DeviceData deviceData = spUnit.Get("DeviceData", DeviceData.class);
        webView1=  findViewById(R.id.webView1);
        WebSettings webSetting1=webView1.getSettings();
        webSetting1.setJavaScriptEnabled(true);
        webSetting1.setDomStorageEnabled(true);
        webSetting1.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webSetting1.setMediaPlaybackRequiresUserGesture(false);
        }
        webView1.setWebChromeClient(new WebChromeClient());
        //webView1.setBackgroundColor(0); // 设置背景色
        webView2=  findViewById(R.id.webView2);
        webView2.setBackgroundColor(0); // 设置背景色

        webView2.setWebViewClient(new WebViewClient() {
            // 解决H5的音视频不能自动播放的问题
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //view.loadUrl("javascript:palyVideo()");
            }

            /*@Override
            public void onPageStarted(WebView view, String url,
                                      Bitmap favicon) {

                super.onPageStarted(view, url, favicon);
            }*/
            @Override
            public void onPageStarted(final WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                //为了使webview加载完数据后resize高度，之所以不放在onPageFinished里，是因为onPageFinished不是每次加载完都会调用
                int w = View.MeasureSpec.makeMeasureSpec(0,
                        View.MeasureSpec.UNSPECIFIED);
                int h = View.MeasureSpec.makeMeasureSpec(0,
                        View.MeasureSpec.UNSPECIFIED);
                //重新测量
                view.measure(w, h);

            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
                handler.proceed();
            }
        });
        webView2.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                super.onShowCustomView(view, callback);
            }
        });

        WebSettings webSetting2=webView2.getSettings();
        webSetting2.setJavaScriptEnabled(true);
        webSetting2.setPluginState(WebSettings.PluginState.ON);
        webSetting2.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSetting2.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        webSetting2.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//设置自适应屏幕的算法，一般是LayoutAlgorithm.SINGLE_COLUMN。如果不做设置，4.2.2及之前版本自适应时可能会出现表格错乱的情况
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webSetting2.setMediaPlaybackRequiresUserGesture(false);
        }
        //webView2.getBackground().setAlpha(0); // 设置填充透明度 范围：0-255
        //webView2.loadUrl("http://192.168.9.201:14084/selfpc2/app/index.html?id=10024");

        Thread playThread=new Thread(new Runnable() {
            @Override
            public void run() {
                if(!Objects.equals(deviceData.getSn(), "")) {
                    GetProgramData(deviceData.getSn());
                    while (true) {
                        Date nowTime=new Date();
                        if(Paras.updateProgram) {
                            endTime=GetProgramData(deviceData.getSn());
                            Paras.updateProgram=false;
                        }
                        if(nowTime.getTime()>endTime.getTime()) {
                            Paras.updateProgram=true;
                        }
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            LogHelper.Error(e);
                        }
                    }
                }
            }
        });
        playThread.start();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(waitDouble == true){
                    waitDouble = false;
                    Thread thread = new Thread(){
                        @Override
                        public void run(){
                            try {
                                sleep(2000);
                                if(waitDouble == false){
                                    waitDouble = true;
                                }
                            } catch (InterruptedException e) {
                                LogHelper.Error(e);
                            }
                        }
                    };
                    thread.start();
                }else{
                    LogHelper.Debug("跳转配置页");
                    waitDouble = true;
                    Paras.first=false;
                    playThread.interrupt();
                    SkipTo(MainActivity.class);
                }
            }
        });
        //截屏，默认隔30分钟截屏一次
    }

    public Date GetProgramData(String sn) {
        Date date=new Date();
        try {
            String jsonStr="";
            try {
                jsonStr = HttpUnitFactory.Get().Get(Paras.mulAPIAddr + "/media/third/getProgramData?sn=" + sn);
            } catch (Exception e) {
                LogHelper.Error("获取节目异常："+e);
                Paras.updateProgram=true;
            }
            if(!Objects.equals(jsonStr, "")) {
                JSONObject object = new JSONObject(jsonStr);
                StringBuilder url = new StringBuilder(Paras.mulHtmlAddr);
                String wvUrl="";
                JSONArray itemArray = object.getJSONArray("data");
                final boolean[] first = {false};
                if(object.getBoolean("success")) {
                    for (int i = 0; i < itemArray.length(); i++) {
                        JSONObject object1 = itemArray.getJSONObject(i);
                        String repeatDay = object1.getString("repet_day");
                        Long programId = object1.getLong("program_id");
                        String underUrl=object1.getString("under_url");
                        DateUtil dateUtil = new DateUtil();
                        String nowWeek = String.valueOf(dateUtil.DayOfWeek());
                        if (repeatDay.contains(nowWeek)) {
                            JSONArray timeList = object1.getJSONArray("time_list");
                            for (int j = 0; j < timeList.length(); j++) {
                                JSONObject timeObject = timeList.getJSONObject(j);
                                String startStr = timeObject.getString("begin_time");
                                String endStr = timeObject.getString("end_time");
                                DateUtil begin = DateUtil.GetByHourMin(startStr);
                                DateUtil end = DateUtil.GetByHourMin(endStr);
                                DateUtil now = DateUtil.Now();
                                if (now.Between(begin, end)&& !first[0]) {
                                    List<String> timeStr= Arrays.asList(endStr.split(":"));
                                    Calendar start = Calendar.getInstance();
                                    int hour= Integer.parseInt(timeStr.get(0));
                                    int minutes= Integer.parseInt(timeStr.get(1));
                                    start.setTime(new Date());
                                    start.set( Calendar.HOUR_OF_DAY,hour);
                                    start.set( Calendar.MINUTE, minutes);
                                    start.set( Calendar.SECOND,0);
                                    date=start.getTime();
                                    url.append("?id=").append(programId);
                                    if(underUrl!=null&& !underUrl.equals("")) {
                                        wvUrl=underUrl;
                                    }
                                    first[0] =true;
                                }
                            }
                        }
                    }
                    String finalWvUrl = wvUrl;
                    ShowActivity.this.runOnUiThread(new Runnable() {
                        //boolean firstLoad=false;
                        public void run() {
                            try {
                                /*if(!firstLoad) {
                                    webView2.loadUrl(url.toString());
                                    webView1.loadUrl(finalWvUrl);
                                    firstLoad=true;
                                }*/
                                webView2.loadUrl(url.toString());
                                webView1.loadUrl(finalWvUrl);
                            } catch (Exception e) {
                                LogHelper.Error(e);
                            }

                        }
                    });
                }
            }
            //Thread.sleep(3000);
        } catch (Exception e) {
            LogHelper.Error(e);
        }
        return date;
    }
    @Override
    public void onPause() {
        super.onPause();
        if(null != webView2) {
            webView2.onPause();
        }
        if(null!=webView1) {
            webView1.onPause();
        }
    }
    @Override
    public void onResume() {
        super.onResume();

            /*new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                        SPUnit spUnit = new SPUnit(Paras.appContext);
                        DeviceData deviceData = spUnit.Get("DeviceData", DeviceData.class);
                        LogHelper.Debug("截屏开始");
                        String picPath = BaseActivity.Screenshot();
                        String base64Str = Base64FileUtil.encodeBase64File(picPath);
                        JSONObject uploadObject=new JSONObject();
                        uploadObject.put("device_id",deviceData.getId());
                        uploadObject.put("fileFormat",".jpg");
                        uploadObject.put("base64Str",base64Str);
                        String res = HttpUnitFactory.Get().Post(Paras.mulAPIAddr + "/media/third/uploadFile",uploadObject.toString());
                        JSONObject resObj= new JSONObject(res);
                        if(!resObj.getBoolean("success")) {
                            LogHelper.Error("截屏失败：" + picPath);
                        }
                        LogHelper.Debug("截屏完成：" + picPath);
                    } catch (Exception e) {
                        LogHelper.Error("截屏失败："+e.getMessage());
                    }
                }
            }).start();*/
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        super.onDestroy();
        webView2.loadUrl("about:blank");
        webView2.stopLoading();
        webView2.setWebChromeClient(null);
        webView2.setWebViewClient(null);
        webView2.destroy();
        webView2 = null;
    }

}