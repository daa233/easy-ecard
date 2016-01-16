package com.duang.easyecard.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class MyDatabaseHelper extends SQLiteOpenHelper {
	
	private static final String CREATE_LOST_INFO = "create table LostInfo ("
			// 事件ID
			+ "info_id char primary key not null, "
			// 姓名
			+ "name text, "
			// 学工号
			+ "stu_id char(11), "
			// 账号
			+ "account text, "
			// 丢失地点
			+ "lost_place text, "
			// 联系电话
			+ "contact text, "
			// 说明
			+ "description text, "
			// 发布时间
			+ "publish_time text, "
			// 招领时间
			+ "found_time text, "
			// 招领状态
			+ "state text, "
			// 操作（一般为空，不显示）
			+ "operation text)";

	@SuppressWarnings("unused")
	private Context mContext;
	
	public MyDatabaseHelper(Context context, String name, CursorFactory
			factory, int version) {
		super(context, name, factory, version);
		mContext = context;
	}
	@Override
	public void onCreate(SQLiteDatabase db)	{
		db.execSQL(CREATE_LOST_INFO);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)	{
		
	}
}
