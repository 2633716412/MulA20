package com.example.mula20.Utils;

import android.text.TextUtils;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Base64FileUtil {

    /**
     * 得到Base64的文件后缀
     *
     * @param iden iden
     * @return {@link String}
     */
    public static String getBase64FileSuffix(String iden){
        if(iden == null || iden.isEmpty()){
            return null;
        }
        if (iden.contains("jpeg")) {
            return ".jpg";
        }else if(iden.contains("png")){
            return ".png";
        }else if(iden.contains("gif")){
            return ".gif";
        }else if(iden.contains("jpg")){
            return ".jpg";
        }
        return null;
    }

    /**
     * 创建临时文件
     *
     * @param filePaht 文件目录
     * @param fileName 文件名称
     * @return {@link File}
     * @throws IOException ioexception
     */
    public static File createFile(String filePaht, String fileName) throws IOException {
        File tempFile = new File(filePaht, fileName);
        if(!tempFile.getParentFile().exists()){
            tempFile.getParentFile().mkdirs();
        }
        if(!tempFile.exists()){
            tempFile.createNewFile();
        }
        return tempFile;
    }

    /*
     *actions: 将文件转成base64 字符串
     *path：文件路径
     */
    public static String encodeBase64File(String path) throws Exception {
        if(TextUtils.isEmpty(path)){
            return null;
        }
        InputStream is = null;
        byte[] data = null;
        String result = null;
        try{
            is = new FileInputStream(path);
            //创建一个字符流大小的数组。
            data = new byte[is.available()];
            //写入数组
            is.read(data);
            //用默认的编码格式进行编码
            result = Base64.encodeToString(data, Base64.NO_CLOSE);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null !=is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }
}
