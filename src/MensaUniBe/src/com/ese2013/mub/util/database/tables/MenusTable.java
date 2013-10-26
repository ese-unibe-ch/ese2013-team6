package com.ese2013.mub.util.database.tables;

public class MenusTable extends AbstractTable {

	public static final String TABLE_MENUS = "menus";
	public static final String COL_HASH = "hash";
	public static final String COL_TITLE = "title";
	public static final String COL_DESC = "desc";
	public static final String COL_DATE = "date";
	
	
	private static final String TABLE_MENUS_CREATE = 
			"create table " + TABLE_MENUS + "(" + 
			COL_HASH + " integer primary key, " + 
			COL_TITLE + " text not null, " + 
			COL_DESC + " text not null, " +
			COL_DATE + " text not null);";
	
	public MenusTable() {
		super(TABLE_MENUS, TABLE_MENUS_CREATE);
	}
}
