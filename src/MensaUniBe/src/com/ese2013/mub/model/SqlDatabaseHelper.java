package com.ese2013.mub.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlDatabaseHelper extends SQLiteOpenHelper {

	public static final String TABLE_MENSALIST = "mensas";
	public static final String COL_ID = "_id";
	public static final String COL_NAME = "name";
	public static final String COL_STREET = "street";
	public static final String COL_ZIP = "zip";
	public static final String COL_LON = "lon";
	public static final String COL_LAT = "lat";
	
	public static final String TABLE_MENUS = "menus";
	public static final String COL_HASH = "hash";
	public static final String COL_TITLE = "title";
	public static final String COL_DESC = "desc";
	public static final String COL_DATE = "date";
	
	public static final String TABLE_MENUS_MENSAS = "menusMensas";

	private static final String DATABASE_NAME = "mensas.db";
	private static final int DATABASE_VERSION = 6;

	private static final String TABLE_MENSAS_CREATE = 
			"create table " + TABLE_MENSALIST + "(" + 
			COL_ID + " integer primary key, " + 
			COL_NAME + " text not null, " + 
			COL_STREET + " text not null, " +
			COL_ZIP + " text not null, " +
			COL_LON + " real not null, " +
			COL_LAT + " real not null);";
	
	private static final String TABLE_MENUS_CREATE = 
			"create table " + TABLE_MENUS + "(" + 
			COL_HASH + " integer primary key, " + 
			COL_TITLE + " text not null, " + 
			COL_DESC + " text not null, " +
			COL_DATE + " text not null);";
	
	private static final String TABLE_MENUS_MENSAS_CREATE = 
			"create table " + TABLE_MENUS_MENSAS + "(" + 
			COL_HASH + " integer not null references " + TABLE_MENUS + "(" + COL_HASH + ")," + 
			COL_ID + " integer not null references " + TABLE_MENSALIST + "(" + COL_ID + ")," +
			"primary key("+COL_HASH+","+ COL_ID+"));";

	public SqlDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(TABLE_MENSAS_CREATE);
		database.execSQL(TABLE_MENUS_CREATE);
		database.execSQL(TABLE_MENUS_MENSAS_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MENSALIST);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MENUS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MENUS_MENSAS);
		onCreate(db);
	}
}