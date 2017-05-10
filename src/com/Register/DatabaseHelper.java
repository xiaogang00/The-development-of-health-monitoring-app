package com.Register;

import android.content.*;
import android.database.sqlite.*;

public class DatabaseHelper extends SQLiteOpenHelper {
	// 定义本地用户数据库的名称和版本
	static String name = "user.db";
	static int dbVersion = 3;

	public DatabaseHelper(Context context) {
		super(context, name, null, dbVersion);
	}

	// 只在创建的时候用一次
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table user(id integer primary key autoincrement,username varchar(20),password varchar(20),name varchar(10),sex varchar(5),age integer,phone varchar(13))";
		db.execSQL(sql);
	}

	// 升级用
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
