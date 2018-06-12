package com.zpone.inter;

/**
 * Created by guzhicheng on 2017/3/9.
 * 接收返回的蓝牙数据
 */

public interface RecordDataCallback {

    /**
     * 接收返回的本次骑行总时间
     * @param totalTime
     */
    public void onDataTotalHours(String totalTime);

    /**
     * 返回本次骑行总历程
     * @param totalKM
     */
    public void onDataTotalKM(double totalKM);

    /**
     * 返回本次骑行平均速度
     * @param avgWATT
     */
    public void onDataAvgWatt(int avgWATT);

    /**
     * 返回本次骑行总卡路里
     * @param AVGWatt
     */
    public void onDataTotalCalores(double AVGWatt);

    /**
     * 返回本次骑行的最大功率
     * @param maxWatt
     */
    public void onDataMaxWatt(int maxWatt);

    /**
     * 返回本次骑行的功率（即时功率）
     * @param watt
     */
    public void onDataWatt(int watt);

    /**
     * 踏频
     * @param rpm
     */
    public void onRPM(int rpm);

    public void onDefaultADC(int adc);

    public void onDataMaxRpm(int rpm);

    public void onDataMaxSpeed(float speed);

}
