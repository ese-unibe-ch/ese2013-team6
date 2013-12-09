package com.ese2013.mub.util.database;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.ese2013.mub.model.DailyMenuplan;
import com.ese2013.mub.model.Day;
import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Menu;
import com.ese2013.mub.model.MenuManager;
import com.ese2013.mub.model.WeeklyMenuplan;
import com.ese2013.mub.util.database.tables.FavoritesTable;
import com.ese2013.mub.util.database.tables.MensasTable;
import com.ese2013.mub.util.database.tables.MenusMensasTable;
import com.ese2013.mub.util.database.tables.MenusTable;

/**
 * Manages storing and loading data from the Mensa SQLite database.
 */
public class MensaDataSource {
	private SQLiteDatabase database;
	private SqlDatabaseHelper dbHelper;
	private SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
	private static MensaDataSource instance;

	private MensaDataSource() {
	}

	public static MensaDataSource getInstance() {
		if (instance == null)
			instance = new MensaDataSource();
		return instance;
	}

	/**
	 * Should be called before the MensaDataSource is used.
	 * 
	 * @param context
	 *            Context in which the DataSource is used, i.e. the application
	 *            context. Must not be null.
	 */
	public void init(Context context) {
		dbHelper = new SqlDatabaseHelper(context);
	}

	/**
	 * Opens the database, must be called before using the database. Caller must
	 * also call close() after using the database to avoid a resource leak.
	 * 
	 * @throws SQLException
	 *             If the database cannot be created or read.
	 */
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	/**
	 * Closes the database. Must be called after using the database.
	 */
	public void close() {
		dbHelper.close();
	}

	/**
	 * Stores the given list of mensas
	 * 
	 * @param mensas
	 *            The List of Mensa objects to be stored. Must not be null and
	 *            must not contain null.
	 */
	public void storeMensaList(List<Mensa> mensas) {
		for (Mensa m : mensas)
			storeMensa(m);
	}

	private void storeMensa(Mensa m) {
		ContentValues values = new ContentValues();
		values.put(MensasTable.COL_ID, m.getId());
		values.put(MensasTable.COL_NAME, m.getName());
		values.put(MensasTable.COL_STREET, m.getStreet());
		values.put(MensasTable.COL_ZIP, m.getZip());
		values.put(MensasTable.COL_LON, m.getLongitude());
		values.put(MensasTable.COL_LAT, m.getLatitude());
		database.replace(MensasTable.TABLE_MENSAS, null, values);
	}

	/**
	 * Loads the list of mensas from the database
	 * 
	 * @return List of Mensas.
	 */
	public List<Mensa> loadMensaList() {
		List<Mensa> mensas = new ArrayList<Mensa>();
		Cursor c = database.rawQuery("SELECT * FROM " + MensasTable.TABLE_MENSAS, null);
		final int POS_ID = c.getColumnIndex(MensasTable.COL_ID);
		final int POS_NAME = c.getColumnIndex(MensasTable.COL_NAME);
		final int POS_STREET = c.getColumnIndex(MensasTable.COL_STREET);
		final int POS_ZIP = c.getColumnIndex(MensasTable.COL_ZIP);
		final int POS_LON = c.getColumnIndex(MensasTable.COL_LON);
		final int POS_LAT = c.getColumnIndex(MensasTable.COL_LAT);
		while (c.moveToNext()) {
			Mensa.MensaBuilder builder = new Mensa.MensaBuilder();
			int mensaId = c.getInt(POS_ID);
			builder.setId(mensaId);
			builder.setName(c.getString(POS_NAME));
			builder.setStreet(c.getString(POS_STREET));
			builder.setZip(c.getString(POS_ZIP));
			builder.setLongitude(c.getDouble(POS_LON));
			builder.setLatitude(c.getDouble(POS_LAT));
			builder.setIsFavorite(isInFavorites(mensaId));
			mensas.add(builder.build());
		}
		c.close();
		return mensas;
	}

	/**
	 * Updates the table of favorite mensas using the given mensa list. Every
	 * mensa which is a favorite mensa gets stored in the favorites table.
	 * 
	 * @param mensas
	 *            List of mensas to update the favorites table.
	 */
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

	/**
	 * Checks if a given mensa id belongs to a favorite mensa.
	 * 
	 * @param mensaId
	 *            Int mensa id to be checked.
	 * @return true if the mensa id belongs to a favorite mensa.
	 */
	public boolean isInFavorites(int mensaId) {
		Cursor c = database.rawQuery("select * from " + FavoritesTable.TABLE_FAV_MENSAS + " where " + MensasTable.COL_ID
				+ "=" + mensaId, null);
		return c.getCount() != 0;
	}

	/**
	 * Stores the given Mensa's weekly menu plan to the database. Caller must
	 * assure that only weekly menu plans of the same week are stored in the
	 * database.
	 * 
	 * @param mensa
	 *            The Mensa the weekly plan belongs to. Must not be null
	 */
	public void storeWeeklyMenuplan(Mensa mensa) {
		WeeklyMenuplan plan = mensa.getMenuplan();
		for (DailyMenuplan d : plan)
			for (Menu m : d.getMenus())
				storeMenu(m, mensa);
	}

	/**
	 * Stores the given Menu in the MenuTable and also stores a pair of Menu Id
	 * and Mensa Id in the MenusMensa Table.
	 * 
	 * @param menu
	 *            Menu to be stored. Must not be null.
	 * @param mensa
	 *            Mensa which the Menu belongs to. Must not be null.
	 */
	private void storeMenu(Menu menu, Mensa mensa) {
		ContentValues values = new ContentValues();
		values.put(MenusTable.COL_ID, menu.getId());
		values.put(MenusTable.COL_TITLE, menu.getOrigTitle());
		values.put(MenusTable.COL_DESC, menu.getOrigDescription());
		values.put(MenusTable.COL_TRANSL_TITLE, menu.getTranslatedTitle());
		values.put(MenusTable.COL_TRANSL_DESC, menu.getTranslatedDescription());

		database.replace(MenusTable.TABLE_MENUS, null, values);

		ContentValues values2 = new ContentValues();
		values2.put(MenusTable.COL_ID, menu.getId());
		values2.put(MensasTable.COL_ID, mensa.getId());
		values2.put(MenusMensasTable.COL_DATE, mensa.getMenuplan().getDayOfServing(menu).format(fm));
		database.replace(MenusMensasTable.TABLE_MENUS_MENSAS, null, values2);
	}

	/**
	 * Loads the weekly menu plan of a given mensa, represented by the mensa id.
	 * 
	 * @param mensaId
	 *            Id of the mensa.
	 * @return WeeklyMenuplan of the given mensa.
	 */
	public WeeklyMenuplan loadMenuplan(int mensaId, MenuManager menuManager) {
		String query = "select * from " + MenusTable.TABLE_MENUS + " inner join " + MenusMensasTable.TABLE_MENUS_MENSAS
				+ " on " + MenusTable.TABLE_MENUS + "." + MenusTable.COL_ID + " = " + MenusMensasTable.TABLE_MENUS_MENSAS
				+ "." + MenusTable.COL_ID + " where " + MenusMensasTable.TABLE_MENUS_MENSAS + "." + MensasTable.COL_ID
				+ " = " + mensaId + " ;";
		Cursor c = database.rawQuery(query, null);
		final int POS_ID = c.getColumnIndex(MenusTable.COL_ID);
		final int POS_TITLE = c.getColumnIndex(MenusTable.COL_TITLE);
		final int POS_DESC = c.getColumnIndex(MenusTable.COL_DESC);
		final int POS_TRANSL_TITLE = c.getColumnIndex(MenusTable.COL_TRANSL_TITLE);
		final int POS_TRANSL_DESC = c.getColumnIndex(MenusTable.COL_TRANSL_DESC);
		final int POS_DATE = c.getColumnIndex(MenusMensasTable.COL_DATE);
		WeeklyMenuplan p = new WeeklyMenuplan();
		while (c.moveToNext()) {
			try {
				Menu menu = menuManager.createMenu(c.getString(POS_ID), c.getString(POS_TITLE), c.getString(POS_DESC),
						c.getString(POS_TRANSL_TITLE), c.getString(POS_TRANSL_DESC));
				p.add(menu, new Day(fm.parse(c.getString(POS_DATE))));
			} catch (ParseException e) {
				throw new AssertionError("Database did not save properly");
			}
		}
		return p;
	}

	/**
	 * Returns the minimum week of the stored menus.
	 * 
	 * @return Minimum number of week of the menus. This means if there are
	 *         menus from week 42 and 43 it will return 42.The return value is
	 *         -1 if the database is empty.
	 */
	public int getWeekOfStoredMenus() {
		Cursor c = database.query(MenusMensasTable.TABLE_MENUS_MENSAS, new String[] { MenusMensasTable.COL_DATE }, null,
				null, MenusMensasTable.COL_DATE, null, null);

		final int POS_DATE = c.getColumnIndex(MenusMensasTable.COL_DATE);
		int minWeek = Integer.MAX_VALUE;
		while (c.moveToNext()) {
			try {
				String dateString = c.getString(POS_DATE);
				Calendar cal = Calendar.getInstance(Locale.GERMAN);
				cal.setTime(fm.parse(dateString));
				int week = cal.get(Calendar.WEEK_OF_YEAR);
				minWeek = week < minWeek ? week : minWeek;
			} catch (ParseException e) {
				throw new AssertionError("Database did not save properly");
			}
		}
		return minWeek == Integer.MAX_VALUE ? -1 : minWeek;
	}

	/**
	 * Deletes all menus from the database.
	 */
	public void deleteMenus() {
		database.delete(MenusTable.TABLE_MENUS, null, null);
		database.delete(MenusMensasTable.TABLE_MENUS_MENSAS, null, null);
	}

	/**
	 * Completely clears the whole database.
	 */
	public void cleanUpAllTables() {
		database.delete(MensasTable.TABLE_MENSAS, null, null);
		database.delete(FavoritesTable.TABLE_FAV_MENSAS, null, null);
		deleteMenus();
	}
}