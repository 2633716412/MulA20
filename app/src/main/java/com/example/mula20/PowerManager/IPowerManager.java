package com.example.mula20.PowerManager;


import android.content.Context;

import com.example.mula20.Modules.OSTime;

import java.util.List;

public interface IPowerManager {
    boolean IsOpen();
    //void SetTime(String _open, String _close, String _repeat);
    void SetTime(List<OSTime> osTimes);
    void StartListen();
    void StopListen();
    void ShutDown();
    void Open();
    void Reboot();
    void setSystemTime(Context context);
    void Install(String path);
    String GetName();
}
