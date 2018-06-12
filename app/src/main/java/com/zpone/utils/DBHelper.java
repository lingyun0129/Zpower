package com.zpone.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zpone.model.HistoryData;
import com.zpone.model.RecordData;

import java.util.ArrayList;

/**
 * Created by user on 2017/8/8.
 * Power by cly
 */

public class DBHelper {
    private final MySQLiteHelper helper;
    private final SQLiteDatabase db;

    public DBHelper(Context context) {
        helper = MySQLiteHelper.getInstance(context);
        db = helper.getWritableDatabase();
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public void closeDb() {
        db.close();
    }

    /**
     * 插入一条数据
     * @param data
     */
    public void insertRecordData(RecordData data) {
        ContentValues values = new ContentValues();
        values.put("date", data.getDate());
        values.put("total_time", BaseUtils.convertStrTimeToLong(data.getTime()));
        values.put("avg_p", data.getAvg_p());
        values.put("avg_rpm", data.getAvg_rpm());
        values.put("km", data.getKm());
        values.put("cal", data.getCalorie());
        db.insert("data_records", null, values);
    }

    /**
     * 读取所有数据
     * @return
     */
    public ArrayList<HistoryData>queryHistoryData(){
        ArrayList<HistoryData>historyDatas=new ArrayList<>();
        //Cursor c = db.rawQuery("SELECT * FROM data_records ", null);
        Cursor c=db.query("data_records",null,null,null,null,null,"_id desc");
        while(c.moveToNext()) {
            String date=c.getString(c.getColumnIndex("date"));
            long time=c.getLong(c.getColumnIndex("total_time"));
            String watt=c.getInt(c.getColumnIndex("avg_p"))+"";

            HistoryData data=new HistoryData(date,BaseUtils.coventLongTimeToStr(time),watt);
            historyDatas.add(data);
        }
        c.close();
        return historyDatas;
    }

    public double getMaxKm(){
        double maxKm=0;
        Cursor c = db.rawQuery("SELECT * FROM data_records order by km desc",null);
        if (c.moveToFirst())
        do {
            maxKm=c.getDouble(c.getColumnIndex("km"));
        }while (false);
        return maxKm;
    }
    public double getMaxKcal(){
        double maxKcal=0;
        Cursor c = db.rawQuery("SELECT * FROM data_records order by cal desc",null);
        if (c.moveToFirst())
            do {
                maxKcal=c.getDouble(c.getColumnIndex("cal"));
            }while (false);
        return maxKcal;
    }
    public long getLongestTime(){
        long longestTime=0;
        Cursor c = db.rawQuery("SELECT * FROM data_records order by total_time desc",null);
        if (c.moveToFirst())
            do {
                longestTime=c.getLong(c.getColumnIndex("total_time"));
            }while (false);
        return longestTime;
    }
}
