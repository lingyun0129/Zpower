package com.zpone.model;

/**
 * Created by user on 2017/8/8.
 * Power by cly
 */

public class RecordData {
    private String date;//日期
    private String time;//用时
    private int avg_p;//平均功率
    private int avg_rpm;//平均踏频
    private double km;//里程
    private double calorie;//卡路里

    public RecordData(){

    }
    public RecordData(String date, String time, int avg_p, int avg_rpm, double km, double calorie) {
        this.date = date;
        this.time = time;
        this.avg_p = avg_p;
        this.avg_rpm = avg_rpm;
        this.km = km;
        this.calorie = calorie;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getAvg_p() {
        return avg_p;
    }

    public int getAvg_rpm() {
        return avg_rpm;
    }

    public double getKm() {
        return km;
    }

    public double getCalorie() {
        return calorie;
    }
}
