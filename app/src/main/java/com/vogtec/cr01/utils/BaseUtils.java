package com.vogtec.cr01.utils;

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

}
