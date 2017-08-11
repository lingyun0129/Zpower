package com.zpower.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class MySQLiteHelper extends SQLiteOpenHelper {

	private static final String tag = MySQLiteHelper.class.getSimpleName();

	private final static String db_name = "zpower";
	private final static int db_version = 1;

	private static MySQLiteHelper mInstance;

	private MySQLiteHelper(Context context) {
		super(context, db_name, null, db_version);
	}

	public static MySQLiteHelper getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new MySQLiteHelper(context);
		}
		return mInstance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(tag, "SQLite Create Users table");
		db.execSQL("create table users (id integer primary key autoincrement, Email varchar, password varchar)");
		db.execSQL("CREATE TABLE IF NOT EXISTS data_records" +
				" (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"date VARCHAR," +
				"total_time INTEGER," +
				"avg_p INTEGER," +
				"avg_rpm INTEGER," +
				"km double," +
				"cal double)");
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(tag, "SQLite onUpgrade()   oldVersion:"+oldVersion+"  newVersion:"+newVersion);
		switch (oldVersion) {
		case 0:
			
			break;

		default:
			break;
		}

	}

}
