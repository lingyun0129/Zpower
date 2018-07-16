package com.zpone.utils;

/**
 * Created by guzhicheng on 2017/3/9.
 */

public class BaseUtils {
    /**
     * byte数组转换为int整数
     *
     * @param bytes
     *            byte数组
     * @param off
     *            开始位置
     * @return int整数
     */
    public static int bytes4ToInt(byte[] bytes, int off) {
        int b0 = bytes[off] & 0xFF;
        int b1 = bytes[off + 1] & 0xFF;
        int b2 = bytes[off +2] & 0xFF;
        int b3 = bytes[off+3] & 0xFF;
        return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
    }

    /**
     * byte数组转换为int整数
     * @param bytes
     * @param off
     * @return
     */
    public static int byte3ToInt(byte[] bytes, int off) {
        int b0 = bytes[off] & 0xFF;
        int b1 = bytes[off + 1] & 0xFF;
        int b2 = bytes[off + 2] & 0xFF;
        return (b2 << 16) | (b1 << 8) | b0;
    }
    /**
     * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序。
     */
    public static int bytes2ToInt(byte[] src, int offset) {
        int value;
        value = (int) (((src[offset+1] & 0xFF) << 8)
                | (src[offset ] & 0xFF));
        return value;
    }

    /**
     * byte数组转换为int整数
     * @param b
     * @return
     */
    public static int byteToInt(byte b) {
        //Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
        //return b & 0xFF;
        return (int)b;
    }

    /**
     * short整数转换为2字节的byte数组(高位在前，低位在后)
     *
     * @param s
     *      short整数
     * @return byte数组
     */
    public static byte[] unsignedShortToByte2(int s) {
        byte[] targets = new byte[2];
        targets[0] = (byte) (s >> 8 & 0xFF);
        targets[1] = (byte) (s & 0xFF);
        return targets;
    }
    public static int convertTwoBytesToInt (byte b1, byte b2)      // signed
    {
        return (b2 << 8) | (b1 & 0xFF);
    }
    /**
         * 将String类型的时间转换成long,如：12:01:08
         * @param strTime String类型的时间
         * @return long类型的时间
         * */
    public static long convertStrTimeToLong(String strTime) {
        // TODO Auto-generated method stub
        String []timeArry=strTime.split(":");
        long longTime=0;
        if (timeArry.length==2) {//如果时间是MM:SS格式
            longTime=Integer.parseInt(timeArry[0])*60+Integer.parseInt(timeArry[1]);
        }else if (timeArry.length==3){//如果时间是HH:MM:SS格式
            longTime=Integer.parseInt(timeArry[0])*60*60+Integer.parseInt(timeArry[1])
                    *60+Integer.parseInt(timeArry[2]);
        }
        return longTime;
    }

    /**
     * 将long类型的秒数时间转化成hh:mm:ss或mm:ss类型的字符串
     * @param longTime
     * @return
     */
    public static String coventLongTimeToStr(long longTime){
        String hh,mm,ss;
        String colon=":";
        int hour=(int)longTime/(60*60);
        int minute=(int)(longTime-hour*60*60)/60;
        int second=(int)(longTime-hour*60*60-minute*60);
        //hh
        if(hour>0){//时间格式为HH:MM:SS
            if(hour<10){
                hh="0"+hour+colon;
            }else {
                hh=hour+colon;
            }
        }else{//时间格式为MM:SS
            hh="";
        }
        //mm
        if(minute>0){
            if(minute<10){
                mm="0"+minute+colon;
            }else{
                mm=minute+colon;
            }
        }else{
            mm="00"+colon;
        }
        //ss
        if(second>0){
            if(second<10){
                ss="0"+second;
            }else{
                ss=second+"";
            }
        }else{
            ss="00";
        }
        return (hh+mm+ss);
    }
}
