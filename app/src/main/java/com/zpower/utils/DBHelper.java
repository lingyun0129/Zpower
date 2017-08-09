package com.zpower.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zpower.model.HistoryData;
import com.zpower.model.RecordData;

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
        values.put("total_time", data.getTime());
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
        Cursor c = db.rawQuery("SELECT * FROM data_records", null);
        while(c.moveToNext()) {
            String date=c.getString(c.getColumnIndex("date"));
            String time=c.getString(c.getColumnIndex("total_time"));
            String watt=c.getInt(c.getColumnIndex("avg_p"))+"";

            HistoryData data=new HistoryData(date,time,watt);
            historyDatas.add(data);
        }
        c.close();
        return historyDatas;
    }

}
