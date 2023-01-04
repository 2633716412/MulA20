package com.example.mula20;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.mula20.HttpUnit.HttpUnitFactory;
import com.example.mula20.Modules.DeviceData;
import com.example.mula20.Modules.IMsgManager;
import com.example.mula20.Modules.LogHelper;
import com.example.mula20.Modules.OSTime;
import com.example.mula20.Modules.Paras;
import com.example.mula20.Modules.SPUnit;
import com.example.mula20.models.CmdManager;
import com.example.mula20.models.DropData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MainActivity extends BaseActivity implements IMsgManager {

    private EditText device_name;
    private EditText inter1;
    private EditText inter2;
    private EditText inter3;
    private EditText inter4;
    private EditText port;
    private Spinner device_type;
    private Button btu_save;
    private TextView switch_text;
    private Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Paras.appContext=this;
        Paras.msgManager=this;
        Paras.handler=new Handler();
        /*Intent intent = new Intent(Paras.appContext, AppService.class);
        startService(intent);*/
        Paras.androidNumber= "Android"+android.os.Build.VERSION.RELEASE;
        Paras.Wiidth = getResources().getDisplayMetrics().widthPixels;
        Paras.Height = getResources().getDisplayMetrics().heightPixels;
        SPUnit spUnit = new SPUnit(MainActivity.this);
        DeviceData deviceData = spUnit.Get("DeviceData", DeviceData.class);
        device_type= findViewById(R.id.device_type);
        switch_text= findViewById(R.id.switch_text);
        spinner=findViewById(R.id.spinner);
        List<DropData> dropList=new ArrayList<DropData>();
        DropData dev0=new DropData("test","TEST");
        dropList.add(dev0);
        DropData dev1=new DropData("a20","DEVA20");
        dropList.add(dev1);
        DropData dev2=new DropData("a40","DEVA40");
        dropList.add(dev2);
        DropData dev3=new DropData("a20xp","DEVA20_XiPin");
        dropList.add(dev3);
        DropData dev4=new DropData("a40xp","DEVA40_XiPin");
        dropList.add(dev4);
        DropData dev5=new DropData("hk","HAI_KANG");
        dropList.add(dev5);
        ArrayAdapter<DropData> adapter = new ArrayAdapter<DropData>(MainActivity.this, android.R.layout.simple_spinner_item, dropList);
        device_type.setAdapter(adapter);

        if(deviceData.getId()>0) {
            device_name=  findViewById(R.id.device_name);
            inter1=  findViewById(R.id.inter1);
            inter2=  findViewById(R.id.inter2);
            inter3=  findViewById(R.id.inter3);
            inter4=  findViewById(R.id.inter4);
            port=  findViewById(R.id.port);
            if(deviceData.getOsTimes().size()>0) {
                StringBuilder timeStr= new StringBuilder();
                for(int i=0;i<7;i++) {
                    String item="";
                    for(OSTime osTime:deviceData.getOsTimes()) {
                        if(osTime.dayofweak==i+1) {
                            String week=GetCnWeek(osTime.dayofweak);
                            item="周"+week+" "+JudgeTime(osTime.open_hour)+":"+JudgeTime(osTime.open_min)+"开"+" "+JudgeTime(osTime.close_hour)+":"+JudgeTime(osTime.close_min)+"关";
                        }
                    }
                    if(item.equals("")) {

                        item="周"+GetCnWeek(i+1)+" 休息";
                    }
                    timeStr.append(item);
                    if(i!=6) {
                        timeStr.append("\n");
                    }
                }
                switch_text.setText(timeStr);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean isStopped=false;
                    while (!isStopped) {
                        Paras.mulAPIAddr=GetApiUrl(Paras.mulAPIAddr,deviceData.getApi_ip(),deviceData.getApi_port());
                        String urlSuffix="";
                        if(!Objects.equals(deviceData.getApi_ip(), "")) {
                            try {
                                String result= HttpUnitFactory.Get().Get(Paras.mulAPIAddr + "/media/third/getUrlSuffix");
                                if(!Objects.equals(result, "")) {
                                    JSONObject object = new JSONObject(result);
                                    urlSuffix = object.getString("data");
                                    isStopped=true;
                                }
                            } catch (Exception e) {
                                LogHelper.Error("获取节目地址异常："+e);
                            }
                        }
                        Paras.mulHtmlAddr=GetUrl(Paras.mulHtmlAddr,deviceData.getApi_ip(),deviceData.getApi_port(),urlSuffix);
                        try {
                            Thread.sleep(5000);
                        } catch (Exception e) {
                            LogHelper.Error(e);
                        }
                    }
                }
            }).start();
            device_name.setText(deviceData.getDevice_name());
            if(!Objects.equals(deviceData.getApi_ip(), "")) {
                List<String> inters= Arrays.asList(deviceData.getApi_ip().split("\\."));
                inter1.setText(inters.get(0));
                inter2.setText(inters.get(1));
                inter3.setText(inters.get(2));
                inter4.setText(inters.get(3));
            }
            port.setText(deviceData.getApi_port());
            for(int i=0;i<dropList.size();i++) {
                DropData data=dropList.get(i);
                if(Objects.equals(deviceData.getDevice_type(), data.getCode())) {
                    device_type.setSelection(i);
                }
            }
            //获取本地ip
            WifiManager wifiManager = (WifiManager) Paras.appContext.getSystemService(Paras.appContext.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }

            int ipAddress = wifiInfo.getIpAddress();
            if(!intToIp(ipAddress).equals("0.0.0.0")) {
                deviceData.setDevice_ip(intToIp(ipAddress));
            } else {
                String ip=getLocalIpAddress();
                deviceData.setDevice_ip(ip);
            }
            if(deviceData.getDevice_ip()!=null&& !Objects.equals(deviceData.getDevice_ip(), "")) {
                spUnit.Set("DeviceData",deviceData);
            }
            if (Paras.first) {
                CmdManager iIniHanlder = new CmdManager();
                iIniHanlder.Init(MainActivity.this, null);
                Paras.updateProgram=true;
                SkipTo(ShowActivity.class);
            }
        }

        btu_save= (Button) findViewById(R.id.btu_save);
        btu_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    device_name=  findViewById(R.id.device_name);
                    inter1=  findViewById(R.id.inter1);
                    inter2=  findViewById(R.id.inter2);
                    inter3=  findViewById(R.id.inter3);
                    inter4=  findViewById(R.id.inter4);
                    port=  findViewById(R.id.port);
                    device_type=  findViewById(R.id.device_type);
                    SPUnit spUnit = new SPUnit(MainActivity.this);
                    DeviceData data=spUnit.Get("DeviceData", DeviceData.class);
                    //获取本地ip
                    WifiManager wifiManager = (WifiManager) Paras.appContext.getSystemService(Paras.appContext.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    if (!wifiManager.isWifiEnabled()) {
                        wifiManager.setWifiEnabled(true);
                    }

                    int ipAddress = wifiInfo.getIpAddress();
                    if(!intToIp(ipAddress).equals("0.0.0.0")) {
                        data.setDevice_ip(intToIp(ipAddress));
                    } else {
                        String ip=getLocalIpAddress();
                        data.setDevice_ip(ip);
                    }

                    String ipStr = inter1.getText().toString() + "." +
                            inter2.getText().toString() +
                            "." +
                            inter3.getText().toString() +
                            "." +
                            inter4.getText().toString();
                    data.setDevice_name(device_name.getText().toString());
                    data.setApi_ip(ipStr);
                    data.setApi_port(port.getText().toString());

                    DropData deviceType=(DropData)device_type.getSelectedItem();
                    data.setDevice_type(deviceType.getCode());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean isStopped=false;
                            while (!isStopped) {
                                Paras.mulAPIAddr=GetApiUrl(Paras.mulAPIAddr,deviceData.getApi_ip(),deviceData.getApi_port());
                                String urlSuffix="";
                                if(!Objects.equals(deviceData.getApi_ip(), "")) {
                                    try {
                                        String result= HttpUnitFactory.Get().Get(Paras.mulAPIAddr + "/media/third/getUrlSuffix");
                                        if(!Objects.equals(result, "")) {
                                            JSONObject object = new JSONObject(result);
                                            urlSuffix = object.getString("data");
                                            isStopped=true;
                                        }
                                    } catch (Exception e) {
                                        LogHelper.Error("获取节目地址异常："+e);
                                    }
                                }
                                Paras.mulHtmlAddr=GetUrl(Paras.mulHtmlAddr,deviceData.getApi_ip(),deviceData.getApi_port(),urlSuffix);
                                try {
                                    Thread.sleep(5000);
                                } catch (Exception e) {
                                    LogHelper.Error(e);
                                }
                            }
                        }
                    }).start();
                    spUnit.Set("DeviceData",data);
                    CmdManager iIniHanlder = new CmdManager();
                    iIniHanlder.Init(MainActivity.this, null);
                    Paras.msgManager.SendMsg("修改配置完成");
                    Paras.updateProgram=true;
                    SkipTo(ShowActivity.class);
                } catch (Exception ex) {
                    LogHelper.Error(ex);
                    //Paras.msgManager.SendMsg("修改配置异常：" + ex.getMessage());
                }
            }
        });

        //获取机构下拉
        inter1=findViewById(R.id.inter1);
        inter2=findViewById(R.id.inter2);
        inter3=findViewById(R.id.inter3);
        inter4=findViewById(R.id.inter4);
        port=findViewById(R.id.port);
        if(inter1.getText()!=null&&inter2.getText()!=null&&inter3.getText()!=null&&inter3.getText()!=null&&port.getText()!=null) {
            StringBuilder ipStr=new StringBuilder(inter1.getText().toString());
            ipStr.append(".");
            ipStr.append(inter2.getText().toString());
            ipStr.append(".");
            ipStr.append(inter3.getText().toString());
            ipStr.append(".");
            ipStr.append(inter4.getText().toString());
            String apiIp=ipStr.toString();
            String apiPort=port.getText().toString();

            new Thread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void run() {
                    boolean isStopped=false;
                    while (!isStopped) {
                        Paras.mulAPIAddr=GetApiUrl(Paras.mulAPIAddr,apiIp,apiPort);
                        try {
                            String result= HttpUnitFactory.Get().Get(Paras.mulAPIAddr + "/media/third/orgList");
                            if(!Objects.equals(result, "")) {
                                JSONObject object = new JSONObject(result);
                                JSONArray jsonArray = object.getJSONArray("data");
                                List<DropData> list=new ArrayList<DropData>();
                                for(int i=0;i<jsonArray.length();i++) {
                                    DropData dropdata=new DropData();
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    dropdata.setId(obj.getLong("id"));
                                    dropdata.setName(obj.getString("org_name"));
                                    list.add(dropdata);
                                }
                                ArrayAdapter<DropData> adapter=new ArrayAdapter<DropData>(Paras.appContext, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,list);
                                spinner.setAdapter(adapter);

                                if(deviceData.getOrgId()>0) {
                                    DropData d=new DropData();
                                    for(int i=0;i<list.size();i++) {
                                        if(Objects.equals(list.get(i).getId(), deviceData.getOrgId())) {
                                            d=list.get(i);
                                        }
                                    }
                                    //DropData d = list.stream().filter(p-> Objects.equals(p.getId(), deviceData.getOrgId())).collect(Collectors.toList()).get(0);
                                    spinner.setSelection(list.indexOf(d));
                                }

                                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        DropData data= (DropData) spinner.getSelectedItem();
                                        deviceData.setOrgId(data.getId());
                                        spUnit.Set("DeviceData",deviceData);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });

                                isStopped=true;
                            }
                        } catch (Exception e) {
                            LogHelper.Error("获取机构列表异常："+e);
                        }
                        try {
                            Thread.sleep(5000);
                        } catch (Exception e) {
                            LogHelper.Error(e);
                        }
                    }
                }
            }).start();
        }

        final Button btn_tts = findViewById(R.id.btu_tts);
        btn_tts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent("com.android.settings.TTS_SETTINGS"));
            }
        });

    }

    private static String intToIp(int ip) {
        return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) &0xFF) + "." + (ip >> 24 & 0xFF);
    }

    public void SendMsg(String msg) {
        Message message = new Message();
        message.obj = msg;
        mHandler.sendMessage(message);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            Toast.makeText(Paras.appContext, msg.obj.toString(), Toast.LENGTH_LONG).show();
        }
    };

    private String GetCnWeek(int n) {
        switch (n) {
            case 1:return "一";
            case 2:return "二";
            case 3:return "三";
            case 4:return "四";
            case 5:return "五";
            case 6:return "六";
            default:return "日";
        }
    }

    private String JudgeTime(int n) {
        if(n<10) {
            return "0"+n;
        } else {
            return String.valueOf(n);
        }
    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception ex) {
            LogHelper.Error(ex);
            return "";
        }
        return "";
    }

    public String GetApiUrl(String oldUrl,String ip,String port) {
        String newStr="";
        String tallStr=oldUrl.substring(oldUrl.indexOf("/self"));
        String headStr=oldUrl.substring(0,oldUrl.indexOf("//")+2);
        newStr=headStr+ip+":"+port+tallStr;
        return newStr;
    }
    public String GetUrl(String oldUrl,String ip,String port,String urlSuffix) {
        String newStr="";
        String tallStr=oldUrl.substring(oldUrl.indexOf("/app"));
        String headStr=oldUrl.substring(0,oldUrl.indexOf("//")+2);
        newStr=headStr+ip+":"+port+"/"+urlSuffix+tallStr;
        return newStr;
    }
}