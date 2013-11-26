package com.ese2013.mub.util.database.tables;

public class MenusTable extends AbstractTable {

	public static final String TABLE_MENUS = "menus";
	public static final String COL_ID = "id";
	public static final String COL_TITLE = "title";
	public static final String COL_DESC = "desc";
	public static final String COL_USERRATING = "user_rating";
	public static final String COL_RATINGSUM = "rating_sum";
	public static final String COL_RATINGCOUNT = "rating_count";
	
	private static final String TABLE_MENUS_CREATE = 
			"create table " + TABLE_MENUS + "(" + 
			COL_ID + " text primary key, " + 
			COL_TITLE + " text not null, " + 
			COL_DESC + " text not null, " +
			COL_USERRATING + " integer not null, " +
			COL_RATINGSUM + " integer not null," +
			COL_RATINGCOUNT + " integer not null" +
			");";
	
	public MenusTable() {
		super(TABLE_MENUS, TABLE_MENUS_CREATE);
		System.out.println(TABLE_MENUS_CREATE);
	}
}
