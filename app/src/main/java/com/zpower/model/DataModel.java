package com.zpower.model;

/**
 * Created by guzhicheng on 2017/3/7.
 * 存储传过来的byte数据
 * buffer[0];//第一个字节代表圈数
 * buffer[1];//秒
 * buffer[2];//毫秒
 * buffer[3]--buffer[5];//ADC的值
 * buffer[6];//电量
 */

public class DataModel {

    private int Battery_VOL;//电量
    private int r_data;//圈数
    private byte[] adc_data;//ADC变化值
    private int second;//秒
    private int milliSecond;//毫秒

    public int getSecond() {
        return second;
    }

    public int getMilliSecond() {
        return milliSecond;
    }

    public int getBattery_VOL() {
        return Battery_VOL;
    }

    public int getR_data() {
        return r_data;
    }

    public byte[] getAdc_data() {
        return adc_data;
    }

    /**
     * 赋值
     *
     * @param buffer
     * @return
     */
    public boolean value(byte[] buffer) {
        if (buffer == null || buffer.length != 7) {
            return false;
        }
            r_data = buffer[0];//第一个字节代表圈数
            adc_data = new byte[3];//4-6字节代表ADC
            System.arraycopy(buffer, 3, adc_data, 0, 3);//把adc的值存入adc_data数组
            second = buffer[1];
            milliSecond = buffer[2];
            Battery_VOL = buffer[6];//第7个字节代表电量
        return true;
    }

    /*@Override
    public String toString() {
        String t = "";
        for(byte b:data) {
            t += b;
        }
            return "Type:"+type+"  Data:"+t;
    }*/
}
