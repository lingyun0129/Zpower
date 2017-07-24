package com.zpower.model;

/**
 * Created by zx on 2017/3/7.
 */

public class HistoryData {
    private String date;
    private String time;
    private String watt;

    public HistoryData() {
    }

    public HistoryData(String date, String time, String watt) {
        this.date = date;
        this.time = time;
        this.watt = watt;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getWatt() {
        return watt;
    }

    public void setWatt(String watt) {
        this.watt = watt;
    }
}
