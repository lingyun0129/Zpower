package com.zpower.utils;

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
    public static int byte6ToInt(byte[] bytes, int off) {
        int b0 = bytes[off] & 0xFF;
        int b1 = bytes[off + 1] & 0xFF;
        int b2 = bytes[off + 2] & 0xFF;
        int b3 = bytes[off + 3] & 0xFF;
        int b4 = bytes[off + 4] & 0xFF;
        int b5 = bytes[off + 5] & 0xFF;
        return (b0 << 40) | (b1 << 32) | (b2 << 24) | (b3 << 16) | (b4 << 8) | b5;
    }

    public static int byte3ToInt(byte[] bytes, int off) {
        int b0 = bytes[off] & 0xFF;
        int b1 = bytes[off + 1] & 0xFF;
        int b2 = bytes[off + 2] & 0xFF;
        return (b0 << 16) | (b1 << 8) | b2;
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
