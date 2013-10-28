package com.ese2013.mub.util.database;

import java.util.ArrayList;
import java.util.List;

import com.ese2013.mub.util.database.tables.AbstractTable;
import com.ese2013.mub.util.database.tables.FavoritesTable;
import com.ese2013.mub.util.database.tables.MensasTable;
import com.ese2013.mub.util.database.tables.MenusMensasTable;
import com.ese2013.mub.util.database.tables.MenusTable;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlDatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "mensas.db";
	private static final int DATABASE_VERSION = 10;

	private List<AbstractTable> tables = new ArrayList<AbstractTable>(4);

	public SqlDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		tables.add(new MensasTable());
		tables.add(new MenusTable());
		tables.add(new FavoritesTable());
		tables.add(new MenusMensasTable());
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for (AbstractTable t : tables)
			t.create(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		for (AbstractTable t : tables)
			t.drop(db);
		onCreate(db);
	}
}