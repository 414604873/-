package com.xhydh.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UserDBHelper extends SQLiteOpenHelper{

	private static final String TAG = "User";  
	public static final int VERSION = 1;  
	
	public UserDBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	// 当第一次创建数据库的时候，调用该方法   
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "create table user(id integer primary key autoincrement,phone varchar(16),pswd varchar(16))";  
		//输出创建数据库的日志信息  
		Log.i(TAG, "create user Database------------->");  
		//execSQL函数用于执行SQL语句  
		db.execSQL(sql);  
	}
	
	//当更新数据库的时候执行该方法  
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
