package com.ese2013.mub.util.database.tables;

public class MensasTable extends AbstractTable {

	public static final String TABLE_MENSAS = "mensas";
	public static final String COL_ID = "_id";
	public static final String COL_NAME = "name";
	public static final String COL_STREET = "street";
	public static final String COL_ZIP = "zip";
	public static final String COL_LON = "lon";
	public static final String COL_LAT = "lat";
	
	private static final String TABLE_MENSAS_CREATE = 
			"create table " + TABLE_MENSAS + "(" + 
			COL_ID + " integer primary key, " + 
			COL_NAME + " text not null, " + 
			COL_STREET + " text not null, " +
			COL_ZIP + " text not null, " +
			COL_LON + " real not null, " +
			COL_LAT + " real not null);";
	
	public MensasTable() {
		super(TABLE_MENSAS, TABLE_MENSAS_CREATE);
	}
}
