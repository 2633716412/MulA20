package com.example.mula20.Modules;

import android.content.Context;
import android.os.Handler;

import com.example.mula20.PowerManager.IPowerManager;

public class Paras {

    //<editor-fold desc="常量">
    public static Handler handler;

    public static final int time_start_listen_power = 60;

    public static final int time_loop_power = 30;

    public static final Long device_id = 10001L;

    public static final boolean DEVELOPMODE = true;

    public static final String FILEPROVICE = "com.ningfan.fileprovider";

    public static final String DEVMR = "test";//mr

    public static final String DEVA40 = "a40";

    public static final String DEVA20 = "a20";

    public static final String DEVA20_XiPin = "a20xp";

    public static final String DEVA40_XiPin = "a40xp";
    //</editor-fold>

    //<editor-fold desc="全局参数">

    public static Context appContext;

    public static int Wiidth = 1920;

    public static int Height = 1080;

    public static String devType = DEVMR;

    public static String mulAPIAddr = "http://ip:port/selfv2api";

    public static String mulHtmlAddr = "http://ip:port/app/index.html";

    public static String name = "";

    public static String androidNumber="";

    public static int volume = 100;
    //</editor-fold>

    //<editor-fold desc="单例">
    public static IPowerManager powerManager;

    public static IMsgManager msgManager;

    public static boolean first = true;

    public static boolean updateProgram = false;

    public static boolean[] hasRun=new boolean[3];

    public static int num=0;
    //</editor-fold>

}
