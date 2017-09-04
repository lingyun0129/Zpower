package com.zpower.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by Administrator on 2017/9/2.
 */

public class FileUtils {
    private final static String FilePath = Environment.getExternalStorageDirectory().getPath()+ File.separator+"zpower.txt";
    private static File file;
    private static FileOutputStream fos ;
    private static OutputStreamWriter writer;
    public  static void init() {
        file=new File(FilePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            fos = new FileOutputStream(file);
            //writer = new OutputStreamWriter(fos, "utf-8");
            writer = new OutputStreamWriter(fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public  static void saveBytesToFile(byte[] data){
        byte[] round=new byte[2];
        byte[] power=new byte[2];
        byte[] temp=new byte[2];
        System.arraycopy(data,0,round,0,2);
        System.arraycopy(data,2,power,0,2);
        try {
            writer.append(BaseUtils.bytes2ToInt(power,0)+"");
            writer.append("    ,    ");
            writer.append(BaseUtils.bytes2ToInt(round,0)+"");
            writer.append("\n");
            for (int i=4;i<data.length;i=i+2){
                System.arraycopy(data,i,temp,0,2);
                writer.append(BaseUtils.bytes2ToInt(temp,0)+"");
                writer.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

/*        final StringBuilder stringBuilder = new StringBuilder(data.length);
        for (byte byteChar : data)
            stringBuilder.append(String.format("%D ", byteChar));
            String hexString=stringBuilder.toString();
            String roundStr=hexString.substring(0,4);
            String powerStr=hexString.substring(4,8);
            String otherStr=hexString.substring(8,hexString.length());
        try {
           writer.append(powerStr);
            writer.append("  ,  ");
            writer.append(roundStr);
            writer.append("\n");
            writer.append(otherStr);
            writer.append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
    public static void closeWriter(){
        if(writer!=null){
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (fos!=null){
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
