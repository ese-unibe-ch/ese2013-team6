package com.ese2013.mub.util.database.tables;

public class FavoritesTable extends AbstractTable {
	public static final String TABLE_FAV_MENSAS = "favoriteMensas";
	
	private static final String TABLE_FAV_MENSAS_CREATE = 
			"create table " + TABLE_FAV_MENSAS + "(" + MensasTable.COL_ID +
			" integer not null references " + MensasTable.TABLE_MENSAS + "(" + MensasTable.COL_ID + ")," + 
			"primary key(" + MensasTable.COL_ID + "));";
	
	public FavoritesTable() {
		super(TABLE_FAV_MENSAS, TABLE_FAV_MENSAS_CREATE);
	}
}