package com.zpone.utils;

public final class CMDUtils {
    public static byte[] CMD_ENTER_TEST_MODE={0x68,0x01};//进入测试模式
    public static byte[] CMD_PARK_TEST={0x68,0x02};//车位检测
    public static byte[] CMD_SET_LMD_TEST={0x68,0x03};//灵敏度测试
    public static byte[] CMD_SET_TIME_TEST={0x68,0x04};//检测时间设置
    public static byte[] CMD_HIGHLEVEL_TIME_TEST={0x68,0x05};//高电平检测时间设置
    public static byte[] CMD_LOWLEVEL_TIME_TEST={0x68,0x06};//低电平检测时间设置
    public static byte[] CMD_BATTERY_TEST={0x68,0x07};//获取电量
    public static byte[] CMD_GET_BATTERY_TEST={0x68,0x08};//正常模式


}
