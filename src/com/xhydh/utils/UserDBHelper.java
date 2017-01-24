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
	// ����һ�δ������ݿ��ʱ�򣬵��ø÷���   
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "create table user(id integer primary key autoincrement,phone varchar(16),pswd varchar(16))";  
		//����������ݿ����־��Ϣ  
		Log.i(TAG, "create user Database------------->");  
		//execSQL��������ִ��SQL���  
		db.execSQL(sql);  
	}
	
	//���������ݿ��ʱ��ִ�и÷���  
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
