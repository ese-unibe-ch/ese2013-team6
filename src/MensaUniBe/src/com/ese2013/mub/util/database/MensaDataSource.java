package com.ese2013.mub.util.database;

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
import com.ese2013.mub.util.DataManager;
import com.ese2013.mub.util.database.tables.FavoritesTable;
import com.ese2013.mub.util.database.tables.MensasTable;
import com.ese2013.mub.util.database.tables.MenusMensasTable;
import com.ese2013.mub.util.database.tables.MenusTable;

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

	public void storeMensaList(JSONArray mensaListJson, JSONArray updateStatusJson) throws JSONException {
		for (int i = 0; i < mensaListJson.length(); i++) {
			JSONObject mensaJsonObject = mensaListJson.getJSONObject(i);
			JSONObject mensaStatusObject = updateStatusJson.getJSONObject(i);
			storeMensa(mensaJsonObject, mensaStatusObject);
		}
	}

	private void storeMensa(JSONObject mensaJson, JSONObject statusJson) throws JSONException {
		ContentValues values = new ContentValues();
		values.put(MensasTable.COL_ID, mensaJson.getInt("id"));
		values.put(MensasTable.COL_NAME, mensaJson.getString("mensa"));
		values.put(MensasTable.COL_STREET, mensaJson.getString("street"));
		values.put(MensasTable.COL_ZIP, mensaJson.getString("plz"));
		values.put(MensasTable.COL_LON, mensaJson.getDouble("lon"));
		values.put(MensasTable.COL_LAT, mensaJson.getDouble("lat"));
		values.put(MensasTable.COL_TIMESTAMP, statusJson.getInt("timestamp"));
		database.replace(MensasTable.TABLE_MENSAS, null, values);
	}

	public List<Mensa> loadMensaList() {
		List<Mensa> mensas = new ArrayList<Mensa>();
		Cursor c = database.rawQuery("SELECT * FROM " + MensasTable.TABLE_MENSAS, null);
		final int POS_ID = c.getColumnIndex(MensasTable.COL_ID);
		final int POS_NAME = c.getColumnIndex(MensasTable.COL_NAME);
		final int POS_STREET = c.getColumnIndex(MensasTable.COL_STREET);
		final int POS_ZIP = c.getColumnIndex(MensasTable.COL_ZIP);
		final int POS_LON = c.getColumnIndex(MensasTable.COL_LON);
		final int POS_LAT = c.getColumnIndex(MensasTable.COL_LAT);
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
		values.put(MenusTable.COL_HASH, hash);
		values.put(MenusTable.COL_TITLE, json.getString("title"));
		values.put(MenusTable.COL_DESC, description);
		String dateString = json.getString("date");

		if (!stringIsDate(dateString))
			throw new ParseException("Unable to parse Date", 0);

		values.put(MenusTable.COL_DATE, json.getString("date"));
		database.replace(MenusTable.TABLE_MENUS, null, values);

		ContentValues values2 = new ContentValues();
		values2.put(MenusTable.COL_HASH, hash);
		values2.put(MensasTable.COL_ID, mensaId);
		database.replace(MenusMensasTable.TABLE_MENUS_MENSAS, null, values2);
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
		String query = "select * from " + MenusTable.TABLE_MENUS + " inner join "
				+ MenusMensasTable.TABLE_MENUS_MENSAS + " on " + MenusTable.TABLE_MENUS + "."
				+ MenusTable.COL_HASH + " = " + MenusMensasTable.TABLE_MENUS_MENSAS + "."
				+ MenusTable.COL_HASH + " where " + MenusMensasTable.TABLE_MENUS_MENSAS + "."
				+ MensasTable.COL_ID + " = " + mensaId + " ;";
		Cursor c = database.rawQuery(query, null);
		final int POS_TITLE = c.getColumnIndex(MenusTable.COL_TITLE);
		final int POS_DESC = c.getColumnIndex(MenusTable.COL_DESC);
		final int POS_DATE = c.getColumnIndex(MenusTable.COL_DATE);
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
		Cursor c = database.rawQuery("select * from " + FavoritesTable.TABLE_FAV_MENSAS + " where "
				+ MensasTable.COL_ID + "=" + mensaId, null);
		return c.getCount() != 0;
	}

	public void storeFavorites(List<Mensa> mensas) {
		database.delete(FavoritesTable.TABLE_FAV_MENSAS, null, null);
		for (Mensa m : mensas) {
			if (m.isFavorite()) {
				ContentValues values = new ContentValues();
				values.put(MensasTable.COL_ID, m.getId());
				database.insert(FavoritesTable.TABLE_FAV_MENSAS, null, values);
			}
		}
	}

	public int getMensaTimestamp(int mensaId) {
		Cursor c = database.rawQuery("select " + MensasTable.COL_TIMESTAMP + " from " + MensasTable.TABLE_MENSAS + " where "
				+ MensasTable.COL_ID + "=" + mensaId, null);
		c.moveToFirst();
		return c.getInt(0);
	}
}