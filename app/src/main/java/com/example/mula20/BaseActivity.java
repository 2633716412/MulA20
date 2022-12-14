package com.example.mula20;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.Image;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.mula20.FileUnit.FileUnitDef;
import com.example.mula20.Modules.LogHelper;
import com.example.mula20.Modules.Paras;
import com.tencent.smtt.utils.FileUtil;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;


public class BaseActivity extends Activity {
    static FileUnitDef fileUnitDef;
    public static BaseActivity currActivity = null;

    private static final ArrayList<BaseActivity> activitys = new ArrayList<>();

    private static final ReentrantLock lock = new ReentrantLock();

    protected Context GetContext() {
        return this;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currActivity = this;

        new Thread(new Runnable() {
            @Override
            public void run() {
                DestoryActivitise(currActivity);
            }
        }).start();
    }

    static private synchronized void DestoryActivitise(BaseActivity newBaseActivity) {

        lock.lock();

        try {
            for (BaseActivity ba : activitys) {
                try {
                    ba.finish();
                } catch (Exception ex) {
                    LogHelper.Error(ex);
                }
            }
        } catch (Exception ex) {
            LogHelper.Error(ex);
        }

        activitys.add(newBaseActivity);

        lock.unlock();
    }

    /*public static Bitmap Screenshot() {
        Bitmap bmp = null;
        Class<?> surfaceClass;
        try {
            if (currActivity != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                String fn = formatter.format(new Date()) + ".jpg";
                //String dir = Environment.getExternalStorageDirectory() + "/nf";
                File fileSave = Paras.appContext.getExternalFilesDir("nf");
                String dir=fileSave.getPath();
                File file=new File(dir,fn);
                Process process = Runtime.getRuntime().exec("su");
                DataOutputStream localDataOutputStream = new DataOutputStream(process.getOutputStream());
                localDataOutputStream.writeBytes("screencap -p " + file.getPath()+" \n");
                localDataOutputStream.writeBytes("exit\n");
                localDataOutputStream.flush();
                process.waitFor();
                int ret = process.exitValue();
                LogHelper.Debug(ret + "");
                *//*View dView = currActivity.getWindow().getDecorView();
                dView.setDrawingCacheEnabled(true);
                dView.destroyDrawingCache();
                dView.buildDrawingCache();
                bmp = dView.getDrawingCache();
                bmp = Bitmap.createBitmap(dView.getWidth(), dView.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bmp);
                dView.draw(canvas);*//*

            }
        } catch (Exception ex) {
            LogHelper.Error(ex);
        } finally {
            return bmp;
        }
    }*/
    public static String Screenshot() {
        File file=Paras.appContext.getExternalFilesDir("nf");
        String dir1 = file.getPath();
        deleteFile(dir1,"jpg");
        try {

            if (currActivity != null) {
                fileUnitDef = new FileUnitDef();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                String fn = formatter.format(new Date()) + ".jpg";
                //String dir = Environment.getExternalStorageDirectory() + "/nf";
                File fileSave = Paras.appContext.getExternalFilesDir("nf");
                String dir=fileSave.getPath();
                file=new File(dir,fn);
                Process process = Runtime.getRuntime().exec("su");
                DataOutputStream localDataOutputStream = new DataOutputStream(process.getOutputStream());
                localDataOutputStream.writeBytes("screencap -p " + file.getPath()+" \n");
                localDataOutputStream.writeBytes("exit\n");
                process.waitFor();
                int ret = process.exitValue();
                LogHelper.Debug(ret + "");
                byte[] bytes=File2Bytes(file);
                file.delete();
                Bitmap bitmap=BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Bitmap bitmap1=adjustPhotoRotation(bitmap,90);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap1.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                fileUnitDef.Save(dir,fn,stream.toByteArray());
                /*View dView = currActivity.getWindow().getDecorView();
                dView.setDrawingCacheEnabled(true);
                dView.destroyDrawingCache();
                dView.buildDrawingCache();
                bmp = dView.getDrawingCache();
                bmp = Bitmap.createBitmap(dView.getWidth(), dView.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bmp);
                dView.draw(canvas);*/

            }
        } catch (Exception ex) {
            LogHelper.Error(ex);
        } finally {
            return file.getPath();
        }
    }

    @Override
    protected void onDestroy() {
        lock.lock();
        activitys.remove(this);
        lock.unlock();
        super.onDestroy();
    }


    public static void SkipTo(Class<? extends Activity> cls) {

        try {

            if (currActivity != null) {
                currActivity.Onleave();
            }

            Intent i = new Intent(currActivity, cls);
            currActivity.startActivity(i);
        } catch (Exception ex) {
            LogHelper.Error(ex);
        }
    }

    public void UIAction(Runnable runnable) {
        this.runOnUiThread(runnable);
    }

    public void Onleave() {

    }
    //????????????byte
    public static byte[] File2Bytes(File file) {
        int byte_size = 1024;
        byte[] b = new byte[byte_size];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(
                    byte_size);
            for (int length; (length = fileInputStream.read(b)) != -1;) {
                outputStream.write(b, 0, length);
            }
            fileInputStream.close();
            outputStream.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            LogHelper.Error(e);
        }
        return null;
    }
    //????????????
    public static Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {
        Matrix m = new Matrix();
        m.postRotate(orientationDegree);
        try {
            Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
            return bm1;
        } catch (OutOfMemoryError ex) {
        }
        return null;
    }

    private static void deleteFile(String path, String type) {
        File file = new File(path);
        // ???????????????????????????????????????
        File[] listFiles = file.listFiles();
        for (File f:listFiles) {
            //?????????????????????
            if (f.isDirectory()) {
                //????????????????????????????????????
                String path2 = f.getPath();
                deleteFile(path2,"");
            }else {
                //?????????????????? ??????delete()????????????
                String fileType = f.getName().substring(f.getName().lastIndexOf(".")+1);
                if(fileType.equals(type)){
                    LogHelper.Debug("???????????????" + f.getAbsolutePath());
                    f.delete();
                }
            }
        }
    }
}
