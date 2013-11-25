package com.ese2013.mub.util.database.tables;

public class MenusTable extends AbstractTable {

	public static final String TABLE_MENUS = "menus";
	public static final String COL_ID = "id";
	public static final String COL_TITLE = "title";
	public static final String COL_DESC = "desc";
	
	private static final String TABLE_MENUS_CREATE = 
			"create table " + TABLE_MENUS + "(" + 
			COL_ID + " text primary key, " + 
			COL_TITLE + " text not null, " + 
			COL_DESC + " text not null);";
	
	public MenusTable() {
		super(TABLE_MENUS, TABLE_MENUS_CREATE);
	}
}
