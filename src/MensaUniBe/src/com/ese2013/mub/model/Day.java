package com.ese2013.mub.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Represents a simple date composed of year, month and day. Partly wraps the
 * functionality of the java.util.Date class.
 */
public class Day implements Comparable<Day> {
	private int year, month, day;

	/**
	 * Creates a Day from a given java.util.Date.
	 * 
	 * @param date
	 *            Date used to retrieve year, month and day. Must not be null.
	 */
	public Day(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		year = cal.get(Calendar.YEAR);
		month = cal.get(Calendar.MONTH);
		day = cal.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * Creates a Day given day, month and year as int. (only used in tests).
	 * 
	 * @param day
	 *            int containing the number of the day in the month. Must be
	 *            between 1 and 28/29/30/31, depending on the month.
	 * @param month
	 *            int containing the number of the month in the year. Must be
	 *            between 1 and 12.
	 * @param year
	 *            int containing the year, must be of the form "2013"
	 */
	public Day(int day, int month, int year) {
		this.year = year;
		this.month = month - 1; // Calendar counts month from 0 to 11
		this.day = day;
	}

	/**
	 * Converts the Day to a java.util.Date. Used internally to allow for
	 * formatting with the SimpleDateFormat class.
	 * 
	 * @return Date set the the day represented by this Day object.
	 */
	public Date getDate() {
		return getCalendar().getTime();
	}

	/**
	 * Converts the Day to a Calendar object representing the same day, using
	 * Locale.GERMAN to avoid confusion with the U.S. calendar standards. Used
	 * for unified output for saving!
	 * 
	 * @return Calendar set to the same day as the Day object.
	 */
	private Calendar getCalendar() {
		Calendar cal = Calendar.getInstance(Locale.GERMAN);
		cal.setTimeInMillis(0);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		return cal;
	}

	/**
	 * Returns the number of the week this day is in.
	 * 
	 * @return Number of week.
	 */
	public int getWeekNumber() {
		return getCalendar().get(Calendar.WEEK_OF_YEAR);
	}

	/**
	 * Returns the Day as a String, formatted using the passed SimpleDateFormat.
	 * 
	 * @param fm
	 *            SimpleDateFormat to be used to format the Day. Must not be
	 *            null and should only use year, month and day as day time
	 *            (hours, minutes etc.) are not well defined for a Day.
	 * @return String representing the Day, created using the SimpleDateFormat.
	 */
	public String format(SimpleDateFormat fm) {
		return fm.format(getDate());
	}

	/**
	 * Returns the day of week as String (e.g. Monday, Tuesday and so on).
	 * 
	 * @return Day of week as String.
	 */
	public String getDayOfWeekString() {
		return format(new SimpleDateFormat("EEEE", Locale.getDefault()));
	}

	@Override
	public String toString() {
		return format(new SimpleDateFormat("EEEE, dd. MMMM yyyy", Locale.getDefault()));
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Day) {
			Day otherDay = (Day) other;
			if (otherDay.day != this.day || otherDay.month != this.month || otherDay.year != this.year)
				return false;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		final int multiplier = 29;
		int hashCode = 17;
		hashCode += year;
		hashCode *= multiplier;
		hashCode += month;
		hashCode *= multiplier;
		hashCode += day;
		return hashCode;
	}

	/**
	 * Returns a new Day Object set to the current day.
	 * 
	 * @return Day object set to the current day.
	 */
	public static Day today() {
		return new Day(Calendar.getInstance(Locale.getDefault()).getTime());
	}

	/**
	 * Returns the day as string, i.e. 6.12.2013 as 20131206
	 * 
	 * @return
	 */
	private int asInt() {
		int i = year;
		i *= 100;
		i += month;
		i *= 100;
		i += day;
		return i;
	}

	@Override
	public int compareTo(Day another) {
		return this.asInt() - another.asInt();
	}
}
