package com.example.mula20.PowerManager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import com.example.mula20.Modules.Paras;

import java.io.File;


public class PowerManagerA2040_XiPin extends BasePowerManager{

    Context context;

    public PowerManagerA2040_XiPin(Context context) {

        this.context = context;
    }

    @Override
    public void ShutDown() {

        Intent intent = new Intent("com.zc.zclcdoff");
        context.sendBroadcast(intent);
        Paras.volume=0;
    }

    @Override
    public void Open() {

//        Intent intent = new Intent("com.zc.zclcdon");
//        context.sendBroadcast(intent);
        Paras.volume=100;
        Intent intent = new Intent("wits.action.reboot");
        context.sendBroadcast(intent);

    }

    @Override
    public void Reboot() {
        Intent intent = new Intent("wits.action.reboot");
        context.sendBroadcast(intent);
    }

    @Override
    public void Install(String path) {
        File file=new File(path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 24) {
            Uri apkUri = FileProvider.getUriForFile(Paras.appContext, "com.example.multimediav2.fileProvider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }

        Paras.appContext.startActivity(intent);
    }
}