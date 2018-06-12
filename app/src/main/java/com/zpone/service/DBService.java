package com.zpone.service;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zpone.ZPower;
import com.zpone.utils.MySQLiteHelper;

/**
 * 相当于DAO层，提供SQLite相关的数据库操作；
 * 主要提供给LoginService等service层使用，不允许UI层直接使用
 * 注：相关方法根据实际需要添加
 * 
 * @author guzhicheng
 *
 */
public class DBService {

	private final static String tag = DBService.class.getCanonicalName();
	
	private static DBService mInstance = new DBService();
	private MySQLiteHelper dbHelper;
	private final SQLiteDatabase db;

	private DBService(){
		dbHelper = MySQLiteHelper.getInstance(ZPower.getInstance().getApplicationContext());
		db = dbHelper.getReadableDatabase();
	};
	public static DBService getInstance(){
		if(mInstance == null){
			mInstance = new DBService();
		}
		return mInstance;
	}

	public boolean checkEmail(String email){

		Cursor cursor = db.query("users",null,null,null,null,null,null);
		while (cursor.moveToNext()){
			String saved_Email = cursor.getString(cursor.getColumnIndex("Email"));
			if (saved_Email.equals(email)){
				return false;
			}
		}
		cursor.close();
		return true;
	}

	/***
	 * 判断邮箱和密码是否正确
	 * @param email
	 * @param password
     * @return
     */
	public boolean checkEmailAndPassword(String email,String password){
		Cursor cursor = db.query("users",null,null,null,null,null,null);
		while (cursor.moveToNext()){
			String saved_Email = cursor.getString(cursor.getColumnIndex("Email"));
			String saved_password = cursor.getString(cursor.getColumnIndex("password"));
			if (saved_Email.equals(email) && saved_password.equals(password)){
				return true;
			}
		}
		cursor.close();
		return false;
	}

	public long insertUser(String email, String password){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put("Email",email);
		values.put("password",password);
		return db.insert("users",null,values);
	}
	
}
