package com.zpone.utils;

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
    private final static String FilePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "zpower.txt";
    private static File file;
    private static FileOutputStream fos;
    private static OutputStreamWriter writer;

    public static void init() {
        file = new File(FilePath);
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

    /**
     * save adc data
     *
     * @param data
     */
    public static void saveBytesToFile(byte[] data) {
        byte[] round = new byte[2];
        byte[] power = new byte[2];
        byte[] temp = new byte[2];
        System.arraycopy(data, 0, round, 0, 2);
        System.arraycopy(data, 2, power, 0, 2);
        try {
            writer.append(BaseUtils.bytes2ToInt(power, 0) + "");
            writer.append("    ,    ");
            writer.append(BaseUtils.bytes2ToInt(round, 0) + "");
            writer.append("\n");
            for (int i = 4; i < data.length; i = i + 2) {
                System.arraycopy(data, i, temp, 0, 2);
                writer.append(BaseUtils.bytes2ToInt(temp, 0) + "");
                writer.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveBytesToFile4(byte[] data) {
        byte[] temp = new byte[2];
        try {
            for (int i = 0; i < data.length; i = i + 2) {
                System.arraycopy(data, i, temp, 0, 2);
                writer.append(BaseUtils.bytes2ToInt(temp, 0) + "");
                writer.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 十六进制保存数据
     *
     * @param data
     */
    public static void saveBytesToFile2(byte[] data) {
        for (byte byteChar : data) {
            try {
                writer.append(String.format("%02X ", byteChar));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            writer.append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void saveBytesToFile5(byte[] data) {
        byte[] temp = new byte[2];
        try {
            for (int i = 0; i < data.length; i = i + 2) {
                System.arraycopy(data, i, temp, 0, 2);
                writer.append(BaseUtils.bytes2ToInt(temp, 0) + "");
                writer.append(" ");
            }
            writer.append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 十进制保存数据
     *
     * @param data
     */
    public static void saveBytesToFile3(byte[] data) {
        byte[] flag = new byte[2];
        byte[] instan_cadence = new byte[2];
        byte[] avg_cadence = new byte[2];
        byte[] instan_power = new byte[2];
        byte[] avg_power = new byte[2];
        System.arraycopy(data, 0, flag, 0, 2);
        System.arraycopy(data, 2, instan_cadence, 0, 2);
        System.arraycopy(data, 4, avg_cadence, 0, 2);
        System.arraycopy(data, 6, instan_power, 0, 2);
        System.arraycopy(data, 8, avg_power, 0, 2);
        try {
            writer.append(BaseUtils.bytes2ToInt(flag, 0) + " ");
            writer.append(BaseUtils.bytes2ToInt(instan_cadence, 0) + " ");
            writer.append(BaseUtils.bytes2ToInt(avg_cadence, 0) + " ");
            writer.append(BaseUtils.bytes2ToInt(instan_power, 0) + " ");
            writer.append(BaseUtils.bytes2ToInt(avg_power, 0) + " ");
            writer.append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveBytesToFile6(byte[] data) {
        byte[] dot = new byte[2];
        byte[] candence = new byte[2];
        byte[] time = new byte[2];
        System.arraycopy(data, 0, dot, 0, 2);
        System.arraycopy(data, 2, candence, 0, 2);
        System.arraycopy(data, 4, time, 0, 2);
        for (int i = 6; i < data.length; i++) {
            try {
                writer.append(String.format("%1$-10s",BaseUtils.bytes2ToInt(dot, 0) + ""));//格式元输出,每个字符串占10位，不足的后面补空格
                writer.append(String.format("%1$-10s",BaseUtils.bytes2ToInt(candence, 0) + ""));
                writer.append(String.format("%1$-10s",BaseUtils.bytes2ToInt(time, 0) + ""));
                writer.append(BaseUtils.byteToInt(data[i])+"");
                writer.append("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void saveBytesToFile7(byte[] data) {
        try {
            for (int i = 0; i < data.length; i = i + 2) {
                writer.append(BaseUtils.convertTwoBytesToInt(data[i], data[i+1]) + "");
                writer.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void closeWriter() {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (fos != null) {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
