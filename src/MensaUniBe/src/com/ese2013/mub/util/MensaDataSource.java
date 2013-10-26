package com.ese2013.mub.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Menu;
import com.ese2013.mub.model.WeeklyMenuplan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class MensaDataSource {

	// Database fields
	private SQLiteDatabase database;
	private SqlDatabaseHelper dbHelper;
	private static SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);

	public MensaDataSource(Context context) {
		dbHelper = new SqlDatabaseHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void storeMensaList(JSONArray content) throws JSONException {
		for (int i = 0; i < content.length(); i++) {
			JSONObject mensaJsonObject = content.getJSONObject(i);
			storeMensa(mensaJsonObject);
		}
	}

	private void storeMensa(JSONObject json) throws JSONException {
		ContentValues values = new ContentValues();
		values.put(SqlDatabaseHelper.COL_ID, json.getInt("id"));
		values.put(SqlDatabaseHelper.COL_NAME, json.getString("mensa"));
		values.put(SqlDatabaseHelper.COL_STREET, json.getString("street"));
		values.put(SqlDatabaseHelper.COL_ZIP, json.getString("plz"));
		values.put(SqlDatabaseHelper.COL_LON, json.getDouble("lon"));
		values.put(SqlDatabaseHelper.COL_LAT, json.getDouble("lat"));
		database.replace(SqlDatabaseHelper.TABLE_MENSAS, null, values);
	}

	public List<Mensa> loadMensaList() {
		List<Mensa> mensas = new ArrayList<Mensa>();
		Cursor c = database.rawQuery("SELECT * FROM " + SqlDatabaseHelper.TABLE_MENSAS, null);
		final int POS_ID = c.getColumnIndex(SqlDatabaseHelper.COL_ID);
		final int POS_NAME = c.getColumnIndex(SqlDatabaseHelper.COL_NAME);
		final int POS_STREET = c.getColumnIndex(SqlDatabaseHelper.COL_STREET);
		final int POS_ZIP = c.getColumnIndex(SqlDatabaseHelper.COL_ZIP);
		final int POS_LON = c.getColumnIndex(SqlDatabaseHelper.COL_LON);
		final int POS_LAT = c.getColumnIndex(SqlDatabaseHelper.COL_LON);
		c.moveToFirst();
		do {
			Mensa.MensaBuilder builder = new Mensa.MensaBuilder();
			int mensaId = c.getInt(POS_ID);
			builder.setId(mensaId);
			builder.setName(c.getString(POS_NAME));
			builder.setStreet(c.getString(POS_STREET));
			builder.setZip(c.getString(POS_ZIP));
			builder.setLongitude(c.getDouble(POS_LON));
			builder.setLatitude(c.getDouble(POS_LAT));
			builder.setIsFavorite(DataManager.getSingleton().isInFavorites(mensaId));
			mensas.add(builder.build());
		} while (c.moveToNext());
		c.close();
		return mensas;
	}

	public void storeMenuplan(JSONArray content, int mensaId) throws JSONException, ParseException {
		for (int i = 0; i < content.length(); i++) {
			JSONObject menuJson = content.getJSONObject(i);
			storeMenu(menuJson, mensaId);
		}
	}

	private void storeMenu(JSONObject json, int mensaId) throws JSONException, ParseException {
		JSONArray desc = json.getJSONArray("menu");
		String description = "";
		for (int i = 0; i < desc.length(); i++) {
			description += desc.getString(i) + "\n";
		}

		ContentValues values = new ContentValues();
		int hash = json.getInt("hash");
		values.put(SqlDatabaseHelper.COL_HASH, hash);
		values.put(SqlDatabaseHelper.COL_TITLE, json.getString("title"));
		values.put(SqlDatabaseHelper.COL_DESC, description);
		String dateString = json.getString("date");

		if (!stringIsDate(dateString))
			throw new ParseException("Unable to parse Date", 0);

		values.put(SqlDatabaseHelper.COL_DATE, json.getString("date"));
		database.replace(SqlDatabaseHelper.TABLE_MENUS, null, values);

		ContentValues values2 = new ContentValues();
		values2.put(SqlDatabaseHelper.COL_HASH, hash);
		values2.put(SqlDatabaseHelper.COL_ID, mensaId);
		database.replace(SqlDatabaseHelper.TABLE_MENUS_MENSAS, null, values2);
	}

	private boolean stringIsDate(String dateString) {
		try {
			fm.parse(dateString);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	public WeeklyMenuplan loadMenuplan(int mensaId) {
		String query = "select * from " + SqlDatabaseHelper.TABLE_MENUS + " inner join "
				+ SqlDatabaseHelper.TABLE_MENUS_MENSAS + " on " + SqlDatabaseHelper.TABLE_MENUS + "."
				+ SqlDatabaseHelper.COL_HASH + " = " + SqlDatabaseHelper.TABLE_MENUS_MENSAS + "."
				+ SqlDatabaseHelper.COL_HASH + " where " + SqlDatabaseHelper.TABLE_MENUS_MENSAS + "."
				+ SqlDatabaseHelper.COL_ID + " = " + mensaId + " ;";
		Cursor c = database.rawQuery(query, null);
		final int POS_TITLE = c.getColumnIndex(SqlDatabaseHelper.COL_TITLE);
		final int POS_DESC = c.getColumnIndex(SqlDatabaseHelper.COL_DESC);
		final int POS_DATE = c.getColumnIndex(SqlDatabaseHelper.COL_DATE);
		WeeklyMenuplan p = new WeeklyMenuplan();
		c.moveToFirst();
		do {
			try {
				Menu.MenuBuilder builder = new Menu.MenuBuilder();
				builder.setTitle(c.getString(POS_TITLE));
				builder.setDescription(c.getString(POS_DESC));
				builder.setDate(fm.parse(c.getString(POS_DATE)));
				p.addMenu(builder.build());
			} catch (ParseException e) {
				// If this happens, the db violated it's contract of properly
				// storing the given data.
				throw new AssertionError("Database did not save properly");
			}

		} while (c.moveToNext());
		return p;
	}

	public boolean isInFavorites(int mensaId) {
		Cursor c = database.rawQuery("select * from " + SqlDatabaseHelper.TABLE_FAV_MENSAS + " where "
				+ SqlDatabaseHelper.COL_ID + "=" + mensaId, null);
		return c.getCount() != 0;
	}

	public void storeFavorites(List<Mensa> mensas) {
		database.delete(SqlDatabaseHelper.TABLE_FAV_MENSAS, null, null);
		for (Mensa m : mensas) {
			if (m.isFavorite()) {
				ContentValues values = new ContentValues();
				values.put(SqlDatabaseHelper.COL_ID, m.getId());
				database.insert(SqlDatabaseHelper.TABLE_FAV_MENSAS, null, values);
			}
		}
	}
}